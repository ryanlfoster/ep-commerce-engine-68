package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderLock;

/**
 * Test cases for <code>OrderLockImpl</code>.
 */
public class OrderLockImplTest {
	
	private OrderLock orderLock;
	
	@Before
	public void setUp() throws Exception {
		orderLock = new OrderLockImpl();
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderLockImpl.getUidPk()'.
	 */
	@Test
	public void testGetSetUidPk() {
		final long uid = 1234L;
		orderLock.setUidPk(uid);
		assertEquals(uid, orderLock.getUidPk());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderLockImpl.getOrder()'.
	 */
	@Test
	public void testGetSetOrder() {
		final Order testOrder = new OrderImpl();
		orderLock.setOrder(testOrder);
		assertEquals(testOrder, orderLock.getOrder());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderLockImpl.getCmUser()'.
	 */
	@Test
	public void testGetSetCmUser() {
		final CmUser testCmUser = new CmUserImpl();
		orderLock.setCmUser(testCmUser);
		assertEquals(testCmUser, orderLock.getCmUser());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderLockImpl.getCreatedDate()'.
	 */
	@Test
	public void testGetSetCreatedDate() {
		final long testDate = System.currentTimeMillis();
		orderLock.setCreatedDate(testDate);
		assertEquals(testDate, orderLock.getCreatedDate());
	}
}
