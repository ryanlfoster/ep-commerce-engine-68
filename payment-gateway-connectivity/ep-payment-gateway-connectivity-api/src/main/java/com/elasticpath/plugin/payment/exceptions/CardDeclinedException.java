/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.plugin.payment.exceptions;

/**
 * This exception is thrown when a credit card being processed has is declined, typically because the card credit limit has been reached.
 */
public class CardDeclinedException extends CardErrorException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * 
	 * @param message error message
	 */
	public CardDeclinedException(final String message) {
		super(message);
	}

}
