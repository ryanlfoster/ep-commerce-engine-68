/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.processing;

/**
 * This exception signifies an error while processing objects.
 */
public class ObjectProcessingException extends RuntimeException {

	private static final long serialVersionUID = -2262718967881153576L;

	/**
	 * Constructs an exception.
	 * 
	 * @param cause the cause
	 */
	public ObjectProcessingException(final Exception cause) {
		super(cause);
	}

}
