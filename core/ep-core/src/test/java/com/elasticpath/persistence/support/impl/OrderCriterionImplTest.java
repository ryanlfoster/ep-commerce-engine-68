/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.support.impl;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.order.AdvancedOrderSearchCriteria;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.impl.AdvancedOrderSearchCriteriaImpl;
import com.elasticpath.persistence.support.OrderCriterion.ResultType;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.EpInvalidOrderCriterionResultTypeException;
import com.elasticpath.service.search.query.OrderReturnSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * Test <code>OrderCriterionImpl</code>.
 */
public class OrderCriterionImplTest {

	private static final int NO_3 = 3;

	private OrderCriterionImpl orderCriterionImpl;

	private static final OrderStatus ORDER_STATUS = OrderStatus.CANCELLED;

	private static final OrderPaymentStatus PAYMENT_STATUS = OrderPaymentStatus.APPROVED;

	private static final OrderShipmentStatus SHIPMENT_STATUS = OrderShipmentStatus.CANCELLED;

	private static final String ZIPCODE = "zipcode";

	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {
		this.orderCriterionImpl = new OrderCriterionImpl();
	}

	private String getOrder() {
		return "select o from OrderImpl as o";
	}

	private String getInnerJoinPayments() {
		return ", in(o.orderPayments) as op";
	}

	private String getInnerJoinShipments() {
		return ", in(o.shipments) as os";
	}

	private String getWhere() {
		return  " where o = o";
	}

	private String getOrderStatusCondition(final int positionOfParam) {
		return " and o.status = ?" + positionOfParam;
	}

	private String getPaymentStatusCondition(final int positionOfParam) {
		return " and op.status = ?" + positionOfParam;
	}

	private String getShipmentStatusCondition(final int positionOfParam) {
		return " and os.status = ?" + positionOfParam;
	}

	/**
	 * Test method for 'com.elasticpath.persistence.support.impl.OrderCriterionImpl.getStatusCriteria'.
	 */
	@Test
	public void testGetStatusCriteria() {
		final StringBuffer fullQuery = new StringBuffer();
		fullQuery.append(getOrder()).append(getInnerJoinPayments()).append(getInnerJoinShipments()).append(getWhere());
		fullQuery.append(getOrderStatusCondition(1)).append(getPaymentStatusCondition(2)).append(getShipmentStatusCondition(NO_3));

		final StringBuffer orderStatusQuery = new StringBuffer();
		orderStatusQuery.append(getOrder()).append(getWhere()).append(getOrderStatusCondition(1));

		final StringBuffer orderPaymentStatusQuery = new StringBuffer();
		orderPaymentStatusQuery.append(getOrder()).append(getInnerJoinPayments()).append(getWhere());
		orderPaymentStatusQuery.append(getOrderStatusCondition(1)).append(getPaymentStatusCondition(2));

		final StringBuffer orderShipmentStatusQuery = new StringBuffer();
		orderShipmentStatusQuery.append(getOrder()).append(getInnerJoinShipments()).append(getWhere());
		orderShipmentStatusQuery.append(getOrderStatusCondition(1)).append(getShipmentStatusCondition(2));

		final StringBuffer paymentStatusQuery = new StringBuffer();
		paymentStatusQuery.append(getOrder()).append(getInnerJoinPayments()).append(getWhere());
		paymentStatusQuery.append(getPaymentStatusCondition(1));

		final StringBuffer paymentShipmentStatusQuery = new StringBuffer();
		paymentShipmentStatusQuery.append(getOrder()).append(getInnerJoinPayments()).append(getInnerJoinShipments()).append(getWhere());
		paymentShipmentStatusQuery.append(getPaymentStatusCondition(1)).append(getShipmentStatusCondition(2));

		final StringBuffer shipmentStatusQuery = new StringBuffer();
		shipmentStatusQuery.append(getOrder()).append(getInnerJoinShipments()).append(getWhere());
		shipmentStatusQuery.append(getShipmentStatusCondition(1));

		final StringBuffer noStatusQuery = new StringBuffer();
		noStatusQuery.append(getOrder()).append(getWhere());

		assertEquals(fullQuery.toString(), orderCriterionImpl.getStatusCriteria(ORDER_STATUS, PAYMENT_STATUS, SHIPMENT_STATUS));
		assertEquals(orderStatusQuery.toString(), orderCriterionImpl.getStatusCriteria(ORDER_STATUS, null, null));
		assertEquals(orderPaymentStatusQuery.toString(), orderCriterionImpl.getStatusCriteria(ORDER_STATUS, PAYMENT_STATUS, null));
		assertEquals(orderShipmentStatusQuery.toString(), orderCriterionImpl.getStatusCriteria(ORDER_STATUS, null, SHIPMENT_STATUS));
		assertEquals(paymentStatusQuery.toString(), orderCriterionImpl.getStatusCriteria(null, PAYMENT_STATUS, null));
		assertEquals(paymentShipmentStatusQuery.toString(), orderCriterionImpl.getStatusCriteria(null, PAYMENT_STATUS, SHIPMENT_STATUS));
		assertEquals(shipmentStatusQuery.toString(), orderCriterionImpl.getStatusCriteria(null, null, SHIPMENT_STATUS));
		assertEquals(noStatusQuery.toString(), orderCriterionImpl.getStatusCriteria(null, null, null));
	}

