package com.elasticpath.service.query.impl;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Exception thrown when calling a query service with an expected result type that isn't supported by the query service.
 */
public class UnsupportedResultTypeException extends EpServiceException {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new unsupported result type exception.
	 *
	 * @param message the message
	 */
	public UnsupportedResultTypeException(final String message) {
		super(message);
	}


}
