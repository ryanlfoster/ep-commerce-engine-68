package com.elasticpath.sfweb.controller.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.InvalidBundleTreeStructureException;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.catalog.impl.BundleIdentifierImpl;
import com.elasticpath.service.catalogview.ProductViewService;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.sfweb.ajax.bean.impl.JsonBundleItemBeanImpl;
import com.elasticpath.sfweb.ajax.service.JsonBundleFactory;
import com.elasticpath.sfweb.ajax.service.JsonBundleService;
import com.elasticpath.sfweb.controller.ShoppingItemFormBeanContainerFactory;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBeanContainer;
import com.elasticpath.sfweb.formbean.impl.CartFormBeanImpl;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Builds a {@code ShoppingItemFormBean} from a product.
 */
public class ShoppingItemFormBeanContainerFactoryImpl implements ShoppingItemFormBeanContainerFactory {

	private StoreProductService storeProductService;
	private ProductSkuService productSkuService;
	private ProductViewService viewProductService;
	private StoreProductLoadTuner productLoadTuner;
	private ShoppingItemAssembler shoppingItemAssembler;
	private PriceLookupFacade priceLookupFacade;
	private BeanFactory beanFactory;
	private CartDirector cartDirector;
	private JsonBundleFactory jsonBundleFactory;
	private JsonBundleService jsonBundleService;
	private final BundleIdentifier bundleIdentifier;
	private SfRequestHelper requestHelper;

	/**
	 * Constructor.
	 */
	public ShoppingItemFormBeanContainerFactoryImpl() {
		this.bundleIdentifier = new BundleIdentifierImpl();
	}

	/**
	 * Gets store product from the product code.
	 *
	 * @param productCode product code
	 * @param store the store
	 * @return the product
	 */
	protected StoreProduct findProductByGuid(final String productCode, final Store store) {
		return viewProductService.getProduct(productCode, productLoadTuner, store);
	}

	@Override
	public ShoppingItemFormBeanContainer createCartFormBean(final StoreProduct storeProduct, final int quantity, final ShoppingCart shoppingCart) {
		String defaultSkuCode = storeProduct.getDefaultSku().getSkuCode();

		ShoppingItemFormBean rootFormBean =
			createShoppingItemFormBean(shoppingCart.getStore(), storeProduct, defaultSkuCode, quantity, "", 0, 0);

		// by default, the root form bean should always be set to be selected
		rootFormBean.setSelected(true);

		Product product = storeProduct.getWrappedProduct();
		Map<String, PriceAdjustment> priceAdjustmentsForBundle = Collections.emptyMap();
		if (product instanceof ProductBundle) {
			ProductBundle bundle = (ProductBundle) product;
			priceAdjustmentsForBundle = priceLookupFacade.getPriceAdjustmentsForBundle(bundle,
					shoppingCart.getStore().getCatalog().getCode(), shoppingCart.getShopper());

			if (bundle.getSelectionRule() != null) {
				rootFormBean.setSelectionRule(bundle.getSelectionRule().getParameter());
			}
			if (bundle.isCalculated()) {
				setPriceAndQuantityForCalculatedBundle(rootFormBean, product, shoppingCart);
			}
		}

		addConstituentShoppingItemFormBeansFromProduct(shoppingCart, rootFormBean, storeProduct, "", 1, priceAdjustmentsForBundle);

		ShoppingItemFormBeanContainer cartFormBean = new CartFormBeanImpl();
		cartFormBean.addShoppingItemFormBean(rootFormBean);

		rootFormBean.setMinQty(calculateMinQty(shoppingCart, rootFormBean));

		return cartFormBean;
	}
	
	private void setPriceAndQuantityForCalculatedBundle(
	        final ShoppingItemFormBean rootFormBean, final Product product, final ShoppingCart shoppingCart) {
	    rootFormBean.setCalculatedBundle(true);
        Price promotedPriceForSku = this.priceLookupFacade.getPromotedPriceForSku(product.getDefaultSku(), shoppingCart.getStore(),
                shoppingCart.getShopper(), shoppingCart.getAppliedRules());
        if (promotedPriceForSku != null) {
            int firstPriceTierMinQty = promotedPriceForSku.getFirstPriceTierMinQty();
            rootFormBean.setPrice(promotedPriceForSku);
            rootFormBean.setQuantity(firstPriceTierMinQty);
        }
	}

