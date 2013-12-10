/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order;

import com.elasticpath.base.exception.EpServiceException;

/**
 * The exception is thrown at attempt to provide some operation over <code>OrderReturn</code>
 * object when latter is in incorrect state.
 */
public class IllegalReturnStateException extends EpServiceException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>IllegalReturnStateException</code> object.
	 *
	 * @param message the message
	 */
	public IllegalReturnStateException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>IllegalReturnStateException</code> object.
	 *
	 * @param message the message
	 * @param cause the root cause
	 */
	public IllegalReturnStateException(final String message, final Throwable cause) {
		super(message, cause);		
	}
}
