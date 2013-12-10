/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/** Test cases for <code>AbstractShippingCostCalculationMethodImpl</code>.*/
public class AbstractShippingCostCalculationMethodImplTest {

	private static final String DUMMY_METHOD_TYPE = "dummyMethodType";

	private static final String DUMMY_METHOD_TEXT = "dummyMethodDisplayText";

	/** Set of keys required for this shipping cost calculation method. */
	protected static final String[] DUMMY_PARAMETER_KEYS = new String[] { "KEY1", "KEY2" };

	private static final String KEY1 = "testKey1";

	private static final String VALUE1 = "testValue1";

	private static final String KEY2 = "testKey2";

	private static final String VALUE2 = "testValue2";

	private ShippingCostCalculationMethod dummyShippingCostCalculationMethodImpl;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Prepare for each test.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		
		this.dummyShippingCostCalculationMethodImpl = new AbstractShippingCostCalculationMethodImpl() {
			private static final long serialVersionUID = 6168317855634775901L;

			protected String getMethodType() {
				return DUMMY_METHOD_TYPE;
			}

			public String[] getParameterKeys() {
				return DUMMY_PARAMETER_KEYS.clone();
			}

			public String getDisplayText() {
				return DUMMY_METHOD_TEXT;
			}

			public Money calculateShippingCost(final ShoppingCart shoppingCart) {
				return null;
			}

			public Money calculateShippingCost(final Collection< ? extends ShoppingItem> lineItems, final Currency currency) {
				return null;
			}
		};
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingCostCalculationMethodImpl.getType()'.
	 */
	@Test
	public void testGetType() {
		assertEquals(DUMMY_METHOD_TYPE, this.dummyShippingCostCalculationMethodImpl.getType());
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingCostCalculationMethodImpl.getParameters()'.
	 */
	@Test
	public void testGetSetParameters() {
		Set<ShippingCostCalculationParameter> paramSet = getParameterSet();
		this.dummyShippingCostCalculationMethodImpl.setParameters(paramSet);
		assertEquals(this.dummyShippingCostCalculationMethodImpl.getParameters(), paramSet);
	}

	private Set<ShippingCostCalculationParameter> getParameterSet() {
		Set<ShippingCostCalculationParameter> paramSet = new HashSet<ShippingCostCalculationParameter>();

		ShippingCostCalculationParameter shippingCostCalculationParameter = new ShippingCostCalculationParameterImpl();
		shippingCostCalculationParameter.setKey(KEY1);
		shippingCostCalculationParameter.setValue(VALUE1);
		paramSet.add(shippingCostCalculationParameter);

		shippingCostCalculationParameter = new ShippingCostCalculationParameterImpl();
		shippingCostCalculationParameter.setKey(KEY2);
		shippingCostCalculationParameter.setValue(VALUE2);
		paramSet.add(shippingCostCalculationParameter);

		return paramSet;
	}
	
	/**
	 * Test that a lineitem's weight is only added to the total weight if the lineitem's productsku is shippable.
	 */
	@Test
	public void testShoppingItemWeightCountsOnlyIfShippable() {
		//two line items with identical weight and quantity, but only one is shippable
		final BigDecimal weight = BigDecimal.TEN;
		final int quantity = 1;
		
		final ShoppingItem shippableLineItem = context.mock(ShoppingItem.class, "shippableLineItem");
		final ShoppingItem nonShippableLineItem = context.mock(ShoppingItem.class, "nonShippableLineItem");
		final ProductSku shippableProductSku = context.mock(ProductSku.class, "shippableProductSku");
		final ProductSku nonShippableProductSku = context.mock(ProductSku.class, "nonShippableProductSku");
		context.checking(new Expectations() { {
			allowing(shippableLineItem).getProductSku(); will(returnValue(shippableProductSku));
			oneOf(shippableLineItem).getQuantity(); will(returnValue(quantity));
			allowing(nonShippableLineItem).getProductSku(); will(returnValue(nonShippableProductSku));
			allowing(nonShippableLineItem).getQuantity(); will(returnValue(quantity));
			oneOf(shippableProductSku).isShippable(); will(returnValue(true));
			oneOf(shippableProductSku).getWeight(); will(returnValue(weight));
			oneOf(nonShippableProductSku).isShippable(); will(returnValue(false));
			allowing(nonShippableProductSku).getWeight(); will(returnValue(weight));
		} });
		List<ShoppingItem> lineItems = new ArrayList<ShoppingItem>();
		lineItems.add(shippableLineItem);
		lineItems.add(nonShippableLineItem);
		
		AbstractShippingCostCalculationMethodImpl method = new AbstractShippingCostCalculationMethodImpl() {

			private static final long serialVersionUID = 2168107203838643471L;

			protected String getMethodType() {
				return DUMMY_METHOD_TYPE;
			}

			public String[] getParameterKeys() {
				return DUMMY_PARAMETER_KEYS.clone();
			}

			public String getDisplayText() {
				return DUMMY_METHOD_TEXT;
			}
			
			public Money calculateShippingCost(final ShoppingCart shoppingCart) {
				// TODO Auto-generated method stub
				return null;
			}

			public Money calculateShippingCost(
					final Collection< ? extends ShoppingItem> lineItems, final Currency currency) {
				// TODO Auto-generated method stub
				return null;
			}			
		};
		
		method.calculateTotalWeight(lineItems);
	}
}