	/**
	 * Creates the shopping item form bean and sets corresponding attributes.
	 * @param store the store
	 * @param storeProduct the store product.
	 * @param skuCode sku code.
	 * @param quantity the quantity.
	 * @param path the path.
	 * @param level the level.
	 * @param shoppingItemUid shopping item uid.
	 *
	 * @return {@link ShoppingItemFormBean}.
	 */
	protected ShoppingItemFormBean createShoppingItemFormBean(final Store store,
			final StoreProduct storeProduct, final String skuCode,
			final int quantity, final String path, final int level,
			final long shoppingItemUid) {
		
		ShoppingItemFormBean formBean = createEmptyShoppingItemFormBean();
		formBean.setProduct(storeProduct);
		formBean.setSkuCode(skuCode);
		formBean.setQuantity(quantity);
		formBean.setPath(path);
		formBean.setLevel(level);
		formBean.setUpdateShoppingItemUid(shoppingItemUid);
		
		if (isBundle(storeProduct)) {
			ProductBundle bundle = (ProductBundle) storeProduct.getWrappedProduct();
			if (bundle.getSelectionRule() != null) {
				formBean.setSelectionRule(bundle.getSelectionRule().getParameter());
			}
			formBean.setCalculatedBundle(bundle.isCalculated());
		}
		return formBean;
	}

	/**
	 * Creates an empty {@link ShoppingItemFormBean}.
	 * @return {@link ShoppingItemFormBean}.
	 */
	protected ShoppingItemFormBean createEmptyShoppingItemFormBean() {
		return getBeanFactory().getBean(ContextIdNames.SHOPPING_ITEM_FORM_BEAN);
	}

	@Override
	public ShoppingItemFormBeanContainer createCartFormBean(final ShoppingItemDto existingShoppingItemDto, final ShoppingCart shoppingCart,
			final boolean dependent) {

		// Re get the bundle definition so that the load tuner gets everything we need (specifically default sku)
		// We get the whole bundle definition because it makes for one database call and provides scope for a future
		// check that the shoppingItem and bundle definition still match.
		final StoreProduct storeProduct = findProductByGuid(existingShoppingItemDto.getProductCode(), shoppingCart.getStore());

		ShoppingItemFormBean rootFormBean = createShoppingItemFormBean(shoppingCart.getStore(), storeProduct,
				existingShoppingItemDto.getSkuCode(), existingShoppingItemDto.getQuantity(), "", 0,
				existingShoppingItemDto.getShoppingItemUidPk());

		rootFormBean.setDependent(dependent);
		rootFormBean.setPrice(existingShoppingItemDto.getPrice());
		rootFormBean.setTotal(existingShoppingItemDto.getTotal());
		rootFormBean.setSelected(existingShoppingItemDto.isSelected());
		rootFormBean.getGiftCertificateFields().initFromShoppingItemDto(existingShoppingItemDto);


		Map<String, PriceAdjustment> adjustments = Collections.emptyMap();
		Product product = storeProduct.getWrappedProduct();

		if (product instanceof ProductBundle) {
			adjustments = priceLookupFacade.getPriceAdjustmentsForBundle((ProductBundle) product,
					shoppingCart.getStore().getCatalog().getCode(), shoppingCart.getShopper());

			if (((ProductBundle) product).isCalculated()) {
				rootFormBean.setCalculatedBundle(true);
				rootFormBean.setPrice(existingShoppingItemDto.getPrice());
			}
		}

		addConstituentShoppingItemFormBeansFromShoppingItemDto(shoppingCart, rootFormBean,
				 existingShoppingItemDto, storeProduct, "", 1, adjustments);

		ShoppingItemFormBeanContainer cartUpdateFormBean = new CartFormBeanImpl();
		cartUpdateFormBean.addShoppingItemFormBean(rootFormBean);

		rootFormBean.setMinQty(calculateMinQty(shoppingCart, rootFormBean));

		return cartUpdateFormBean;
	}

