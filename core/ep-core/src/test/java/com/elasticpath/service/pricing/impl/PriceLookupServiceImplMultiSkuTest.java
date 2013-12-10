package com.elasticpath.service.pricing.impl;

import static com.elasticpath.test.util.MatcherFactory.listContaining;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceScheduleImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.PricingSchemeImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.domain.pricing.impl.PriceListDescriptorImpl;
import com.elasticpath.domain.pricing.impl.PriceListStackImpl;
import com.elasticpath.service.catalog.impl.BundleIdentifierImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PaymentScheduleHelper;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.test.util.Assert;

/**
 * Test PriceLookupServiceImpl with multi-sku products.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class PriceLookupServiceImplMultiSkuTest {

    private static final int YESTERDAY = 414141412;
    private static final int TODAY = 444444444;
    private static final int TOMORROW = 474747478;

    private static final String PRICE_WAS_NOT_AS_EXPECTED = "Price was not as expected";

    private static final int QUANTITY_1 = 1;
    private static final int QUANTITY_2 = 2;
    private static final int QUANTITY_3 = 3;

    private static final int SEVENTY = 70;
    private static final int EIGHTY = 80;
    private static final int NINETY = 90;
    private static final int ONE_HUNDRED = 100;
    private static final int ONE_HUNDRED_TEN = 110;
    private static final int ONE_HUNDRED_FIFTEEN = 115;
    private static final int ONE_HUNDRED_TWENTY = 120;

    private static final String PL1_GUID = "PL1_GUID";
    private static final String PL2_GUID = "PL2_GUID";
    private static final String PL3_GUID = "PL3_GUID";
    private static final String PRODUCT_GUID = "PRODUCT_GUID";
    private static final String SKU1_GUID = "SKU1_GUID";
    private static final String SKU2_GUID = "SKU2_GUID";
    private static final String SKU3_GUID = "SKU3_GUID";

    private static final String CURRENCY_CODE = "CAD";
    private static final Currency CURRENCY = Currency.getInstance(CURRENCY_CODE);


	@Rule
	public final JUnitRuleMockery mockery = new JUnitRuleMockery();

    private PriceListDescriptor priceList1;
    private PriceListDescriptor priceList2;
    private PriceListDescriptor priceList3;
    private Product product;
    private PriceListStack plStack;
    private final List<BaseAmount> baseAmounts = new LinkedList<BaseAmount>();

    private final PriceLookupServiceImpl priceLookupService = new PriceLookupServiceImpl();
    private BaseAmountService baseAmountService;
    private BeanFactory beanFactory;
	private PricePopulatorImpl pricePopulator;
	private BaseAmountFinderImpl baseAmountFinder;
	private final PricedEntityFactoryImpl pricedEntityFactory = new PricedEntityFactoryImpl();
	private PaymentScheduleHelper paymentScheduleHelper;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/** Set up the tests. */
    @Before
    public void setUp() {
        beanFactory = mockery.mock(BeanFactory.class);
        baseAmountService = mockery.mock(BaseAmountService.class);
        expectationsFactory = new BeanFactoryExpectationsFactory(mockery, beanFactory);
        baseAmountFinder = new BaseAmountFinderImpl();

        paymentScheduleHelper = mockery.mock(PaymentScheduleHelper.class);


        pricePopulator = new PricePopulatorImpl();
        pricePopulator.setBeanFactory(beanFactory);

        pricedEntityFactory.setBaseAmountFinder(baseAmountFinder);
        pricedEntityFactory.setPricePopulator(pricePopulator);
        pricedEntityFactory.setBeanFactory(beanFactory);
        pricedEntityFactory.setPaymentScheduleHelper(paymentScheduleHelper);
        pricedEntityFactory.setBundleIdentifier(new BundleIdentifierImpl());
        pricedEntityFactory.setDefaultDataSource(baseAmountService);

        priceLookupService.setBeanFactory(beanFactory);
        priceLookupService.setBaseAmountFinder(baseAmountFinder);
        priceLookupService.setPricedEntityFactory(pricedEntityFactory);

		final TimeService timeService = mockery.mock(TimeService.class);
		mockery.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.TIME_SERVICE);
				will(returnValue(timeService));

				allowing(timeService).getCurrentTime();
				will(returnValue(new Date(TODAY)));
			}
		});

        priceList1 = createPriceListDescriptor(PL1_GUID);
        priceList2 = createPriceListDescriptor(PL2_GUID);
        priceList3 = createPriceListDescriptor(PL3_GUID);
        product = new ProductImpl();
        product.setGuid(PRODUCT_GUID);

        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_TIER, PriceTierImpl.class);
        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_SCHEDULE, PriceScheduleImpl.class);
        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SCHEME, PricingSchemeImpl.class);
    }

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

    /**
     * In the following scenario:
     *        Product        SKU1        SKU2
     * PL1       $100        $110        $115
     *
     * Lowest price should be $110.
     */
    @Test
    public void testWhenAllSkusHavePricesAndProductPriceIsLower() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);
        final ProductSku sku2 = createSku(product, SKU2_GUID);

        createPriceListStack(priceList1);

        addPrice(priceList1, sku1, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_TEN, priceList1.getGuid()).newInstance());
        addPrice(priceList1, sku2, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_FIFTEEN, priceList1.getGuid()).newInstance());
        addPrice(priceList1, product, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_TEN, priceList1.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku2); will(returnValue(null));
        	}
        });

        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }

    /**
     * In the following scenario:
     *        Product        SKU1        SKU2
     * PL1       $120        $110        $115
     *
     * Lowest price should be $110.
     */
    @Test
    public void testWhenAllSkusHavePricesAndProductPriceIsHigher() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);
        final ProductSku sku2 = createSku(product, SKU2_GUID);

        createPriceListStack(priceList1);

        addPrice(priceList1, sku1, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_TEN, priceList1.getGuid()).newInstance());
        addPrice(priceList1, sku2, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_FIFTEEN, priceList1.getGuid()).newInstance());
        addPrice(priceList1, product, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_TWENTY, priceList1.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_TEN, priceList1.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku2); will(returnValue(null));
        	}
        });

        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }

    /**
     * In the following scenario:
     *        Product        SKU1        SKU2        SKU3
     * PL1       $100        $110        $115           -
     *
     * Lowest price should be $100.
     */
    //Test
    public void testWhenNotAllSkusHavePricesAndProductPriceIsLower() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);
        final ProductSku sku2 = createSku(product, SKU2_GUID);
        createSku(product, SKU3_GUID);

        createPriceListStack(priceList1);

        addPrice(priceList1, sku1, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_TEN, priceList1.getGuid()).newInstance());
        addPrice(priceList1, sku2, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_FIFTEEN, priceList1.getGuid()).newInstance());
        addPrice(priceList1, product, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku2); will(returnValue(null));
        	}
        });


        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }

    /**
     * In the following scenario:
     *        Product        SKU1        SKU2        SKU3
     * PL1       $120        $110        $115           -
     *
     * Lowest price should be $110.
     */
    @Test
    public void testWhenNotAllSkusHavePricesAndProductPriceIsHigher() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);
        final ProductSku sku2 = createSku(product, SKU2_GUID);
        final ProductSku sku3 = createSku(product, SKU3_GUID);

        createPriceListStack(priceList1);

        addPrice(priceList1, sku1, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_TEN, priceList1.getGuid()).newInstance());
        addPrice(priceList1, sku2, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_FIFTEEN, priceList1.getGuid()).newInstance());
        addPrice(priceList1, product, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_TWENTY, priceList1.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_TEN, priceList1.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku2); will(returnValue(null));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku3); will(returnValue(null));
        	}
        });

        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }

    /**
     * In the following scenario:
     *        Product        SKU1        SKU2
     * PL1       $100
     * PL2                   $110        $115
     *
     * Lowest price should be $100.
     */
    @Test
    public void testWhenAllSkusHavePricesButHigherPriorityPriceListHasLowerProductPrice() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);
        final ProductSku sku2 = createSku(product, SKU2_GUID);

        createPriceListStack(priceList1, priceList2);

        addPrice(priceList1, product, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).newInstance());

        addPrice(priceList2, sku1, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_TEN, priceList2.getGuid()).newInstance());
        addPrice(priceList2, sku2, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_FIFTEEN, priceList2.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku2); will(returnValue(null));
        	}
        });

        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }

    /**
     * In the following scenario:
     *        Product        SKU1        SKU2
     * PL1       $120
     * PL2                   $110        $115
     *
     * Lowest price should be $120.
     */
    @Test
    public void testWhenAllSkusHavePricesButHigherPriorityPriceListHasHigherProductPrice() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);
        final ProductSku sku2 = createSku(product, SKU2_GUID);

        createPriceListStack(priceList1, priceList2);

        addPrice(priceList1, product, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_TWENTY, priceList1.getGuid()).newInstance());

        addPrice(priceList2, sku1, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_TEN, priceList2.getGuid()).newInstance());
        addPrice(priceList2, sku2, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_FIFTEEN, priceList2.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED_TWENTY, priceList1.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku2); will(returnValue(null));
        	}
        });

        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }

    /**
     * In the following scenario:
     *        Product        SKU1
     * PL1       $100      2+ $90
     *
     * Price Tiers should be:
     *  1  $100
     *  2+  $90
     *  .
     */
    @Test
    public void testWhenSkuHasHigherTierPrice() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);

        createPriceListStack(priceList1);

        addPrice(priceList1, product, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).newInstance());

        addPrice(priceList1, sku1, createPriceBuilder().addTier(QUANTITY_2, NINETY, priceList1.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).
                                                         addTier(QUANTITY_2, NINETY, priceList1.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	}
        });


        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }

    /**
     * In the following scenario:
     *        Product
     * PL1       $100
     * PL2     2+ $90
     * PL3        $80
     *
     * Price Tiers should be:
     *  1  $100
     *  2+  $90
     *  .
     */
    @Test
    public void testWhenLowerPriorityPriceListHasHigherTierProductPrice() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);
        createPriceListStack(priceList1, priceList2, priceList3);

        addPrice(priceList1, product, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).newInstance());

        addPrice(priceList2, product, createPriceBuilder().addTier(QUANTITY_2, NINETY, priceList2.getGuid()).newInstance());

        addPrice(priceList3, product, createPriceBuilder().addTier(QUANTITY_2, EIGHTY, priceList3.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).
                                                         addTier(QUANTITY_2, NINETY, priceList2.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	ignoring(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	}
        });


        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }

    /**
     * In the following scenario:
     *        Product        SKU1
     * PL1       $100
     * PL2                 2+ $90
     *
     * Price Tiers should be:
     *  1  $100
     *  2+  $90
     *  .
     */
    @Test
    public void testWhenLowerPriorityPriceListHasHigherTierSkuPrice() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);

        createPriceListStack(priceList1, priceList2);

        addPrice(priceList1, product, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).newInstance());

        addPrice(priceList2, sku1, createPriceBuilder().addTier(QUANTITY_2, NINETY, priceList2.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).
                                                         addTier(QUANTITY_2, NINETY, priceList2.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	}
        });

        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }

    /**
     * In the following scenario:
     *        Product
     * PL1     2+ $90
     * PL2       $100
     *
     * Price Tiers should be:
     *  1  $100
     *  2+  $90
     *  .
     */
    @Test
    public void testWhenHigherPriorityPriceListHasHigherTierProductPrice() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);
        createPriceListStack(priceList1, priceList2);

        addPrice(priceList1, product, createPriceBuilder().addTier(QUANTITY_2, NINETY, priceList1.getGuid()).newInstance());

        addPrice(priceList2, product, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList2.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList2.getGuid()).
                                                         addTier(QUANTITY_2, NINETY, priceList1.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	}
        });

        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }

    /**
     * In the following scenario:
     *        Product        SKU1
     * PL1                 2+ $90
     * PL2       $100
     *
     * Price Tiers should be:
     *  1  $100
     *  2+  $90
     *  .
     */
    @Test
    public void testWhenHigherPriorityPriceListHasHigherTierSkuPrice() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);

        createPriceListStack(priceList1, priceList2);

        addPrice(priceList1, sku1, createPriceBuilder().addTier(QUANTITY_2, NINETY, priceList1.getGuid()).newInstance());

        addPrice(priceList2, product, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList2.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList2.getGuid()).
                                                         addTier(QUANTITY_2, NINETY, priceList1.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	}
        });

        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }

    /**
     * In the following scenario:
     *        Product        SKU1          SKU2
     * PL1          -         $90          $100
     *
     * Price Tiers should be:
     *  1  $90
     *  .
     */
    @Test
    public void testWhenProductHasNoPrice() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);
        final ProductSku sku2 = createSku(product, SKU2_GUID);

        createPriceListStack(priceList1);

        addPrice(priceList1, sku1, createPriceBuilder().addTier(QUANTITY_1, NINETY, priceList1.getGuid()).newInstance());
        addPrice(priceList1, sku2, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, NINETY, priceList1.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku2); will(returnValue(null));
        	}
        });

        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }

	/** The (lowest) price of a product should not be of a sku which is enabled in the future. */
    @Test
	public void testLowestPriceEnabledFuture() {
		final ProductSku sku1 = createSku(product, SKU1_GUID);
		final ProductSku sku2 = createSku(product, SKU2_GUID);

		sku1.setStartDate(new Date(YESTERDAY));
		sku1.setEndDate(null);

		sku2.setStartDate(new Date(TOMORROW));
		sku2.setEndDate(null);

		createPriceListStack(priceList1);
		addPrice(priceList1, sku1, createPriceBuilder().addTier(QUANTITY_1, NINETY, priceList1.getGuid()).newInstance());
		addPrice(priceList1, sku2, createPriceBuilder().addTier(QUANTITY_1, EIGHTY, priceList1.getGuid()).newInstance());
		final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, NINETY, priceList1.getGuid()).newInstance();

		shouldGetBaseAmounts();
		mockery.checking(new Expectations() {
			{
				allowing(paymentScheduleHelper).isPaymentScheduleCapable(product);
				will(returnValue(false));
				allowing(paymentScheduleHelper).getPaymentSchedule(sku1);
				will(returnValue(null));
				allowing(paymentScheduleHelper).getPaymentSchedule(sku2);
				will(returnValue(null));
			}
		});
		Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
	}

	/** The (lowest) price of a product should not be of a sku which is disabled in the past. */
    @Test
	public void testLowestPriceDisabledPast() {
		final ProductSku sku1 = createSku(product, SKU1_GUID);
		final ProductSku sku2 = createSku(product, SKU2_GUID);

		sku1.setStartDate(new Date(YESTERDAY));
		sku1.setEndDate(null);

		sku2.setStartDate(new Date(YESTERDAY));
		sku2.setEndDate(new Date(YESTERDAY));

		createPriceListStack(priceList1);
		addPrice(priceList1, sku1, createPriceBuilder().addTier(QUANTITY_1, NINETY, priceList1.getGuid()).newInstance());
		addPrice(priceList1, sku2, createPriceBuilder().addTier(QUANTITY_1, EIGHTY, priceList1.getGuid()).newInstance());
		final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, NINETY, priceList1.getGuid()).newInstance();

		shouldGetBaseAmounts();
		mockery.checking(new Expectations() {
			{
				allowing(paymentScheduleHelper).isPaymentScheduleCapable(product);
				will(returnValue(false));
				allowing(paymentScheduleHelper).getPaymentSchedule(sku1);
				will(returnValue(null));
				allowing(paymentScheduleHelper).getPaymentSchedule(sku2);
				will(returnValue(null));
			}
		});
		Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
	}

    /**
     * In the following scenario:
     *        Product        SKU1
     * PL1          -         $90
     * PL2          -         $80
     *
     * Price Tiers should be:
     *  1  $90
     *  .
     */
    @Test
    public void testWhenProductHasNoPriceAndSkuIsInMultiplePriceLists() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);

        createPriceListStack(priceList1, priceList2);

        addPrice(priceList1, sku1, createPriceBuilder().addTier(QUANTITY_1, NINETY, priceList1.getGuid()).newInstance());

        addPrice(priceList2, sku1, createPriceBuilder().addTier(QUANTITY_1, EIGHTY, priceList2.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, NINETY, priceList1.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	}
        });

        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }


    /**
     * In the following scenario:
     *        Product
     * PL1     1 $100
     * PL2    3+  $80
     * PL3    2+  $70
     *
     * Price Tiers should be:
     *  1  $100
     *  2+  $70
     *  3+  $80
     *  .
     */
    @Test
    public void testWhenTieredPricesAreNotInOrder() {
        final ProductSku sku1 = createSku(product, SKU1_GUID);
        createPriceListStack(priceList1, priceList2, priceList3);

        addPrice(priceList1, product, createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).newInstance());
        addPrice(priceList2, product, createPriceBuilder().addTier(QUANTITY_3, EIGHTY, priceList2.getGuid()).newInstance());
        addPrice(priceList3, product, createPriceBuilder().addTier(QUANTITY_2, SEVENTY, priceList3.getGuid()).newInstance());

        final Price expectedPrice = createPriceBuilder().addTier(QUANTITY_1, ONE_HUNDRED, priceList1.getGuid()).
                                                         addTier(QUANTITY_2, SEVENTY, priceList3.getGuid()).
                                                         addTier(QUANTITY_3, EIGHTY, priceList2.getGuid()).newInstance();

        shouldGetBaseAmounts();

        mockery.checking(new Expectations() {
        	{
        	allowing(paymentScheduleHelper).isPaymentScheduleCapable(product); will(returnValue(false));
        	allowing(paymentScheduleHelper).getPaymentSchedule(sku1); will(returnValue(null));
        	}
        });

        Assert.assertEqualsReflectively(PRICE_WAS_NOT_AS_EXPECTED, expectedPrice, priceLookupService.getProductPrice(product, plStack));
    }




    private void shouldGetBaseAmounts() {
        final List<String> plGuids = new LinkedList<String>();
        final List<String> objectGuids = new LinkedList<String>();

        for (final String plGuid : plStack.getPriceListStack()) {
            plGuids.add(plGuid);
        }

        objectGuids.add(PRODUCT_GUID);
        for (final ProductSku sku : product.getProductSkus().values()) {
            objectGuids.add(sku.getGuid());
        }

        mockery.checking(new Expectations() { {
            allowing(baseAmountService).getBaseAmounts(with(plGuids), with(listContaining(objectGuids)));
            will(returnValue(baseAmounts));
            //price lookup service changed to call getSkuPrice which then invoked getBaseAmounts for EACH SKU!!
            for (final ProductSku sku : product.getProductSkus().values()) {
                final List<String> skuGuids = new LinkedList<String>();
                skuGuids.add(sku.getGuid());
                skuGuids.add(PRODUCT_GUID);
                allowing(baseAmountService).getBaseAmounts(with(plGuids), with(listContaining(skuGuids)));
                will(returnValue(baseAmounts));
            } }
        });

    }

    private ProductSku createSku(final Product product, final String skuGuid) {
        ProductSku sku = new ProductSkuImpl();
        sku.setStartDate(new Date(TODAY));
        sku.setGuid(skuGuid);
        sku.setSkuCode(skuGuid);
        sku.setProduct(product);
        product.addOrUpdateSku(sku);
        return sku;
    }

    private void createPriceListStack(final PriceListDescriptor... priceLists) {
        plStack = new PriceListStackImpl();
        plStack.setCurrency(CURRENCY);
        for (final PriceListDescriptor priceListDescriptor : priceLists) {
            plStack.addPriceList(priceListDescriptor.getGuid());
        }
    }

    private void addPrice(final PriceListDescriptor priceListDescriptor, final Product product, final Price price) {
        final String objectGuid = product.getGuid();
        final String priceListDescriptorGuid = priceListDescriptor.getGuid();

        addPrice(priceListDescriptorGuid, objectGuid, price, "PRODUCT");
    }

    private void addPrice(final PriceListDescriptor priceListDescriptor, final ProductSku sku, final Price price) {
        final String objectGuid = sku.getGuid();
        final String priceListDescriptorGuid = priceListDescriptor.getGuid();

        addPrice(priceListDescriptorGuid, objectGuid, price, "SKU");
    }

    private void addPrice(final String priceListDescriptorGuid, final String objectGuid, final Price price, final String objectType) {
        final SortedMap<Integer, PriceTier> priceTiers = price.getPriceTiers();
        for (Entry<Integer, PriceTier> entry : priceTiers.entrySet()) {
            final int minQty = entry.getKey();
            final PriceTier priceTier = entry.getValue();
            final BigDecimal listPrice = priceTier.getListPrice();
            final BigDecimal salePrice = priceTier.getSalePrice();

            BaseAmountImpl baseAmount = new BaseAmountImpl();
            baseAmount.setObjectGuid(objectGuid);
            baseAmount.setObjectType(objectType);
            baseAmount.setPriceListDescriptorGuid(priceListDescriptorGuid);
            baseAmount.setListValue(listPrice);
            baseAmount.setSaleValue(salePrice);
            baseAmount.setQuantity(new BigDecimal(minQty));

            baseAmounts.add(baseAmount);
        }
    }

    private PriceBuilder createPriceBuilder() {
        return new PriceBuilder();
    }

    private PriceListDescriptor createPriceListDescriptor(final String guid) {
        final PriceListDescriptorImpl priceListDescriptor = new PriceListDescriptorImpl();
        priceListDescriptor.setGuid(guid);
        priceListDescriptor.setCurrencyCode(CURRENCY_CODE);
        return priceListDescriptor;
    }

    /**
     * Builder for Price.
     */
    private class PriceBuilder {

        private final PriceImpl price = new PriceImpl();
        private final PricingScheme pricingScheme = new PricingSchemeImpl();
        private final PriceSchedule priceSchedule = new PriceScheduleImpl();

        public PriceBuilder() {
            price.setCurrency(CURRENCY);
            priceSchedule.setType(PriceScheduleType.PURCHASE_TIME);
            price.setPricingScheme(pricingScheme);
        }

        public PriceBuilder addTier(final int quantity, final int price, final String priceListGuid) {
            final Money money = MoneyFactory.createMoney(new BigDecimal(price), CURRENCY);
            this.price.setListPrice(money, quantity);
            this.price.getPriceTierByExactMinQty(quantity).setPriceListGuid(priceListGuid);
            pricingScheme.setPriceForSchedule(priceSchedule, this.price);
            return this;
        }

        public Price newInstance() {
            return price;
        }

    }
}
