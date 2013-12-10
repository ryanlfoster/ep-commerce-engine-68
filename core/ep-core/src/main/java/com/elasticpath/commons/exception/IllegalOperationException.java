/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;


/**
 * The exception for an operation which is illegal.
 */
public class IllegalOperationException extends EpSystemException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>IllegalOperationException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>IllegalOperationException</code>.
	 */
	public IllegalOperationException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>IllegalOperationException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this <code>IllegalOperationException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>IllegalOperationException</code>.
	 */
	public IllegalOperationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
