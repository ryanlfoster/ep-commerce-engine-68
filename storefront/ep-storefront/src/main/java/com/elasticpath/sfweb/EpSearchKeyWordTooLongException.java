/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb;

/**
 * This exception gets thrown when the search keyword given is too long.
 */
public class EpSearchKeyWordTooLongException extends EpSfWebException {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new object with the given message.
	 * 
	 * @param message the reason for this <code>EpWebException</code>.
	 */
	public EpSearchKeyWordTooLongException(final String message) {
		super(message);
	}

	/**
	 * Creates a new object using the given message and cause exception.
	 * 
	 * @param message the reason
	 * @param cause the <code>Throwable</code> that caused this exception
	 */
	public EpSearchKeyWordTooLongException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
