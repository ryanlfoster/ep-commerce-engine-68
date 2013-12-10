/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;


/**
 * The exception for userId address already exists.
 * 
 * @author wliu
 */
public class UserIdExistException extends EpSystemException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>UserIdExistException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>UserIdExistException</code>.
	 */
	public UserIdExistException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>UserIdExistException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this <code>UserIdExistException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>UserIdExistException</code>.
	 */
	public UserIdExistException(final String message, final Throwable cause) {
		super(message, cause);
	}
}