package com.elasticpath.plugin.payment.dto.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.OrderSkuDto;

/**
 * Implementation of {@link OrderSkuDto}. Used in Payment Gateways.
 */
public class OrderSkuDtoImpl implements OrderSkuDto {
	private String skuCode;
	
	private String displayName;
	
	private BigDecimal unitPrice;
	
	private BigDecimal taxAmount;
	
	private int quantity;

	@Override
	public String getSkuCode() {
		return skuCode;
	}

	@Override
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	@Override
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	@Override
	public void setUnitPrice(final BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	@Override
	public int getQuantity() {
		return quantity;
	}

	@Override
	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}

	@Override
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	@Override
	public void setTaxAmount(final BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
