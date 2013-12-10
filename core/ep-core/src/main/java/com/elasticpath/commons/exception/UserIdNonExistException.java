/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;



/**
 * The exception for user Id doesn't exist in the database.
 */
public class UserIdNonExistException extends EpSystemException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>UserIdNonExistException</code> object with the given message.
	 *
	 * @param message the message 
	 */
	public UserIdNonExistException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>UserIdNonExistException</code> object using the given message and cause exception.
	 *
	 * @param message the mesage
	 * @param cause the cause exception
	 */
	public UserIdNonExistException(final String message, final Throwable cause) {
		super(message, cause);
	}
}