package com.elasticpath.sellingchannel.impl;

import static org.hamcrest.Matchers.anyOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.misc.MoneyFormatter;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ViewHistory;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.catalog.ProductCharacteristicsService;
import com.elasticpath.service.catalogview.ProductViewService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.sfweb.EpSfWebException;
import com.elasticpath.sfweb.controller.ShoppingItemFormBeanContainerFactory;
import com.elasticpath.sfweb.viewbean.ProductViewBean;

/**
 * Tests the {@code TemplateModelFactoryImpl}.
 *
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.TooManyStaticImports", "unchecked", "deprecation" })
public class TemplateModelFactoryImplTest {

	private static final String VIEW_CART = "viewCart";
	private static final String PRODUCT_TEMPLATE = "product";
	private static final String PRODUCT_ID_PRODUCT_B = "ProductB";
	private static final String PRICE = "price";
	private static final String WARRANTIES = "warranties";
	private static final String WAREHOUSE = "warehouse";
	private static final String CATALOG = "catalog";
	private static final String PRODUCT_VIEW_BEAN = "productViewBean";
	private static final int EXPECTED_MODEL_MAP_SIZE = 8;
	private static final String PRODUCT_ID_PRODUCT_A = "productA";
	private static final Currency CURRENCY = Currency.getInstance("CAD");
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final TemplateModelFactoryImpl modelFactory = new TemplateModelFactoryImpl();
	private final StoreProductLoadTuner productLoadTuner = context.mock(StoreProductLoadTuner.class);
	private final ShoppingCartService shoppingCartService = context.mock(ShoppingCartService.class);
	private final ProductViewService productViewService = context.mock(ProductViewService.class);
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
	private final Warehouse warehouse = context.mock(Warehouse.class);
	
	private final StoreProduct product = context.mock(StoreProduct.class);
	private final ViewHistory viewHistory = context.mock(ViewHistory.class);
	private final ProductViewBean productViewBean = context.mock(ProductViewBean.class);
	private final Store store = context.mock(Store.class);
	private final Catalog catalog = context.mock(Catalog.class);
	private final Category category = context.mock(Category.class);
	private final CatalogViewResultHistory catalogViewResultHistory = context.mock(CatalogViewResultHistory.class);
	private final CatalogViewResult catalogViewResult = context.mock(CatalogViewResult.class);
	private final ProductSku sku = context.mock(ProductSku.class);
	private final Set<ProductAssociation> warranties = context.mock(Set.class);
	private final ShoppingItem cartItem = context.mock(ShoppingItem.class);
	private final ProductType productType = context.mock(ProductType.class);
	private final PriceLookupFacade priceLookupFacade = context.mock(PriceLookupFacade.class);
	private final ShoppingItemFormBeanContainerFactory cartUpdateFormBeanFactory = context.mock(ShoppingItemFormBeanContainerFactory.class);
	private final ProductCharacteristicsService productCharacteristicsService = context.mock(ProductCharacteristicsService.class);
	private final MoneyFormatter moneyFormatter = context.mock(MoneyFormatter.class);

	/**
	 * Common test setup. Configures the model factory.
	 */
	@Before
	public void setUp() {
		modelFactory.setStoreProductLoadTuner(productLoadTuner);
		modelFactory.setShoppingCartService(shoppingCartService);
		modelFactory.setProductViewService(productViewService);
		modelFactory.setBeanFactory(beanFactory);
		modelFactory.setPriceLookupFacade(priceLookupFacade);
		modelFactory.setCartFormBeanFactory(cartUpdateFormBeanFactory);
		modelFactory.setProductCharacteristicsService(productCharacteristicsService);
		modelFactory.setMoneyFormatter(moneyFormatter);
	}
	
	/**
	 * Test happy path.
	 */
	@Test
	public void testHappyPath() {
		
		addCommonExpectations();
		
		String updatePage = "";
		Long cartItemId = new Long(0);
		
		context.checking(new Expectations() { {
			oneOf(shoppingCartService).saveIfNotPersisted(shoppingCart); will(returnValue(shoppingCart));
		} });
		
		Map<String, Object> modelMap = modelFactory.createModel(shoppingCart, updatePage, cartItemId, warehouse, catalog, product);
		
		assertMapCorrect(warehouse, modelMap);
	}

	private void assertMapCorrect(final Warehouse warehouse,
			final Map<String, Object> modelMap) {
		assertEquals("ProductViewBean in model map should match the mock we provided.", productViewBean, modelMap.get(PRODUCT_VIEW_BEAN));
		assertEquals("Catalog in model map should match the mock we provided.", catalog, modelMap.get(CATALOG));
		assertEquals("Warehouse in model map should match the mock we provided.", warehouse, modelMap.get(WAREHOUSE));
		assertEquals("Warranties in model map should match the mock we provided.", warranties, modelMap.get(WARRANTIES));
		assertNotNull("A price object should exist in the model map.", modelMap.get(PRICE));
		assertEquals("There should be " + EXPECTED_MODEL_MAP_SIZE + " objects in the model map.", EXPECTED_MODEL_MAP_SIZE, modelMap.size());

	}

	private void addCommonExpectations() {
		
		context.checking(new Expectations() { {
			oneOf(shoppingCart).getViewHistory(); will(returnValue(viewHistory));
			oneOf(viewHistory).addProduct(product);
			oneOf(beanFactory).getBean(PRODUCT_VIEW_BEAN); will(returnValue(productViewBean));
			oneOf(productViewBean).setProduct(product);
			allowing(shoppingCart).getStore(); will(returnValue(store));
			allowing(shoppingCart).getAppliedRules(); will(returnValue(new HashSet <Long>()));
			oneOf(store).getCatalog(); will(returnValue(catalog));
			oneOf(product).getDefaultCategory(catalog); will(returnValue(category));
			allowing(product).getCode(); will(returnValue(PRODUCT_ID_PRODUCT_A));
			allowing(product).getProductType(); will(returnValue(productType));
			allowing(product).getProductAssociations(); will(returnValue(new HashSet<ProductAssociation>()));
			oneOf(productViewBean).setProductCategory(category);
			oneOf(shoppingCart).getCatalogViewResultHistory(); will(returnValue(catalogViewResultHistory));
			oneOf(catalogViewResultHistory).getLastResult(); will(returnValue(catalogViewResult));
			oneOf(productViewBean).setCurrentCatalogViewResult(catalogViewResult);
			allowing(shoppingCart).getCurrency(); will(returnValue(CURRENCY));
			allowing(shoppingCart).getShopper(); will(returnValue(null));
			atLeast(1).of(product).getDefaultSku(); will(returnValue(sku));
			oneOf(priceLookupFacade).getPromotedPriceForSku(
					with(any(ProductSku.class)), with(any(Store.class)), with(anyOf(aNull(Shopper.class), any(Shopper.class))), with(any(Set.class)));
			allowing(warehouse).getUidPk(); will(returnValue(1L));
			allowing(productType).getTemplate(); will(returnValue(PRODUCT_TEMPLATE));
			allowing(priceLookupFacade).getPromotedPricesForProducts(
				with(any(Collection.class)), 
				with(any(Store.class)), 
				with(anyOf(aNull(Shopper.class), any(Shopper.class))),
				with(any(Set.class)));
			oneOf(product).getAssociationsByType(ProductAssociation.WARRANTY); will(returnValue(warranties));
			allowing(shoppingCart).getLocale(); will(returnValue(Locale.getDefault()));
			allowing(productCharacteristicsService).getProductCharacteristicsMap(with(any(Collection.class)));
				will(returnValue(Collections.emptyMap()));
			allowing(moneyFormatter).formatCurrencySymbol(CURRENCY);
		} });
	}
	
	/**
	 * Test including an update page to go to billing and review.
	 */
	@Test
	public void testWithUpdatePage() {
		addCommonExpectations();

				
		context.checking(new Expectations() { {
			
			// Expectations new for this test
			oneOf(shoppingCartService).saveIfNotPersisted(shoppingCart); will(returnValue(shoppingCart));
			oneOf(shoppingCart).getCartItemById(0); will(returnValue(cartItem));
			oneOf(productViewBean).setUpdatePage("billingAndReview");
			oneOf(cartItem).getProductSku(); will(returnValue(sku));
			oneOf(product).setDefaultSku(sku);
			oneOf(cartItem).getUidPk(); will(returnValue(1L));
			oneOf(productViewBean).setUpdateCartItemUid(1);
			oneOf(cartItem).getQuantity(); will(returnValue(2));
			oneOf(productViewBean).setUpdateCartItemQty(2);
			
			allowing(productType).getTemplate(); will(returnValue(PRODUCT_TEMPLATE));
		} });
		
		String updatePage = "billingAndReview";
		Long cartItemId = new Long(0);
		
		Map<String, Object> modelMap = modelFactory.createModel(shoppingCart, updatePage, cartItemId, warehouse, catalog, product);
		
		assertMapCorrect(warehouse, modelMap);
	}
	
	/**
	 * Test including an update page that's not billing and review.
	 */
	@Test
	public void testInvalidUpdate() {
		
		addCommonExpectations();
		context.checking(new Expectations() { {
			oneOf(shoppingCartService).saveIfNotPersisted(shoppingCart); will(returnValue(shoppingCart));
		} });
		
		String updatePage = "anyOtherPage";
		Long cartItemId = new Long(0);
		
		Map<String, Object> modelMap = modelFactory.createModel(shoppingCart, updatePage, cartItemId, warehouse, catalog, product);
		
		assertMapCorrect(warehouse, modelMap);
	}
	
	/**
	 * Test including an update page to view cart.
	 */
	@Test
	public void testWithUpdatePageViewCart() {
		
		addCommonExpectations();
				
		context.checking(new Expectations() { {
			
			// Expectations new for this test
			oneOf(shoppingCartService).saveIfNotPersisted(shoppingCart); will(returnValue(shoppingCart));
			oneOf(shoppingCart).getCartItemById(0); will(returnValue(cartItem));
			oneOf(productViewBean).setUpdatePage(VIEW_CART);
			oneOf(cartItem).getProductSku(); will(returnValue(sku));
			oneOf(product).setDefaultSku(sku);
			oneOf(cartItem).getUidPk(); will(returnValue(1L));
			oneOf(productViewBean).setUpdateCartItemUid(1);
			oneOf(cartItem).getQuantity(); will(returnValue(2));
			oneOf(productViewBean).setUpdateCartItemQty(2);
		} });
		
		String updatePage = VIEW_CART;
		Long cartItemId = new Long(0);
		
		Map<String, Object> modelMap = modelFactory.createModel(shoppingCart, updatePage, cartItemId, warehouse, catalog, product);
		
		assertMapCorrect(warehouse, modelMap);
	}
	
	
	/**
	 * Test shoppingCart not being persistent.
	 */
	@Test
	public void testShoppingCartNotPersistent() {
		
		addCommonExpectations();
		
		context.checking(new Expectations() { {
			oneOf(shoppingCartService).saveIfNotPersisted(shoppingCart); will(returnValue(shoppingCart));
		} });

		String updatePage = "";
		Long cartItemId = new Long(0);
		
		Map<String, Object> modelMap = modelFactory.createModel(shoppingCart, updatePage, cartItemId, warehouse, catalog, product);
		
		assertMapCorrect(warehouse, modelMap);
	}

	
	/**
	 * Test product not found.
	 */
	@Test
	public void testNoProductFound() {
		boolean exceptionCaught = false;
		try {
			modelFactory.getProduct(null, null);
		} catch (EpSfWebException e) {
			exceptionCaught = true;
			assertEquals("Product id is not given.", e.getMessage());
		}
		assertTrue(exceptionCaught);
	}

	/**
	 * testNullProductIsNotShown.
	 */
	@Test
	public void testNullProductIsNotShown() {
		assertFalse(modelFactory.showInStorefront(null));
	}

	/**
	 * testNotDisplayableProductIsNotShown.
	 */
	@Test
	public void testNotDisplayableProductIsNotShown() {
		context.checking(new Expectations() { {
			oneOf(product).isDisplayable(); will(returnValue(false));
		} });
		assertFalse(modelFactory.showInStorefront(product));
	}
	
	/**
	 * testDisplayableProductShown.
	 */
	@Test
	public void testDisplayableProductShown() {
		context.checking(new Expectations() { {
			oneOf(product).isDisplayable(); will(returnValue(true));
		} });
		assertTrue(modelFactory.showInStorefront(product));
	}
	
	/**
	 * Test product not displayable.
	 */
	@Test
	public void testProductNotDisplayable() {
		
		context.checking(new Expectations() { {
			oneOf(productViewService).getProduct(PRODUCT_ID_PRODUCT_B, productLoadTuner, (ShoppingCart) null); will(returnValue(null));
		} });
		
		boolean exceptionCaught = false;
		try {
			modelFactory.getProduct(PRODUCT_ID_PRODUCT_B, null);
		} catch (EpSfWebException e) {
			exceptionCaught = true;
			assertEquals("Product is not available, product id:ProductB", e.getMessage());
		}
		assertTrue(exceptionCaught);
	}
	
	/**
	 * Tests that getTemplateName returns the template name based on the returned product.
	 */
	@Test
	public void testGetTemplateName() {
		TemplateModelFactoryImpl factory = new TemplateModelFactoryImpl();
		factory.setProductViewService(productViewService);
		
		context.checking(new Expectations() { {
			oneOf(productViewService).getProduct(PRODUCT_ID_PRODUCT_A, null, (ShoppingCart) null); will(returnValue(product));
			allowing(product).isDisplayable(); will(returnValue(true));
			allowing(product).getTemplate(); will(returnValue("giftCertificate"));
		} });
		
		assertEquals("getTemplateName should return templateName from product",
				"giftCertificate", factory.getTemplateName(
						PRODUCT_ID_PRODUCT_A, null));
	}
	
	/**
	 * Test that we can create the model if the price is null.
	 */
	@Test
	public void testNullPrice() {
		
		// For this test we don't care about the view bean, cart history, product association prices or association characteristics.
		final TemplateModelFactoryImpl modelFactory = new TemplateModelFactoryImpl() {

			@Override
			protected void addProductToShoppingCartViewHistory(final ShoppingCart shoppingCart, final StoreProduct product) {
				// Do nothing
			}

			@Override
			protected ProductViewBean createProductViewBean(final StoreProduct currentProduct, final ShoppingCart shoppingCart) {
				return productViewBean;
			}
			
			@Override
			protected Map<String, Price> getPrices(final ShoppingCart cart, final Currency currency, final Catalog catalog, 
					final List<Product> products) {
				return Collections.emptyMap();
			}
			
		};
		modelFactory.setPriceLookupFacade(priceLookupFacade);
		modelFactory.setProductCharacteristicsService(productCharacteristicsService);
		modelFactory.setShoppingCartService(shoppingCartService);
		modelFactory.setMoneyFormatter(moneyFormatter);

		final Set<Long> appliedRules = new HashSet<Long>();
		context.checking(new Expectations() { {
			allowing(shoppingCart).getStore(); will(returnValue(store));

			oneOf(shoppingCartService).saveIfNotPersisted(shoppingCart); will(returnValue(shoppingCart));

			allowing(product).getDefaultSku(); will(returnValue(sku));
			allowing(shoppingCart).getShopper(); will(returnValue(null));
			allowing(shoppingCart).getAppliedRules(); will(returnValue(appliedRules));
			allowing(product).getAssociationsByType(ProductAssociation.WARRANTY); will(returnValue(warranties));
			allowing(shoppingCart).getCurrency(); will(returnValue(CURRENCY));
			allowing(product).getProductAssociations(); will(returnValue(new HashSet<ProductAssociation>()));
			
			oneOf(priceLookupFacade).getPromotedPriceForSku(sku, store, null, appliedRules); will(returnValue(null));
			allowing(shoppingCart).getLocale(); will(returnValue(Locale.getDefault()));
			
			allowing(productCharacteristicsService).getProductCharacteristicsMap(with(any(Collection.class)));
			will(returnValue(Collections.emptyMap()));

			allowing(moneyFormatter).formatCurrencySymbol(CURRENCY);
		} });
		
		String updatePage = "";
		Long cartItemId = new Long(0);
		Map<String, Object> modelMap = modelFactory.createModel(shoppingCart, updatePage, cartItemId, warehouse, catalog, product);
		assertNull("The price on the model map should be null", modelMap.get(PRICE));
		
	}
	
}
