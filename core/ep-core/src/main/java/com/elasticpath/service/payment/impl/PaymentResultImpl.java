/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.payment.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;
import com.elasticpath.service.payment.PaymentResult;

/**
 * Used by the payment service to provide information on a operation result.
 */
public class PaymentResultImpl implements PaymentResult, Serializable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private final Collection<OrderPayment> processedPayments = new ArrayList<OrderPayment>();

	private int resultCode = CODE_OK;

	private PaymentProcessingException cause;

	/**
	 * Returns the processed payments.
	 * 
	 * @return collection of order payments
	 */
	public Collection<OrderPayment> getProcessedPayments() {
		return processedPayments;
	}

	/**
	 * Gets the overall result code of all the processed payments.
	 * 
	 * @return OK_CODE or other for failure
	 */
	public int getResultCode() {
		return resultCode;
	}

	/**
	 * Sets the result payments.
	 * 
	 * @param processedPayment the processed payment
	 */
	public void addProcessedPayment(final OrderPayment processedPayment) {
		if (processedPayment != null) {
			this.processedPayments.add(processedPayment);
			// check whether this transaction is not of reverse auth type and if failed set CODE_FAILED
			if (!OrderPayment.REVERSE_AUTHORIZATION.equals(processedPayment.getTransactionType())
					&& processedPayment.getStatus() == OrderPaymentStatus.FAILED) {
				setResultCode(CODE_FAILED);
			}
		}
	}

	/**
	 * Sets the result payments.
	 * 
	 * @param processedPayments the processed payments
	 */
	public void addProcessedPayments(final Collection<OrderPayment> processedPayments) {
		if (processedPayments != null) {
			for (OrderPayment processedPayment : processedPayments) {
				addProcessedPayment(processedPayment);
			}
		}
	}

	/**
	 * Sets the result code.
	 * 
	 * @param resultCode the result code
	 */
	public void setResultCode(final int resultCode) {
		this.resultCode = resultCode;
	}

	@Override
	public PaymentProcessingException getCause() {
		return cause;
	}

	@Override
	public void setCause(final PaymentProcessingException throwable) {
		this.cause = throwable;
	}

	@Override
	public Collection<OrderPayment> getFailedPayments() {
		Collection<OrderPayment> failedPayments = new ArrayList<OrderPayment>();
		for (OrderPayment payment : getProcessedPayments()) {
			if (payment.getStatus() == OrderPaymentStatus.FAILED) {
				failedPayments.add(payment);
			}
		}
		return failedPayments;
	}

}
