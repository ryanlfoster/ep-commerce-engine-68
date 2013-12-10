/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;

/**
 * This exception is thrown when an attempt is made to insert an object when the given name already exists but must be unique.
 */
public class DuplicateNameException extends EpSystemException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new object with the given message.
	 * 
	 * @param message the reason
	 */
	public DuplicateNameException(final String message) {
		super(message);
	}

	/**
	 * Creates a new object using the given message and cause exception.
	 * 
	 * @param message the reason
	 * @param cause the <code>Throwable</code> that caused this exception
	 */
	public DuplicateNameException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
