/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.service.dataimport;

import com.elasticpath.base.exception.EpServiceException;


/**
 * The exception for an import job that already exists.
 */
public class ImportJobExistException extends EpServiceException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>ImportJobExistException</code> object with the given message.
	 *
	 * @param message the reason for this <code>ImportJobExistException</code>.
	 */
	public ImportJobExistException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>ImportJobExistException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>ImportJobExistException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>ImportJobExistException</code>.
	 */
	public ImportJobExistException(final String message, final Throwable cause) {
		super(message, cause);
	}
}