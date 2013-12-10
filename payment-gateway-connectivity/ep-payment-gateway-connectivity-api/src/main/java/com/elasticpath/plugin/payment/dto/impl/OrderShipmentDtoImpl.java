package com.elasticpath.plugin.payment.dto.impl;

import java.math.BigDecimal;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.OrderSkuDto;

/**
 * Implementation of {@link OrderShipmentDto}. Used in Payment Gateways.
 */
public class OrderShipmentDtoImpl implements OrderShipmentDto {
	private boolean physical;
	
	private String carrier;
	
	private String trackingCode;
	
	private String serviceLevel;
	
	private BigDecimal shippingTax;
	
	private BigDecimal shippingCost;
	
	private String shipmentNumber;
	
	private String externalOrderNumber;
	
	private Set<OrderSkuDto> orderSkuDtos;

	@Override
	public boolean isPhysical() {
		return physical;
	}
	
	@Override
	public void setPhysical(final boolean physical) {
		this.physical = physical;
	}

	@Override
	public String getCarrier() {
		return carrier;
	}

	@Override
	public void setCarrier(final String carrier) {
		this.carrier = carrier;
	}

	@Override
	public String getTrackingCode() {
		return trackingCode;
	}

	@Override
	public void setTrackingCode(final String trackingCode) {
		this.trackingCode = trackingCode;
	}

	@Override
	public String getServiceLevel() {
		return serviceLevel;
	}

	@Override
	public void setServiceLevel(final String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	@Override
	public BigDecimal getShippingCost() {
		return shippingCost;
	}

	@Override
	public void setShippingCost(final BigDecimal shippingCost) {
		this.shippingCost = shippingCost;
	}

	@Override
	public BigDecimal getShippingTax() {
		return shippingTax;
	}

	@Override
	public void setShippingTax(final BigDecimal shippingTax) {
		this.shippingTax = shippingTax;
	}

	@Override
	public String getShipmentNumber() {
		return shipmentNumber;
	}

	@Override
	public void setShipmentNumber(final String shipmentNumber) {
		this.shipmentNumber = shipmentNumber;
	}
	
	@Override
	public String getExternalOrderNumber() {
		return externalOrderNumber;
	}

	@Override
	public void setExternalOrderNumber(final String externalOrderNumber) {
		this.externalOrderNumber = externalOrderNumber;
	}

	@Override
	public Set<OrderSkuDto> getOrderSkuDtos() {
		return orderSkuDtos;
	}

	@Override
	public void setOrderSkuDtos(final Set<OrderSkuDto> orderSkuDtos) {
		this.orderSkuDtos = orderSkuDtos;
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
