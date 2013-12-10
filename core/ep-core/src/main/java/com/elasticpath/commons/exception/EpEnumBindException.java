/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.exception;


/**
 * This exception will be thrown in case any errors happen when binding a bad 
 * integer value to a any kind of Enum.
 * 
 * For example, if the integer is out of range.
 */
public class EpEnumBindException extends EpBindException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new exception.
	 * 
	 * @param msg the message
	 */
	public EpEnumBindException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a new exception.
	 * 
	 * @param msg the message
	 * @param cause the root cause
	 */
	public EpEnumBindException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
