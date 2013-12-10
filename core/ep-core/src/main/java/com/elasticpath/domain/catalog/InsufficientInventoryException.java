/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import com.elasticpath.base.exception.EpServiceException;


/**
 * This exception is thrown when a an unspecified error occurs
 * while processing the given card information.
 */
public class InsufficientInventoryException extends EpServiceException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * @param message error message
	 */
	public InsufficientInventoryException(final String message) {
		super(message);
	}

}
