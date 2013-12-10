/**
 * 
 */
package com.elasticpath.sellingchannel.impl;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.misc.MoneyFormatter;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.sellingchannel.TemplateModelFactory;
import com.elasticpath.service.catalog.ProductCharacteristicsService;
import com.elasticpath.service.catalogview.ProductViewService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.sfweb.EpSfWebException;
import com.elasticpath.sfweb.controller.ShoppingItemFormBeanContainerFactory;
import com.elasticpath.sfweb.viewbean.ProductViewBean;

/**
 * Creates a model that the storefront templates can use to display products for sale.
 */
public class TemplateModelFactoryImpl implements TemplateModelFactory {

	private StoreProductLoadTuner productLoadTuner;

	private ShoppingCartService shoppingCartService;

	private ProductViewService productViewService;

	private PriceLookupFacade priceLookupFacade;
	
	private BeanFactory beanFactory;

	private ShoppingItemFormBeanContainerFactory cartFormBeanFactory;
	
	private ProductCharacteristicsService productCharacteristicsService;

	private MoneyFormatter moneyFormatter;
	
	@Override
	public Map<String, Object> createModel(final ShoppingCart shoppingCart, final String updatePage, final Long cartItemIdParameter,
			final Warehouse warehouse, final Catalog catalog, final StoreProduct product) {
		
		// Ensures that if this is not an official update request or a gift certificate
		// then the cartItemId will be null. This helps the further logic testing for an update request
		// by checking the value of cartItemId.
		Long cartItemId = null;
		final boolean isUpdateRequest = !"".equals(updatePage);
		if (isUpdateRequest) {
			cartItemId = cartItemIdParameter;						
		}
	
		addProductToShoppingCartViewHistory(shoppingCart, product);
	
		final ProductViewBean productViewBean = createProductViewBean(product, shoppingCart);
		
		// The guided SKU selection currently requires that shopping carts be persistent
		// so that the cart can be retrieved server-side for rules firing when a new
		// SKU is requested through DWR
		ShoppingCart updatedCart = getShoppingCartService().saveIfNotPersisted(shoppingCart);

		final Price price = priceLookupFacade.getPromotedPriceForSku(product.getDefaultSku(), 
				updatedCart.getStore(), updatedCart.getShopper(), updatedCart.getAppliedRules());
		
		final Set<ProductAssociation> warranties = product.getAssociationsByType(ProductAssociation.WARRANTY);
		
		List<Product> associatedProducts = getAssociatedProducts(product);
		
		Map <String, Price> associationPrices = getPrices(updatedCart, updatedCart.getCurrency(), catalog, associatedProducts);
		Map <String, ProductCharacteristics> associationCharacteristics = getProductCharacteristicsService().getProductCharacteristicsMap(
				associatedProducts);
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (updatePageIsValid(updatePage)) {
			addCartItemModificationDataToProductViewBean(updatedCart.getCartItemById(cartItemId.longValue()),
					product,
					updatePage, productViewBean);
		}
						
		model.put("productViewBean", productViewBean);
		model.put("catalog", catalog);
		model.put("warehouse", warehouse);
		model.put("warranties", warranties);
		model.put("price", price);
		model.put("associationPrices", associationPrices);
		model.put("associationCharacteristics", associationCharacteristics);
		
		model.put("currencySymbol", moneyFormatter.formatCurrencySymbol(shoppingCart.getCurrency()));
		return model;
	}

	/**
	 * Get prices for products.
	 * Apply promos on prices.
	 * 
	 * @param cart shopping cart
	 * @param currency the currency
	 * @param catalog the catalog for the prices
	 * @param products the products
	 * 
	 * @return list of Prices
	 */
	protected Map<String, Price> getPrices(final ShoppingCart cart, final Currency currency, final Catalog catalog, 
			final List<Product> products) {
		
		return priceLookupFacade.getPromotedPricesForProducts(products, 
				cart.getStore(), cart.getShopper(), cart.getAppliedRules());
		
	}

	/**
	 * Adds the data required for updating gift certificates.
	 * 
	 * @param cartItemId The id of the cart item to be updated.
	 * @param updatePage The page to refer the customer to.
	 * @param model The model to modify
	 */
	void addGiftCertificateModificationData(final Long cartItemId,
			final String updatePage, final Map<String, Object> model) {
		// giftCertificate has a different update page so we don't want to check it.
		model.put(WebConstants.REQUEST_CART_ITEM_ID, cartItemId);
		model.put(WebConstants.REQUEST_UPDATE, updatePage);
	}

	/**
	 * True if the product is a gift certificate.
	 * 
	 * @param product The product to test.
	 * @return True if the product is a gift certificate. False otherwise.
	 */
	boolean isGiftCertificate(final StoreProduct product) {
		return "giftCertificate".equals(product.getProductType().getTemplate());
	}

	/**
	 * @param product source product to get associations
	 * @return list of associated products
	 */
	List<Product> getAssociatedProducts(final StoreProduct product) {
		List <Product> targetAssociationProducts = new ArrayList<Product>();
		for (ProductAssociation association : product.getProductAssociations()) {
			targetAssociationProducts.add(association.getTargetProduct());
		}
		return targetAssociationProducts;
	}

