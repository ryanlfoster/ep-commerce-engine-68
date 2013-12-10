/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.search.query;

import com.elasticpath.base.exception.EpServiceException;

/**
 * This exception will be thrown in case an empty search criteria is given.
 */
public class EpEmptySearchCriteriaException extends EpServiceException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 */
	public EpEmptySearchCriteriaException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 * @param cause the root cause
	 */
	public EpEmptySearchCriteriaException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