	/**
	 * First finds the minimum tier quantity from all shopping items in the tree.
	 * Then finds the minimum order quantity for the top level shopping item.
	 * Finally, it returns the max of those two numbers.
	 *
	 * @param shoppingCart The shopping cart.
	 * @param rootFormBean The root shopping item.
	 * @return The minimum quantity that can be ordered.
	 */
	protected int calculateMinQty(final ShoppingCart shoppingCart, final ShoppingItemFormBean rootFormBean) {
		int min = Integer.MAX_VALUE;
		if (bundleIdentifier.isCalculatedBundle(rootFormBean.getProduct())) {
			JsonBundleItemBeanImpl jsonBundleBean = (JsonBundleItemBeanImpl) jsonBundleFactory.createJsonBundleFromShoppingItemFormBean(rootFormBean);
			jsonBundleService.updateJsonBundleUsingSession(jsonBundleBean, shoppingCart.getShopper());
			if (jsonBundleBean.getAggregatedPrices().size() > 0) {
				min = jsonBundleBean.getAggregatedPrices().get(0).getMinQty();
			} else {
				min = 1;
			}
		} else {
			Price price;
			if (rootFormBean.getProductSku() == null) {
				price = this.priceLookupFacade.getPromotedPriceForProduct(rootFormBean.getProduct(), shoppingCart.getStore(),
						shoppingCart.getShopper(), shoppingCart.getAppliedRules());
			} else {
				price = this.priceLookupFacade.getPromotedPriceForSku(rootFormBean.getProductSku(), shoppingCart.getStore(),
						shoppingCart.getShopper(), shoppingCart.getAppliedRules());
			}
			if (price == null) {
				return 1;
			}
			Set<Integer> tierQtys = price.getPricingScheme().getPriceTiersMinQuantities();
			for (Integer tierQty : tierQtys) {
				min = Math.min(min, tierQty);
			}
		}
		return Math.max(rootFormBean.getProduct().getMinOrderQty(), min);
	}

	@Override
	public ShoppingItemFormBeanContainer createCartFormBean() {
		return new CartFormBeanImpl();
	}

	/**
	 * Creates {@code ShoppingItemFormBean}s for all constituents and all children of all constituents recursively.
	 * The resulting beans are all added to the {@code rootFormBean}. Each node is visited with NodeFunctor.visit().
	 * @param shoppingCart the shopping cart
	 * @param parentFormBean The form bean which is required for the nodeFunctor.
	 * @param existingShoppingItemDto The dto to read with the sku information.
	 * @param storeProduct The Product which represents this node.
	 * @param parentPath The path to get to the parent of these beans in Spring reference format.
	 * @param level The level in the tree.
	 * @param adjustments map of adjustments on this bundle tree
	 */
	protected void addConstituentShoppingItemFormBeansFromShoppingItemDto(final ShoppingCart shoppingCart,
			final ShoppingItemFormBean parentFormBean, final ShoppingItemDto existingShoppingItemDto,
			final StoreProduct storeProduct, final String parentPath, final int level, final Map<String, PriceAdjustment> adjustments) {

		if (!isBundle(storeProduct)) {
			return;
		}

		ShoppingItemFormBean rootFormBean = getRootFormBean(parentFormBean);
		Store store = shoppingCart.getStore();
		List<ShoppingItemDto> constituentDtos = existingShoppingItemDto.getConstituents();
		ProductBundle bundle = (ProductBundle) storeProduct.getWrappedProduct();
		List<BundleConstituent> bundleConstituents = bundle.getConstituents();

		if (constituentDtos.size() != bundleConstituents.size()) {
			throw new InvalidBundleTreeStructureException("constituent DTOs and bundle constituents have different sizes.");
		}
		
		for (int index = 0; index < constituentDtos.size(); index++) {
			ShoppingItemDto dto = constituentDtos.get(index);
			BundleConstituent bundleConstituent = bundleConstituents.get(index);
			ConstituentItem constituentItem = bundleConstituent.getConstituent();
			StoreProduct constituent = storeProductService.getProductForStore(constituentItem.getProduct(), store);
			Integer quantity = bundleConstituent.getQuantity();
			String path = createPath(parentPath, index);

			ShoppingItemFormBean childFormBean =
				createShoppingItemFormBean(shoppingCart.getStore(), constituent, dto.getSkuCode(), quantity, path, level, 0);
			childFormBean.setParent(parentFormBean);
			populateChildFormBeanFromShoppingItemDto(childFormBean, shoppingCart, adjustments, bundle, dto,
					bundleConstituent.getGuid(), bundleConstituent);
			rootFormBean.addConstituent(childFormBean);
			addConstituentShoppingItemFormBeansFromShoppingItemDto(shoppingCart, childFormBean, dto,
					constituent, path, level + 1, adjustments);
		}
	}

