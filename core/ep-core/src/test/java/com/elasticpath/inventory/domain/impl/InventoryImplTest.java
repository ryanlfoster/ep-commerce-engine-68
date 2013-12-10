/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.inventory.domain.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.inventory.impl.InventoryDtoImpl;

/**
 * Test cases for <code>Inventory</code> class.
 */
public class InventoryImplTest {

	private static final String DOMAIN_EXCEPTION_EXPECTED = "Domain Exception Expected";

	private InventoryDtoImpl inventoryImpl;

	private static final int QUANTITY_ON_HAND = 5;
	private static final int RESERVED_QUANTITY = 3;
	private static final int REORDER_MINIMUM = 2;


	@Before
	public void setUp() throws Exception {
		inventoryImpl = new InventoryDtoImpl();
	}

	/**
	 * Test for: Get the reserved quantity.
	 */
	@Test
	public void testGetSetReservedQuantity() {
		inventoryImpl.setQuantityOnHand(QUANTITY_ON_HAND);
		inventoryImpl.setReservedQuantity(RESERVED_QUANTITY);
		assertEquals(RESERVED_QUANTITY, inventoryImpl.getReservedQuantity());

		try {
			inventoryImpl.setReservedQuantity(-1);
			fail(DOMAIN_EXCEPTION_EXPECTED);
		} catch (EpDomainException epde) {
			//Success
			assertNotNull(epde);
		}
	}


	/**
	 * Test for: Get the reorder minimum quantity.
	 */
	@Test
	public void testGetSetReorderMinimum() {
		inventoryImpl.setReorderMinimum(REORDER_MINIMUM);
		assertEquals(REORDER_MINIMUM, inventoryImpl.getReorderMinimum());
	}


	/**
	 * Test for: Get the expected date when this inventory item will be restocked.
	 */
	@Test
	public void testGetSetRestockDate() {
		Date restockDate = new Date();
		inventoryImpl.setRestockDate(restockDate);
		assertSame(restockDate, inventoryImpl.getRestockDate());
	}



}
