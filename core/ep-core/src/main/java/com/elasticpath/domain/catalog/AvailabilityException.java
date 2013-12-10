/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;


/**
 * This exception is thrown when a an order cannot be fulfilled due to product availability.
 */
public class AvailabilityException extends RuntimeException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * @param message error message
	 */
	public AvailabilityException(final String message) {
		super(message);
	}

}
