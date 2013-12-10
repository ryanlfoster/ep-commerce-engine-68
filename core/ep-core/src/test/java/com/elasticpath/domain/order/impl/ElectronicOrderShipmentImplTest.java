/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;

/**
 * Test cases for <code>ElectronicOrderShipmentImpl</code>.
 */
public class ElectronicOrderShipmentImplTest {

	private ElectronicOrderShipment orderShipmentImpl;

	@Before
	public void setUp() throws Exception {
		orderShipmentImpl = new ElectronicOrderShipmentImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ElectronicOrderShipmentImpl.getCreatedDate()'.
	 */
	@Test
	public void testGetSetCreatedDate() {
		Date testDate = new Date();
		orderShipmentImpl.setCreatedDate(testDate);
		assertEquals(testDate, orderShipmentImpl.getCreatedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ElectronicOrderShipmentImpl.getLastModifiedDate()'.
	 */
	@Test
	public void testGetSetLastModifiedDate() {
		Date testDate = new Date();
		orderShipmentImpl.setLastModifiedDate(testDate);
		assertEquals(testDate, orderShipmentImpl.getLastModifiedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ElectronicOrderShipmentImpl.getShipmentDate()'.
	 */
	@Test
	public void testGetSetShipmentDate() {
		Date testDate = new Date();
		orderShipmentImpl.setShipmentDate(testDate);
		assertEquals(testDate, orderShipmentImpl.getShipmentDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.ElectronicOrderShipmentImpl.getStatus()'.
	 */
	@Test
	public void testGetSetStatus() {
		orderShipmentImpl.setStatus(OrderShipmentStatus.ONHOLD);
		assertSame(OrderShipmentStatus.ONHOLD, orderShipmentImpl.getShipmentStatus());
	}

	
	/**
	 * Verify that an electronic shipment can never be canceled.
	 */
	@Test
	public void testNotCancellableInAnyState() {
		for (OrderShipmentStatus status : OrderShipmentStatus.values()) {
			orderShipmentImpl.setStatus(status);
			assertFalse(orderShipmentImpl.isCancellable());
		}
	}
}
