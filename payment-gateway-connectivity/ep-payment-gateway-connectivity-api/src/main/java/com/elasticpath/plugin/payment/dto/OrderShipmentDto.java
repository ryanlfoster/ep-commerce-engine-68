package com.elasticpath.plugin.payment.dto;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for Shipments from Orders. Used in Payment Gateways.
 */
public interface OrderShipmentDto {
	
	/**
	 * Checks if is physical.
	 *
	 * @return true, if is physical
	 */
	boolean isPhysical();
	
	/**
	 * Sets the physical.
	 *
	 * @param physical the new physical
	 */
	void setPhysical(boolean physical);
	
	/**
	 * Gets the carrier.
	 *
	 * @return Returns the carrier.
	 */
	String getCarrier();

	/**
	 * Sets the carrier.
	 *
	 * @param carrier The carrier to set.
	 */
	void setCarrier(final String carrier);

	/**
	 * Gets the tracking code.
	 *
	 * @return Returns the trackingCode.
	 */
	String getTrackingCode();

	/**
	 * Sets the tracking code.
	 *
	 * @param trackingCode The trackingCode to set.
	 */
	void setTrackingCode(final String trackingCode);

	/**
	 * Gets the service level.
	 *
	 * @return Returns the serviceLevel.
	 */
	String getServiceLevel();

	/**
	 * Sets the service level.
	 *
	 * @param serviceLevel The serviceLevel to set.
	 */
	void setServiceLevel(final String serviceLevel);
	
	/**
	 * Get the shipping cost in <code>BigDecimal</code>.
	 *
	 * @return the  shipping cost in <code>BigDecimal</code>.
	 */
	BigDecimal getShippingCost();

	/**
	 * Set the shipping cost in <code>BigDecimal</code>.
	 *
	 * @param shippingCost the shipping cost
	 */
	void setShippingCost(final BigDecimal shippingCost);
	
	/**
	 * Get the shipping tax in <code>BigDecimal</code>.
	 *
	 * @return the shipping tax in <code>BigDecimal</code>.
	 */
	BigDecimal getShippingTax();

	/**
	 * Sets the shipping tax.
	 *
	 * @param shippingTax the new shipping tax
	 */
	void setShippingTax(BigDecimal shippingTax);

	/**
	 * Gets the external order number.
	 *
	 * @return the external order number
	 */
	String getExternalOrderNumber();

	/**
	 * Sets the external order number.
	 *
	 * @param externalOrderNumber the external order number.
	 */
	void setExternalOrderNumber(String externalOrderNumber);

	/**
	 * Gets the shipment number.
	 *
	 * @return the shipment number
	 */
	String getShipmentNumber();

	/**
	 * Sets the shipment number.
	 *
	 * @param shipmentNumber the shipment number.
	 */
	void setShipmentNumber(String shipmentNumber);

	/**
	 * Gets the order sku dtos.
	 *
	 * @return the order sku dtos
	 */
	Set<OrderSkuDto> getOrderSkuDtos();
	
	/**
	 * Sets the order sku dtos.
	 *
	 * @param orderSkuDtos the new order sku dtos
	 */
	void setOrderSkuDtos(Set<OrderSkuDto> orderSkuDtos);
}
