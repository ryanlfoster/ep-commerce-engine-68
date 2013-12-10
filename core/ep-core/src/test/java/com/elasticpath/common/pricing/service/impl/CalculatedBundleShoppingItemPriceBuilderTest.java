/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.common.pricing.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceScheduleImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.PricingSchemeImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.CustomerSessionMemento;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionMementoImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.ShopperMemento;
import com.elasticpath.domain.shopper.impl.ShopperImpl;
import com.elasticpath.domain.shopper.impl.ShopperMementoImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.impl.BundleIdentifierImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test functionality of the {@link CalculatedBundleShoppingItemPriceBuilder}.
 */
public class CalculatedBundleShoppingItemPriceBuilderTest {

	private static final BigDecimal TEN = new BigDecimal("10.00");

	private static final String PRODUCT_CODE = "PRODUCT_CODE";

	private static final String CONSTITUENT_SKU_CODE = "CONSTITUENT_SKU_CODE";

	private static final String SIMPLE_SKU_CODE = "SIMPLE_SKU_CODE";

	private static final String ROOT_SKU_CODE = "SKU_CODE";

	private static final Currency CAD = Currency.getInstance("CAD");

	private static final String SIMPLE_SKU_CODE_WITH_PRICE = "SIMPLE_SKU_CODE_WITH_PRICE";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory expectationsFactory;

	private BundleIdentifier bundleIdentifier;

	private PriceLookupFacade priceLookupFacade;

	private CalculatedBundleShoppingItemPriceBuilder calculatedBundleShoppingItemPriceBuilder;

	private Store store;

	private Set<Long> ruleTracker;

	private CustomerSession customerSession;

	private Shopper shopper;

	/**
	 * Set up test environment.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		bundleIdentifier = new BundleIdentifierImpl();
		priceLookupFacade = context.mock(PriceLookupFacade.class);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SCHEME, PricingSchemeImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_SCHEDULE, PriceScheduleImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_TIER, PriceTierImpl.class);

		calculatedBundleShoppingItemPriceBuilder = new CalculatedBundleShoppingItemPriceBuilder(priceLookupFacade, beanFactory);
		calculatedBundleShoppingItemPriceBuilder.setBundleIdentifier(bundleIdentifier);

		store = new StoreImpl();
		ruleTracker = new HashSet<Long>();

		customerSession = createCustomerSession();
		shopper = createShopper(customerSession);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Ensure calculatedBundleShoppingItemPriceBuilder#build works only on calculated bundles.
	 */
	@Test(expected = EpSystemException.class)
	public void ensureBuildWorksOnlyOnCalculatedBundles() {
		final Product product = createProductBundleAsCalculated(false);
		ProductSku productSku = createProductSku(product, ROOT_SKU_CODE);
		ShoppingItem bundleShoppingItem = createShoppingItemWithProductSkuAndQuantity(productSku, 1);

		calculatedBundleShoppingItemPriceBuilder.build(bundleShoppingItem, null, null, null);
	}

	/**
	 * Ensure simple calculated bundle builds price.
	 */
	@Test
	public void ensureSimpleCalculatedBundleBuildsPrice() {
		final Product simpleProduct = createProduct(PRODUCT_CODE);
		final ProductSku simpleProductSku = createProductSku(simpleProduct, SIMPLE_SKU_CODE);

		final ProductBundle rootBundle = createProductBundleAsCalculated(true);
		ProductSku rootBundleProductSku = createProductSku(rootBundle, ROOT_SKU_CODE);

		addProductSkuConstituentWithQuantityToBundle(simpleProductSku, 1, rootBundle);

		ShoppingItem simpleProductShoppingItem = createShoppingItemWithProductSkuAndQuantity(simpleProductSku, 1);
		ShoppingItem rootBundleShoppingItem = createShoppingItemWithProductSkuAndQuantity(rootBundleProductSku, 1);

		List<ShoppingItem> rootBundleItems = createBundleItems(simpleProductShoppingItem);

		rootBundleShoppingItem.setBundleItems(rootBundleItems);

		final Price expectedPrice = createPrice();

		shouldGetPromotedPriceForSku(simpleProductSku, expectedPrice);
		shouldGetPromotedPriceForSku(simpleProductSku, expectedPrice);

		Price price = calculatedBundleShoppingItemPriceBuilder.build(rootBundleShoppingItem, shopper, store, ruleTracker);
		assertNotNull("A price should be returned.", price);
		assertEquals("Lowest price should be ten.", TEN, price.getLowestPrice().getAmount());
	}

