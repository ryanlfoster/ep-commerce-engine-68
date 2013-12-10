/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.exception;


/**
 * This exception will be thrown if the product license is invalid.
 */
public class EpLicensingMissingException extends EpLicensingException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new object.
	 *
	 * @param msg the message
	 */
	public EpLicensingMissingException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a new object.
	 *
	 * @param msg the message
	 * @param cause the root cause
	 */
	public EpLicensingMissingException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}

