/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.service.catalog;

import com.elasticpath.base.exception.EpServiceException;


/**
 * The exception for a sku option key that already exists.
 */
public class SkuOptionKeyExistException extends EpServiceException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>SkuOptionKeyExistException</code> object with the given message.
	 *
	 * @param message the reason for this <code>SkuOptionKeyExistException</code>.
	 */
	public SkuOptionKeyExistException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>SkuOptionKeyExistException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>SkuOptionKeyExistException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>SkuOptionKeyExistException</code>.
	 */
	public SkuOptionKeyExistException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
