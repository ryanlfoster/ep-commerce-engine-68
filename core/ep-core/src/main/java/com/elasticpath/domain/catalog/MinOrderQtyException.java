/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;


/**
 * This exception is thrown when a an order cannot be fulfilled becasue the cart qty of an item is less than the permitted minimum order qty.
 */
public class MinOrderQtyException extends RuntimeException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * @param message error message
	 */
	public MinOrderQtyException(final String message) {
		super(message);
	}

}
