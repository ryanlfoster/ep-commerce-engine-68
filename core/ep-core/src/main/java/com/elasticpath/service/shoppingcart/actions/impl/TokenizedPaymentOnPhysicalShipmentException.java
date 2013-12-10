/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpServiceException;

/**
 * {@link EpServiceException} thrown when tokenized payment is attempted on an order with physical goods.   
 */
public class TokenizedPaymentOnPhysicalShipmentException extends EpServiceException {

	/** Serial version id. */
	public static final long serialVersionUID = 5000000001L;
	
	/**
	 * The constructor.
	 *
	 * @param message the message
	 */
	public TokenizedPaymentOnPhysicalShipmentException(final String message) {
		super(message);
	}

	/**
	 * Constructor with a throwable.
	 *
	 * @param message the message
	 * @param throwable the cause
	 */
	public TokenizedPaymentOnPhysicalShipmentException(final String message, final Throwable throwable) {
		super(message, throwable);
	}

}
