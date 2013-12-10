/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.order.impl;

import com.elasticpath.domain.EpDomainException;

/**
 * This exception will be thrown when a change that should only be made to a persisted
 * Order is made to an Order that has not been persisted. For example, anything that relies
 * on the order number which is generated at persistence time.
 */
public class OrderNotPersistedException extends EpDomainException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new instance of this exception along with a root cause.
	 * 
	 * @param message the message to go with this exception.
	 * @param cause the root cause.
	 */
	public OrderNotPersistedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new instance of this exception.
	 * 
	 * @param message the message to go with this exception.
	 */
	public OrderNotPersistedException(final String message) {
		super(message);
	}

}
