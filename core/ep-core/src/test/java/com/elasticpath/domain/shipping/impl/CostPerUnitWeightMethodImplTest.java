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
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/** Test cases for <code>CostPerUnitWeightMethodImpl</code>. */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class CostPerUnitWeightMethodImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private static final BigDecimal COST_PER_UNIT_WEIGHT = new BigDecimal("5.0");

	private CostPerUnitWeightMethodImpl method;

	/**
	 * Set the test fixture up.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		method = new CostPerUnitWeightMethodImpl();

		method.setParameters(
				someCalculationParams(
						aCostCalculationParam(
								ShippingCostCalculationParametersEnum.COST_PER_UNIT_WEIGHT, COST_PER_UNIT_WEIGHT,
								Currency.getInstance("CAD"))));
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.CostPerUnitWeightMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetDisplayText() {
		CostPerUnitWeightMethodImpl method = new CostPerUnitWeightMethodImpl();
		assertNotNull(method.getDisplayText());
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.CostPerUnitWeightMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetType() {
		assertEquals(method.getType(), method.getMethodType());
	}

	/**
	 * Test that no items incurs no shipping cost.
	 */
	@Test
	public void testCalculateShippingCostNoItems() {
		final ShoppingCart shoppingCart = aCart(context).withCurrencyCAD().build();
		assertShippingCost("0.00", method, shoppingCart);
	}

	/**
	 * Test that the shipping cost is the cost per unit weight * the total weight of all the applicable line items, rounded to two decimals.
	 */
	@Test
	public void testCalculateShippingCostTwoShippable() {
		final ShoppingCart shoppingCart = aCart(context).withCurrencyCAD()
			.with(aShoppingItem(context).withQuantity(1).withWeight(BigDecimal.TEN)).build();

		assertShippingCost("50.00", method, shoppingCart);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedBaseAndCostPerUnitWeightMethodImpl.getDefaultParameters(currencyList)'.
	 */
	@Test
	public void testGetDefaultParametersSet() {
		CostPerUnitWeightMethodImpl method = new CostPerUnitWeightMethodImpl() {
			private static final long serialVersionUID = 6582808358854860854L;

			@Override
			ShippingCostCalculationParameter getShippingCostCalculationParameterBean() {
				return new ShippingCostCalculationParameterImpl();
			}
		};
		List<Currency> currencyList = new ArrayList<Currency>();
		Currency usd = Currency.getInstance("USD");
		Currency cad = Currency.getInstance("CAD");
		currencyList.add(usd);
		currencyList.add(cad);
		List<ShippingCostCalculationParameter> calcParams = method.getDefaultParameters(currencyList);
		Iterator<ShippingCostCalculationParameter> parameterIter = calcParams.iterator();
		int usdNum = 0, cadNum = 0;
		while (parameterIter.hasNext()) {
			ShippingCostCalculationParameter param = parameterIter.next();
			Currency currency = param.getCurrency();
			assertNotNull(currency);
			if (currency.equals(usd)) {
				++usdNum;
			} else if (currency.equals(cad)) {
				++cadNum;
			} else {
				assertTrue("Unexpected currrency: " + currency, currency.equals(cad) || currency.equals(usd));
			}
		}

		final int expectedParamNumber = 2;

		assertEquals(1, usdNum);
		assertEquals(1, cadNum);
		assertEquals(expectedParamNumber, calcParams.size());
	}

}