	/**
	 * Test method for 'com.elasticpath.persistence.support.impl.OrderCriterionImpl.getOrderCustomerCriteria'.
	 */
	@Test
	public void testGetOrderCustomerCriteria() {
		//final String propertyName, final String criteriaValue, final boolean isExactMatch
		final String propertyName = "email";
		final String criteriaValue = "test";
		final String exactMatchQuery = "select o from OrderImpl as o where o.customer." + propertyName + " = '" + criteriaValue + "'";
		final String fuzzyMatchQuery = "select o from OrderImpl as o where o.customer." + propertyName + " like '%" + criteriaValue + "%'";

		assertEquals(exactMatchQuery, orderCriterionImpl.getOrderCustomerCriteria(propertyName, criteriaValue, true));
		assertEquals(fuzzyMatchQuery, orderCriterionImpl.getOrderCustomerCriteria(propertyName, criteriaValue, false));
 	}

	/**
	 * Test method for 'com.elasticpath.persistence.support.impl.OrderCriterionImpl.getAdvancedOrderCriteria'.
	 */
	@Test
	public void testGetAdvancedOrderCriteria() {
		final AdvancedOrderSearchCriteria orderSearchCriteria = new AdvancedOrderSearchCriteriaImpl();
		final String emptySearch = "select o from OrderImpl as o";
		assertEquals(emptySearch, orderCriterionImpl.getAdvancedOrderCriteria(orderSearchCriteria));

		final Map<String, String> shipmentAddressCriteria = new HashMap<String, String>();
		shipmentAddressCriteria.put("zipOrPostalCode", "V5Y");
		orderSearchCriteria.setShipmentAddressCriteria(shipmentAddressCriteria);
		final String expectedQuery =
			"select o from OrderImpl as o, in(o.shipments) as os where os.shipmentAddress.zipOrPostalCode like '%V5Y%'";
		assertEquals(expectedQuery, orderCriterionImpl.getAdvancedOrderCriteria(orderSearchCriteria));

		orderSearchCriteria.setOrderToDate(new Date());
		final String expectedQuery1 = expectedQuery + " and o.createdDate <= ?1";
		assertEquals(expectedQuery1, orderCriterionImpl.getAdvancedOrderCriteria(orderSearchCriteria));

		orderSearchCriteria.setOrderStatus(OrderStatus.CANCELLED);
		Calendar from = new GregorianCalendar(Calendar.YEAR, Calendar.AUGUST, Calendar.DAY_OF_MONTH);
		orderSearchCriteria.setOrderFromDate(from.getTime());
		final String expectedQuery2 = expectedQuery + " and o.status = CANCELLED and o.createdDate >= ?1 and o.createdDate <= ?2";
		assertEquals(expectedQuery2, orderCriterionImpl.getAdvancedOrderCriteria(orderSearchCriteria));

		final Map<String, String> customerCriteriaMap = new HashMap<String, String>();
		orderSearchCriteria.setCustomerCriteria(customerCriteriaMap);
		assertEquals(expectedQuery2, orderCriterionImpl.getAdvancedOrderCriteria(orderSearchCriteria));

		customerCriteriaMap.put("email", "test");
		customerCriteriaMap.put("firstName", "name");
		orderSearchCriteria.setCustomerCriteria(customerCriteriaMap);
		final String expectedQuery3 = expectedQuery2 + " and o.customer.email like '%test%' and o.customer.firstName like '%name%'";
		assertEquals(expectedQuery3, orderCriterionImpl.getAdvancedOrderCriteria(orderSearchCriteria));

//		orderSearchCriteria.setSkuCode("SKUCODE");
//		final String expectedQuery4 = expectedQuery3 + " and os.shipmentOrderSkusInternal.skuCode like '%SKUCODE%'";
//		assertEquals(expectedQuery4, orderCriterionImpl.getAdvancedOrderCriteria(orderSearchCriteria));

		orderSearchCriteria.setShipmentStatus(OrderShipmentStatus.RELEASED);
		final String expectedQuery5 = expectedQuery3 + " and o.shipments.status = RELEASED";
		assertEquals(expectedQuery5, orderCriterionImpl.getAdvancedOrderCriteria(orderSearchCriteria));


	}

