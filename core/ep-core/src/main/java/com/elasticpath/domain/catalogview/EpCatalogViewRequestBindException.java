/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalogview;

import com.elasticpath.commons.exception.EpBindException;

/**
 * This exception will be thrown when binding an invalid parameter or attribute from a catalog view request.
 */
public class EpCatalogViewRequestBindException extends EpBindException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 */
	public EpCatalogViewRequestBindException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 * @param cause the root cause
	 */
	public EpCatalogViewRequestBindException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
