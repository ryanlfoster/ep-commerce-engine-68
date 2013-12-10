/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;



/**
 * The exception for email address doesn't exist in the database.
 */
public class EmailNonExistException extends EpSystemException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>EmailNonExistException</code> object with the given message.
	 *
	 * @param message the message 
	 */
	public EmailNonExistException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EmailNonExistException</code> object using the given message and cause exception.
	 *
	 * @param message the mesage
	 * @param cause the cause exception
	 */
	public EmailNonExistException(final String message, final Throwable cause) {
		super(message, cause);
	}
}