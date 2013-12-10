/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalogview;

import com.elasticpath.domain.EpDomainException;

/**
 * This exception will be thrown when a new catalog view request is not compatible with the previous ones. Currently, it means the new search request
 * contains new key words or the new browsing request contains a new category uid.
 */
public class CatalogViewRequestUnmatchException extends EpDomainException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 */
	public CatalogViewRequestUnmatchException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 * @param cause the root cause
	 */
	public CatalogViewRequestUnmatchException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
