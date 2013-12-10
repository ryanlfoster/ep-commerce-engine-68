/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.plugin.payment.exceptions;

/**
 * This exception is thrown when the payment processor fails
 * to process a request.
 */
public class PaymentGatewayException extends RuntimeException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * @param message error message
	 */
	public PaymentGatewayException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>PaymentGatewayException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>PaymentGatewayException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>PaymentGatewayException</code>.
	 */
	public PaymentGatewayException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
