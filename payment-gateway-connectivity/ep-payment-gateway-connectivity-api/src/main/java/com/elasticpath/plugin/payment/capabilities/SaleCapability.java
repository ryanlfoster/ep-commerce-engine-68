package com.elasticpath.plugin.payment.capabilities;

import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;

/**
 * A capability that Payment Gateways which support the sale operation (combined authorization and capture) should implement.
 */
public interface SaleCapability extends PaymentGatewayCapability {
	/**
	 * Marks a transaction for immediate fund transfer without any pre-authorization. Note that
	 * Visa and Mastercard regulations prohibit capturing CC transaction funds until a product or
	 * service has been shipped to the buyer.
	 *
	 * @param payment the payment to be immediately processed
	 * @param billingAddress the name and address of the person being billed
	 * @param shipment the shipment
	 * @throws com.elasticpath.plugin.payment.exceptions.CardExpiredException if the card has expired
	 * @throws com.elasticpath.plugin.payment.exceptions.CardErrorException if there was an error processing the given information
	 * @throws com.elasticpath.plugin.payment.exceptions.PaymentGatewayException if the payment processing fails
	 */
	void sale(OrderPaymentDto payment, AddressDto billingAddress, final OrderShipmentDto shipment);
}
