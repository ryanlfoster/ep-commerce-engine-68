/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.service.cmuser;

import com.elasticpath.base.exception.EpServiceException;


/**
 * The exception for the userName already exists.
 * @author wliu
 */
public class UserNameExistException extends EpServiceException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>UserNameExistException</code> object with the given message.
	 *
	 * @param message the reason for this <code>UserNameExistException</code>.
	 */
	public UserNameExistException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>UserNameExistException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>UserIdExistException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>UserIdExistException</code>.
	 */
	public UserNameExistException(final String message, final Throwable cause) {
		super(message, cause);
	}
}