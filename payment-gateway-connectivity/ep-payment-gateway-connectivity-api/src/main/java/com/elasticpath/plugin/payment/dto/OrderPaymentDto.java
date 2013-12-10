package com.elasticpath.plugin.payment.dto;

import com.elasticpath.plugin.payment.transaction.PaymentTransactionRequest;
import com.elasticpath.plugin.payment.transaction.PaymentTransactionResponse;

/**
 * DTO for Payment on an Order. Used in Payment Gateways.
 */
public interface OrderPaymentDto extends CardDetailsPaymentMethod, TokenPaymentMethod, Money, PaymentTransactionResponse, PaymentTransactionRequest {
	/**
	 * Get the requestToken. The request token is a code returned by the payment processor for every request. It is used to associate any transaction
	 * with its associated follow-on transaction, such as a capture transaction with its preceding preauthorization, much like the requestId.
	 *
	 * @return the request token.
	 */
	String getRequestToken();

	/**
	 * Set the request token.
	 *
	 * @param requestToken the request token
	 */
	void setRequestToken(final String requestToken);

	/**
	 * Get the authorization code, returned with every transaction. It is used to associate any transaction with its associated follow-on
	 * transaction, such as a capture transaction with its preceding preauthorization, much like the requestToken.
	 *
	 * @return the authorization code
	 */
	String getAuthorizationCode();

	/**
	 * Set the authorization code, returned with every transaction.
	 *
	 * @param authorizationCode the authorization code
	 */
	void setAuthorizationCode(final String authorizationCode);
}
