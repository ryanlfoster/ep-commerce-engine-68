/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.store.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

/**
 * Tests methods of {@link WarehouseImpl}.
 */
public class WarehouseImplTest {

	/**
	 * Test method for {@link com.elasticpath.domain.store.impl.WarehouseImpl#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		WarehouseImpl warehouse1 = new WarehouseImpl();
		WarehouseImpl warehouse2 = new WarehouseImpl();
		
		assertEquals(warehouse1.hashCode(), warehouse2.hashCode());
		
		warehouse1.setCode("code1");
		assertNotSame(warehouse1.hashCode(), warehouse2.hashCode());
		
		warehouse2.setCode("code1");
		
		assertEquals(warehouse1.hashCode(), warehouse2.hashCode());
	}

	/**
	 * Test method for {@link com.elasticpath.domain.store.impl.WarehouseImpl#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		WarehouseImpl warehouse1 = new WarehouseImpl();
		WarehouseImpl warehouse2 = new WarehouseImpl();
		
		assertEquals(warehouse1, warehouse2);
		
		warehouse1.setCode("code11");
		assertFalse(warehouse1.equals(warehouse2));
		
		warehouse2.setCode("code11");
		
		assertEquals(warehouse1, warehouse2);
	}

}
