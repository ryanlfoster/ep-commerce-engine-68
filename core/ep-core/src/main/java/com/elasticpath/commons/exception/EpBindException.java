/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;


/**
 * This exception will be thrown in case any errors happen when binding an invalid value to a normal field, an attribute or an association field.
 */
public class EpBindException extends EpSystemException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 */
	public EpBindException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 * @param cause the root cause
	 */
	public EpBindException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
