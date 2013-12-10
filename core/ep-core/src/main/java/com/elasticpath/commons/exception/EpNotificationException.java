package com.elasticpath.commons.exception;

/**
 * EpNotificationException for notification exceptions when dealing with our enterprise integration solution.
 */
public class EpNotificationException extends Exception {

	/** Serial version id. */
	private static final long serialVersionUID = 6000000001L;
	
	/**
	 * Creates a new <code>EpNotificationException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this exception
	 */
	public EpNotificationException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EpNotificationException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this exception
	 * @param cause the <code>Throwable</code> that caused this exception
	 */
	public EpNotificationException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
