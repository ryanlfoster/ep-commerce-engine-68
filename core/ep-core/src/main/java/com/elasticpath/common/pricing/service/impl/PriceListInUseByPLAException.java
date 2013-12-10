package com.elasticpath.common.pricing.service.impl;

import com.elasticpath.base.exception.EpServiceException;

/**
 * This exception identifies problems with Price Lists.
 */
public class PriceListInUseByPLAException extends EpServiceException {

	private static final long serialVersionUID = 8604117098451829469L;

	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message
	 */
	public PriceListInUseByPLAException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message
	 * @param cause the cause of the exception
	 */
	public PriceListInUseByPLAException(final String message, final Throwable cause) {
		super(message, cause);
	}

}