	/**
	 * Ensure simple calculated bundle without price builds returning null.
	 */
	@Test
	public void ensureSimpleCalculatedBundleWithoutPriceBuildsReturningNull() {
		final Product simpleProduct = createProduct(PRODUCT_CODE);
		final ProductSku simpleProductSku = createProductSku(simpleProduct, SIMPLE_SKU_CODE);

		final ProductBundle rootBundle = createProductBundleAsCalculated(true);
		ProductSku rootBundleProductSku = createProductSku(rootBundle, ROOT_SKU_CODE);

		addProductSkuConstituentWithQuantityToBundle(simpleProductSku, 1, rootBundle);

		ShoppingItem simpleProductShoppingItem = createShoppingItemWithProductSkuAndQuantity(simpleProductSku, 1);
		ShoppingItem rootBundleShoppingItem = createShoppingItemWithProductSkuAndQuantity(rootBundleProductSku, 1);

		List<ShoppingItem> rootBundleItems = createBundleItems(simpleProductShoppingItem);

		rootBundleShoppingItem.setBundleItems(rootBundleItems);

		shouldGetPromotedPriceForSku(simpleProductSku, null);

		Price price = calculatedBundleShoppingItemPriceBuilder.build(rootBundleShoppingItem, shopper, store, ruleTracker);
		assertNull("Null should be returned when no pricing exists on any item in the bundle.", price);
	}

	/**
	 * Ensure simple calculated bundle with one item without a price builds and returns null.
	 */
	@Test
	public void ensureSimpleCalculatedBundleWithSingleItemWithoutPriceBuildsAndReturnsNull() {
		final Product simpleProduct = createProduct(PRODUCT_CODE);
		final ProductSku simpleProductSkuWithoutPrice = createProductSku(simpleProduct, SIMPLE_SKU_CODE);
		final ProductSku simpleProductSkuWithPrice = createProductSku(simpleProduct, SIMPLE_SKU_CODE_WITH_PRICE);

		final ProductBundle rootBundle = createProductBundleAsCalculated(true);
		ProductSku rootBundleProductSku = createProductSku(rootBundle, ROOT_SKU_CODE);

		addProductSkuConstituentWithQuantityToBundle(simpleProductSkuWithoutPrice, 1, rootBundle);
		addProductSkuConstituentWithQuantityToBundle(simpleProductSkuWithPrice, 1, rootBundle);

		ShoppingItem simpleProductShoppingItemWithoutPrice = createShoppingItemWithProductSkuAndQuantity(simpleProductSkuWithoutPrice, 1);
		ShoppingItem simpleProductShoppingItemWithPrice = createShoppingItemWithProductSkuAndQuantity(simpleProductSkuWithPrice, 1);
		ShoppingItem rootBundleShoppingItem = createShoppingItemWithProductSkuAndQuantity(rootBundleProductSku, 1);

		List<ShoppingItem> rootBundleItems = createBundleItems(simpleProductShoppingItemWithPrice, simpleProductShoppingItemWithoutPrice);

		rootBundleShoppingItem.setBundleItems(rootBundleItems);

		shouldGetPromotedPriceForSku(simpleProductSkuWithoutPrice, null);
		shouldGetPromotedPriceForSku(simpleProductSkuWithPrice, createPrice());

		Price price = calculatedBundleShoppingItemPriceBuilder.build(rootBundleShoppingItem, shopper, store, ruleTracker);
		assertNull("Null should be returned when no pricing exists on any item in the bundle.", price);
	}


