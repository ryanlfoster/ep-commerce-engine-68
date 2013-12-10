/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.plugin.payment.exceptions;


/**
 * Exception to throw if the gift certificate has insufficient fund to complete the order.
 */
public class InsufficientGiftCertificateBalanceException extends PaymentProcessingException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
		
	/**
	 * Constructor.
	 * @param message error message
	 */
	public InsufficientGiftCertificateBalanceException(final String message) {
		super(message);
	}
}

