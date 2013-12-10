/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderSku;

/**
 * Test cases for <code>OrderReturnSkuImpl</code>.
 */
public class OrderReturnSkuImplTest {

	private OrderReturnSku orderReturnSku;

	/**
	 * Prepare for the tests.
	 * 
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		orderReturnSku = new OrderReturnSkuImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnSkuImpl.getOrderSku()'.
	 */
	@Test
	public void testGetSetOrderSku() {
		final OrderSku orderSku = new OrderSkuImpl();
		orderReturnSku.setOrderSku(orderSku);
		assertEquals(orderSku, orderReturnSku.getOrderSku());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnSkuImpl.getQuantity()'.
	 */
	@Test
	public void testGetSetQuantity() {
		final int quantity = 2;
		orderReturnSku.setQuantity(quantity);
		assertEquals(quantity, orderReturnSku.getQuantity());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnSkuImpl.returnAmount()'.
	 */
	@Test
	public void testGetSetReturnAmount() {
		final BigDecimal returnAmount = new BigDecimal(2);
		orderReturnSku.setReturnAmount(returnAmount);
		assertEquals(returnAmount, orderReturnSku.getReturnAmount());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnSkuImpl.()'.
	 */
	@Test
	public void testGetSetReturnReason() {
		orderReturnSku.setReturnReason("OrderReturnSkuReason.FAULTY");
		assertEquals("OrderReturnSkuReason.FAULTY", orderReturnSku.getReturnReason());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnSkuImpl.()'.
	 */
	@Test
	public void testGetSetReceivedQuantity() {
		orderReturnSku.setReceivedQuantity(2);
		assertEquals(2, orderReturnSku.getReceivedQuantity());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnSkuImpl.()'.
	 */
	@Test
	public void testGetSetReceivedState() {
		orderReturnSku.setReceivedState("OrderReturnReceivedState.PERFECT");
		assertEquals("OrderReturnReceivedState.PERFECT", orderReturnSku.getReceivedState());
	}

}