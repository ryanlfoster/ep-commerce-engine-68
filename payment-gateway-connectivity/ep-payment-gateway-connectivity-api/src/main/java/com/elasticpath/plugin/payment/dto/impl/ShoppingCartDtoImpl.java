package com.elasticpath.plugin.payment.dto.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;

/**
 * Implementation of {@link ShoppingCartDto}. Used in Payment Gateways.
 */
public class ShoppingCartDtoImpl implements ShoppingCartDto {
	
	private String country;
	
	private String currencyCode;
	
	private BigDecimal totalAmount;
	
	private AddressDto shippingAddress;
	
	private boolean requiresShipping;

	@Override
	public String getCountry() {
		return country;
	}

	@Override
	public void setCountry(final String country) {
		this.country = country;
	}

	@Override
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	@Override
	public void setTotalAmount(final BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String getCurrencyCode() {
		return currencyCode;
	}

	@Override
	public void setCurrencyCode(final String currencyCode) {
		this.currencyCode = currencyCode;
	}

	@Override
	public AddressDto getShippingAddress() {
		return shippingAddress;
	}

	@Override
	public void setShippingAddress(final AddressDto shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	@Override
	public boolean isRequiresShipping() {
		return requiresShipping;
	}

	@Override
	public void setRequiresShipping(final boolean requireShipping) {
		this.requiresShipping = requireShipping;
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
