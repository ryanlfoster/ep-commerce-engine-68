/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.plugin.payment.exceptions;

/**
 * Common ancestor for all payment processing exceptions.
 */
public class PaymentProcessingException extends RuntimeException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>PaymentProcessingException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>PaymentProcessingException</code>.
	 */
	public PaymentProcessingException(final String message) {
		super(message);
	}

}