	/**
	 * @param parentFormBean
	 * @return
	 */
	private ShoppingItemFormBean getRootFormBean(final ShoppingItemFormBean parentFormBean) {
		ShoppingItemFormBean rootFormBean = parentFormBean;
		while (rootFormBean.getParent() != null) {
			rootFormBean = rootFormBean.getParent();
		}
		return rootFormBean;
	}


	/**
	 * Populate the child form bean fields :
	 * - fixedSku
	 * - productSku
	 * - selected
	 * - price adjustment
	 *
	 * If it is a calculated bundle call an internal method to set the price.
	 *
	 *
	 * @param childFormBean the child form bean
	 * @param shoppingCart the shopping cart
	 * @param adjustments price adjustments
	 * @param bundle the product bundle
	 * @param dto the shopping item dto
	 * @param bundleConstituentGuid the bundle constituent guid
	 * @param bundleConstituent the constituent
	 */
	protected void populateChildFormBeanFromShoppingItemDto(final ShoppingItemFormBean childFormBean,
			final ShoppingCart shoppingCart,
			final Map<String, PriceAdjustment> adjustments,
			final ProductBundle bundle, final ShoppingItemDto dto,
			final String bundleConstituentGuid,
			final BundleConstituent bundleConstituent) {

		// if the child form bean is null for some bizarre reason just return
		if (childFormBean == null) {
			return;
		}

		final ConstituentItem constituentItem = bundleConstituent.getConstituent();

		// if the constituent item is a product sku, set fixed sku to true and set the product sku
		// of the constituent item, otherwise set fixed sku to false
		if (constituentItem.isProductSku()) {
			childFormBean.setFixedSku(true);
			childFormBean.setProductSku(constituentItem.getProductSku());
		} else if (dto.getSkuCode() != null && dto.getSkuCode().trim().length() > 0) {
			childFormBean.setProductSku(productSkuService.findBySkuCode(dto.getSkuCode()));
			childFormBean.setFixedSku(false);
		} else {
			childFormBean.setFixedSku(false);
		}

		childFormBean.setSelected(dto.isSelected());
		PriceAdjustment childFormBeanAdjustment = adjustments.get(bundleConstituentGuid);
		childFormBean.setPriceAdjustment(childFormBeanAdjustment);
		if (bundle.isCalculated()) {
			setChildFormBeanPriceForCalcBundle(shoppingCart, dto, bundleConstituent, childFormBean);
		}
	}

	/**
	 *
	 * Set the child form bean price for a calculated bundle.
	 *
	 * In the event the product is not a product bundle, and represents an actual product sku,
	 * first attempt to retrieve the price from the DTO, and if not available, call the price lookup facade.
	 *
	 * If the DTO returns null, and the pricelookup facade returns null, the child form bean price will also
	 * be set to null.
	 *
	 *
	 * @param shoppingCart the shopping cart
	 * @param dto the shopping item dto
	 * @param bundleConstituent the constituent
	 * @param childFormBean the child form bean
	 */
	protected void setChildFormBeanPriceForCalcBundle(
			final ShoppingCart shoppingCart, 
			final ShoppingItemDto dto, 
			final BundleConstituent bundleConstituent,
			final ShoppingItemFormBean childFormBean) {

		childFormBean.setCalculatedBundleItem(true);
		Price childPrice = null;
		if (!(bundleConstituent.getConstituent().isBundle())) {
			childPrice = dto.getPrice();
			if (childPrice == null) {
				ProductSku productSku = bundleConstituent.getConstituent().getProductSku();
				childPrice = priceLookupFacade.getPromotedPriceForSku(productSku, shoppingCart.getStore(),
						shoppingCart.getShopper(), shoppingCart.getAppliedRules());
			}
		}
		childFormBean.setPrice(childPrice);
	}

