package com.elasticpath.service.search.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;

/**
 * Test <code>OrderSearchCriteriaImpl</code>.
 */
public class OrderSearchCriteriaTest {


	private OrderSearchCriteria orderSearchCriteria;

	@Before
	public void setUp() throws Exception {
		this.orderSearchCriteria = new OrderSearchCriteria();
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.getOrderStatus()'.
	 */
	@Test
	public void testGetOrderStatus() {
		assertNull(this.orderSearchCriteria.getOrderStatus());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.setOrderStatus(OrderStatus)'.
	 */
	@Test
	public void testSetOrderStatus() {
		this.orderSearchCriteria.setOrderStatus(OrderStatus.ONHOLD);
		assertSame(OrderStatus.ONHOLD, this.orderSearchCriteria.getOrderStatus());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.getOrderFromDate()'.
	 */
	@Test
	public void testGetOrderFromDate() {
		assertNull(this.orderSearchCriteria.getOrderFromDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.setOrderFromDate(Date)'.
	 */
	@Test
	public void testSetOrderFromDate() {
		final Date date = new Date();
		this.orderSearchCriteria.setOrderFromDate(date);
		assertEquals(date, this.orderSearchCriteria.getOrderFromDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.getOrderToDate()'.
	 */
	@Test
	public void testGetOrderToDate() {
		assertNull(this.orderSearchCriteria.getOrderToDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.setOrderToDate(Date)'.
	 */
	@Test
	public void testSetOrderToDate() {
		final Date date = new Date();
		this.orderSearchCriteria.setOrderToDate(date);
		assertEquals(date, this.orderSearchCriteria.getOrderToDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.getCustomerSearchCriteria()'.
	 */
	@Test
	public void testGetCustomerSearchCriteria() {
		assertNull(this.orderSearchCriteria.getCustomerSearchCriteria());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.setCustomerSearchCriteria(CustomerSearchCriteria)'.
	 */
	@Test
	public void testSetCustomerSearchCriteria() {
		final CustomerSearchCriteria criteria = new CustomerSearchCriteria();
		this.orderSearchCriteria.setCustomerSearchCriteria(criteria);
		assertSame(criteria, this.orderSearchCriteria.getCustomerSearchCriteria());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.getShipmentZipcode()'.
	 */
	@Test
	public void testGetShipmentZipcode() {
		assertNull(this.orderSearchCriteria.getShipmentZipcode());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.setShipmentZipcode(String)'.
	 */
	@Test
	public void testSetShipmentZipcode() {
		final String zipcode = "zipcode";
		this.orderSearchCriteria.setShipmentZipcode(zipcode);
		assertSame(zipcode, this.orderSearchCriteria.getShipmentZipcode());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.getShipmentStatus()'.
	 */
	@Test
	public void testGetShipmentStatus() {
		assertNull(this.orderSearchCriteria.getShipmentStatus());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.setShipmentStatus(OrderShipmentStatus)'.
	 */
	@Test
	public void testSetShipmentStatus() {
		final OrderShipmentStatus status = OrderShipmentStatus.ONHOLD;
		this.orderSearchCriteria.setShipmentStatus(status);
		assertSame(status, this.orderSearchCriteria.getShipmentStatus());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.getOrderNumber()'.
	 */
	@Test
	public void testGetOrderNumber() {
		assertNull(this.orderSearchCriteria.getOrderNumber());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl.setOrderNumber(OrderNumber)'.
	 */
	@Test
	public void testSetOrderNumber() {
		final String orderNumber = "ordernumber";
		this.orderSearchCriteria.setOrderNumber(orderNumber);
		assertSame(orderNumber, this.orderSearchCriteria.getOrderNumber());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.SkuSearchCriteriaImpl.getSkuCode()'.
	 */
	@Test
	public void testGetSkuCode() {
		assertNull(this.orderSearchCriteria.getSkuCode());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.SkuSearchCriteriaImpl.setSkuCode(SkuCode)'.
	 */
	@Test
	public void testSetSkuCode() {
		final String skuCode = "skucode";
		this.orderSearchCriteria.setSkuCode(skuCode);
		assertSame(skuCode, this.orderSearchCriteria.getSkuCode());
	}
}
