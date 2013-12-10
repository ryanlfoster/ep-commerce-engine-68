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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

/** Test cases for <code>FixedPriceMethodImpl</code>. */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class FixedPriceMethodImplTest {

	private static final BigDecimal FIXED_PRICE = new BigDecimal("10.00");

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

		this.shippingCostCalculationMethod = new FixedPriceMethodImpl();
		shippingCostCalculationMethod.setParameters(
				someCalculationParams(
						aCostCalculationParam(ShippingCostCalculationParametersEnum.FIXED_PRICE,
								FIXED_PRICE, Currency.getInstance("CAD"))));

	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedPriceMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetDisplayText() {
		assertNotNull(this.shippingCostCalculationMethod.getDisplayText());
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.FixedPriceMethodImpl.getDisplayText()'.
	 */
	@Test
	public void testGetType() {
		assertEquals(this.shippingCostCalculationMethod.getType(), ((FixedPriceMethodImpl) this.shippingCostCalculationMethod).getMethodType());
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
	 * Test that two shippable items only have the fixed price applied once.
	 */
	@Test
	public void testCalculateShippingCostTwoShippable() {
		ShoppingCart shoppingCart = aCart(context).withCurrencyCAD()
				.with(aShoppingItem(context).thatsShippable())
				.with(aShoppingItem(context).thatsShippable()).build();

		assertShippingCost(FIXED_PRICE.toString(), shippingCostCalculationMethod, shoppingCart);
	}

	/**
	 * Test that with mixed (shippable and non-shippable) items the
	 * fixed price is only applied once.
	 */
	@Test
	public void testCalculateShippingCostOneNonShippableOneShippable() {
		ShoppingCart shoppingCart = aCart(context).withCurrencyCAD()
				.with(aShoppingItem(context).thatsShippable())
				.with(aShoppingItem(context).thatsNotShippable()).build();

		assertShippingCost(FIXED_PRICE.toString(), shippingCostCalculationMethod, shoppingCart);
	}

	/**
	 * Test that with all non-shippable items no shipping charge is incurred.
	 */
	@Test
	public void testCalculateShippingCostAllNonShippable() {
		ShoppingCart shoppingCart = aCart(context).withCurrencyCAD()
				.with(aShoppingItem(context).thatsNotShippable())
				.with(aShoppingItem(context).thatsNotShippable()).build();

		assertShippingCost("0.00", shippingCostCalculationMethod, shoppingCart);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.shipping.impl.FixedBaseAndCostPerUnitWeightMethodImpl.getDefaultParameters(currencyList)'.
	 */
	@Test
	public void testGetDefaultParametersSet() {
		expectationsFactory.
		allowingBeanFactoryGetBean(ContextIdNames.SHIPPING_COST_CALCULATION_PARAMETER, ShippingCostCalculationParameterImpl.class);

		List< Currency > currencyList = new ArrayList< Currency >();
		Currency usd = Currency.getInstance("USD");
		Currency cad = Currency.getInstance("CAD");
		currencyList.add(usd);
		currencyList.add(cad);
		List< ShippingCostCalculationParameter > calcParams = shippingCostCalculationMethod.getDefaultParameters(currencyList);
		Iterator< ShippingCostCalculationParameter > parameterIter = calcParams.iterator();
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

	/**
	 * Test that shipping can be calculated by currency, depending on shopping cart currency, and
	 * shipping level parameters set.
	 */
	@Test
	public void testCalculateShippingCostByCurrency() {
		expectationsFactory.
			allowingBeanFactoryGetBean(ContextIdNames.SHIPPING_COST_CALCULATION_PARAMETER, ShippingCostCalculationParameterImpl.class);


		List< Currency > currencyList = new ArrayList< Currency >();
		final Currency currencyUS = Currency.getInstance("USD");
		final Currency currencyGermany = Currency.getInstance(Locale.GERMANY);
		currencyList.add(currencyUS);
		currencyList.add(currencyGermany);

		List< ShippingCostCalculationParameter > params = shippingCostCalculationMethod.getDefaultParameters(currencyList);
		Set< ShippingCostCalculationParameter > paramSet = new HashSet< ShippingCostCalculationParameter >();
		for (ShippingCostCalculationParameter param : params) {
			param.setKey(ShippingCostCalculationParametersEnum.FIXED_PRICE.getKey());
			if (param.getCurrency().equals(Currency.getInstance(Locale.US))) {
				param.setValue("5.5");
			}
			if (param.getCurrency().equals(Currency.getInstance(Locale.GERMANY))) {
				param.setValue("9.5");
			}
			paramSet.add(param);
		}
		this.shippingCostCalculationMethod.setParameters(paramSet);

		ShoppingCart cartGermany = aCart(context).withCurrency(currencyGermany)
			.with(aShoppingItem(context).thatsShippable()).build();
		assertShippingCost("9.50", shippingCostCalculationMethod, cartGermany);

		ShoppingCart cartUS = aCart(context).withCurrency(currencyUS)
			.with(aShoppingItem(context).thatsShippable()).build();
		assertShippingCost("5.50", shippingCostCalculationMethod, cartUS);

	}

}
