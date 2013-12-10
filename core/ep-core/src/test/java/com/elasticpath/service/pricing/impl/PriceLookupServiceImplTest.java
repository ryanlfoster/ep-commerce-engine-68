package com.elasticpath.service.pricing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elasticpath.test.util.MatcherFactory.listContaining;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.SimplePrice;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceScheduleImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.PricingSchemeImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.domain.pricing.impl.PriceAdjustmentImpl;
import com.elasticpath.domain.pricing.impl.PriceListStackImpl;
import com.elasticpath.service.catalog.impl.BundleIdentifierImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PaymentScheduleHelper;
import com.elasticpath.service.pricing.PriceAdjustmentService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test the Price lookup service.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class PriceLookupServiceImplTest {
	private static final String REC_LIST_PRICE = "3.49";
	private static final String SKU_GUID0 = "SKU-GUID0";
	private static final Currency USD = Currency.getInstance("USD");
	private static final String INVALID_PL_GUID = "invalidPlGuid";
	private static final String GUID = "GUID";
	private static final String SKU = "SKU";

	private static final String ANY = "any";
	private static final String ANY1 = "any1";
	private static final String ANY2 = "any2";

	private static final String PRODUCT_GUID = "PRODUCT-GUID";
	private static final String SKU_GUID1 = "SKU-GUID1";
	private static final String PL_GUID = "PLR";



	private static final int TWELVE = 12;
	private static final int THIRTEEN = 13;

	private final PriceLookupServiceImpl service = new PriceLookupServiceImpl();
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private final PriceAdjustmentService priceAdjustmentService = context.mock(PriceAdjustmentService.class);
	private BeanFactoryExpectationsFactory expectationsFactory;
	private final BaseAmountService baseAmountService = context.mock(BaseAmountService.class);
	private PricePopulatorImpl pricePopulator;
	private BaseAmountFinderImpl baseAmountFinder;
	private final PricedEntityFactoryImpl pricedEntityFactory = new PricedEntityFactoryImpl();
	private PaymentScheduleHelper paymentScheduleHelper;
	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
        baseAmountFinder = new BaseAmountFinderImpl();

        paymentScheduleHelper = context.mock(PaymentScheduleHelper.class);

        pricePopulator = new PricePopulatorImpl();
        pricePopulator.setBeanFactory(beanFactory);

        pricedEntityFactory.setBaseAmountFinder(baseAmountFinder);
        pricedEntityFactory.setPricePopulator(pricePopulator);
        pricedEntityFactory.setBeanFactory(beanFactory);
        pricedEntityFactory.setPaymentScheduleHelper(paymentScheduleHelper);
        pricedEntityFactory.setBundleIdentifier(new BundleIdentifierImpl());
        pricedEntityFactory.setDefaultDataSource(baseAmountService);

        final TimeService timeService = context.mock(TimeService.class);
        context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.TIME_SERVICE);
				will(returnValue(timeService));
				allowing(timeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});

		service.setBeanFactory(beanFactory);
		service.setPriceAdjustmentService(priceAdjustmentService);
		service.setBaseAmountFinder(baseAmountFinder);
		service.setPricedEntityFactory(pricedEntityFactory);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test that if there is no sku price in the catalog, price lookup falls
	 * back to the Product's price in the catalog.
	 */
	@Test
	public void testPriceLookupFallbackNoSkuPrice() {
		final Price verifyPrice = new PriceImpl();
		final Money listPrice = MoneyFactory.createMoney(new BigDecimal(THIRTEEN), USD);
		verifyPrice.setListPrice(listPrice);

		final List<BaseAmount> list = new ArrayList<BaseAmount>();
		BaseAmount amount = new BaseAmountImpl(
				"guid-2", PRODUCT_GUID, "PRODUCT",
				new BigDecimal(TWELVE), new BigDecimal(THIRTEEN), new BigDecimal(THIRTEEN),
				ANY);
		list.add(amount);

		final List<String> objGuids = new ArrayList<String>();
		objGuids.add(SKU);
		objGuids.add(PRODUCT_GUID);

		final PriceListStackImpl stack = new PriceListStackImpl();
		stack.addPriceList(ANY);
		stack.setCurrency(USD);

		final ProductSku sku = context.mock(ProductSku.class);
		final Product product = context.mock(Product.class);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_TIER, PriceTierImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_SCHEDULE, PriceScheduleImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SCHEME, PricingSchemeImpl.class);

		context.checking(new Expectations() {
			{
				allowing(sku).getSkuCode();
				will(returnValue(SKU));
				allowing(sku).getProduct();
				will(returnValue(product));
				allowing(product).getGuid();
				will(returnValue(PRODUCT_GUID));

				allowing(baseAmountService).getBaseAmounts(with(stack.getPriceListStack()), with(listContaining(objGuids)));
					will(returnValue(list));

				allowing(paymentScheduleHelper).getPaymentSchedule(sku); will(returnValue(null));
			}
		});

		assertEquals(verifyPrice.getListPrice(TWELVE), service.getSkuPrice(sku, stack).getListPrice());
	}


	/**
	 * Tests that getLowestProductPrice returns null if there is no price assigned for product or catalog.
	 */
	@Ignore("unknown value of broken test")
	@Test
	public void testGetLowestPriceForNoPricedProduct() {
		final Product product = context.mock(Product.class);
		context.checking(new Expectations() {
			{
				allowing(product).hasMultipleSkus();
				will(returnValue(true));
			}
		});

		PriceLookupServiceImpl testService = new PriceLookupServiceImpl();

		assertNull("If there is no priced assigned for product/catalog, then it should return null!",
					testService.getProductPrice(product, null));
	}

	/**
	 * Test for get prices for all price lists for sku.
	 */
	@Test
	public void testGetSkuPrices() {

		final Product product = context.mock(Product.class);
		final ProductSku sku0 = context.mock(ProductSku.class, "sku0");

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_TIER, PriceTierImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_LIST_STACK, PriceListStackImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_SCHEDULE, PriceScheduleImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SCHEME, PricingSchemeImpl.class);

		context.checking(new Expectations() {
			{

				allowing(sku0).getSkuCode();
				will(returnValue(SKU_GUID1));

				allowing(sku0).getProduct();
				will(returnValue(product));

				allowing(product).getGuid();
				will(returnValue("someGuid-1"));

				allowing(paymentScheduleHelper).getPaymentSchedule(sku0);
				will(returnValue(null));
			}
		});

		PriceListStackImpl plStack = new PriceListStackImpl();
		plStack.addPriceList(ANY);
		plStack.addPriceList(ANY1);
		plStack.addPriceList(ANY2);
		plStack.setCurrency(Currency.getInstance("CAD"));

		final List<BaseAmount> baList = new ArrayList<BaseAmount>();

		// first
		BaseAmount amount = null;

		amount = new BaseAmountImpl("guid-1-1", SKU_GUID1, SKU,
				new BigDecimal(TWELVE), BigDecimal.TEN, BigDecimal.TEN, ANY);
		baList.add(amount);

		amount = new BaseAmountImpl("guid-1-2", SKU_GUID1, SKU,
				new BigDecimal(TWELVE + 1), new BigDecimal(THIRTEEN), new BigDecimal(THIRTEEN),	ANY);
		baList.add(amount);

		// second price list
		amount = new BaseAmountImpl("guid-1-3", SKU_GUID1, SKU,
				new BigDecimal(TWELVE), BigDecimal.TEN, BigDecimal.TEN, ANY1);
		baList.add(amount);

		amount = new BaseAmountImpl("guid-1-4", SKU_GUID1, SKU,
				new BigDecimal(TWELVE + 1), new BigDecimal(THIRTEEN), new BigDecimal(THIRTEEN),	ANY1);
		baList.add(amount);

		amount = new BaseAmountImpl("guid-1-5", SKU_GUID1, SKU,
				new BigDecimal(TWELVE + 2), new BigDecimal(THIRTEEN + 1), new BigDecimal(THIRTEEN + 1),	ANY1);
		baList.add(amount);

		// third price list
		amount = new BaseAmountImpl("guid-1-6", SKU_GUID1, SKU,
				new BigDecimal(TWELVE), BigDecimal.TEN, BigDecimal.TEN, ANY2);

		baList.add(amount);

		BaseAmountServiceImpl baService = new BaseAmountServiceImpl() {
			@Override
			public List<BaseAmount> getBaseAmounts(final List<String> plGuids, final List<String> objectGuids) {
				return baList;
			}
		};
        pricedEntityFactory.setDefaultDataSource(baService);

		final PriceLookupServiceImpl testService = new PriceLookupServiceImpl();
		testService.setBeanFactory(beanFactory);
		testService.setBaseAmountFinder(baseAmountFinder);
		testService.setPriceAdjustmentService(priceAdjustmentService);
		testService.setPricedEntityFactory(pricedEntityFactory);

		Map<String, Price> rez = testService.getSkuPrices(sku0, plStack);

		assertNotNull(rez);

		assertEquals(1, rez.get(ANY2).getPriceTiers().size());

		assertEquals(2, rez.get(ANY).getPriceTiers().size());

		assertEquals(2 + 1, rez.get(ANY1).getPriceTiers().size());
	}

	/**
	 * Test that the lowest price sku is selected.
	 */
	@Test
	public void testLowestSkuPrice() {
		final Price verifyPrice = new PriceImpl();
		final Money listPrice = MoneyFactory.createMoney(BigDecimal.TEN, Currency.getInstance("UAH"));
		verifyPrice.setListPrice(listPrice);

		final List<BaseAmount> list = new ArrayList<BaseAmount>();

		BaseAmount amount = new BaseAmountImpl("guid-1-0", SKU_GUID0, SKU,
				new BigDecimal(TWELVE), new BigDecimal(THIRTEEN), new BigDecimal(THIRTEEN), ANY);
		list.add(amount);

		amount = new BaseAmountImpl("guid-1-1", SKU_GUID1, SKU,
				new BigDecimal(TWELVE), BigDecimal.TEN, BigDecimal.TEN, ANY); // lowest is 10
		list.add(amount);

		amount = new BaseAmountImpl("guid-2", PRODUCT_GUID, "PRODUCT",
				new BigDecimal(TWELVE), new BigDecimal(THIRTEEN), new BigDecimal(THIRTEEN),	ANY);
		list.add(amount);

		final Product product = context.mock(Product.class);
		final ProductSku sku0 = context.mock(ProductSku.class, "sku0");
		final ProductSku sku1 = context.mock(ProductSku.class, "sku1");
		final StoreProduct storeProduct = context.mock(StoreProduct.class);

		final Map<String, ProductSku> prodSkus = new HashMap<String, ProductSku>();
		prodSkus.put(SKU_GUID0, sku0);
		prodSkus.put(SKU_GUID1, sku1);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_TIER, PriceTierImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_SCHEDULE, PriceScheduleImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SCHEME, PricingSchemeImpl.class);

		final PriceListStackImpl plStack = new PriceListStackImpl();
		plStack.addPriceList(ANY);
		plStack.setCurrency(Currency.getInstance("UAH"));

		final List<String> objectGuids = new ArrayList<String>();
		objectGuids.add(PRODUCT_GUID);
		objectGuids.add(SKU_GUID0);
		objectGuids.add(SKU_GUID1);

		context.checking(new Expectations() {
			{
				allowing(sku0).getProduct(); will(returnValue(product));
				allowing(sku1).getProduct(); will(returnValue(product));
				allowing(sku0).getSkuCode(); will(returnValue(SKU_GUID0));
				allowing(sku1).getSkuCode(); will(returnValue(SKU_GUID1));
				allowing(sku0).isWithinDateRange(with(any(Date.class))); will(returnValue(true));
				allowing(sku1).isWithinDateRange(with(any(Date.class))); will(returnValue(true));

				allowing(product).getProductSkus(); will(returnValue(prodSkus));
				allowing(product).getGuid(); will(returnValue(PRODUCT_GUID));

				allowing(storeProduct).getWrappedProduct(); will(returnValue(product));
				allowing(storeProduct).getGuid(); will(returnValue(PRODUCT_GUID));
				allowing(storeProduct).getProductSkus(); will(returnValue(prodSkus));

				allowing(baseAmountService).getBaseAmounts(with(plStack.getPriceListStack()), with(listContaining(objectGuids)));
					will(returnValue(list));
				for (final String skuGuid : prodSkus.keySet()) {
					final List<String> skuGuids = new ArrayList<String>();
		            skuGuids.add(PRODUCT_GUID);
		            skuGuids.add(skuGuid);
		            allowing(baseAmountService).getBaseAmounts(with(plStack.getPriceListStack()), with(listContaining(skuGuids)));
		            will(returnValue(list));
		       }

				allowing(paymentScheduleHelper).isPaymentScheduleCapable(storeProduct); will(returnValue(false));
				allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
				allowing(paymentScheduleHelper).getPaymentSchedule(sku0); will(returnValue(null));
				allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
			}
		});

		assertEquals(
				verifyPrice.getListPrice(TWELVE).getAmount(),
				service.getProductPrice(product, plStack).getListPrice(TWELVE).getAmount());

		Collection<Product> storeProducts = new ArrayList<Product>();
		storeProducts.add(storeProduct);
		Map<Product, Price> productsPrices = service.getProductsPrices(storeProducts, plStack);
		assertEquals(
				verifyPrice.getListPrice(TWELVE).getAmount(),
				productsPrices.get(storeProduct).getListPrice(TWELVE).getAmount());
	}

	/**
	 * Test that the returned map is never null, even when using an invalid price list GUID.
	 */
	@Test
	public void testGetProductBundlePriceAdjustmentsMapWithInvalidPriceList() {

		final ProductBundleImpl productBundle = new ProductBundleImpl();
		productBundle.setCalculated(Boolean.FALSE);
		productBundle.setGuid(GUID);

		context.checking(new Expectations() {
			{
				allowing(priceAdjustmentService).findByPriceListAndBundleAsMap(INVALID_PL_GUID, productBundle);
				will(returnValue(new HashMap<String, PriceAdjustment>()));
			}
		});

		assertNotNull("Should not return null when no price adjustments are found due to an invalid price list GUID.",
				service.getProductBundlePriceAdjustmentsMap(productBundle, INVALID_PL_GUID));
		assertEquals("Should return an empty map when no price adjustment are found due to an invalid price list GUID.",
				Collections.emptyMap(),
				service.getProductBundlePriceAdjustmentsMap(productBundle, INVALID_PL_GUID));
	}

	/**
	 * Test that the price adjustment filtering for bundles works.
	 */
	@Test
	public void testGetProductBundlePriceAdjustmentsMapWithZeroPriceAdjustments1() {
		final ProductBundle productBundle = context.mock(ProductBundle.class);
		final Map<String, PriceAdjustment> priceAdjustments = new HashMap<String, PriceAdjustment>();
		priceAdjustments.put(ANY, createPA(ANY, 0));
		priceAdjustments.put(ANY1, createPA(ANY1, 1));
		priceAdjustments.put(ANY2, createPA(ANY2, -1));
		context.checking(new Expectations() {
			{
				allowing(productBundle).isCalculated();
				will(returnValue(Boolean.FALSE));

				allowing(priceAdjustmentService).findByPriceListAndBundleAsMap(GUID, productBundle);
				will(returnValue(priceAdjustments));
			}
		});
		final Map<String, PriceAdjustment> expectedAdjustments = new HashMap<String, PriceAdjustment>();
		expectedAdjustments.put(ANY1, priceAdjustments.get(ANY1));
		assertEquals(expectedAdjustments, service.getProductBundlePriceAdjustmentsMap(productBundle, GUID));
	}

	/**
	 * Test that the price adjustment filtering for bundles works.
	 */
	@Test
	public void testGetProductBundlePriceAdjustmentsMapWithZeroPriceAdjustments2() {
		final ProductBundle productBundle = context.mock(ProductBundle.class);
		final Map<String, PriceAdjustment> priceAdjustments = new HashMap<String, PriceAdjustment>();
		priceAdjustments.put(ANY, createPA(ANY, 0));
		priceAdjustments.put(ANY1, createPA(ANY1, 1));
		priceAdjustments.put(ANY2, createPA(ANY2, -1));
		context.checking(new Expectations() {
			{
				allowing(productBundle).isCalculated();
				will(returnValue(Boolean.TRUE));

				allowing(priceAdjustmentService).findByPriceListAndBundleAsMap(GUID, productBundle);
				will(returnValue(priceAdjustments));
			}
		});
		final Map<String, PriceAdjustment> expectedAdjustments = new HashMap<String, PriceAdjustment>();
		expectedAdjustments.put(ANY2, priceAdjustments.get(ANY2));
		assertEquals(expectedAdjustments, service.getProductBundlePriceAdjustmentsMap(productBundle, GUID));
	}




	private PriceAdjustment createPA(final String guid, final int amount) {
		final PriceAdjustment priceAdj = new PriceAdjustmentImpl();
		priceAdj.setGuid(guid);
		priceAdj.setAdjustmentAmount(BigDecimal.valueOf(amount));
		return priceAdj;
	}


	private BaseAmount createBaseAmount(final String priceListGuid,
			final String quantity, final String list, final String sale,
			final String objectGuid, final String objectType) {
		BaseAmountImpl baseAmount = new BaseAmountImpl();
		baseAmount.setPriceListDescriptorGuid(priceListGuid);
		baseAmount.setObjectGuid(objectGuid);
		baseAmount.setObjectType(objectType);
		baseAmount.setSaleValue(new BigDecimal(sale));
		baseAmount.setListValue(new BigDecimal(list));
		baseAmount.setQuantity(new BigDecimal(quantity));
		return baseAmount;
	}


	/**
	 * Test that requesting a multi-faceted price returns the expected facets for a SKU.
	 */
	@Test
	public void testPricingScheme() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_TIER, PriceTierImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_SCHEDULE, PriceScheduleImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SCHEME, PricingSchemeImpl.class);

		final Collection<BaseAmount> amounts = new ArrayList<BaseAmount>();
		BaseAmount baseAmount = createBaseAmount(PL_GUID, "1", "17.99", "14.99", PRODUCT_GUID, BaseAmountObjectType.PRODUCT.toString());

		amounts.add(baseAmount);

		final PriceListStackImpl stack = new PriceListStackImpl();
		stack.addPriceList(PL_GUID);
		stack.setCurrency(USD);

		final Product product = context.mock(Product.class);
		final ProductSku productSku = context.mock(ProductSku.class);
		final Map<String, ProductSku> prodSkus = new HashMap<String, ProductSku>();
		prodSkus.put("SKU-GUID2", productSku);

		final List<String> objectGuids = new ArrayList<String>();
		objectGuids.add("SKU-GUID2");
		objectGuids.add(PRODUCT_GUID);

		context.checking(new Expectations() {
			{
				allowing(product).getGuid();
				will(returnValue(PRODUCT_GUID));
				allowing(product).getProductSkus();
				will(returnValue(prodSkus));
				allowing(productSku).getProduct();
				will(returnValue(product));
				allowing(productSku).getSkuCode();
				will(returnValue("SKU-GUID2"));

				allowing(baseAmountService).getBaseAmounts(with(stack.getPriceListStack()), with(listContaining(objectGuids)));
				will(returnValue(amounts));
				allowing(paymentScheduleHelper).isPaymentScheduleCapable(product);
				will(returnValue(false));
				allowing(paymentScheduleHelper).getPaymentSchedule(productSku);
				will(returnValue(null));
			}
		});

		Price price = service.getSkuPrice(productSku, stack);

		assertPriceValue("Classic price returned wrong price", "17.99", price.getListPrice());

		assertNotNull("A price scheme should be available", price.getPricingScheme());
		PricingScheme scheme = price.getPricingScheme();

		assertEquals("Incorrect number of schedules", 1, scheme.getSchedules().size());

		Collection<PriceSchedule> schedules = scheme.getSchedules(PriceScheduleType.PURCHASE_TIME);
		assertFalse("A purchase time price schedule should be found", schedules.isEmpty());

		PriceSchedule purchaseTimeSchedule = schedules.iterator().next();
		SimplePrice oneTimePrice = scheme.getSimplePriceForSchedule(purchaseTimeSchedule);
		assertNotNull("A one-time price should be defined", oneTimePrice);
		assertPriceValue("The one-time price incorrect", "17.99", oneTimePrice.getListPrice(1));

		Collection<PriceSchedule> recurringSchedules = scheme.getSchedules(PriceScheduleType.RECURRING);
		assertTrue("A recurring price schedule shouldn't be found", recurringSchedules.isEmpty());
	}

	/**
	 * Test that a calculated bundle gets a price based on the constituents.
	 */
	@Test
	public void testCalculatedBundlePrice() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_TIER, PriceTierImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_SCHEDULE, PriceScheduleImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SCHEME, PricingSchemeImpl.class);

		final ProductSku productSku = context.mock(ProductSku.class);
		final ProductBundle productBundle = context.mock(ProductBundle.class);
		final Map<String, ProductSku> skuMap = new HashMap<String, ProductSku>();
		skuMap.put(SKU_GUID0, productSku);

		final List<BundleConstituent> constituents = new ArrayList<BundleConstituent>();
		final BundleConstituent constituent = context.mock(BundleConstituent.class);
		constituents.add(constituent);

		final ConstituentItem constituentItem = context.mock(ConstituentItem.class);
		final Product constituentProduct = context.mock(Product.class, "constituentProduct");
		final ProductSku constituentProductProductSku = context.mock(ProductSku.class, "sku2");

		final String constituentGuid = "CONSTITUENT";
		final String constituentSkuGuid = "CONSTITUENT_SKU";
		final Map<String, ProductSku> constituentSkuMap = new HashMap<String, ProductSku>();
		constituentSkuMap.put(constituentSkuGuid, constituentProductProductSku);

		final Collection<BaseAmount> amounts = new ArrayList<BaseAmount>();
		BaseAmount baseAmount1 = createBaseAmount(PL_GUID, "1", "8.99", REC_LIST_PRICE, constituentGuid, BaseAmountObjectType.PRODUCT.toString());
		amounts.add(baseAmount1);

		final PriceListStackImpl stack = new PriceListStackImpl();
		stack.addPriceList(PL_GUID);
		stack.setCurrency(USD);

		final List<String> objectGuids = new ArrayList<String>();
		objectGuids.add(SKU_GUID0);
		objectGuids.add(PRODUCT_GUID);

		final List<String> constitutentGuids = new ArrayList<String>();
		constitutentGuids.add(constituentGuid);
		constitutentGuids.add(constituentSkuGuid);

		final List<String> emptyList = new ArrayList<String>();

		context.checking(new Expectations() {
			{
				allowing(productSku).getSkuCode(); will(returnValue(SKU_GUID0));
				allowing(productSku).getProduct(); will(returnValue(productBundle));
				allowing(productSku).isWithinDateRange(with(any(Date.class))); will(returnValue(true));

				allowing(productBundle).isCalculated(); will(returnValue(Boolean.TRUE));
				allowing(productBundle).getGuid(); will(returnValue(PRODUCT_GUID));
				allowing(productBundle).getConstituents(); will(returnValue(constituents));
				allowing(productBundle).getSelectionRule(); will(returnValue(null));
				allowing(productBundle).getProductSkus(); will(returnValue(skuMap));

				allowing(constituent).getConstituent(); will(returnValue(constituentItem));
				allowing(constituent).getQuantity(); will(returnValue(1));

				allowing(constituent).getPriceAdjustmentForPriceList(PL_GUID);
				will(returnValue(null));

				allowing(constituentItem).isProductSku(); will(returnValue(false));
				allowing(constituentItem).isBundle(); will(returnValue(false));
				allowing(constituentItem).isProduct(); will(returnValue(true));
				allowing(constituentItem).getProduct(); will(returnValue(constituentProduct));
				allowing(constituentProduct).getGuid(); will(returnValue(constituentGuid));
				allowing(constituentProduct).getProductSkus(); will(returnValue(constituentSkuMap));
				allowing(constituentProductProductSku).getSkuCode(); will(returnValue(constituentSkuGuid));
				allowing(constituentProductProductSku).getProduct(); will(returnValue(constituentProduct));
				allowing(constituentProductProductSku).isWithinDateRange(with(any(Date.class))); will(returnValue(true));

				allowing(baseAmountService).getBaseAmounts(with(stack.getPriceListStack()), with(listContaining(objectGuids)));
				will(returnValue(Collections.emptyList()));

				allowing(baseAmountService).getBaseAmounts(with(stack.getPriceListStack()), with(listContaining(constitutentGuids)));
				will(returnValue(amounts));

				allowing(baseAmountService).getBaseAmounts(with(stack.getPriceListStack()), with(emptyList));
				will(returnValue(Collections.emptyList()));

				allowing(paymentScheduleHelper).isPaymentScheduleCapable(constituentProduct); will(returnValue(false));
				allowing(paymentScheduleHelper).getPaymentSchedule(productSku); will(returnValue(null));
				allowing(paymentScheduleHelper).getPaymentSchedule(constituentProductProductSku); will(returnValue(null));
			}
		});
		Price skuPrice = service.getSkuPrice(productSku, stack);
		assertNotNull("Price should not be null", skuPrice);


	}
	/**
	 * Test that a calculated bundle doesn't get a price when one of the constituents has a null price,
	 * even if the bundle itself has a base amount.
	 */
	@Test
	public void testCalculatedBundlePriceWithNullConstituentPrice() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_TIER, PriceTierImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_SCHEDULE, PriceScheduleImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SCHEME, PricingSchemeImpl.class);

		final ProductSku productSku = context.mock(ProductSku.class);
		final ProductBundle productBundle = context.mock(ProductBundle.class);

		final List<BundleConstituent> constituents = new ArrayList<BundleConstituent>();
		final BundleConstituent constituent = context.mock(BundleConstituent.class);
		constituents.add(constituent);

		final ConstituentItem constituentItem = context.mock(ConstituentItem.class);
		final Product constituentProduct = context.mock(Product.class, "constituentProduct");

		final String constituentGuid = "CONSTITUENT";

		final Collection<BaseAmount> amounts = new ArrayList<BaseAmount>();
		BaseAmount baseAmount1 = createBaseAmount(PL_GUID, "1", "9.49", REC_LIST_PRICE, PRODUCT_GUID, BaseAmountObjectType.PRODUCT.toString());
		amounts.add(baseAmount1);

		final PriceListStackImpl stack = new PriceListStackImpl();
		stack.addPriceList(PL_GUID);
		stack.setCurrency(USD);

		final List<String> objectGuids = new ArrayList<String>();
		objectGuids.add(SKU_GUID0);
		objectGuids.add(PRODUCT_GUID);

		final List<String> constitutentGuids = new ArrayList<String>();
		constitutentGuids.add(constituentGuid);

		final List<String> emptyList = new ArrayList<String>();

		context.checking(new Expectations() {
			{
				allowing(productSku).getGuid(); will(returnValue(SKU_GUID0));
				allowing(productSku).getProduct(); will(returnValue(productBundle));

				allowing(productBundle).isCalculated(); will(returnValue(Boolean.TRUE));
				allowing(productBundle).getGuid(); will(returnValue(PRODUCT_GUID));
				allowing(productBundle).getConstituents(); will(returnValue(constituents));

				allowing(constituent).getConstituent(); will(returnValue(constituentItem));
				allowing(constituentItem).isProductSku(); will(returnValue(false));
				allowing(constituentItem).isBundle(); will(returnValue(false));
				allowing(constituentItem).isProduct(); will(returnValue(true));
				allowing(constituentItem).getProduct(); will(returnValue(constituentProduct));
				allowing(constituentProduct).getGuid(); will(returnValue(constituentGuid));
				allowing(constituentProduct).getProductSkus(); will(returnValue(Collections.emptyMap()));

				allowing(baseAmountService).getBaseAmounts(with(stack.getPriceListStack()), with(listContaining(objectGuids)));
				will(returnValue(amounts));

				allowing(baseAmountService).getBaseAmounts(with(stack.getPriceListStack()), with(listContaining(constitutentGuids)));
				will(returnValue(Collections.emptyList()));

				allowing(baseAmountService).getBaseAmounts(with(stack.getPriceListStack()), with(emptyList));
				will(returnValue(Collections.emptyList()));

				allowing(paymentScheduleHelper).isPaymentScheduleCapable(constituentProduct); will(returnValue(false));
				allowing(paymentScheduleHelper).getPaymentSchedule(productSku); will(returnValue(null));

				allowing(productBundle).getSelectionRule(); will(returnValue(null));


			}
		});
		Price skuPrice = service.getSkuPrice(productSku, stack);
		assertNull("Price should be null", skuPrice);

	}


	private void assertPriceValue(final String message, final String expected, final Money actual) {
		assertEquals(message, expected, actual.getAmount().toString());
	}

}
