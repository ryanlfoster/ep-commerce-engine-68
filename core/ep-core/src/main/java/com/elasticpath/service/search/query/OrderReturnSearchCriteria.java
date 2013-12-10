package com.elasticpath.service.search.query;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;

/**
 * A criteria for advanced order return search.
 */
public class OrderReturnSearchCriteria extends AbstractSearchCriteriaImpl {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private CustomerSearchCriteria customerSearchCriteria;

	private String orderNumber;

	private String rmaCode;

	private Set<String> warehouseCodes;

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
	 * Optimizes a search criteria by removing unnecessary information.
	 */
	public void optimize() {
		if (!isStringValid(orderNumber)) {
			orderNumber = null;
		}
		if (!isStringValid(rmaCode)) {
			rmaCode = null;
		}
		if (customerSearchCriteria != null) {
			customerSearchCriteria.optimize();
		}
	}

	@Override
	@SuppressWarnings("PMD.CloneMethodMustImplementCloneable")
	public SearchCriteria clone() throws CloneNotSupportedException {
		OrderReturnSearchCriteria searchCriteria = (OrderReturnSearchCriteria) super.clone();

		if (customerSearchCriteria != null) {
			searchCriteria.setCustomerSearchCriteria((CustomerSearchCriteria) customerSearchCriteria.clone());
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

	public void setWarehouseCodes(final Set<String> warehouseCodes) {
		this.warehouseCodes = warehouseCodes;
	}

	public Set<String> getWarehouseCodes() {
		return warehouseCodes;
	}

	/**
	 * @return the warehouse codes criteria.
	 */
	public String getWarehouseCode() {
		if (CollectionUtils.isNotEmpty(warehouseCodes)) {
			return (String) CollectionUtils.get(warehouseCodes, 0);
		}

		return null;
	}

	/**
	 * @param warehouseCode the warehouse code criteria.
	 */
	public void setWarehouseCode(final String warehouseCode) {
		warehouseCodes = new HashSet<String>();
		warehouseCodes.add(warehouseCode);
	}

}
