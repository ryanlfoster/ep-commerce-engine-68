/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.plugin.payment.exceptions;


/**
 * This exception is thrown when currency of the shopping cart GC does not match.
 */
public class GiftCertificateCurrencyMismatchException extends PaymentProcessingException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * 
	 * @param message error message
	 */
	public GiftCertificateCurrencyMismatchException(final String message) {
		super(message);
	}

}
