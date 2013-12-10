/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.api;

import com.elasticpath.base.exception.EpSystemException;

/**
 * A general exception to wrap errors happen at persistence layer.
 *
 */
public class EpPersistenceException extends EpSystemException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>EPPersistenceException</code> object.
	 *
	 * @param msg the message
	 */
	public EpPersistenceException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a new <code>EPPersistenceException</code> object.
	 *
	 * @param msg the message
	 * @param cause the root cause
	 */
	public EpPersistenceException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
