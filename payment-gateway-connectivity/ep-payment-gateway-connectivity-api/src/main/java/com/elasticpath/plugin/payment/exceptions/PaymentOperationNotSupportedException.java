/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.plugin.payment.exceptions;


/**
 * Thrown when a payment gateway is requested to perform an operation it does not support.
 * For example certain gateways don't support refund to different accounts.
 */
public class PaymentOperationNotSupportedException extends PaymentGatewayException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message describing the exception
	 */
	public PaymentOperationNotSupportedException(final String message) {
		super(message);
	}
}