	/**
	 * Test method for 'com.elasticpath.persistence.support.impl.OrderCriterionImpl.getAdvancedOrderCriteria'.
	 */
	@Test
	public void testGetOrderSearchCriteria() {
		final OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria();
		List<Object> parameters = new LinkedList<Object>();
		Set<String> storeCodes = new HashSet<String>();
		final String emptySearch = "select o from OrderImpl as o";
		assertEquals(emptySearch
				+ " order by o.orderNumber desc",
				orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, parameters,
						storeCodes, ResultType.ENTITY));

		orderSearchCriteria.setOrderToDate(new Date());
		final String expectedQuery1 = emptySearch + " where o.createdDate <= ?1 order by o.orderNumber desc";
		assertEquals(expectedQuery1, orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, parameters, storeCodes, ResultType.ENTITY));

		orderSearchCriteria.setOrderStatus(OrderStatus.CANCELLED);
		Calendar from = new GregorianCalendar(Calendar.YEAR, Calendar.AUGUST, Calendar.DAY_OF_MONTH);
		orderSearchCriteria.setOrderFromDate(from.getTime());
		final String expectedQuery2 = emptySearch
			+ " where o.status = ?1 and o.createdDate <= ?2 and o.createdDate >= ?3 order by o.orderNumber desc";
		assertEquals(expectedQuery2, orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, parameters, storeCodes, ResultType.ENTITY));

		orderSearchCriteria.clear();
		orderSearchCriteria.setSkuCode("SKUCODE");
		final String expectedQuery4 = emptySearch
			+ ", in(o.shipments) as os, in(os.shipmentOrderSkusInternal) as sku where sku.skuCode = ?1 order by o.orderNumber desc";
		assertEquals(expectedQuery4, orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, parameters, storeCodes, ResultType.ENTITY));

		orderSearchCriteria.clear();
		orderSearchCriteria.setSortingOrder(SortOrder.ASCENDING);
		orderSearchCriteria.setSortingType(StandardSortBy.CUSTOMER_NAME);
		orderSearchCriteria.setShipmentStatus(OrderShipmentStatus.RELEASED);
		final String expectedQuery5 = emptySearch
		+ ", in(o.shipments) as os where os.status = ?1 order by o.billingAddress.firstName asc, o.billingAddress.lastName asc, o.orderNumber asc";
		assertEquals(expectedQuery5, orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, parameters, storeCodes, ResultType.ENTITY));

		orderSearchCriteria.clear();
		orderSearchCriteria.setShipmentZipcode(ZIPCODE);
		final String expectedQuery6 = emptySearch + " where o.billingAddress.zipOrPostalCode LIKE ?1 order by o.billingAddress.firstName asc, "
										+ "o.billingAddress.lastName asc, o.orderNumber asc";
		assertEquals(expectedQuery6, orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, parameters, storeCodes, ResultType.ENTITY));

	}

	/**
	 * Test method for 'com.elasticpath.persistence.support.impl.OrderCriterionImpl.getAdvancedOrderCriteria'.
	 */
	@Test
	public void testGetOrderReturnSearchCriteria() {
		final OrderReturnSearchCriteria orderReturnSearchCriteria = new OrderReturnSearchCriteria();
		orderReturnSearchCriteria.setSortingOrder(SortOrder.ASCENDING);
		List<Object> parameters = new LinkedList<Object>();

		final String emptySearch = "select ort from OrderReturnImpl as ort";
		assertEquals(emptySearch + " order by ort.rmaCode asc",
				orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, parameters, ResultType.ENTITY));

		final String rmaCode = "1";
		orderReturnSearchCriteria.setRmaCode(rmaCode);
		final String expectedQuery1 = emptySearch + " where ort.rmaCode = ?1 order by ort.rmaCode asc";
		assertEquals(expectedQuery1, orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, parameters, ResultType.ENTITY));
		String parameter1 = (String) parameters.get(0);
		assertEquals(rmaCode, parameter1);

		String orderNumber = "100000";
		orderReturnSearchCriteria.setOrderNumber(orderNumber);
		final String expectedQuery2 = emptySearch + ", in(ort.order) as o where ort.rmaCode = ?1 and o.orderNumber = ?2 order by ort.rmaCode asc";
		assertEquals(expectedQuery2, orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, parameters, ResultType.ENTITY));
		String parameter2 = (String) parameters.get(1);
		assertEquals(orderNumber, parameter2);

		String firstName = "firstName";
		CustomerSearchCriteria customerSearchCriteria = new CustomerSearchCriteria();
		customerSearchCriteria.setFirstName(firstName);
		orderReturnSearchCriteria.setCustomerSearchCriteria(customerSearchCriteria);
		final String expectedQuery3 = emptySearch
			+ ", in(ort.order) as o where ort.rmaCode = ?1 and o.orderNumber = ?2 "
			+ "and o.billingAddress.firstName = ?3 order by ort.rmaCode asc";
		assertEquals(expectedQuery3, orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, parameters, ResultType.ENTITY));
		String parameter3 = (String) parameters.get(2);
		assertEquals(firstName, parameter3);

		String lastName = "lastName";
		customerSearchCriteria.setLastName(lastName);
		final String expectedQuery4 = emptySearch
			+ ", in(ort.order) as o where ort.rmaCode = ?1 and o.orderNumber = ?2 "
			+ "and o.billingAddress.firstName = ?3 and o.billingAddress.lastName = ?4 "
			+ "order by ort.rmaCode asc";
		assertEquals(expectedQuery4, orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, parameters, ResultType.ENTITY));
		final int index = 3;
		String parameter4 = (String) parameters.get(index);
		assertEquals(lastName, parameter4);
	}

	/**
	 * Test method for 'com.elasticpath.persistence.support.impl.OrderCriterionImpl.getAdvancedOrderCriteria'.
	 */
	@Test
	public void testGetOrderReturnSearchCriteriaSorting() {
		final OrderReturnSearchCriteria orderReturnSearchCriteria = new OrderReturnSearchCriteria();
		orderReturnSearchCriteria.setSortingOrder(SortOrder.ASCENDING);
		List<Object> parameters = new LinkedList<Object>();

		//default rmaCode
		final String emptySearch = "select ort from OrderReturnImpl as ort";
		assertEquals(emptySearch + " order by ort.rmaCode asc",
				orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, parameters, ResultType.ENTITY));

		orderReturnSearchCriteria.setSortingOrder(SortOrder.DESCENDING);
		assertEquals(emptySearch + " order by ort.rmaCode desc",
				orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, parameters, ResultType.ENTITY));

		orderReturnSearchCriteria.setSortingType(StandardSortBy.ORDER_NUMBER);
		assertEquals(emptySearch + " order by o.orderNumber desc",
				orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, parameters, ResultType.ENTITY));

		orderReturnSearchCriteria.setSortingType(StandardSortBy.DATE);
		assertEquals(emptySearch + " order by ort.createdDate desc",
				orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, parameters, ResultType.ENTITY));

		orderReturnSearchCriteria.setSortingType(StandardSortBy.STATUS);
		assertEquals(emptySearch + " order by ort.returnStatus desc",
				orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, parameters, ResultType.ENTITY));
	}

	/**
	 * Test method for 'OrderCriterionImpl.getOrderSearchCriteria(OrderSearchCriteria, List<Object>, Collection<String>, ResultType)'.
	 */
	@Test
	public void testGetSearchCriteriaForOrderNumber() {
		final OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria();
		List<Object> parameters = new LinkedList<Object>();
		Set<String> storeCodes = new HashSet<String>();
		final String emptySearch = "select o.orderNumber from OrderImpl as o";
		assertEquals(emptySearch
				+ " order by o.orderNumber desc",
				orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, parameters,
						storeCodes, ResultType.ORDER_NUMBER));

		orderSearchCriteria.setOrderToDate(new Date());
		final String expectedQuery1 = emptySearch + " where o.createdDate <= ?1 order by o.orderNumber desc";
		assertEquals(expectedQuery1, orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, parameters, storeCodes, ResultType.ORDER_NUMBER));

		orderSearchCriteria.setOrderStatus(OrderStatus.CANCELLED);
		Calendar from = new GregorianCalendar(Calendar.YEAR, Calendar.AUGUST, Calendar.DAY_OF_MONTH);
		orderSearchCriteria.setOrderFromDate(from.getTime());
		final String expectedQuery2 = emptySearch
			+ " where o.status = ?1 and o.createdDate <= ?2 and o.createdDate >= ?3 order by o.orderNumber desc";
		assertEquals(expectedQuery2, orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, parameters, storeCodes, ResultType.ORDER_NUMBER));

		orderSearchCriteria.clear();
		orderSearchCriteria.setSkuCode("SKUCODE");
		final String expectedQuery4 = emptySearch
			+ ", in(o.shipments) as os, in(os.shipmentOrderSkusInternal) as sku where sku.skuCode = ?1 order by o.orderNumber desc";
		assertEquals(expectedQuery4, orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, parameters, storeCodes, ResultType.ORDER_NUMBER));

		orderSearchCriteria.clear();
		orderSearchCriteria.setSortingOrder(SortOrder.ASCENDING);
		orderSearchCriteria.setSortingType(StandardSortBy.CUSTOMER_NAME);
		orderSearchCriteria.setShipmentStatus(OrderShipmentStatus.RELEASED);
		final String expectedQuery5 = emptySearch
		+ ", in(o.shipments) as os where os.status = ?1 order by o.billingAddress.firstName asc, o.billingAddress.lastName asc, o.orderNumber asc";
		assertEquals(expectedQuery5, orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, parameters, storeCodes, ResultType.ORDER_NUMBER));

		orderSearchCriteria.clear();
		orderSearchCriteria.setShipmentZipcode(ZIPCODE);
		final String expectedQuery6 = emptySearch + " where o.billingAddress.zipOrPostalCode LIKE ?1 order by o.billingAddress.firstName asc, "
										+ "o.billingAddress.lastName asc, o.orderNumber asc";
		assertEquals(expectedQuery6, orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, parameters, storeCodes, ResultType.ORDER_NUMBER));

	}

	/**
	 * Test method for 'com.elasticpath.persistence.support.impl.OrderCriterionImpl.getOrderReturnSearchCriteria'.
	 */
	@Test(expected = EpInvalidOrderCriterionResultTypeException.class)
	public void testGetOrderReturnSearchCriteriaWithException() {
		final OrderReturnSearchCriteria orderReturnSearchCriteria = new OrderReturnSearchCriteria();
		orderReturnSearchCriteria.setSortingOrder(SortOrder.ASCENDING);
		List<Object> parameters = new LinkedList<Object>();

		orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, parameters, ResultType.ORDER_NUMBER);
	}
}