	/**
	 * Ensure bundle with nested calculated bundle item builds price.
	 */
	@Test
	public void ensureBundleWithNestedCalculatedBundleItemBuildsPrice() {
		final Product simpleProduct = createProduct(PRODUCT_CODE);
		final ProductSku simpleProductSku = createProductSku(simpleProduct, SIMPLE_SKU_CODE);

		final ProductBundle childBundle = createProductBundleAsCalculated(true);
		ProductSku childProductSku = createProductSku(childBundle, CONSTITUENT_SKU_CODE);

		addProductSkuConstituentWithQuantityToBundle(simpleProductSku, 1, childBundle);

		final ProductBundle rootBundle = createProductBundleAsCalculated(true);
		ProductSku rootBundleProductSku = createProductSku(rootBundle, ROOT_SKU_CODE);

		addProductConstituentWithQuantityToBundle(childBundle, 1, rootBundle);


		ShoppingItem simpleProductShoppingItem = createShoppingItemWithProductSkuAndQuantity(simpleProductSku, 1);
		ShoppingItem childCalculatedBundleItem = createShoppingItemWithProductSkuAndQuantity(childProductSku, 1);
		ShoppingItem rootBundleShoppingItem = createShoppingItemWithProductSkuAndQuantity(rootBundleProductSku, 1);

		List<ShoppingItem> childBundleItems = createBundleItems(simpleProductShoppingItem);
		List<ShoppingItem> rootBundleItems = createBundleItems(childCalculatedBundleItem);

		childCalculatedBundleItem.setBundleItems(childBundleItems);
		rootBundleShoppingItem.setBundleItems(rootBundleItems);

		final Price expectedPrice = createPrice();

		shouldGetPromotedPriceForSku(simpleProductSku, expectedPrice);
		shouldGetPromotedPriceForSku(simpleProductSku, expectedPrice);

		Price price = calculatedBundleShoppingItemPriceBuilder.build(rootBundleShoppingItem, shopper, store, ruleTracker);
		assertNotNull("A price should be returned.", price);
		assertEquals("Lowest price should be ten.", TEN, price.getLowestPrice().getAmount());
	}

	/**
	 * Ensure bundle with nested calculated bundle item without price builds returning null.
	 */
	@Test
	public void ensureBundleWithNestedCalculatedBundleItemWithoutPriceBuildsReturningNull() {
		final Product simpleProduct = createProduct(PRODUCT_CODE);
		final ProductSku simpleProductSku = createProductSku(simpleProduct, SIMPLE_SKU_CODE);

		final ProductBundle childBundle = createProductBundleAsCalculated(true);
		ProductSku childBundleProductSku = createProductSku(childBundle, CONSTITUENT_SKU_CODE);

		addProductSkuConstituentWithQuantityToBundle(simpleProductSku, 1, childBundle);

		final ProductBundle rootBundle = createProductBundleAsCalculated(true);
		ProductSku rootProductSku = createProductSku(rootBundle, ROOT_SKU_CODE);

		addProductConstituentWithQuantityToBundle(childBundle, 1, rootBundle);


		ShoppingItem simpleProductShoppingItem = createShoppingItemWithProductSkuAndQuantity(simpleProductSku, 1);
		ShoppingItem childBundleItem = createShoppingItemWithProductSkuAndQuantity(childBundleProductSku, 1);
		ShoppingItem rootBundleShoppingItem = createShoppingItemWithProductSkuAndQuantity(rootProductSku, 1);

		List<ShoppingItem> childBundleItems = createBundleItems(simpleProductShoppingItem);
		List<ShoppingItem> bundleItems = createBundleItems(childBundleItem);

		childBundleItem.setBundleItems(childBundleItems);
		rootBundleShoppingItem.setBundleItems(bundleItems);

		shouldGetPromotedPriceForSku(simpleProductSku, null);
		shouldGetPromotedPriceForSku(childBundleProductSku, null);

		Price price = calculatedBundleShoppingItemPriceBuilder.build(rootBundleShoppingItem, shopper, store, ruleTracker);
		assertNull("Null should be returned when no pricing exists on any item in the bundle.", price);
	}

	private void shouldGetPromotedPriceForSku(final ProductSku productSku, final Price price) {
		context.checking(new Expectations() {
			{
				oneOf(priceLookupFacade).getPromotedPriceForSku(productSku, store, shopper, ruleTracker);
				will(returnValue(price));
			}
		});
	}

