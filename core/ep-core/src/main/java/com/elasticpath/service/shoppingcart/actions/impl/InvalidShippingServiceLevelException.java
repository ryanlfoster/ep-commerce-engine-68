/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpServiceException;

/**
 * {@link EpServiceException} thrown when an invalid {@link com.elasticpath.domain.shipping.ShippingServiceLevel} 
 * is set on {@link com.elasticpath.domain.shoppingcart.ShoppingCart}. 
 */
public class InvalidShippingServiceLevelException extends EpServiceException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The constructor.
	 *
	 * @param message the message
	 */
	public InvalidShippingServiceLevelException(final String message) {
		super(message);
	}

	/**
	 * Constructor with a throwable.
	 *
	 * @param message the message
	 * @param throwable the cause
	 */
	public InvalidShippingServiceLevelException(final String message, final Throwable throwable) {
		super(message, throwable);
	}

}
