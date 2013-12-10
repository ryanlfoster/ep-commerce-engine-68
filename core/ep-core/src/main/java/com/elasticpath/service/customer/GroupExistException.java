/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.service.customer;

import com.elasticpath.base.exception.EpServiceException;


/**
 * The exception for group with a certain name already exists.
 */
public class GroupExistException extends EpServiceException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>GroupExistException</code> object with the given message.
	 *
	 * @param message the reason for this <code>GroupExistException</code>.
	 */
	public GroupExistException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>GroupExistException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>GroupExistException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>GroupExistException</code>.
	 */
	public GroupExistException(final String message, final Throwable cause) {
		super(message, cause);
	}
}