package com.elasticpath.service.pricing.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.pricing.service.impl.BaseAmountFilterImpl;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test class for @{PricePopulatorImpl}.
 */
public class PricePopulatorImplTest {
	private static final String GUID = "GUID";
	private static final String SKU = "SKU";
	private static final int TEN = 10;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private BeanFactoryExpectationsFactory expectationsFactory;
	private final PricePopulatorImpl pricePopulator = new PricePopulatorImpl();
	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
		pricePopulator.setBeanFactory(beanFactory);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test that if no base amounts are found, Price is null.
	 */
	@Test
	public void testPopulatePriceWithNoBaseAmounts() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.BASE_AMOUNT_FILTER, BaseAmountFilterImpl.class);

		Collection<BaseAmount> amounts = new ArrayList<BaseAmount>();
		Currency currency = Currency.getInstance(Locale.CANADA);
		Price price = beanFactory.getBean(ContextIdNames.PRICE);
		boolean found = pricePopulator.populatePriceFromBaseAmounts(amounts, currency, price);
		assertEquals(found, false);
	}

	/**
	 * Test that if null list price AND sale price are found, Price is null.
	 */
	@Test
	public void testPopulatePriceWithNullBaseAmounts() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.BASE_AMOUNT_FILTER, BaseAmountFilterImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_TIER, PriceTierImpl.class);

		BaseAmount baseAmount = new BaseAmountImpl(GUID, GUID, SKU, BigDecimal.ONE, null, null, GUID);
		Collection<BaseAmount> amounts = new ArrayList<BaseAmount>();
		amounts.add(baseAmount);
		Currency currency = Currency.getInstance(Locale.CANADA);
		Price price = beanFactory.getBean(ContextIdNames.PRICE);
		boolean found = pricePopulator.populatePriceFromBaseAmounts(amounts, currency, price);
		assertEquals(found, false);
	}

	/**
	 * Test the price object does not contain a price tier for null prices.
	 */
	@Test
	public void testPopulatePriceWithNullPrices() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.BASE_AMOUNT_FILTER, BaseAmountFilterImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_TIER, PriceTierImpl.class);

		Collection<BaseAmount> amounts = new ArrayList<BaseAmount>();
		amounts.add(createBaseAmount("PL", "1", "9.99", "4.99", "PROD",
				BaseAmountObjectType.PRODUCT.toString()));

		amounts.add(new BaseAmountImpl(GUID, GUID, SKU, BigDecimal.TEN, null, null, GUID));

		Currency currency = Currency.getInstance(Locale.CANADA);
		Price price = beanFactory.getBean(ContextIdNames.PRICE);
		boolean found = pricePopulator.populatePriceFromBaseAmounts(amounts, currency, price);
		assertEquals(found, true);
		assertEquals(1, price.getPriceTiers().size());
	}
	/**
	 * Test the price object is populated by the base amounts.
	 */
	@Test
	public void testPopulatePrice() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.BASE_AMOUNT_FILTER, BaseAmountFilterImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_TIER, PriceTierImpl.class);

		Collection<BaseAmount> amounts = new ArrayList<BaseAmount>();
		amounts.add(createBaseAmount("PL", "1", "9.99", "4.99", "PROD",
				BaseAmountObjectType.PRODUCT.toString()));
		amounts.add(createBaseAmount("PL", "5", "8.99", "3.99", "PROD",
				BaseAmountObjectType.PRODUCT.toString()));

		Currency currency = Currency.getInstance(Locale.CANADA);
		Price price = beanFactory.getBean(ContextIdNames.PRICE);
		boolean found = pricePopulator.populatePriceFromBaseAmounts(amounts, currency, price);
		assertEquals(found, true);
		assertEquals(2, price.getPriceTiers().size());

		assertEquals(MoneyFactory.createMoney(new BigDecimal("9.99"), currency), price
				.getListPrice(1));
		assertEquals(MoneyFactory.createMoney(new BigDecimal("4.99"), currency), price
				.getSalePrice(1));

		assertEquals(MoneyFactory.createMoney(new BigDecimal("8.99"), currency), price
				.getListPrice(TEN));
		assertEquals(MoneyFactory.createMoney(new BigDecimal("3.99"), currency), price
				.getSalePrice(TEN));
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

}
