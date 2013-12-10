package com.elasticpath.common.pricing.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;

import com.elasticpath.common.pricing.service.BaseAmountFilter;


/**
 * Implementation of the BaseAmountFilter.
 */
public class BaseAmountFilterImpl implements BaseAmountFilter, Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private String objectGuid;
	private String objectType;
	private BigDecimal sale;
	private BigDecimal list;
	private BigDecimal quantity;
	private String descriptorGuid;
	
	@Override
	public String getObjectGuid() {
		return objectGuid;
	}

	@Override
	public String getObjectType() {
		return objectType;
	}

	@Override
	public String getPriceListDescriptorGuid() {
		return descriptorGuid;
	}

	
	@Override
	public BigDecimal getListValue() {
		return list;
	}
	
	@Override
	public BigDecimal getSaleValue() {
		return sale;
	}
	
	@Override
	public BigDecimal getQuantity() {
		return quantity;
	}

	@Override
	public void setObjectGuid(final String guid) {
		this.objectGuid = guid;
	}

	@Override
	public void setObjectType(final String type) {
		this.objectType = type;
	}

	@Override
	public void setPriceListDescriptorGuid(final String descriptorGuid) {
		this.descriptorGuid = descriptorGuid;
	}

	@Override
	public void setSaleValue(final BigDecimal sale) {
		this.sale = sale;
	}

	@Override
	public void setListValue(final BigDecimal list) {
		this.list = list;
	}

	@Override
	public void setQuantity(final BigDecimal quantity) {
		this.quantity = quantity;
	}

	@Override
	public int hashCode() {
		return objectGuid.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return (objectGuid != null && objectGuid.equals(((BaseAmountFilter) obj).getObjectGuid()))
		|| (descriptorGuid != null && descriptorGuid.equals(((BaseAmountFilter) obj).getPriceListDescriptorGuid()));
	}

	@Override
	public String toString() {
		return objectGuid;
	}

}
