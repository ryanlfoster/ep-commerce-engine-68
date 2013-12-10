/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order;

import com.elasticpath.persistence.api.ChangeCollisionException;

/**
 * Subclass of <code>ChangeCollisionException</code>. Thrown when concurrent modification of
 * <code>OrderReturn</code> entity noticed.
 */
public class OrderReturnOutOfDateException extends ChangeCollisionException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>IllegalReturnStateException</code> object.
	 *
	 * @param message the message
	 */
	public OrderReturnOutOfDateException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>IllegalReturnStateException</code> object.
	 *
	 * @param message the message
	 * @param cause the root cause
	 */
	public OrderReturnOutOfDateException(final String message, final Throwable cause) {
		super(message, cause);		
	}
}
