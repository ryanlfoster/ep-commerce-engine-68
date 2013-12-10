package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;

/**
 * Exception occurs when CM user tries to enter password that doesn't satisfy some of the validation policies.
 */
public class EpPasswordValidationException extends EpSystemException {
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 */
	public EpPasswordValidationException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a new object.
	 * 
	 * @param msg the message
	 * @param cause the root cause
	 */
	public EpPasswordValidationException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
