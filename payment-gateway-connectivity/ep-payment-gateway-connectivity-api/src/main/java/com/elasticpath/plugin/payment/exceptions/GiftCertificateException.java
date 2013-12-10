/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.plugin.payment.exceptions;


/**
 * Exception to throw if the some thing wrong on the gift certificate to complete the order.
 */
public class GiftCertificateException extends PaymentProcessingException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * @param message error message
	 */
	public GiftCertificateException(final String message) {
		super(message);
	}
}

