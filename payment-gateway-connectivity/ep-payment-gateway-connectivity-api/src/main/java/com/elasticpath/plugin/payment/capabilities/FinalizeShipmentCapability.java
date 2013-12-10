package com.elasticpath.plugin.payment.capabilities;

import com.elasticpath.plugin.payment.dto.OrderShipmentDto;

/**
 * A {@link PaymentGatewayCapability} that Payment Gateways which need to finalize shipments should implement.
 * 
 * Gateways should implement this if they need to finalize a shipment
 * once all payment process has been completed.  This may include, for
 * example, sending confirmation emails from external checkouts.
 */
public interface FinalizeShipmentCapability extends PaymentGatewayCapability {
	/**
	 * Finalize a shipment.  
	 * @param orderShipment <CODE>OrderShipment</CODE> to be finalized.
	 */
	void finalizeShipment(OrderShipmentDto orderShipment);
}