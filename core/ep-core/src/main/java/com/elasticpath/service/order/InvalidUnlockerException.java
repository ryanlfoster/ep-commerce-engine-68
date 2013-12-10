/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Exception is thrown when order lock to be released was obtained not by user that is unlocking the order.
 */
public class InvalidUnlockerException extends EpServiceException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The constructor.
	 * @param message the message
	 */
	public InvalidUnlockerException(final String message) {
		super(message);
	}

	/**
	 * The constructor.
	 * @param message the message
	 * @param cause the cause
	 */
	public InvalidUnlockerException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