	private void addCartItemModificationDataToProductViewBean(
			final ShoppingItem cartItem, final StoreProduct product,
			final String updatePage, final ProductViewBean productViewBean) {
		productViewBean.setUpdatePage(updatePage);
		if (cartItem != null) {
			product.setDefaultSku(cartItem.getProductSku());
			productViewBean.setUpdateCartItemUid(cartItem.getUidPk());
			productViewBean.setUpdateCartItemQty(cartItem.getQuantity());
		}
	}
	
	/**
	 * Add the given product to the cart view history.
	 * 
	 * @param shoppingCart the shopping cart
	 * @param product the product to add
	 */
	protected void addProductToShoppingCartViewHistory(final ShoppingCart shoppingCart, final StoreProduct product) {
		// Add the product to the shopping cart view history
		shoppingCart.getViewHistory().addProduct(product);
	}
	
	private boolean updatePageIsValid(final String updatePage) {
		return updatePage != null
				&& (updatePage.equals(WebConstants.REQUEST_UPDATE_BILLING_REVIEW)
				    || updatePage.equals(WebConstants.REQUEST_UPDATE_VIEW_CART));
	}
		
	/**
	 * Create a product view bean.
	 * 
	 * @param currentProduct the product being viewed
	 * @param shoppingCart the shopping cart
	 * @return the product view bean
	 */
	protected ProductViewBean createProductViewBean(final StoreProduct currentProduct, final ShoppingCart shoppingCart) {
		final ProductViewBean productViewBean = getBeanFactory().getBean("productViewBean");
		productViewBean.setProduct(currentProduct);
		productViewBean.setProductCategory(currentProduct.getDefaultCategory(shoppingCart.getStore().getCatalog()));
	
		final CatalogViewResultHistory catalogViewResultHistory = shoppingCart.getCatalogViewResultHistory();
		CatalogViewResult catalogViewResult = null;
		if (catalogViewResultHistory != null) {
			catalogViewResult = catalogViewResultHistory.getLastResult();
		}
		productViewBean.setCurrentCatalogViewResult(catalogViewResult);
		return productViewBean;
	}
	
	/**
	 * Gets the {@code BeanFactory}.
	 * @return The {@code BeanFactory}. 
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	/**
	 * Sets the {@code BeanFactory}.
	 * @param beanFactory The {@code BeanFactory}.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * @param productId productId 
	 * @param shoppingCart shoppingCart
	 * @return StoreProduct
	 */
	StoreProduct getProduct(final String productId, final ShoppingCart shoppingCart) {
		if (StringUtils.isEmpty(productId)) {
			throw new EpSfWebException("Product id is not given.");
		}
	
		final StoreProduct product = this.productViewService.getProduct(productId, productLoadTuner, shoppingCart);
	
		if (!showInStorefront(product)) {
			throw new EpSfWebException("Product is not available, product id:" + productId);
		}
	
		return product;
	}
	
	/**
	 * Check if the product should be shown in the storefront.
	 * <br />
	 * Product should be shown if product is displayable.
	 * 
	 * 
	 * @param product instance of StoreProduct
	 * @return true if product should be shown in the storefront
	 */
	boolean showInStorefront(final StoreProduct product) {
		return product != null && (product.isDisplayable());
	}
	
	@Override
	public String getTemplateName(final String productCode, final ShoppingCart shoppingCart) {
		StoreProduct product = getProduct(productCode, shoppingCart);
		return product.getTemplate();
	}
	
	/**
	 * Sets the product load tuner.
	 * 
	 * @param productLoadTuner the product load tuner
	 */
	public void setStoreProductLoadTuner(final StoreProductLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
	}
	
	/**
	 * Sets the product view service.
	 * 
	 * @param productViewService the product view service
	 */
	public void setProductViewService(final ProductViewService productViewService) {
		this.productViewService = productViewService;
	}
	
	/**
	 * Sets the shopping cart service.
	 * 
	 * @param shoppingCartService the shopping cart service
	 */
	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	protected ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}

	/**
	 * 
	 * @param factory The factory for {@code CartUpdateFormBean}s.
	 */
	public void setCartFormBeanFactory(final ShoppingItemFormBeanContainerFactory factory) {
		this.cartFormBeanFactory = factory;
	}
	
	/**
	 * 
	 * @return The builder for {@code ShoppingCartItemFormBean}s.
	 */
	protected ShoppingItemFormBeanContainerFactory getCartFormBeanFactory() {
		return this.cartFormBeanFactory;
	}

	/**
	 * @param priceLookupFacade {@code PriceLookupFacade}
	 */
	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}

	protected ProductCharacteristicsService getProductCharacteristicsService() {
		return productCharacteristicsService;
	}

	public void setProductCharacteristicsService(final ProductCharacteristicsService productCharacteristicsService) {
		this.productCharacteristicsService = productCharacteristicsService;
	}

	protected MoneyFormatter getMoneyFormatter() {
		return this.moneyFormatter;
	}

	public void setMoneyFormatter(final MoneyFormatter moneyFormatter) {
		this.moneyFormatter = moneyFormatter;
	}
}

