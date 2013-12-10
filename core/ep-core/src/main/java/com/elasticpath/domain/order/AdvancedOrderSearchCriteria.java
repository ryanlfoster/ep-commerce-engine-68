package com.elasticpath.domain.order;

import java.util.Date;
import java.util.Map;

import com.elasticpath.domain.EpDomain;

/**
 * Represents criteria for advancec order search.
 * Allow to specify criteria on order(order creation date range and order status), order customer(email,
 * firstName, lastName and phoneNumber), shippin address zip / postal code, order sku code(if the order contains
 * the sku with the specified sku code) and order shipment status.
 */
public interface AdvancedOrderSearchCriteria extends EpDomain {

	/**
	 * Get the order status criteria.
	 * @return the order status criteria.
	 */
	OrderStatus getOrderStatus();

	/**
	 * Set the order status criteria.
	 * @param orderStatus the order status criteria.
	 */
	void setOrderStatus(final OrderStatus orderStatus);

	/**
	 * Get the from date for order createDate search.
	 * @return the from date for order createDate search.
	 */
	Date getOrderFromDate();

	/**
	 * Set the from date for order createDate search.
	 * @param orderFromDate the from date for order createDate search.
	 */
	void setOrderFromDate(final Date orderFromDate);

	/**
	 * Get the to date for order createDate search.
	 * @return the to date for order createDate search.
	 */
	Date getOrderToDate();

	/**
	 * Set the to date for order createDate search.
	 * @param orderToDate the to date for order createDate search.
	 */
	void setOrderToDate(final Date orderToDate);

	/**
	 * Get the map of order customer property name to criteria value.
	 * @return the map of order customer property name to criteria value
 	 */
	Map<String, String> getCustomerCriteria();

	/**
	 * Set the map of order customer property name to criteria value.
	 * @param customerCriteria the map of order customer property name to criteria value
	 */
	void setCustomerCriteria(final Map<String, String> customerCriteria);

	/**
	 * Get the map of order shipmentAddress property name to criteria value.
	 * @return the map of order shipmentAddress property name to criteria value
 	 */
	Map<String, String> getShipmentAddressCriteria();

	/**
	 * Set the map of order shipmentAddress property name to criteria value.
	 * @param shipmentAddressCriteria the map of order shipmentAddress property name to criteria value
 	 */
	void setShipmentAddressCriteria(final Map<String, String> shipmentAddressCriteria);

	/**
	 * Get the sku code for search.
	 * @return the sku code for search.
	 */
	String getSkuCode();

	/**
	 * Set the sku code for search.
	 * @param skuCode the sku code for search.
	 */
	void setSkuCode(final String skuCode);

	/**
	 * Get the shipment status.
	 * @return the shipment status.
	 */
	OrderShipmentStatus getShipmentStatus();

	/**
	 * Set the shipment status.
	 * @param shipmentStatus the shipment status.
	 */
	void setShipmentStatus(final OrderShipmentStatus shipmentStatus);

	/**
	 * Get the store code for search.
	 *
	 * @return the store code for search.
	 */
	String getStoreCode();

	/**
	 * Set the store code for search.
	 *
	 * @param storeCode the store code for search.
	 */
	void setStoreCode(final String storeCode);
}
