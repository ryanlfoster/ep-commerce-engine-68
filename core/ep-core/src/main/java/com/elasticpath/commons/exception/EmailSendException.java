package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;

/**
 * The exception for email that could not be sent.
 */
public class EmailSendException extends EpSystemException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>EmailSendException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>EmailSendException</code>.
	 */
	public EmailSendException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EmailSendException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this <code>EmailSendException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>EmailSendException</code>.
	 */
	public EmailSendException(final String message, final Throwable cause) {
		super(message, cause);
	}
}