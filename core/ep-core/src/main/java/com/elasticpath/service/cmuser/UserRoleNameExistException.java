/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.service.cmuser;

import com.elasticpath.base.exception.EpServiceException;


/**
 * The exception for userRole name already exists.
 */
public class UserRoleNameExistException extends EpServiceException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>UserRoleNameExistExceptionception</code> object with the given message.
	 *
	 * @param message the reason for this <code>UserRoleNameExistException</code>.
	 */
	public UserRoleNameExistException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>UserRoleNameExistException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>UserRoleNameExistException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>UserRoleNameExistException</code>.
	 */
	public UserRoleNameExistException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
