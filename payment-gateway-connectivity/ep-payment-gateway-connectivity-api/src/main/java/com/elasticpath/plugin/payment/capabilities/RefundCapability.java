package com.elasticpath.plugin.payment.capabilities;

import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;

/**
 * A capability that Payment Gateways which support refund of a capture should implement.
 */
public interface RefundCapability extends PaymentGatewayCapability {
	/**
	 * Refunds a previous capture or refunds to a stand-alone transaction.
	 *
	 * There are two type of refunds:
	 * - stand-alone - no previous capture is needed
	 * - follow-up - refunds towards a past capture
	 *
	 * @param payment the payment to be refunded
	 * @param billingAddress the billing address if the refund is of stand-alone type or null otherwise
	 * @throws com.elasticpath.plugin.payment.exceptions.CardExpiredException if the card has expired
	 * @throws com.elasticpath.plugin.payment.exceptions.CardErrorException if there was an error processing the given information
	 * @throws com.elasticpath.plugin.payment.exceptions.PaymentGatewayException if the payment processing fails
	 */
	void refund(OrderPaymentDto payment, AddressDto billingAddress);
}