	/**
	 * Creates {@code ShoppingItemFormBean}s for all constituents and all children of all constituents recursively.
	 * The resulting beans are all added to the {@code rootFormBean}. Each node is visited with NodeFunctor.visit().
	 * @param shoppingCart the schopping cart
	 * @param parentFormBean The form bean which is required for the nodeFunctor.
	 * @param storeProduct The Product which represents this node.
	 * @param parentPath The path to get to the parent of these beans in Spring reference format.
	 * @param level The level in the tree.
	 * @param priceAdjustmentsForBundle map of price adjustments for the bundle tree
	 */
	protected void addConstituentShoppingItemFormBeansFromProduct(
			final ShoppingCart shoppingCart,
			final ShoppingItemFormBean parentFormBean,
			final StoreProduct storeProduct, 
			final String parentPath, 
			final int level,
			final Map<String, PriceAdjustment> priceAdjustmentsForBundle) {

		if (!isBundle(storeProduct)) {
			return;
		}
		
		ShoppingItemFormBean rootFormBean = getRootFormBean(parentFormBean);
		Store store = shoppingCart.getStore();
		ProductBundle bundle = ((ProductBundle) storeProduct.getWrappedProduct());
		List<BundleConstituent> constituents = bundle.getConstituents();
		
		int selectionRule = 0;
		if (bundle.getSelectionRule() != null) {
			selectionRule = bundle.getSelectionRule().getParameter();
		}

		int selectedItems = 0;
		for (int index = 0; index < constituents.size(); index++) {
			BundleConstituent bundleConstituent = constituents.get(index);
			ConstituentItem constituentItem = bundleConstituent.getConstituent();
			String path = createPath(parentPath, index);
			StoreProduct constituent = storeProductService.getProductForStore(constituentItem.getProduct(), store);
			String skuCode = constituentItem.getProductSku().getSkuCode();
			Integer quantity = bundleConstituent.getQuantity();
			ShoppingItemFormBean childFormBean = createShoppingItemFormBean(store, constituent, skuCode, quantity, path, level, 0);
			childFormBean.setParent(parentFormBean);
			childFormBean.setFixedSku(constituentItem.isProductSku());
			SelectedItemsBean selectedItemsBean = new SelectedItemsBean(selectedItems, selectionRule);
			selectedItems = populateChildFormBeanFromProduct(
					shoppingCart, 
					parentFormBean, 
					priceAdjustmentsForBundle, 
					bundle,
					selectedItemsBean, 
					bundleConstituent, 
					childFormBean);

			rootFormBean.addConstituent(childFormBean);
			addConstituentShoppingItemFormBeansFromProduct(shoppingCart, childFormBean,	constituent, path, level + 1, priceAdjustmentsForBundle);
		}
	}

	/**
	 * Local convenience class for passing parameters to sub functions.
	 */
	final class SelectedItemsBean {
		private final int selectedItems;
		private final int selectionRule;

		/**
		 *
		 * @param selectedItems the selected items value
		 * @param selectionRule the selection rule value
		 */
		protected SelectedItemsBean(final int selectedItems, final int selectionRule) {
			super();
			this.selectedItems = selectedItems;
			this.selectionRule = selectionRule;
		}

		/**
		 *
		 * @return the selected items
		 */
		public int getSelectedItems() {
			return selectedItems;
		}

		/**
		 *
		 * @return the selection rule
		 */
		public int getSelectionRule() {
			return selectionRule;
		}
	}

	/**
	 *
	 * @param shoppingCart the shopping cart
	 * @param parentFormBean the parent form bean
	 * @param adjustments the price adjustments
	 * @param bundle the product bundle reference
	 * @param selectedItemsBean the selecteditems bean
	 * @param bundleConstituent the bundle constituent item
	 * @param childFormBean the child form bean
	 * @return the updated selection rule
	 */
	protected int populateChildFormBeanFromProduct(
			final ShoppingCart shoppingCart, 
			final ShoppingItemFormBean parentFormBean,
			final Map<String, PriceAdjustment> adjustments, 
			final ProductBundle bundle, 
			final SelectedItemsBean selectedItemsBean,
			final BundleConstituent bundleConstituent, 
			final ShoppingItemFormBean childFormBean) {

		int localSelectedItems = selectedItemsBean.getSelectedItems();
		int selectionRule = selectedItemsBean.getSelectionRule();
		
		ConstituentItem constituentItem = bundleConstituent.getConstituent();
		if (constituentItem.isProductSku()) {
			childFormBean.setProductSku(constituentItem.getProductSku());
		}
		
		String bundleGuid = bundleConstituent.getGuid();
		PriceAdjustment priceAdjustment = adjustments.get(bundleGuid);
		childFormBean.setPriceAdjustment(priceAdjustment);
		if (bundle.isCalculated()) {
			setChildFormBeanPriceForCalcBundle(shoppingCart, bundleConstituent, childFormBean);
		}

		// to select default items
		// selectionRule 0 means all items are selected
		// otherwise only select the specified number of items.
		if (isParentSelected(parentFormBean, childFormBean)	&& (selectionRule == 0 || localSelectedItems < selectionRule)) {
			childFormBean.setSelected(true);
			localSelectedItems++;
		}
		return localSelectedItems;
	}


