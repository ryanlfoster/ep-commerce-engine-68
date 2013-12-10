/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.payment;

import java.util.Collection;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;

/**
 * Payment result interface.
 */
public interface PaymentResult {

	/**
	 * Operation OK code.
	 */
	int CODE_OK = 42;
	
	/**
	 * Operation code for failure to process payments.
	 */
	int CODE_FAILED = 1;


	/**
	 * Returns the result code of the operations.
	 * 
	 * @return result code
	 */
	int getResultCode();
	
	/**
	 * Sets the result code.
	 * 
	 * @param resultCode the result code
	 */
	void setResultCode(final int resultCode);

	/**
	 * Processed payments during the operation.
	 * 
	 * @return collection of order payments
	 */
	Collection<OrderPayment> getProcessedPayments();

	/**
	 * Adds the order payment and adjusts the overall result.
	 * 
	 * @param orderPayment the order payment to be set and checked
	 */
	void addProcessedPayment(OrderPayment orderPayment);

	/**
	 * Adds the order payments and adjusts the overall result.
	 * 
	 * @param orderPayments the order payments to be set and checked
	 */
	void addProcessedPayments(Collection<OrderPayment> orderPayments);

	/**
	 * Sets the cause of the payment failure.
	 * 
	 * @param throwable the exception that caused the failure
	 */
	void setCause(PaymentProcessingException throwable);
	
	/**
	 * Get the exception in case an error occurred.
	 * 
	 * @return {@link Throwable}
	 */
	PaymentProcessingException getCause();

	/**
	 * Return a collection of all failed payments.
	 * 
	 * @return collection of order payments
	 */
	Collection<OrderPayment> getFailedPayments();
}
