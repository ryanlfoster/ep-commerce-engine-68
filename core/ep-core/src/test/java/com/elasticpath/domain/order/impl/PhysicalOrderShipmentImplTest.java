/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;

/**
 * Test cases for <code>PhysicalOrderShipmentImpl</code>.
 */
public class PhysicalOrderShipmentImplTest {

	private static final String TEST_STRING = "testString";

	private PhysicalOrderShipment orderShipmentImpl;

	@Before
	public void setUp() throws Exception {
		orderShipmentImpl = new PhysicalOrderShipmentImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.PhysicalOrderShipmentImpl.getCreatedDate()'.
	 */
	@Test
	public void testGetSetCreatedDate() {
		Date testDate = new Date();
		orderShipmentImpl.setCreatedDate(testDate);
		assertEquals(testDate, orderShipmentImpl.getCreatedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.PhysicalOrderShipmentImpl.getLastModifiedDate()'.
	 */
	@Test
	public void testGetSetLastModifiedDate() {
		Date testDate = new Date();
		orderShipmentImpl.setLastModifiedDate(testDate);
		assertEquals(testDate, orderShipmentImpl.getLastModifiedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.PhysicalOrderShipmentImpl.getShipmentDate()'.
	 */
	@Test
	public void testGetSetShipmentDate() {
		Date testDate = new Date();
		orderShipmentImpl.setShipmentDate(testDate);
		assertEquals(testDate, orderShipmentImpl.getShipmentDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.PhysicalOrderShipmentImpl.getCarrier()'.
	 */
	@Test
	public void testGetSetCarrier() {
		orderShipmentImpl.setCarrier(TEST_STRING);
		assertEquals(TEST_STRING, orderShipmentImpl.getCarrier());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.PhysicalOrderShipmentImpl.getServiceLevel()'.
	 */
	@Test
	public void testGetSetServiceLevel() {
		orderShipmentImpl.setServiceLevel(TEST_STRING);
		assertEquals(TEST_STRING, orderShipmentImpl.getServiceLevel());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.PhysicalOrderShipmentImpl.getTrackingCode()'.
	 */
	@Test
	public void testGetSetTrackingCode() {
		orderShipmentImpl.setTrackingCode(TEST_STRING);
		assertEquals(TEST_STRING, orderShipmentImpl.getTrackingCode());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl.getStatus()'.
	 */
	@Test
	public void testGetSetStatus() {
		orderShipmentImpl.setStatus(OrderShipmentStatus.ONHOLD);
		assertSame(OrderShipmentStatus.ONHOLD, orderShipmentImpl.getShipmentStatus());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.PhysicalOrderShipmentImpl.getShippingCost()'.
	 */
	@Test
	public void testGetSetShippingCost() {
		BigDecimal testAmount = BigDecimal.ONE;
		orderShipmentImpl.setShippingCost(testAmount);
		assertEquals(testAmount, orderShipmentImpl.getShippingCost());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.PhysicalOrderShipmentImpl.getBeforeTaxShippingCost()'.
	 */
	@Test
	public void testGetSetBeforeTaxShippingCost() {
		BigDecimal testAmount = BigDecimal.ONE;
		orderShipmentImpl.setBeforeTaxShippingCost(testAmount);
		assertEquals(testAmount, orderShipmentImpl.getBeforeTaxShippingCost());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.PhysicalOrderShipmentImpl.getShipmentAddress()'.
	 */
	@Test
	public void testGetSetShipmentAddress() {
		OrderAddress orderAddress = new OrderAddressImpl();
		orderShipmentImpl.setShipmentAddress(orderAddress);
		assertEquals(orderAddress, orderShipmentImpl.getShipmentAddress());
	}
	
	/**
	 * Test that a shipment cannot be cancelled twice.
	 */
	@Test
	public void testNotCancellableWhenCancelled() {
		orderShipmentImpl.setStatus(OrderShipmentStatus.CANCELLED);
		assertFalse(orderShipmentImpl.isCancellable());
	}
	
	/**
	 * Test that a shipment cannot be cancelled once it's been shipped.
	 */
	@Test
	public void testNotCancellableWhenShipped() {
		orderShipmentImpl.setStatus(OrderShipmentStatus.SHIPPED);
		assertFalse(orderShipmentImpl.isCancellable());
	}
	
	/**
	 * Test that a shipment can be cancelled after being released for packing.
	 */
	@Test
	public void testCancellableWhenReleased() {
		orderShipmentImpl.setStatus(OrderShipmentStatus.RELEASED);
		assertTrue(orderShipmentImpl.isCancellable());
	}
	
	/**
	 * Test that a shipment can be cancelled while awaiting inventory.
	 */
	@Test
	public void testCancellableWhenAwaitingInventory() {
		orderShipmentImpl.setStatus(OrderShipmentStatus.AWAITING_INVENTORY);
		assertTrue(orderShipmentImpl.isCancellable());
	}
	
	/**
	 * Test that a shipment can be cancelled one inventory has been assigned.
	 */
	@Test
	public void testCancellableWhenInventoryAssigned() {
		orderShipmentImpl.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
		assertTrue(orderShipmentImpl.isCancellable());
	}
	
	/**
	 * Test that a shipment can be cancelled while on hold.
	 */
	@Test
	public void testCancellableWhenOnHold() {
		orderShipmentImpl.setStatus(OrderShipmentStatus.ONHOLD);
		assertTrue(orderShipmentImpl.isCancellable());
	}
}
