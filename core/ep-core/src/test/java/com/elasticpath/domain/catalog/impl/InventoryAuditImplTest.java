/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.InventoryAudit;
import com.elasticpath.domain.catalog.InventoryEventType;

/**
 * Test cases for <code>InventoryAudit</code> class.
 */
public class InventoryAuditImplTest {

	private InventoryAudit inventoryEventImpl;

	@Before
	public void setUp() throws Exception {
		inventoryEventImpl = new InventoryAuditImpl();
	}

	/**
	 * Test method for get/set EventOriginator.
	 */
	@Test
	public void testGetSetEventOriginator() {
		assertNull(inventoryEventImpl.getEventOriginator());
		inventoryEventImpl.setEventOriginator(InventoryAudit.EVENT_ORIGINATOR_CMUSER);
		assertEquals(InventoryAudit.EVENT_ORIGINATOR_CMUSER, inventoryEventImpl.getEventOriginator());
	}

	/**
	 * Test method for get/set EventType.
	 */
	@Test
	public void testGetSetEventType() {
		assertNull(inventoryEventImpl.getEventType());
		inventoryEventImpl.setEventType(InventoryEventType.STOCK_RECEIVED);
		assertEquals(InventoryEventType.STOCK_RECEIVED, inventoryEventImpl.getEventType());
	}

	/**
	 * Test method for get/set Quantity.
	 */
	@Test
	public void testGetSetQuantity() {
		assertEquals(0, inventoryEventImpl.getQuantity()); //Default 0.
		inventoryEventImpl.setQuantity(1);
		assertEquals(1, inventoryEventImpl.getQuantity());
	}

	/**
	 * Test method for get/set Comment.
	 */
	@Test
	public void testGetSetComment() {
		assertNull(inventoryEventImpl.getComment());
		inventoryEventImpl.setComment("");
		assertEquals("", inventoryEventImpl.getComment());
	}

	/**
	 * Test method for get/set LogDate.
	 */
	@Test
	public void testGetSetLogDate() {
		assertNull(inventoryEventImpl.getLogDate());
		Date now = new Date();
		inventoryEventImpl.setLogDate(now);
		assertEquals(now, inventoryEventImpl.getLogDate());
	}

}
