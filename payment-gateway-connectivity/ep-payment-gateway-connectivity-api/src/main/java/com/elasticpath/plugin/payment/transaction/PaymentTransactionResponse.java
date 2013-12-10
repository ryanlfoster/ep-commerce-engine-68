package com.elasticpath.plugin.payment.transaction;


/**
 * Represents a response returned from a payment gateway transaction.
 */
public interface PaymentTransactionResponse {
	/**
	 * Get the reference id. The reference ID is basically a merchant reference code, and is usually set to the Order Number associated with this
	 * payment.
	 * 
	 * @return the reference id.
	 */
	String getReferenceId();

	/**
	 * Set the reference id. 
	 * 
	 * @param referenceId the reference id.
	 */
	void setReferenceId(final String referenceId);
	
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
	
	/**
	 * Get the customer's email address (Required for card processing by PayPal Express).
	 * 
	 * @return the customer email address
	 */
	String getEmail();

	/**
	 * Set the customer's email address (Required for card processing by PayPal Express).
	 * 
	 * @param email the customer's email address
	 */
	void setEmail(final String email);

}