	/**
	 *
	 * Set the child form bean price for a calculated bundle.
	 *
	 * @param shoppingCart the shopping cart
	 * @param bundleConstituent the constituent item
	 * @param childFormBean the child form bean
	 */
	protected void setChildFormBeanPriceForCalcBundle(
			final ShoppingCart shoppingCart,
			final BundleConstituent bundleConstituent,
			final ShoppingItemFormBean childFormBean) {

		childFormBean.setCalculatedBundleItem(true);

		if (!(bundleConstituent.getConstituent().isBundle())) {
			Price promotedPriceForSku = priceLookupFacade.getPromotedPriceForSku(
					bundleConstituent.getConstituent().getProductSku(),
					shoppingCart.getStore(), 
					shoppingCart.getShopper(), 
					shoppingCart.getAppliedRules());
			childFormBean.setPrice(promotedPriceForSku);

			// set price adjustment according to the effective price list
			if (promotedPriceForSku != null) {
				// If the sku has a recurring schedule then the top level Price doesn't have a priceListGuid.
				// So we get the price from the schedule which is complete.
				Price recurringPrice = (Price) promotedPriceForSku.getPricingScheme().getPriceSchedules().values().iterator().next();

				int quantity = childFormBean.getAccumulatedQuantity();
				PriceTier priceTier = recurringPrice.getPriceTierByQty(quantity);
				if (priceTier != null) {
					String priceListGuid = priceTier.getPriceListGuid();
					PriceAdjustment priceAdjustment = bundleConstituent.getPriceAdjustmentForPriceList(priceListGuid);
					childFormBean.setPriceAdjustment(priceAdjustment);
				}
			}
		}
	}

	/**
	 * @param rootFormBean root shopping item.
	 * @param currentFormBean current shopping item.
	 * @return true if parent shopping item is selected.
	 */
	protected boolean isParentSelected(final ShoppingItemFormBean rootFormBean,	final ShoppingItemFormBean currentFormBean) {
		String path = currentFormBean.getPath();
		String parentPath = "";
		int parentIndex = path.lastIndexOf('.');
		if (parentIndex >= 0) {
			parentPath = path.substring(0, parentIndex);
		}
		ShoppingItemFormBean parentFormBean = rootFormBean;
		for (ShoppingItemFormBean item : rootFormBean.getConstituents()) {
			if (item.getPath().equals(parentPath)) {
				parentFormBean = item;
			}
		}
		return parentFormBean.isSelected();
	}

	/**
	 *
	 * @param storeProduct
	 * @return true if the given storeProduct is a bundle.
	 */
	private boolean isBundle(final StoreProduct storeProduct) {
		return storeProduct.getWrappedProduct() instanceof ProductBundle;
	}

	/**
	 *
	 * @param parentPath
	 * @param childIndex
	 * @return
	 */
	private String createPath(final String parentPath, final int childIndex) {
		String path = parentPath;
		if (!StringUtils.isEmpty(parentPath)) {
			path += ".";
		}

		path += "constituents[" + childIndex + "]";
		return path;
	}

	/**
	 * Sets the product view service.
	 *
	 * @param productService the product view service
	 */
	public void setProductViewService(final ProductViewService productService) {
		this.viewProductService = productService;
	}

