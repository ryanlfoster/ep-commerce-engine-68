package com.elasticpath.service.query.impl;

import com.elasticpath.base.exception.EpServiceException;

/**
 * An exception that is thrown if a query is attempted using a load tuner not supported by the query service.
 */
public class UnsupportedLoadTunerException extends EpServiceException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new unsupported load tuner exception.
	 *
	 * @param message the message
	 */
	public UnsupportedLoadTunerException(final String message) {
		super(message);
	}

}
