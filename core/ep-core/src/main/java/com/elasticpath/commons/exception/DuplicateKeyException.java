/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;


/**
 * The exception for an attribute key that already exists.
 */
public class DuplicateKeyException extends EpSystemException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>DuplicateKeyException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>DuplicateKeyException</code>.
	 */
	public DuplicateKeyException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>DuplicateKeyException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this <code>DuplicateKeyException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>DuplicateKeyException</code>.
	 */
	public DuplicateKeyException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
