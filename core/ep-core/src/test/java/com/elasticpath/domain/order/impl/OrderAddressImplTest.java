/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;

/**
 * Test cases for <code>OrderAddressImpl</code>. Currently OrderAddressImpl is the same as CustomerAddressImpl but is subclassed for
 * hiberate/XDoclet to store it in a different table.
 */
public class OrderAddressImplTest {

	private static final String FIRST_NAME = "First Name";

	private static final int UIDPK1 = 1001;

	private static final int UIDPK2 = 1002;

	private OrderAddressImpl orderAddressImpl;

	@Before
	public void setUp() throws Exception {
		orderAddressImpl = new OrderAddressImpl();
	}

	/**
	 * Test initialization from a customer address.
	 */
	@Test
	public void testInit() {
		CustomerAddress customerAddress = new CustomerAddressImpl();
		customerAddress.setUidPk(UIDPK1);
		customerAddress.setFirstName(FIRST_NAME);

		orderAddressImpl.setUidPk(UIDPK2);
		orderAddressImpl.init(customerAddress);

		assertFalse(orderAddressImpl.getUidPk() == customerAddress.getUidPk()); // NOPMD
		assertEquals(orderAddressImpl.getFirstName(), customerAddress.getFirstName());
	}

}
