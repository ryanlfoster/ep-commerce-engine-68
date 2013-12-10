/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.exception;


/**
 * This exception will be thrown in case any errors happen when binding a bad string value to a <code>boolean</code> value.
 */
public class EpBooleanBindException extends EpBindException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 */
	public EpBooleanBindException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 * @param cause the root cause
	 */
	public EpBooleanBindException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
