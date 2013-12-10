/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.exception;


/**
 * This exception will be thrown when unsupported operation is called.
 * Generally, it should be a bug(program error) if this exception is caught.
 */
public class EpUnsupportedOperationException extends EpBindException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 */
	public EpUnsupportedOperationException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 * @param cause the root cause
	 */
	public EpUnsupportedOperationException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
