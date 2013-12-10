package com.elasticpath.service.search.query;

import com.elasticpath.base.exception.EpServiceException;

/**
 * This exception will be thrown in case an invalid order search criterion result type is given.
 */
public class EpInvalidOrderCriterionResultTypeException extends EpServiceException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Creates a new object.
	 *
	 * @param msg the message
	 */
	public EpInvalidOrderCriterionResultTypeException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a new object.
	 *
	 * @param msg the message
	 * @param cause the root cause
	 */
	public EpInvalidOrderCriterionResultTypeException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
