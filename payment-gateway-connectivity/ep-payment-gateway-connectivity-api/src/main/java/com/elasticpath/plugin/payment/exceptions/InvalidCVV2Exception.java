/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.plugin.payment.exceptions;


/**
 * This exception is thrown when CVV2 does not match with Credit Card.
 */
public class InvalidCVV2Exception extends PaymentProcessingException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * 
	 * @param message error message
	 */
	public InvalidCVV2Exception(final String message) {
		super(message);
	}

}
