/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/** Test cases for <code>ShippingCostCalculationParameterImpl</code>.*/
public class ShippingCostCalculationParameterImplTest {
	
	private static final String TEST_VALUE = "testValue";
	
	private ShippingCostCalculationParameterImpl shippingCostCalculationParameterImpl;
	

	/**
	 * Prepare for each test.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		this.shippingCostCalculationParameterImpl = new ShippingCostCalculationParameterImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingCostCalculationParameterImpl.getKey()'.
	 */
	@Test
	public void testGetSetKey() {
		final String testKey = "testKey";
		this.shippingCostCalculationParameterImpl.setKey(testKey);
		assertEquals(testKey, this.shippingCostCalculationParameterImpl.getKey());
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingCostCalculationParameterImpl.getValue()'.
	 */
	@Test
	public void testGetSetValue() {
		this.shippingCostCalculationParameterImpl.setValue(TEST_VALUE);
		assertEquals(TEST_VALUE, this.shippingCostCalculationParameterImpl.getValue());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingCostCalculationParameterImpl.getValue()'.
	 */
	@Test
	public void testGetDisplayText() {
		// Test that value is returned if displayText is null
		this.shippingCostCalculationParameterImpl.setValue(TEST_VALUE);		
		assertEquals(TEST_VALUE, this.shippingCostCalculationParameterImpl.getDisplayText());
		
		final String displayText = "testText";
		this.shippingCostCalculationParameterImpl.setDisplayText(displayText);
		assertEquals(displayText, this.shippingCostCalculationParameterImpl.getDisplayText());
	}

}

