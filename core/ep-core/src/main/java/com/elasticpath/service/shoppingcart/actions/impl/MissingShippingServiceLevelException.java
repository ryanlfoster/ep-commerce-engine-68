/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpServiceException;

/**
 * {@link EpServiceException} thrown when {@link com.elasticpath.domain.shipping.ShippingServiceLevel} is not set 
 * on {@link com.elasticpath.domain.shoppingcart.ShoppingCart}. 
 */
public class MissingShippingServiceLevelException extends EpServiceException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The constructor.
	 *
	 * @param message the message
	 */
	public MissingShippingServiceLevelException(final String message) {
		super(message);
	}

	/**
	 * Constructor with a throwable.
	 *
	 * @param message the message
	 * @param throwable the cause
	 */
	public MissingShippingServiceLevelException(final String message, final Throwable throwable) {
		super(message, throwable);
	}

}
