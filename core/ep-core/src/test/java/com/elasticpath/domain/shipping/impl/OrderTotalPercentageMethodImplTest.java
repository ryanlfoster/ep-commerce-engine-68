/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping.impl;


import static com.elasticpath.domain.shipping.impl.ShippingAsserts.assertShippingCost;
import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.aCostCalculationParam;
import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.someCalculationParams;
import static com.elasticpath.test.factory.ShoppingCartStubBuilder.aCart;
import static com.elasticpath.test.factory.ShoppingCartStubBuilder.aShoppingItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/** Test cases for <code>OrderTotalPercentageMethodImpl</code>. */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class OrderTotalPercentageMethodImplTest {

	private static final BigDecimal PERCENTAGE = new BigDecimal("10.0");

	private static final BigDecimal SHIPPING_COST = new BigDecimal("6.50");

	private ShippingCostCalculationMethod shippingCostCalculationMethod;

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

		this.shippingCostCalculationMethod = new OrderTotalPercentageMethodImpl();
		shippingCostCalculationMethod.setParameters(
				someCalculationParams(
						aCostCalculationParam(ShippingCostCalculationParametersEnum.PERCENTAGE_OF_ORDER_TOTOAL, PERCENTAGE)));
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.OrderTotalPercentageMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetDisplayText() {
		assertNotNull(this.shippingCostCalculationMethod.getDisplayText());
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.OrderTotalPercentageMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetType() {
		assertEquals(this.shippingCostCalculationMethod.getType(), ((OrderTotalPercentageMethodImpl) this.shippingCostCalculationMethod)
				.getMethodType());
	}

	/**
	 * Test that no items incurs no shipping cost.
	 */
	@Test
	public void testCalculateShippingCostNoItems() {
		ShoppingCart shoppingCart = aCart(context).withCurrencyCAD().build();
		assertShippingCost("0.00", shippingCostCalculationMethod, shoppingCart);
	}

	/**
	 * Test that two shippable items have the right price applied once.
	 */
	@Test
	public void testCalculateShippingCostTwoShippableItems() {
		ShoppingCart shoppingCart = aCart(context).withCurrencyCAD()
			.with(aShoppingItem(context).thatsShippable().costing(new BigDecimal("65.00"))).build();

		// Cost: 10% * $65.00 = $6.50
		assertShippingCost(SHIPPING_COST.toString(), shippingCostCalculationMethod, shoppingCart);
	}

	/**
	 * Test that with all non-shippable items no shipping charge is incurred.
	 */
	@Test
	public void testCalculateShippingCostAllNonShippable() {
		ShoppingCart shoppingCart = aCart(context).withCurrencyCAD()
			.with(aShoppingItem(context).thatsNotShippable().costing(BigDecimal.ONE))
			.with(aShoppingItem(context).thatsNotShippable().costing(BigDecimal.ONE)).build();

		assertShippingCost("0.00", shippingCostCalculationMethod, shoppingCart);
	}

	/**
	 * Test that with one shippable and one non-shippable items in the list
	 * the total cost is correct.
	 */
	@Test
	public void testCalculateShippingCostOneNonShippable() {
		ShoppingCart shoppingCart = aCart(context).withCurrencyCAD()
			.with(aShoppingItem(context).thatsShippable().costing(BigDecimal.TEN))
			.with(aShoppingItem(context).thatsNotShippable().costing(BigDecimal.ONE)).build();

		// Cost: 10% * $10.00 = $1.00
		assertShippingCost("1.00", shippingCostCalculationMethod, shoppingCart);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedBaseAndCostPerUnitWeightMethodImpl.getDefaultParameters(currencyList)'.
	 */
	@Test
	public void testGetDefaultParametersSet() {
		expectationsFactory.
			allowingBeanFactoryGetBean(ContextIdNames.SHIPPING_COST_CALCULATION_PARAMETER, ShippingCostCalculationParameterImpl.class);

		List<Currency> currencyList = new ArrayList<Currency>();
		Currency usd = Currency.getInstance("USD");
		Currency cad = Currency.getInstance("CAD");
		currencyList.add(usd);
		currencyList.add(cad);
		List<ShippingCostCalculationParameter> calcParams = shippingCostCalculationMethod.getDefaultParameters(currencyList);
		Iterator<ShippingCostCalculationParameter> parameterIter = calcParams.iterator();
		int usdNum = 0, cadNum = 0;
		while (parameterIter.hasNext()) {
			ShippingCostCalculationParameter param = parameterIter.next();
			Currency currency = param.getCurrency();
			if (currency != null) {
				if (currency.equals(usd)) {
					++usdNum;
				} else if (currency.equals(cad)) {
					++cadNum;
				} else {
					assertTrue("Unexpected currrency: " + currency, currency.equals(cad) || currency.equals(usd));
				}
			}
		}

		final int expectedParamNumber = 1;

		assertEquals(0, usdNum);
		assertEquals(0, cadNum);
		assertEquals(expectedParamNumber, calcParams.size());
	}
}
