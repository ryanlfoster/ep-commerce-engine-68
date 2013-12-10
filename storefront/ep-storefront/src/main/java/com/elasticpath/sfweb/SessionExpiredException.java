/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb;

/**
 * Exception thrown when an action is taken by a customer who's session has expired.
 */
public class SessionExpiredException extends EpSfWebException {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>SessionExpiredException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>SessionExpiredException</code>.
	 */
	public SessionExpiredException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>SessionExpiredException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this <code>SessionExpiredException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>SessionExpiredException</code>.
	 */
	public SessionExpiredException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