	/**
	 * Sets the store product service.
	 *
	 * @param productService the product view service
	 */
	public void setStoreProductService(final StoreProductService productService) {
		this.storeProductService = productService;
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
	 * Adds given list of {@link ShoppingItemFormBean} to the given shopping cart form bean.
	 *
	 * @param cartFormBean the {@link com.elasticpath.sfweb.formbean.ShoppingCartFormBean}
	 * @param shoppingItemFormBeans a list of {@link ShoppingItemFormBean}s
	 */
	void addShoppingItemFormBeans(final ShoppingItemFormBeanContainer cartFormBean, final List<ShoppingItemFormBean> shoppingItemFormBeans) {
		if (shoppingItemFormBeans == null) {
			return;
		}

		for (ShoppingItemFormBean shoppingItemFormBean : shoppingItemFormBeans) {
			cartFormBean.addShoppingItemFormBean(shoppingItemFormBean);
		}
	}

	/**
	 * Gets all shopping items from {@code shoppingCart}, maps them to form beans and adds them to {@code cartFormBean}.
	 *
	 * @param request The cart to copy from.
	 * @param updateShoppingCartFormBean The form bean to copy to.
	 */
	void mapCartItemsToFormBeans(final HttpServletRequest request, final ShoppingItemFormBeanContainer updateShoppingCartFormBean) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShopper().getCurrentShoppingCart();
		final List<ShoppingItem> shoppingItems = shoppingCart.getCartItems();
		final List<Long> invalidItemsInCart = new LinkedList<Long>();
		
		for (ShoppingItem shoppingItem : shoppingItems) {
			try {
				final ShoppingItemDto shoppingItemDto = shoppingItemAssembler.assembleShoppingItemDtoFrom(shoppingItem);
				final ShoppingItemFormBeanContainer aCartFormBean = createCartFormBean(
						shoppingItemDto,
						shoppingCart, cartDirector.isDependent(shoppingItems, shoppingItem));
	
				addShoppingItemFormBeans(updateShoppingCartFormBean, aCartFormBean
						.getCartItems());
			} catch (InvalidBundleTreeStructureException e) {
				invalidItemsInCart.add(shoppingItem.getUidPk());
			}
		}
		
		if (!invalidItemsInCart.isEmpty()) {
			for (Long cartItemUidpk : invalidItemsInCart) {
				shoppingCart.removeCartItem(cartItemUidpk);
			}
			
			final ShoppingCart updatedShoppingCart = cartDirector.saveShoppingCart(shoppingCart);
			customerSession.getShopper().setCurrentShoppingCart(updatedShoppingCart);
			request.setAttribute("invalidItemsWereRemoved", true);
		}

	}

	/**
	 * Setter for {@link ShoppingItemAssembler}.
	 *
	 * @param shoppingItemAssembler {@link ShoppingItemAssembler}.
	 */
	public void setShoppingItemAssembler(final ShoppingItemAssembler shoppingItemAssembler) {
		this.shoppingItemAssembler = shoppingItemAssembler;
	}

	/**
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 *
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the cart director
	 */
	public CartDirector getCartDirector() {
		return cartDirector;
	}

	/**
	 * @param cartDirector the cart director
	 */
	public void setCartDirector(final CartDirector cartDirector) {
		this.cartDirector = cartDirector;
	}

	/**
	 *
	 * @param priceLookupFacade the priceLookupFacade to set
	 */
	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}

	/**
	 *
	 * @param productSkuService The ProductSkuService to set.
	 */
	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

	/**
	 * @return the priceLookupFacade
	 */
	public PriceLookupFacade getPriceLookupFacade() {
		return priceLookupFacade;
	}

	/**
	 * @param jsonBundleFactory The JsonBundleFactory.
	 */
	public void setJsonBundleFactory(final JsonBundleFactory jsonBundleFactory) {
		this.jsonBundleFactory = jsonBundleFactory;
	}

	/**
	 * @return The JsonBundleFactory.
	 */
	public JsonBundleFactory getJsonBundleFactory() {
		return jsonBundleFactory;
	}

	/**
	 * @param jsonBundleService The JsonBundleService.
	 */
	public void setJsonBundleService(final JsonBundleService jsonBundleService) {
		this.jsonBundleService = jsonBundleService;
	}

	/**
	 * @return The JsonBundleService.
	 */
	public JsonBundleService getJsonBundleService() {
		return jsonBundleService;
	}

	/** Sets the requestHelper instance.
	 *
	 * @param requestHelper -the requesthelper instance.
	 */
	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}
	/**
	 * Gets the requestHelper instance.
	 *
	 * @return - the current requestHelper instance.
	 */
	public SfRequestHelper getRequestHelper() {
		return this.requestHelper;
	}
}
