package com.elasticpath.plugin.payment.capabilities;

import com.elasticpath.plugin.payment.dto.OrderPaymentDto;

/**
 * A capability that Payment Gateways which support the voiding of a previously authorized or captured amount should implement.
 */
public interface VoidCaptureCapability extends PaymentGatewayCapability {
	/**
	 * Void a previous capture or credit. Can usually only be executed on the same day of the
	 * original transaction.
	 *
	 * @param payment the payment to be voided
	 * @throws com.elasticpath.plugin.payment.exceptions.CardExpiredException if the card has expired
	 * @throws com.elasticpath.plugin.payment.exceptions.CardErrorException if there was an error processing the given information
	 * @throws com.elasticpath.plugin.payment.exceptions.PaymentGatewayException if the payment processing fails
	 */
	void voidCaptureOrCredit(OrderPaymentDto payment);
}
