/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping.impl;


import static com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum.COST_PER_UNIT_WEIGHT;
import static com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum.FIXED_BASE;
import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.aCostCalculationParam;
import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.someCalculationParams;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/** Test cases for <code>FixedBaseAndCostPerUnitWeightMethodImpl</code>. */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class FixedBaseAndCostPerUnitWeightMethodImplTest {

	private static final Currency CURRENCY_CAD = Currency.getInstance("CAD");
	private static final Currency CURRENCY_USD = Currency.getInstance("USD");

	private static final BigDecimal FIXED_BASE_VAL = new BigDecimal("5.0");

	private static final BigDecimal COST_PER_UNIT_WEIGHT_VAL = new BigDecimal("5.0");

	private static final int QTY_1 = 1;
	private static final int QTY_5 = 5;

	private static final boolean SHIPPABLE = true;
	private static final boolean NON_SHIPPABLE = false;

	private FixedBaseAndCostPerUnitWeightMethodImpl shippingCostCalculationMethod;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepare for each test.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		expectationsFactory.
			allowingBeanFactoryGetBean(ContextIdNames.SHIPPING_COST_CALCULATION_PARAMETER, ShippingCostCalculationParameterImpl.class);

		shippingCostCalculationMethod = new FixedBaseAndCostPerUnitWeightMethodImpl();
		this.setDefaultParameterSet(this.shippingCostCalculationMethod);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedBaseAndCostPerUnitWeightMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetDisplayText() {
		assertNotNull(this.shippingCostCalculationMethod.getDisplayText());
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedBaseAndCostPerUnitWeightMethodImpl.getMethodType()'.
	 */
	@Test
	public void testGetType() {
		assertEquals(this.shippingCostCalculationMethod.getType(), (this.shippingCostCalculationMethod)
				.getMethodType());
	}

	/**
	 * Test that two shippable items have the right price applied once.
	 */
	@Test
	public void testCalculateShippingCostThreeShippableItems() {

		List<ShoppingItem> items = items(
				shoppingItem(QTY_1, SHIPPABLE),
				shoppingItem(QTY_1, SHIPPABLE),
				shoppingItem(QTY_1, SHIPPABLE));

		// Cost: $5.00 + ($5.00 * 3) = $20.00
		assertCost("20.00", items, CURRENCY_CAD);
	}

	/**
	 * Test that with all non-shippable items no shipping charge is incurred.
	 */
	@Test
	public void testCalculateShippingCostAllNonShippable() {

		List<ShoppingItem> items = items(
				shoppingItem(QTY_1, NON_SHIPPABLE),
				shoppingItem(QTY_1, NON_SHIPPABLE));

		assertCost("0.00", items, CURRENCY_CAD);
	}

	/**
	 * Test that when calculating the shipping cost, the fixed base and the cost per unit weight parameter
	 * values are retrieved for the given currency.
	 */
	@Test
	public void testParameterRetrievedForGivenCurrency() {
		BigDecimal fixedBaseUsd = BigDecimal.ONE;
		BigDecimal costPerUnitWeightUsd = BigDecimal.ONE;

		BigDecimal fixedBaseCad = BigDecimal.TEN;
		BigDecimal costPerUnitWeightCad = BigDecimal.TEN;

		this.shippingCostCalculationMethod.setParameters(
				someCalculationParams(
						aCostCalculationParam(FIXED_BASE, fixedBaseUsd, CURRENCY_USD),
						aCostCalculationParam(COST_PER_UNIT_WEIGHT, costPerUnitWeightUsd, CURRENCY_USD),

						aCostCalculationParam(FIXED_BASE, fixedBaseCad, CURRENCY_CAD),
						aCostCalculationParam(COST_PER_UNIT_WEIGHT, costPerUnitWeightCad, CURRENCY_CAD)));


		List<ShoppingItem> items = items(
				shoppingItem(QTY_1, SHIPPABLE),
				shoppingItem(QTY_1, SHIPPABLE),
				shoppingItem(QTY_1, SHIPPABLE));

		assertCost("40.00", items, CURRENCY_CAD);
		assertCost("4.00", items, CURRENCY_USD);
	}

	private void assertCost(final String expectedCost, final List<ShoppingItem> items, final Currency currency) {
		assertEquals(
				new BigDecimal(expectedCost),
				shippingCostCalculationMethod.calculateShippingCost(items, currency).getAmount());
	}

	/**
	 * Test that no items incurs no shipping cost.
	 */
	@Test
	public void testCalculateShippingCostNoItems() {
		assertCost("0.00", items(), CURRENCY_CAD);
	}

	/**
	 * Test that with one shippable and one non-shippable items in the list
	 * the total cost is correct.
	 */
	@Test
	public void testCalculateShippingCostOneNonShippable() {

		List<ShoppingItem> items = items(
				shoppingItem(QTY_1, NON_SHIPPABLE),
				shoppingItem(QTY_5, SHIPPABLE));


		assertCost("30.00", items, CURRENCY_CAD);
	}


	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedBaseAndCostPerUnitWeightMethodImpl.getDefaultParameters(currencyList)'.
	 */
	@Test
	public void testGetDefaultParametersSet() {
		List<Currency> currencyList = new ArrayList<Currency>();
		currencyList.add(CURRENCY_USD);
		currencyList.add(CURRENCY_CAD);
		List<ShippingCostCalculationParameter> calcParams = shippingCostCalculationMethod.getDefaultParameters(currencyList);
		Iterator<ShippingCostCalculationParameter> parameterIter = calcParams.iterator();
		int usdNum = 0, cadNum = 0;
		while (parameterIter.hasNext()) {
			ShippingCostCalculationParameter param = parameterIter.next();
			Currency currency = param.getCurrency();
			assertNotNull(currency);
			if (currency.equals(CURRENCY_USD)) {
				++usdNum;
			} else if (currency.equals(CURRENCY_CAD)) {
				++cadNum;
			} else {
				assertTrue("Unexpected currrency: " + currency, currency.equals(CURRENCY_CAD) || currency.equals(CURRENCY_USD));
			}
		}

		final int expectedParamNumber = 4;

		assertEquals(2, usdNum);
		assertEquals(2, cadNum);
		assertEquals(expectedParamNumber, calcParams.size());
	}

	private void setDefaultParameterSet(final ShippingCostCalculationMethod shippingCostCalculationMethod) {
		shippingCostCalculationMethod.setParameters(
				someCalculationParams(
						aCostCalculationParam(FIXED_BASE, FIXED_BASE_VAL, CURRENCY_CAD),
						aCostCalculationParam(COST_PER_UNIT_WEIGHT, COST_PER_UNIT_WEIGHT_VAL, CURRENCY_CAD)));
	}

	private List<ShoppingItem> items(final ShoppingItem ... items) {
		return Arrays.asList(items);
	}

	private ShoppingItem shoppingItem(final int quantity, final boolean shippable) {
		ProductSku sku = new ProductSkuImpl();
		sku.setShippable(shippable);
		sku.setWeight(BigDecimal.ONE);

		ShoppingItemImpl item = new ShoppingItemImpl();
		item.setProductSku(sku);
		item.setQuantity(quantity);

		return item;
	}

}
