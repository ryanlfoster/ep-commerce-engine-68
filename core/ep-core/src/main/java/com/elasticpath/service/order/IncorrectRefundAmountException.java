/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.service.order;

import com.elasticpath.base.exception.EpServiceException;


/**
 * The exception for shipping region with a certain name already exists.
 */
public class IncorrectRefundAmountException extends EpServiceException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>ShippingRegionExistException</code> object with the given message.
	 *
	 * @param message the reason for this <code>ShippingRegionExistException</code>.
	 */
	public IncorrectRefundAmountException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>ShippingRegionExistException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>ShippingRegionExistException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>ShippingRegionExistException</code>.
	 */
	public IncorrectRefundAmountException(final String message, final Throwable cause) {
		super(message, cause);
	}
}