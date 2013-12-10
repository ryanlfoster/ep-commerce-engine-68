package com.elasticpath.service.search.query;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;

/**
 * A criteria for advanced order search.
 */
public class OrderSearchCriteria extends AbstractSearchCriteriaImpl {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private OrderStatus orderStatus;

	private OrderStatus excludedOrderStatus;

	private Date fromDate;

	private Date toDate;

	private CustomerSearchCriteria customerSearchCriteria;

	private String shipmentZipcode;

	private OrderShipmentStatus shipmentStatus;

	private String orderNumber;

	private String skuCode;

	private String rmaCode;

	private Set<String> storeCodes;

	/**
	 * Get the order status that will be included.
	 * 
	 * @return the order status
	 */
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	/**
	 * Set the order status.
	 * 
	 * @param orderStatus the order status that will be included
	 */
	public void setOrderStatus(final OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	/**
	 * Gets the excluded order statuses.
	 *
	 * @return the excluded order statuses
	 */
	public OrderStatus getExcludedOrderStatus() {
		return excludedOrderStatus;
	}

	/**
	 * Sets the excluded order statuses.
	 *
	 * @param excludeOrderStatus the new excluded order statuses
	 */
	public void setExcludedOrderStatus(final OrderStatus excludeOrderStatus) {
		excludedOrderStatus = excludeOrderStatus;
	}

	/**
	 * Get the from date for order createDate search.
	 * 
	 * @return the from date for order createDate search.
	 */
	public Date getOrderFromDate() {
		return fromDate;
	}

	/**
	 * Set the from date for order createDate search.
	 * 
	 * @param orderFromDate the from date for order createDate search.
	 */
	public void setOrderFromDate(final Date orderFromDate) {
		fromDate = orderFromDate;
	}

	/**
	 * Get the to date for order createDate search.
	 * 
	 * @return the to date for order createDate search.
	 */
	public Date getOrderToDate() {
		return toDate;
	}

	/**
	 * Set the to date for order createDate search.
	 * 
	 * @param orderToDate the to date for order createDate search.
	 */
	public void setOrderToDate(final Date orderToDate) {
		toDate = orderToDate;
	}

	/**
	 * Returns the customer search criteria.
	 * 
	 * @return the customer search criteria
	 */
	public CustomerSearchCriteria getCustomerSearchCriteria() {
		return customerSearchCriteria;
	}

	/**
	 * Sets the customer search criteria.
	 * 
	 * @param customerSearchCriteria the customer search criteria
	 */
	public void setCustomerSearchCriteria(final CustomerSearchCriteria customerSearchCriteria) {
		this.customerSearchCriteria = customerSearchCriteria;
	}

	/**
	 * Gets the shipment zipcode.
	 * 
	 * @return the shipment zipcode.
	 */
	public String getShipmentZipcode() {
		return shipmentZipcode;
	}

	/**
	 * Sets the shipment zipcode.
	 * 
	 * @param shipmentZipcode the shipment zipcode.
	 */
	public void setShipmentZipcode(final String shipmentZipcode) {
		this.shipmentZipcode = shipmentZipcode;
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

	/**
	 * Returns the order number.
	 * 
	 * @return the order number
	 */
	public String getOrderNumber() {
		return orderNumber;
	}

	/**
	 * Sets the order number.
	 * 
	 * @param orderNumber the order number
	 */
	public void setOrderNumber(final String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * Returns the sku code.
	 * 
	 * @return the sku code
	 */
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * Sets the sku code.
	 * 
	 * @param skuCode the sku code
	 */
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 * Gets the RMA code.
	 * 
	 * @return the RMA code
	 */
	public String getRmaCode() {
		return rmaCode;
	}

	/**
	 * Sets the RMA code.
	 * 
	 * @param rmaCode the RMA code
	 */
	public void setRmaCode(final String rmaCode) {
		this.rmaCode = rmaCode;
	}

	/**
	 * Sets store code depending on the applicability.
	 * 
	 * @return String representing the store code
	 */
	public Set<String> getStoreCodes() {
		return storeCodes;
	}

	/**
	 * Gets store code depending on the applicability.
	 * 
	 * @param storeCodes the store code
	 */
	public void setStoreCodes(final Set<String> storeCodes) {
		this.storeCodes = storeCodes;
	}

	/**
	 * Optimizes a search criteria by removing unnecessary information.
	 */
	public void optimize() {
		if (!isStringValid(shipmentZipcode)) {
			shipmentZipcode = null;
		}
		if (!isStringValid(orderNumber)) {
			orderNumber = null;
		}
		if (!isStringValid(skuCode)) {
			skuCode = null;
		}
		if (!isStringValid(rmaCode)) {
			rmaCode = null;
		}
		if (CollectionUtils.isEmpty(storeCodes)) {
			storeCodes = null;
		} else {
			Set<String> tempStoreCodes = new HashSet<String>();
			for (String storeCode : storeCodes) {
				if (isStringValid(storeCode)) {
					tempStoreCodes.add(storeCode);
				}
			}
			storeCodes = tempStoreCodes;
		}
		if (customerSearchCriteria != null) {
			customerSearchCriteria.optimize();
		}
	}

	@Override
	@SuppressWarnings("PMD.CloneMethodMustImplementCloneable")
	public SearchCriteria clone() throws CloneNotSupportedException {
		OrderSearchCriteria searchCriteria = (OrderSearchCriteria) super.clone();

		if (storeCodes != null) {
			Set<String> storeCodes = new HashSet<String>();
			storeCodes.addAll(this.storeCodes);
			searchCriteria.setStoreCodes(storeCodes);
		}
		if (customerSearchCriteria != null) {
			searchCriteria.setCustomerSearchCriteria((CustomerSearchCriteria) customerSearchCriteria.clone());
		}
		if (fromDate != null) {
			searchCriteria.setOrderFromDate(new Date(fromDate.getTime()));
		}
		if (toDate != null) {
			searchCriteria.setOrderToDate(new Date(toDate.getTime()));
		}

		return searchCriteria;
	}

	/**
	 * Returns the index type this criteria deals with.
	 * @return the index type this criteria deals with.
	 */
	public IndexType getIndexType() {
		return null;
	}

	/**
	 * This method will clear the search criteria of any fields that have
	 * already been set.
	 */
	public void clear() {
		orderNumber = null;
		if (customerSearchCriteria != null) {
			customerSearchCriteria.clear();
		}
		fromDate = null;
		toDate = null;
		orderStatus = null;
		excludedOrderStatus = null;
		rmaCode = null;
		shipmentStatus = null;
		shipmentZipcode = null;
		skuCode = null;
		storeCodes = null;
	}
}