	private Price createPrice() {
		final Price expectedPrice = new PriceImpl();
		expectedPrice.setPricingScheme(createPricingScheme());
		expectedPrice.setPersistentPriceTiers(createPriceTiers());
		return expectedPrice;
	}

	private PricingScheme createPricingScheme() {
		PricingScheme pricingScheme = new PricingSchemeImpl();
		pricingScheme.setPriceForSchedule(createPriceSchedule(PriceScheduleType.PURCHASE_TIME), createSimplePrice());
		return pricingScheme;
	}

	private Map<Integer, PriceTier> createPriceTiers() {
		Map<Integer, PriceTier> priceTiers = new HashMap<Integer, PriceTier>();
		PriceTier priceTier = createPriceTier("PRICE_LIST_GUID", BigDecimal.TEN);
		priceTiers.put(1, priceTier);
		return priceTiers;
	}

	private PriceSchedule createPriceSchedule(final PriceScheduleType priceScheduleType) {
		PriceSchedule schedule = new PriceScheduleImpl();
		schedule.setType(priceScheduleType);
		return schedule;
	}

	private Price createSimplePrice() {
		Price simplePrice = new PriceImpl();
		simplePrice.setPersistentPriceTiers(createPriceTiers());
		simplePrice.setCurrency(CAD);
		return simplePrice;
	}

	private PriceTier createPriceTier(final String priceListGuid, final BigDecimal listPrice) {
		PriceTier priceTier = new PriceTierImpl();
		priceTier.setPriceListGuid(priceListGuid);
		priceTier.setListPrice(listPrice);
		return priceTier;
	}

	private List<ShoppingItem> createBundleItems(final ShoppingItem... items) {
		return Arrays.asList(items);
	}

	private Shopper createShopper(final CustomerSession customerSession) {
		ShopperMemento shopperMemento = new ShopperMementoImpl();
		Shopper shopper = new ShopperImpl();
		shopper.setShopperMemento(shopperMemento);
		shopper.setGuid("SHOPPER_GUID");
		shopper.updateTransientDataWith(customerSession);
		return shopper;
	}

	private CustomerSession createCustomerSession() {
		CustomerSessionMemento customerSessionMemento = new CustomerSessionMementoImpl();
		CustomerSession customerSession = new CustomerSessionImpl();
		customerSession.setCustomerSessionMemento(customerSessionMemento);
		customerSession.setCurrency(CAD);
		return customerSession;
	}

	private void addProductSkuConstituentWithQuantityToBundle(final ProductSku productSku, final int quantity, final ProductBundle bundle) {
		BundleConstituentImpl bundleConstituent = new BundleConstituentImpl();
		bundleConstituent.setSkuConstituent(productSku);
		bundleConstituent.setQuantity(quantity);
		bundle.addConstituent(bundleConstituent);
	}

	private void addProductConstituentWithQuantityToBundle(final Product product, final int quantity, final ProductBundle bundle) {
		BundleConstituentImpl bundleConstituent = new BundleConstituentImpl();
		bundleConstituent.setProductConstituent(product);
		bundleConstituent.setQuantity(quantity);
		bundle.addConstituent(bundleConstituent);
	}

	private ProductBundle createProductBundleAsCalculated(final boolean calculated) {
		final ProductBundle product = new ProductBundleImpl();
		product.setCalculated(calculated);
		return product;
	}

	private Product createProduct(final String productCode) {
		final Product product = new ProductImpl();
		product.setCode(productCode);
		return product;
	}

	private ProductSku createProductSku(final Product product, final String skuCode) {
		ProductSku productSku = new ProductSkuImpl();
		productSku.setProduct(product);
		productSku.setSkuCode(skuCode);
		return productSku;
	}

	private ShoppingItem createShoppingItemWithProductSkuAndQuantity(final ProductSku productSku, final int quantity) {
		ShoppingItemImpl shoppingItem = new ShoppingItemImpl();
		shoppingItem.setProductSku(productSku);
		shoppingItem.setQuantity(quantity);
		return shoppingItem;
	}

}
