package com.elasticpath.service.tax.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.domain.tax.TaxValue;
import com.elasticpath.domain.tax.impl.TaxCategoryImpl;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.domain.tax.impl.TaxRegionImpl;
import com.elasticpath.domain.tax.impl.TaxValueImpl;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Replacement class for DefaultTaxCalculationServiceTest which uses JUnit 4 and JMock 2 and removes dependency on ElasticPathTestCase.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class DefaultTaxCalculationServiceImplTest {

	private static final String TEST_SKU_CODE = "00MYCODE";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final BigDecimal SHIPPING_TAXES = new BigDecimal("1.04");

	private static final BigDecimal SEVEN_PERCENT_DECIMAL = new BigDecimal("0.07");

	private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");

	private static final BigDecimal TEN = BigDecimal.TEN.setScale(2);

	private static final BigDecimal SHIPPING_COST = new BigDecimal("8.00");

	private static final BigDecimal TWENTY = new BigDecimal("20.00");

	private static final BigDecimal PRODUCT_PRICE = new BigDecimal("44");

	private static final BigDecimal PST_TAX_PERCENTAGE = new BigDecimal("7");

	private static final BigDecimal GST_TAX_PERCENTAGE = new BigDecimal("6");

	private static final BigDecimal ENV_TAX_RATE = new BigDecimal("0.05");

	private static final BigDecimal VAT_TAX_RATE = new BigDecimal("0.15");

	private static final String PST_TAX_CODE = "PST";

	private static final String GST_TAX_CODE = "GST";

	private static final String VAT_TAX_CODE = "VAT";

	private static final String ENV_TAX_CODE = "ENV";

	private static final String REGION_CODE_CA = "CA";

	private static final String REGION_CODE_BC = "BC";

	private static final String REGION_CODE_GB = "GB";

	private static final String CAD = "CAD";

	private static final String SALES_TAX_CODE_GOODS = "GOODS";

	private static final Currency CA_CURRENCY = Currency.getInstance(CAD);

	private static final Currency GB_CURRENCY = Currency.getInstance("GBP");

	private DefaultTaxCalculationServiceImpl taxCalculationService;

	private TaxCalculationResult inclusiveTaxCalculationResult;

	private TaxCalculationResult exclusiveTaxCalculationResult;

	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory expectationsFactory;
	private StoreService storeService;
	private Store inclusiveStore, exclusiveStore;


	/**
	 * Testing override to get rid of bean factory calls.
	 */
	protected class TestTaxCategoryImpl extends TaxCategoryImpl {

		private static final long serialVersionUID = 1L;

		@Override
		public String getDisplayName(final Locale locale) {
			return getName();
		}

		@Override
		public LocalizedProperties getLocalizedProperties() {
			return new LocalizedPropertiesImpl();
		}
	}

	/**
	 * Initialise commonly used variables.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		inclusiveStore = createInclusiveStore();
		exclusiveStore = createExclusiveStore();
		storeService = context.mock(StoreService.class);
		context.checking(new Expectations() { {
			allowing(storeService).findStoreWithCode(inclusiveStore.getCode()); will(returnValue(inclusiveStore));
			allowing(storeService).findStoreWithCode(exclusiveStore.getCode()); will(returnValue(exclusiveStore));
		} });

		taxCalculationService = new DefaultTaxCalculationServiceImpl();
		taxCalculationService.setStoreService(storeService);

		inclusiveTaxCalculationResult = createTaxCalculationResult(GB_CURRENCY);
		exclusiveTaxCalculationResult = createTaxCalculationResult(CA_CURRENCY);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test that shipping cost is not added to the total amount of taxes if SHIPPING isn't one of the active taxes.
	 */
	@Test
	public void testInactiveShippingTaxCalculation() {
		exclusiveTaxCalculationResult.setDefaultCurrency(CA_CURRENCY);
		taxCalculationService.calculateShippingCostTaxAndBeforeTaxPrice(new HashSet<String>(),
				MoneyFactory.createMoney(PRODUCT_PRICE, CA_CURRENCY),
				CA_CURRENCY,
				null,
				exclusiveTaxCalculationResult);
		assertEquals(MoneyFactory.createMoney(BigDecimal.ZERO, CA_CURRENCY), exclusiveTaxCalculationResult.getShippingTax());
	}

	/**
	 * Test that the tax is correctly factored out of a price that includes tax, and the result is rounded up to 10 decimals.
	 */
	@Test
	public void testGetPretaxAmountFromInclusiveTaxAmount() {
		BigDecimal amountIncludingTax = ONE_HUNDRED;
		BigDecimal decimalTaxRate = SEVEN_PERCENT_DECIMAL;
		BigDecimal amountBeforeTax = new BigDecimal("93.4579439252");
		assertEquals(amountBeforeTax, taxCalculationService.getPretaxAmountFromInclusiveTaxAmount(amountIncludingTax, decimalTaxRate));
	}

	/**
	 * Test that tax rates are properly converted from percentages to decimals.
	 */
	@Test
	public void testGetDecimalTaxRate() {
		final BigDecimal taxPercentage = new BigDecimal("7.5");
		BigDecimal taxDecimal = new BigDecimal("0.0750000000");
		final TaxRegion mockTaxRegion = context.mock(TaxRegion.class);
		context.checking(new Expectations() {
			{
				oneOf(mockTaxRegion).getTaxRate(with("someTaxCode"));
				will(returnValue(taxPercentage));
			}
		});
		assertEquals(taxDecimal, taxCalculationService.getDecimalTaxRate(mockTaxRegion, "someTaxCode"));
	}

	/**
	 * Simple test of the calculate tax method of DefaultTaxCalculationService.
	 */
	@Test
	public void testCalculateTaxSimple() {
		final Money inputMoney = MoneyFactory.createMoney(TEN, CA_CURRENCY);
		final Money expectedTax = MoneyFactory.createMoney(new BigDecimal("0.50"), CA_CURRENCY);
		Money calculatedTax = taxCalculationService.calculateTax(inputMoney.getAmount(), ENV_TAX_RATE, CA_CURRENCY);
		assertEquals(expectedTax, calculatedTax);
	}

	/**
	 * Simple test of the calculate tax method of DefaultTaxCalculationService with zero amount.
	 */
	@Test
	public void testCalculateTaxZeroAmount() {
		final Money inputMoney = MoneyFactory.createMoney(BigDecimal.ZERO, CA_CURRENCY);

		final Money expectedInclusiveTax = MoneyFactory.createMoney(BigDecimal.ZERO, CA_CURRENCY);
		final Money calculatedTax = taxCalculationService.calculateTax(inputMoney.getAmount(), ENV_TAX_RATE, CA_CURRENCY);
		assertEquals(expectedInclusiveTax, calculatedTax);
	}

	/**
	 * Simple test of the calculate tax method of DefaultTaxCalculationService with zero amount.
	 */
	@Test
	public void testCalculateTaxZeroRate() {
		final Money inputMoney = MoneyFactory.createMoney(TEN, CA_CURRENCY);

		final Money expectedInclusiveTax = MoneyFactory.createMoney(BigDecimal.ZERO, CA_CURRENCY);
		final Money calculatedTax = taxCalculationService.calculateTax(inputMoney.getAmount(), BigDecimal.ZERO, CA_CURRENCY);
		assertEquals(expectedInclusiveTax, calculatedTax);
	}

	/**
	 * Test calculateLineItemTaxes calculation when we have multiple inclusive tax rates on a single line item.
	 */
	@Test
	public void testCalculateLineItemTaxesWithMultipleInclusive() {
		final TaxJurisdiction inclusiveTaxJurisdiction = mockInclusiveMultipleTaxJurisdiction();

		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);

		final Money lineItemPrice = MoneyFactory.createMoney(PRODUCT_PRICE, GB_CURRENCY);
		final Money lineItemTaxes = taxCalculationService.calculateShoppingItemTaxes(SALES_TAX_CODE_GOODS,
				GB_CURRENCY,
				inclusiveTaxJurisdiction,
				inclusiveTaxCalculationResult,
				shoppingItem,
				lineItemPrice);
		final Money expectedLineItemTaxes = MoneyFactory.createMoney(new BigDecimal("7.33"), GB_CURRENCY);
		assertEquals(expectedLineItemTaxes, lineItemTaxes);

		// test the individual tax category values calculated
		final Money vatTax = MoneyFactory.createMoney(new BigDecimal("5.50"), GB_CURRENCY);
		final Money envTax = MoneyFactory.createMoney(new BigDecimal("1.83"), GB_CURRENCY);
		final Map<TaxCategory, Money> taxMap = inclusiveTaxCalculationResult.getTaxMap();

		for (TaxCategory taxCategory : taxMap.keySet()) {
			if (taxCategory.equals(getGbVatTax())) {
				assertEquals("VAT tax must be calculated correctly.", vatTax, taxMap.get(taxCategory));

			} else if (taxCategory.equals(getGbEnvironmentalTax())) {
				assertEquals("ENV tax must be calculated correctly.", envTax, taxMap.get(taxCategory));

			} else {
				fail("Only VAT and ENV taxes expected. Unexpected tax category found: " + taxCategory.getName());
			}
		}
	}

	/**
	 * Tests calculateTaxesAndAddToResult() by passing invalid (null) parameter.
	 */
	@Test
	public void testCalculateTaxesAndAddToResultWithInvalidParameter() {
		try {
			taxCalculationService.calculateTaxesAndAddToResult(exclusiveTaxCalculationResult, null, null, null, null, null, null);
			fail("should throw exception if an invalid parameter is passed");
		} catch (EpServiceException exc) {
			assertNotNull(exc);
		}
	}

	/**
	 * Test that when a null address is passed in, no tax calculations are performed.
	 */
	@Test
	public void testCalculateTaxesWithNullAddressReturnsEmptyResult() {
		DefaultTaxCalculationServiceImpl service = new DefaultTaxCalculationServiceImpl() {
			@Override
			protected Money getMoneyZero(final Currency currency) {
				return MoneyFactory.createMoney(BigDecimal.ZERO, currency);
			}

			@Override
			protected TaxJurisdiction findTaxJurisdiction(final Store store, final Address address) {
				return null;
			}
		};
		service.setStoreService(storeService);

		Money shippingCost = MoneyFactory.createMoney(BigDecimal.ZERO, CA_CURRENCY);
		Money discount = MoneyFactory.createMoney(BigDecimal.ZERO, CA_CURRENCY);

		TaxCalculationResult result = service.calculateTaxesAndAddToResult(exclusiveTaxCalculationResult,
				inclusiveStore.getCode(),
				null,
				CA_CURRENCY,
				shippingCost,
				new ArrayList<ShoppingItem>(),
				discount);

		final TaxCalculationResult emptyResult =  createTaxCalculationResult(CA_CURRENCY);

		assertEquals(result, emptyResult);
	}

	/**
	 * Test single inclusive tax calculation from the top with discount.
	 */
	@Test
	public void testCalculateInclusiveTaxesFromTopWithDiscount() {
		final BigDecimal itemTaxes = new BigDecimal("11.74");
		final BigDecimal discount = BigDecimal.TEN;
		testCalculateInclusiveTaxesFromTop(discount, ONE_HUNDRED, SHIPPING_COST, itemTaxes, SHIPPING_TAXES);
	}

	/**
	 * Test single inclusive taxes using highest level method.
	 */
	@Test
	public void testCalculateInclusiveTaxesFromTop() {
		final BigDecimal itemTaxes = new BigDecimal("13.04");

		testCalculateInclusiveTaxesFromTop(BigDecimal.ZERO, ONE_HUNDRED, SHIPPING_COST, itemTaxes, SHIPPING_TAXES);
	}

	private void testCalculateInclusiveTaxesFromTop(final BigDecimal discount,
			final BigDecimal itemPrice,
			final BigDecimal shippingCost,
			final BigDecimal itemTax,
			final BigDecimal shippingTax) {
		taxCalculationService.setTaxJurisdictionService(new TaxJurisdictionServiceImpl() {
			@Override
			public TaxJurisdiction retrieveEnabledInStoreTaxJurisdiction(final Store store, final Address address) throws EpServiceException {
				return mockInclusiveTaxJurisdiction();
			}
		});

		final Money lineItemPriceMoney = MoneyFactory.createMoney(itemPrice, GB_CURRENCY);
		final Money shippingCostMoney = MoneyFactory.createMoney(shippingCost, GB_CURRENCY);
		final Money discountMoney = MoneyFactory.createMoney(discount, GB_CURRENCY);

		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);

		final ProductSku productSku = createTestProductSku(TEST_SKU_CODE, SALES_TAX_CODE_GOODS);
		prepareExpectaionsOnTaxCalculations(lineItemPriceMoney, shoppingItem, productSku);

		Collection<? extends ShoppingItem> lineItems = Arrays.asList(shoppingItem);

		final TaxCalculationResult result = taxCalculationService.calculateTaxesAndAddToResult(inclusiveTaxCalculationResult,
				inclusiveStore.getCode(),
				getUkAddress(),
				GB_CURRENCY,
				shippingCostMoney,
				lineItems,
				discountMoney);

		assertEquals(itemTax, result.getTotalItemTax().getAmount());
		assertEquals(shippingTax, result.getShippingTax().getAmount());
		assertEquals(lineItemPriceMoney, result.getSubtotal());
		assertEquals(shippingCostMoney.subtract(MoneyFactory.createMoney(shippingTax, GB_CURRENCY)), result.getBeforeTaxShippingCost());
		assertEquals(shippingTax.add(itemTax), result.getTotalTaxes().getAmount());
		assertEquals(lineItemPriceMoney.getAmount().subtract(itemTax), result.getBeforeTaxSubTotal().getAmount());
	}

	private void prepareExpectaionsOnTaxCalculations(final Money lineItemPriceMoney, final ShoppingItem shoppingItem, final ProductSku productSku) {
		context.checking(new Expectations() {
			{
				allowing(shoppingItem).getTotal();
				will(returnValue(lineItemPriceMoney));
				allowing(shoppingItem).getProductSku();
				will(returnValue(productSku));
				allowing(shoppingItem).getGuid();
				will(returnValue(TEST_SKU_CODE));
				allowing(shoppingItem).getListUnitPrice();
				will(returnValue(lineItemPriceMoney));
				atLeast(0).of(shoppingItem).hasBundleItems();
				will(returnValue(false));
				allowing(shoppingItem).isBundle();
				allowing(shoppingItem).isDiscountable();
				will(returnValue(true));
			}
		});
	}

	/**
	 * Test calculation for multiple inclusive taxes from highest level method.
	 */
	@Test
	public void testCalculateInclusiveMultipleTaxesFromTop() {
		final BigDecimal itemTaxes = new BigDecimal("16.67");
		final BigDecimal shippingTaxes = new BigDecimal("1.33");

		testCalculateInclusiveTwoTaxesFromTop(BigDecimal.ZERO, ONE_HUNDRED, SHIPPING_COST, itemTaxes, shippingTaxes);
	}

	/**
	 * Test calculation for multiple inclusive taxes from highest level method with a discount applied.
	 */
	@Test
	public void testCalculateInclusiveMultipleTaxesWithDiscountFromTop() {
		final BigDecimal itemTaxes = new BigDecimal("15.00");
		final BigDecimal shippingTaxes = new BigDecimal("1.33");

		final BigDecimal discount = BigDecimal.TEN;
		testCalculateInclusiveTwoTaxesFromTop(discount, ONE_HUNDRED, SHIPPING_COST, itemTaxes, shippingTaxes);
	}

	private void testCalculateInclusiveTwoTaxesFromTop(final BigDecimal discount,
			final BigDecimal itemPrice,
			final BigDecimal shippingCost,
			final BigDecimal itemTaxes,
			final BigDecimal shippingTaxes) {

		taxCalculationService.setTaxJurisdictionService(new TaxJurisdictionServiceImpl() {
			@Override
			public TaxJurisdiction retrieveEnabledInStoreTaxJurisdiction(final Store store, final Address address) throws EpServiceException {
				return mockInclusiveMultipleTaxJurisdiction();
			}
		});
		final Money lineItemPriceMoney = MoneyFactory.createMoney(itemPrice, GB_CURRENCY);
		final Money shippingCostMoney = MoneyFactory.createMoney(shippingCost, GB_CURRENCY);
		final Money discountMoney = MoneyFactory.createMoney(discount, GB_CURRENCY);

		final ShoppingItem lineItem = context.mock(ShoppingItem.class);

		final ProductSku productSku = createTestProductSku(TEST_SKU_CODE, SALES_TAX_CODE_GOODS);
		prepareExpectaionsOnTaxCalculations(lineItemPriceMoney, lineItem, productSku);

		Collection<? extends ShoppingItem> lineItems = Arrays.asList(lineItem);

		final TaxCalculationResult result = taxCalculationService.calculateTaxesAndAddToResult(inclusiveTaxCalculationResult,
				inclusiveStore.getCode(),
				getUkAddress(),
				GB_CURRENCY,
				shippingCostMoney,
				lineItems,
				discountMoney);

		assertEquals(itemTaxes, result.getTotalItemTax().getAmount());
		assertEquals(shippingTaxes, result.getShippingTax().getAmount());
		assertEquals(lineItemPriceMoney, result.getSubtotal());
		assertEquals(shippingCostMoney.subtract(MoneyFactory.createMoney(shippingTaxes, GB_CURRENCY)), result.getBeforeTaxShippingCost());
		assertEquals(shippingTaxes.add(itemTaxes), result.getTotalTaxes().getAmount());
		assertEquals(lineItemPriceMoney.getAmount().subtract(itemTaxes), result.getBeforeTaxSubTotal().getAmount());
	}

	/**
	 * Test exclusive tax calculation from high level calculation method including a discount and verify the tax calculation result values are as
	 * expected.
	 */
	@Test
	public void testCalculationExclusiveTaxesWithDiscountFromTop() {
		final BigDecimal itemTaxes = new BigDecimal("11.70");
		final BigDecimal shippingTaxes = SHIPPING_TAXES;
		final BigDecimal discount = TEN;

		testCalculateExclusiveTaxesFromTop(discount, ONE_HUNDRED, SHIPPING_COST, itemTaxes, shippingTaxes, SALES_TAX_CODE_GOODS);
	}

	/**
	 * Test exclusive tax calculation from high level calculation method and verify the tax calculation result values are as expected.
	 */
	@Test
	public void testCalculateExclusiveTaxesFromTop() {
		final BigDecimal itemTaxes = new BigDecimal("13.00");
		final BigDecimal shippingTaxes = SHIPPING_TAXES;

		testCalculateExclusiveTaxesFromTop(BigDecimal.ZERO, ONE_HUNDRED, SHIPPING_COST, itemTaxes, shippingTaxes, SALES_TAX_CODE_GOODS);
	}

	private void testCalculateExclusiveTaxesFromTop(final BigDecimal discount,
			final BigDecimal itemPrice,
			final BigDecimal shippingCost,
			final BigDecimal itemTaxes,
			final BigDecimal shippingTaxes,
			final String productTaxCode) {

		taxCalculationService.setTaxJurisdictionService(new TaxJurisdictionServiceImpl() {
			@Override
			public TaxJurisdiction retrieveEnabledInStoreTaxJurisdiction(final Store store, final Address address) throws EpServiceException {
				return mockExclusiveTaxJurisdiction();
			}
		});
		final Money lineItemPriceMoney = MoneyFactory.createMoney(itemPrice, CA_CURRENCY);
		final Money shippingCostMoney = MoneyFactory.createMoney(shippingCost, CA_CURRENCY);
		final Money discountMoney = MoneyFactory.createMoney(discount, CA_CURRENCY);

		final ShoppingItem lineItem = context.mock(ShoppingItem.class);

		final ProductSku productSku = createTestProductSku(TEST_SKU_CODE, productTaxCode);
		prepareExpectaionsOnTaxCalculations(lineItemPriceMoney, lineItem, productSku);

		Collection<? extends ShoppingItem> lineItems = Arrays.asList(lineItem);

		final TaxCalculationResult result = taxCalculationService.calculateTaxesAndAddToResult(exclusiveTaxCalculationResult,
				exclusiveStore.getCode(),
				getCaAddress(),
				CA_CURRENCY,
				shippingCostMoney,
				lineItems,
				discountMoney);

		assertEquals(itemTaxes, result.getTotalItemTax().getAmount());
		assertEquals(shippingTaxes, result.getShippingTax().getAmount());
		assertEquals(lineItemPriceMoney, result.getSubtotal());
		assertEquals(shippingCostMoney, result.getBeforeTaxShippingCost());
		assertEquals(shippingTaxes.add(itemTaxes), result.getTotalTaxes().getAmount());
		assertEquals(lineItemPriceMoney.getAmount(), result.getBeforeTaxSubTotal().getAmount());
	}

	/**
	 * Test the shipping tax calculation for a single inclusive tax.
	 */
	@Test
	public void testShippingInclusiveTaxCalculation() {
		inclusiveTaxCalculationResult.setTaxInclusive(true);
		final TaxJurisdiction taxJurisdiction = mockInclusiveTaxJurisdiction();
		final Money shippingCost = MoneyFactory.createMoney(SHIPPING_COST, GB_CURRENCY);
		final BigDecimal expectedShippingTaxes = SHIPPING_TAXES;
		Set<String> activeTaxCodeNames = getActiveInclusiveTaxCodeNames();

		taxCalculationService.calculateShippingCostTaxAndBeforeTaxPrice(activeTaxCodeNames,
				shippingCost,
				GB_CURRENCY,
				taxJurisdiction,
				inclusiveTaxCalculationResult);

		assertEquals(expectedShippingTaxes, inclusiveTaxCalculationResult.getShippingTax().getAmount());
	}

	/**
	 * Test the shipping tax calculation for multiple inclusive taxes.
	 */
	@Test
	public void testShippingMultipleInclusiveTaxCalcuation() {
		inclusiveTaxCalculationResult.setTaxInclusive(true);
		final TaxJurisdiction taxJurisdiction = mockInclusiveMultipleTaxJurisdiction();
		final Money shippingCost = MoneyFactory.createMoney(SHIPPING_COST, GB_CURRENCY);
		final BigDecimal expectedShippingTaxes = new BigDecimal("1.33");
		Set<String> activeTaxCodeNames = getActiveInclusiveTaxCodeNames();

		taxCalculationService.calculateShippingCostTaxAndBeforeTaxPrice(activeTaxCodeNames,
				shippingCost,
				GB_CURRENCY,
				taxJurisdiction,
				inclusiveTaxCalculationResult);

		assertEquals(expectedShippingTaxes, inclusiveTaxCalculationResult.getShippingTax().getAmount());
	}

	/**
	 * Given a product price and one tax category (VAT for example), calculate the amount of tax included in the inclusive price of the line item.
	 */
	@Test
	public void testGetLineItemInclusiveTaxSingleTax() {
		final BigDecimal gbTestPrice = TWENTY;

		// test tax calculation with a single tax category
		TaxJurisdiction inclusiveTaxJurisdiction = mockInclusiveTaxJurisdiction();

		BigDecimal sumOfTaxRates = taxCalculationService.getTotalDecimalTaxRate(SALES_TAX_CODE_GOODS, inclusiveTaxJurisdiction);

		BigDecimal lineItemInclusiveTax = taxCalculationService.getTaxIncludedInPrice(sumOfTaxRates, VAT_TAX_RATE, gbTestPrice);
		BigDecimal expectedTaxRateAmount = new BigDecimal("2.6086956522");
		assertEquals("Incorrect line item tax calculated.", expectedTaxRateAmount, lineItemInclusiveTax);
	}

	/**
	 * Given a product price and multiple tax categories (VAT and environmental for example), calculate the amount of tax included in the inclusive
	 * price of the line item for a single tax.
	 */
	@Test
	public void testGetLineItemInclusiveTaxMultipleTaxes() {
		final BigDecimal gbTestPrice = TWENTY;

		// now test two tax categories
		TaxJurisdiction inclusiveTaxJurisdiction = mockInclusiveMultipleTaxJurisdiction();

		BigDecimal sumOfTaxRates = taxCalculationService.getTotalDecimalTaxRate(SALES_TAX_CODE_GOODS, inclusiveTaxJurisdiction);

		BigDecimal lineItemInclusiveTax = taxCalculationService.getTaxIncludedInPrice(sumOfTaxRates, ENV_TAX_RATE, gbTestPrice);
		BigDecimal expectedTaxRateAmount = new BigDecimal("0.8333333333");
		assertEquals("Incorrect line item tax calculated.", expectedTaxRateAmount, lineItemInclusiveTax);

	}

	/**
	 * Given a tax jurisdiction and a tax code find the sum of the applicable tax rates.
	 */
	@Test
	public void testGetTotalDecimalTaxRateMultipleInclusive() {
		TaxJurisdiction inclusiveTaxJurisdiction = mockInclusiveMultipleTaxJurisdiction();

		BigDecimal sumOfTaxRates = taxCalculationService.getTotalDecimalTaxRate(SALES_TAX_CODE_GOODS, inclusiveTaxJurisdiction);
		BigDecimal expectedSumOfTaxRates = new BigDecimal("0.20");
		assertEquals("Incorrect sum of tax rates.", expectedSumOfTaxRates, sumOfTaxRates.setScale(2));
	}

	/**
	 * Given a tax jurisdiction and a tax code find the sum of the applicable tax rates.
	 */
	@Test
	public void testGetTotalDecimalTaxRateMultipleExclusive() {
		TaxJurisdiction exclusiveTaxJurisdiction = mockExclusiveTaxJurisdiction();

		BigDecimal sumOfTaxRates = taxCalculationService.getTotalDecimalTaxRate(SALES_TAX_CODE_GOODS, exclusiveTaxJurisdiction);
		BigDecimal expectedSumOfTaxRates = new BigDecimal("0.13");
		assertEquals("Incorrect sum of tax rates.", expectedSumOfTaxRates, sumOfTaxRates.setScale(2));
	}

	// Begin helper methods

	/**
	 * When a tax code is disabled on the store level, no product should have the tax applied.
	 */
	@Test
	public void testCalculateTaxesWithNonStoreEnabledTax() {
		final BigDecimal zeroCurrencyScale = BigDecimal.ZERO.setScale(2);
		testCalculateExclusiveTaxesFromTopUnusedTaxCode(zeroCurrencyScale,
				ONE_HUNDRED,
				SHIPPING_COST,
				zeroCurrencyScale,
				SHIPPING_TAXES,
				"NOTAXCODE");
	}

	private void testCalculateExclusiveTaxesFromTopUnusedTaxCode(final BigDecimal discount,
			final BigDecimal itemPrice,
			final BigDecimal shippingCost,
			final BigDecimal itemTaxes,
			final BigDecimal shippingTaxes,
			final String productTaxCode) {

		taxCalculationService.setTaxJurisdictionService(new TaxJurisdictionServiceImpl() {
			@Override
			public TaxJurisdiction retrieveEnabledInStoreTaxJurisdiction(final Store store, final Address address) throws EpServiceException {
				return mockExclusiveTaxJurisdiction();
			}
		});
		final Money lineItemPriceMoney = MoneyFactory.createMoney(itemPrice, CA_CURRENCY);
		final Money shippingCostMoney = MoneyFactory.createMoney(shippingCost, CA_CURRENCY);
		final Money discountMoney = MoneyFactory.createMoney(discount, CA_CURRENCY);

		final ShoppingItem lineItem = context.mock(ShoppingItem.class);

		final ProductSku productSku = createTestProductSku(TEST_SKU_CODE, productTaxCode);
		prepareExpectaionsOnTaxCalculations(lineItemPriceMoney, lineItem, productSku);

		Collection<? extends ShoppingItem> lineItems = Arrays.asList(lineItem);

		final TaxCalculationResult result = taxCalculationService.calculateTaxesAndAddToResult(exclusiveTaxCalculationResult,
				exclusiveStore.getCode(),
				getCaAddress(),
				CA_CURRENCY,
				shippingCostMoney,
				lineItems,
				discountMoney);

		assertEquals(itemTaxes, result.getTotalItemTax().getAmount());
		assertEquals(shippingTaxes, result.getShippingTax().getAmount());
		assertEquals(lineItemPriceMoney, result.getSubtotal());
		assertEquals(shippingCostMoney, result.getBeforeTaxShippingCost());
		assertEquals(shippingTaxes.add(itemTaxes), result.getTotalTaxes().getAmount());
		assertEquals(lineItemPriceMoney.getAmount(), result.getBeforeTaxSubTotal().getAmount());
	}

	// Helper methods

	private TaxCalculationResultImpl createTaxCalculationResult(final Currency currency) {
		TaxCalculationResultImpl result = new TaxCalculationResultImpl() {

			private static final long serialVersionUID = 1L;

			@Override
			Money getMoneyZero() {
				return MoneyFactory.createMoney(BigDecimal.ZERO, currency);
			}
		};
		result.setDefaultCurrency(currency);
		return result;
	}

	private Set<String> getActiveInclusiveTaxCodeNames() {
		Set<TaxCode> activeTaxCodes = getActiveInclusiveTaxCodes();
		Set<String> activeTaxCodeNames = new HashSet<String>();
		for (TaxCode taxCode : activeTaxCodes) {
			activeTaxCodeNames.add(taxCode.getCode());
		}
		return activeTaxCodeNames;
	}

	private ProductSku createTestProductSku(final String skuCode, final String productTaxCode) {
		final ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(skuCode);
		Product product = new ProductImpl();
		TaxCode taxCode = new TaxCodeImpl();
		taxCode.setCode(productTaxCode);
		product.setTaxCode(taxCode);
		productSku.setProduct(product);
		return productSku;
	}

	private TaxJurisdiction mockExclusiveTaxJurisdiction() {
		return mockTaxJurisdiction(TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE, getExclusiveTaxCategories());
	}

	private TaxJurisdiction mockInclusiveMultipleTaxJurisdiction() {
		final Set<TaxCategory> inclusiveTaxCategories = getInclusiveTaxCategories();
		inclusiveTaxCategories.add(getGbEnvironmentalTax());
		return mockTaxJurisdiction(TaxJurisdiction.PRICE_CALCULATION_INCLUSIVE, inclusiveTaxCategories);
	}

	private TaxJurisdiction mockInclusiveTaxJurisdiction() {
		return mockTaxJurisdiction(TaxJurisdiction.PRICE_CALCULATION_INCLUSIVE, getInclusiveTaxCategories());
	}

	private TaxJurisdiction mockTaxJurisdiction(final boolean inclusive, final Set<TaxCategory> taxCategories) {
		final TaxJurisdiction taxJurisdiction = context.mock(TaxJurisdiction.class);
		context.checking(new Expectations() {
			{
				allowing(taxJurisdiction).getPriceCalculationMethod();
				will(returnValue(inclusive));

				allowing(taxJurisdiction).getTaxCategorySet();
				will(returnValue(taxCategories));
			}
		});

		return taxJurisdiction;
	}

	private Set<TaxCategory> getExclusiveTaxCategories() {
		Set<TaxCategory> taxCategories = new HashSet<TaxCategory>();
		taxCategories.add(getCaGSTTax());
		taxCategories.add(getCaPSTTax());
		return taxCategories;
	}

	private Set<TaxCategory> getInclusiveTaxCategories() {
		Set<TaxCategory> taxCategories = new HashSet<TaxCategory>();
		taxCategories.add(getGbVatTax());
		return taxCategories;
	}

	private TaxCategory getCaGSTTax() {
		return createTaxCategory(GST_TAX_CODE, TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY, REGION_CODE_CA, GST_TAX_PERCENTAGE);
	}

	private TaxCategory getCaPSTTax() {
		return createTaxCategory(PST_TAX_CODE, TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY, REGION_CODE_BC, PST_TAX_PERCENTAGE);
	}

	private TaxCategory getGbVatTax() {
		return createTaxCategory(VAT_TAX_CODE, TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY, REGION_CODE_GB, new BigDecimal("15.00"));
	}

	private TaxCategory createTaxCategory(final String name,
			final TaxCategoryTypeEnum fieldMatchType,
			final String regionName,
			final BigDecimal rate) {
		TaxCategory taxCategory = new TestTaxCategoryImpl();

		taxCategory.setFieldMatchType(fieldMatchType);
		taxCategory.setName(name);

		TaxRegion taxRegion = new TaxRegionImpl();

		Map<String, TaxValue> taxValueMap = new HashMap<String, TaxValue>();

		TaxValue taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_GOODS));
		taxValue.setTaxValue(rate);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxValue.setTaxValue(rate);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);
		taxRegion.setRegionName(regionName);

		taxCategory.addTaxRegion(taxRegion);

		return taxCategory;
	}

	/**
	 * Create an inclusive tax category for an environmental tax in GB. Used to test multiple inclusive taxes.
	 */
	private TaxCategory getGbEnvironmentalTax() {
		TaxCategory taxCategory = new TestTaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY);
		taxCategory.setName(ENV_TAX_CODE);

		final TaxRegion taxRegion = new TaxRegionImpl();

		Map<String, TaxValue> taxValueMap = new HashMap<String, TaxValue>();

		TaxValue taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_GOODS));
		taxValue.setTaxValue(new BigDecimal("5"));

		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxValue.setTaxValue(new BigDecimal("5"));
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);
		taxRegion.setRegionName(REGION_CODE_GB);

		taxCategory.addTaxRegion(taxRegion);

		return taxCategory;
	}

	private static TaxCode createTaxCode(final String taxCodeName) {
		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setCode(taxCodeName);
		taxCode.setGuid(System.currentTimeMillis() + taxCodeName);
		return taxCode;
	}

	private Store createInclusiveStore() {
		return createStore("inclusive", getActiveInclusiveTaxCodes(), CA_CURRENCY);
	}

	private Store createExclusiveStore() {
		return createStore("exclusive", getActiveExclusiveTaxCodes(), CA_CURRENCY);
	}

	private Store createStore(final String storeCode, final Set<TaxCode> taxCodes, final Currency currency) {
		Store store = new StoreImpl();
		store.setCode(storeCode);
		store.setTaxCodes(taxCodes);
		store.setDefaultCurrency(currency);
		return store;
	}

	private Set<TaxCode> getActiveInclusiveTaxCodes() {
		Set<TaxCode> taxCodes = new HashSet<TaxCode>();
		taxCodes.add(createTaxCode(SALES_TAX_CODE_GOODS));
		taxCodes.add(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxCodes.add(createTaxCode(VAT_TAX_CODE));
		return taxCodes;
	}

	private Set<TaxCode> getActiveExclusiveTaxCodes() {
		Set<TaxCode> taxCodes = new HashSet<TaxCode>();
		taxCodes.add(createTaxCode(SALES_TAX_CODE_GOODS));
		taxCodes.add(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxCodes.add(createTaxCode(GST_TAX_CODE));
		taxCodes.add(createTaxCode(PST_TAX_CODE));
		return taxCodes;
	}

	private Address getCaAddress() {
		Address address = new CustomerAddressImpl();
		address.setFirstName("Joe");
		address.setLastName("Doe");
		address.setCountry("CA");
		address.setStreet1("1295 Charleston Road");
		address.setCity("Mountain View");
		address.setSubCountry("BC");
		address.setZipOrPostalCode("94043");

		return address;
	}

	private Address getUkAddress() {
		Address address = new CustomerAddressImpl();
		address.setFirstName("Joe");
		address.setLastName("Doe");
		address.setCountry("UK");
		address.setStreet1("1295 Charing Cross Road");
		address.setCity("London");
		address.setZipOrPostalCode("V2T 1E4");

		return address;
	}
}
