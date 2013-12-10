package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;

/**
 * Superclass for all licensing exceptions.
 */
public class EpLicensingException extends EpSystemException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new object.
	 *
	 * @param msg the message
	 */
	public EpLicensingException(final String msg) {
		super(msg);
	}
	
	/**
	 * Creates a new object.
	 *
	 * @param msg the message
	 * @param cause the root cause
	 */
	public EpLicensingException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
