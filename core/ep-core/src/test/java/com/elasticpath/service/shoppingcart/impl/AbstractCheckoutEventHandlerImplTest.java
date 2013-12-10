/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart.impl;

import org.junit.Before;
import org.junit.Test;

/** 
 * Add test cases for <code>AbstractCheckoutEventHandlerImpl</code> here.
 * (No functional concrete implementation of this handler is provided) 
 */
public class AbstractCheckoutEventHandlerImplTest {

	private AbstractCheckoutEventHandlerImpl eventHandler;

	@Before
	public void setUp() throws Exception {
		//Use a null handler as a concrete class to test
		eventHandler = new NullCheckoutEventHandlerImpl();
	}

	/**
	 * Test method for 'com.elasticpath.service.shoppingcart.impl.AbstractCheckoutEventHandlerImpl.preCheckout(ShoppingCart, OrderPayment)'.
	 */
	@Test
	public void testPreCheckout() {
		eventHandler.preCheckout(null, null);
	}

	/**
	 * Test method for 'com.elasticpath.service.shoppingcart.impl.AbstractCheckoutEventHandlerImpl.preCheckoutOrderPersist(ShoppingCart,
	 * OrderPayment, Order)'.
	 */
	@Test
	public void testPreCheckoutOrderPersist() {
		eventHandler.preCheckoutOrderPersist(null, null, null);
	}

	/**
	 * Test method for 'com.elasticpath.service.shoppingcart.impl.AbstractCheckoutEventHandlerImpl.postCheckout(ShoppingCart, OrderPayment, Order)'.
	 */
	@Test
	public void testPostCheckout() {
		eventHandler.postCheckout(null, null, null);
	}

}
