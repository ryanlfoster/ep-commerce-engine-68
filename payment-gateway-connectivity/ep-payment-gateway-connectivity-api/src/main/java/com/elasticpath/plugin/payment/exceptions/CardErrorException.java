/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.plugin.payment.exceptions;



/**
 * This exception is thrown when a an unspecified error occurs
 * while processing the given card information.
 */
public class CardErrorException extends PaymentProcessingException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * @param message error message
	 */
	public CardErrorException(final String message) {
		super(message);
	}

}
