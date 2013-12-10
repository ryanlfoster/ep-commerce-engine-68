/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.order.impl;

import java.util.Date;
import java.util.Map;

import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.order.AdvancedOrderSearchCriteria;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;

/**
 * The default implementation of <code>OrderSearchCriteria</code>.
 */
public class AdvancedOrderSearchCriteriaImpl extends AbstractEpDomainImpl implements AdvancedOrderSearchCriteria {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private OrderStatus orderStatus;

	private Date orderFromDate;

	private Date orderToDate;

	private Map<String, String> customerCriteria;

	private Map<String, String> shipmentAddressCriteria;

	private String skuCode;

	private OrderShipmentStatus shipmentStatus;

	private String storeCode;

	/**
	 * Get the order status criteria.
	 *
	 * @return the order status criteria.
	 */
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	/**
	 * Set the order status criteria.
	 *
	 * @param orderStatus the order status criteria.
	 */
	public void setOrderStatus(final OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	/**
	 * Get the from date for order createDate search.
	 *
	 * @return the from date for order createDate search.
	 */
	public Date getOrderFromDate() {
		return orderFromDate;
	}

	/**
	 * Set the from date for order createDate search.
	 *
	 * @param orderFromDate the from date for order createDate search.
	 */
	public void setOrderFromDate(final Date orderFromDate) {
		this.orderFromDate = orderFromDate;
	}

	/**
	 * Get the to date for order createDate search.
	 *
	 * @return the to date for order createDate search.
	 */
	public Date getOrderToDate() {
		return orderToDate;
	}

	/**
	 * Set the to date for order createDate search.
	 *
	 * @param orderToDate the to date for order createDate search.
	 */
	public void setOrderToDate(final Date orderToDate) {
		this.orderToDate = orderToDate;
	}

	/**
	 * Get the map of order customer property name to criteria value.
	 *
	 * @return the map of order customer property name to criteria value
	 */
	public Map<String, String> getCustomerCriteria() {
		return customerCriteria;
	}

	/**
	 * Set the map of order customer property name to criteria value.
	 *
	 * @param customerCriteria the map of order customer property name to criteria value
	 */
	public void setCustomerCriteria(final Map<String, String> customerCriteria) {
		this.customerCriteria = customerCriteria;
	}

	/**
	 * Get the map of order shipmentAddress property name to criteria value.
	 *
	 * @return the map of order shipmentAddress property name to criteria value
	 */
	public Map<String, String> getShipmentAddressCriteria() {
		return shipmentAddressCriteria;
	}

	/**
	 * Set the map of order shipmentAddress property name to criteria value.
	 *
	 * @param shipmentAddressCriteria the map of order shipmentAddress property name to criteria value
	 */
	public void setShipmentAddressCriteria(final Map<String, String> shipmentAddressCriteria) {
		this.shipmentAddressCriteria = shipmentAddressCriteria;
	}

	/**
	 * Get the sku code for search.
	 *
	 * @return the sku code for search.
	 */
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * Set the sku code for search.
	 *
	 * @param skuCode the sku code for search.
	 */
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 * Get the shipment status.
	 *
	 * @return the shipment status.
	 */
	public OrderShipmentStatus getShipmentStatus() {
		return shipmentStatus;
	}

	/**
	 * Set the shipment status.
	 *
	 * @param shipmentStatus the shipment status.
	 */
	public void setShipmentStatus(final OrderShipmentStatus shipmentStatus) {
		this.shipmentStatus = shipmentStatus;
	}

	@Override
	public String getStoreCode() {
		return storeCode;
	}

	@Override
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}
}
