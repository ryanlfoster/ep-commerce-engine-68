/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;

/**
 * Test cases for <code>OrderSearchCriteriaImpl</code>.
 */
public class OrderSearchCriteriaImplTest {
	private static final OrderStatus ORDER_STATUS = OrderStatus.COMPLETED;

	private static final OrderShipmentStatus SHIPMENT_STATUS = OrderShipmentStatus.INVENTORY_ASSIGNED;

	private AdvancedOrderSearchCriteriaImpl orderSearchCriteriaImpl;

	@Before
	public void setUp() throws Exception {
		orderSearchCriteriaImpl = new AdvancedOrderSearchCriteriaImpl();
	}

	/**
	 * Test getter/setter's of a OrderSearchCriteria.
	 */
	@Test
	public void testGetSet() {
		assertEquals(null, orderSearchCriteriaImpl.getCustomerCriteria());
		final Map<String, String> customerCriteria = new HashMap<String, String>();
		orderSearchCriteriaImpl.setCustomerCriteria(customerCriteria);
		assertEquals(customerCriteria, orderSearchCriteriaImpl.getCustomerCriteria());

		assertNull(orderSearchCriteriaImpl.getOrderFromDate());
		final Date testDate = new Date();
		orderSearchCriteriaImpl.setOrderFromDate(testDate);
		assertEquals(testDate, orderSearchCriteriaImpl.getOrderFromDate());

		assertNull(orderSearchCriteriaImpl.getOrderToDate());
		orderSearchCriteriaImpl.setOrderToDate(testDate);
		assertEquals(testDate, orderSearchCriteriaImpl.getOrderToDate());

		assertNull(orderSearchCriteriaImpl.getOrderStatus());
		orderSearchCriteriaImpl.setOrderStatus(ORDER_STATUS);
		assertEquals(ORDER_STATUS, orderSearchCriteriaImpl.getOrderStatus());

		assertNull(orderSearchCriteriaImpl.getShipmentAddressCriteria());
		final Map<String, String> shipmentAddressCriteria = new HashMap<String, String>();
		orderSearchCriteriaImpl.setShipmentAddressCriteria(shipmentAddressCriteria);
		assertEquals(shipmentAddressCriteria, orderSearchCriteriaImpl.getShipmentAddressCriteria());

		assertNull(orderSearchCriteriaImpl.getShipmentStatus());
		orderSearchCriteriaImpl.setShipmentStatus(SHIPMENT_STATUS);
		assertEquals(SHIPMENT_STATUS, orderSearchCriteriaImpl.getShipmentStatus());



	}



}

