/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.plugin.payment.exceptions;


/**
 * Exception to throw if the credit card has insufficient fund to complete the order.
 */
public class InsufficientFundException extends PaymentProcessingException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * @param message error message
	 */
	public InsufficientFundException(final String message) {
		super(message);
	}
}

