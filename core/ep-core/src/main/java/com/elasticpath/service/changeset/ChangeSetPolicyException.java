/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset;

import com.elasticpath.base.exception.EpServiceException;

/**
 * This exception identifies problems with change sets.
 */
public class ChangeSetPolicyException extends EpServiceException {

	private static final long serialVersionUID = 1565304458841420845L;

	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message
	 */
	public ChangeSetPolicyException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message
	 * @param cause the cause of the exception
	 */
	public ChangeSetPolicyException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
