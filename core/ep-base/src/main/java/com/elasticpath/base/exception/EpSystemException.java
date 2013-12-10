/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.base.exception;

/**
 * The generic exception class for the <code>com.elasticpath</code> package.
 */
public class EpSystemException extends RuntimeException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>EpSystemException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>EpSystemException</code>.
	 */
	public EpSystemException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EpSystemException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this <code>EpSystemException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>EpSystemException</code>.
	 */
	public EpSystemException(final String message, final Throwable cause) {
		super(message, cause);
	}
}