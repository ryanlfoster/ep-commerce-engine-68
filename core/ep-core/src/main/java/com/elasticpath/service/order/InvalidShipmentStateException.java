/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Exception is thrown when order shipment is in an invalid state.
 */
public class InvalidShipmentStateException extends EpServiceException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The constructor.
	 * @param message the message
	 */
	public InvalidShipmentStateException(final String message) {
		super(message);
		
	}

	/**
	 * The constructor.
	 * @param message the message
	 * @param cause the cause
	 */
	public InvalidShipmentStateException(final String message, final Throwable cause) {
		super(message, cause);
		
	}

}
