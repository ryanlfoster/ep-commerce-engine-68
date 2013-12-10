package com.elasticpath.service.search.query;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

/**
 * Test <code>OrderReturnSearchCriteriaImpl</code>.
 */
public class OrderReturnSearchCriteriaImplTest {


	private OrderReturnSearchCriteria orderReturnSearchCriteria;

	@Before
	public void setUp() throws Exception {
		this.orderReturnSearchCriteria = new OrderReturnSearchCriteria();
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderReturnSearchCriteriaImpl.getCustomerSearchCriteria()'.
	 */
	@Test
	public void testGetCustomerSearchCriteria() {
		assertNull(this.orderReturnSearchCriteria.getCustomerSearchCriteria());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderReturnSearchCriteriaImpl.setCustomerSearchCriteria(CustomerSearchCriteria)'.
	 */
	@Test
	public void testSetCustomerSearchCriteria() {
		final CustomerSearchCriteria criteria = new CustomerSearchCriteria();
		this.orderReturnSearchCriteria.setCustomerSearchCriteria(criteria);
		assertSame(criteria, this.orderReturnSearchCriteria.getCustomerSearchCriteria());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderReturnSearchCriteriaImpl.getOrderNumber()'.
	 */
	@Test
	public void testGetOrderNumber() {
		assertNull(this.orderReturnSearchCriteria.getOrderNumber());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderReturnSearchCriteriaImpl.setOrderNumber(OrderNumber)'.
	 */
	@Test
	public void testSetOrderNumber() {
		final String orderNumber = "ordernumber";
		this.orderReturnSearchCriteria.setOrderNumber(orderNumber);
		assertSame(orderNumber, this.orderReturnSearchCriteria.getOrderNumber());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderReturnSearchCriteriaImpl.getRmaCode()'.
	 */
	@Test
	public void testGetRmaCode() {
		assertNull(this.orderReturnSearchCriteria.getRmaCode());
	}


	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderReturnSearchCriteriaImpl.setRmaCode(rmaCode)'.
	 */
	@Test
	public void testSetRmaCode() {
		final String rmaCode = "rmacode";
		this.orderReturnSearchCriteria.setRmaCode(rmaCode);
		assertSame(rmaCode, this.orderReturnSearchCriteria.getRmaCode());
	}
}
