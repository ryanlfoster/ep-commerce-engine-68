package com.elasticpath.plugin.payment.transaction;

/**
 * Represents a request for a payment gateway transaction.
 */
public interface PaymentTransactionRequest {
	
	/**
	 * Get the merchant reference id for this {@link PaymentTransactionRequest}. 
	 * The merchant reference ID is basically a merchant reference code, and is usually set to the Order Number associated with this
	 * payment.
	 *
	 * @return the reference id.
	 */
	String getReferenceId();
	
	/**
	 * Set the merchant reference id on this {@link PaymentTransactionRequest}. 
	 * The merchant reference ID is basically a merchant reference code, and is usually set to the Order Number
	 * or shipment number associated with this payment.
	 *
	 * @param merchantReferenceId the merchant reference id.
	 */
	void setReferenceId(String merchantReferenceId);
}
