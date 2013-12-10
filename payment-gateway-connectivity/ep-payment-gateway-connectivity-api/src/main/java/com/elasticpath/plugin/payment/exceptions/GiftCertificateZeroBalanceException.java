/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.plugin.payment.exceptions;


/**
 * This exception is thrown when GC balance is less than zero.
 */
public class GiftCertificateZeroBalanceException extends PaymentProcessingException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * 
	 * @param message error message
	 */
	public GiftCertificateZeroBalanceException(final String message) {
		super(message);
	}

}
