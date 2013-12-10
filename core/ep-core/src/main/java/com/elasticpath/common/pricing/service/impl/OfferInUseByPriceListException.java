package com.elasticpath.common.pricing.service.impl;

import com.elasticpath.base.exception.EpServiceException;

/**
 * This exception identifies problems with Price Lists.
 */
public class OfferInUseByPriceListException extends EpServiceException {

	private static final long serialVersionUID = -5395829668494717372L;

	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message
	 */
	public OfferInUseByPriceListException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message
	 * @param cause the cause of the exception
	 */
	public OfferInUseByPriceListException(final String message, final Throwable cause) {
		super(message, cause);
	}

}

