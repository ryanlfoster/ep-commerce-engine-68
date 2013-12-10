package com.elasticpath.plugin.payment.capabilities;

import com.elasticpath.plugin.payment.dto.OrderPaymentDto;

/**
 * A capability that Payment Gateways which support the subsequent reversal of a previously authorized amount should implement.
 */
public interface ReversePreAuthorizationCapability extends PaymentGatewayCapability {
	/**
	 * Reverse a previous pre-authorization. This can only be executed on Visas using the "Vital"
	 * processor and authorizations cannot be reversed using the test server and card info because
	 * the auth codes are not valid (Cybersource).
	 *
	 * @param payment the payment that was previously pre-authorized
	 * @throws com.elasticpath.plugin.payment.exceptions.CardExpiredException if the card has expired
	 * @throws com.elasticpath.plugin.payment.exceptions.CardErrorException if there was an error processing the given information
	 * @throws com.elasticpath.plugin.payment.exceptions.PaymentGatewayException if the payment processing fails
	 */
	void reversePreAuthorization(OrderPaymentDto payment);
}
