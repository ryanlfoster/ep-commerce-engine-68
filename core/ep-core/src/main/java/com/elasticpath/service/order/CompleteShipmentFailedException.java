/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Exception is thrown when the shipment complete can't be executed.
 */
public class CompleteShipmentFailedException extends EpServiceException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The constructor.
	 *
	 * @param message the message
	 */
	public CompleteShipmentFailedException(final String message) {
		super(message);
	}

	/**
	 * Constructor with a throwable.
	 *
	 * @param message the message
	 * @param throwable the cause
	 */
	public CompleteShipmentFailedException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
	
}
