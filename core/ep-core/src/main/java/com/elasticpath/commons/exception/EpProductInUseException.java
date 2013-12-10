/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;

/**
 * This exception can be thrown in product deletion where product is in a bundle.
 */
public class EpProductInUseException extends EpSystemException {

	/** Serial version id. */
	private static final long serialVersionUID = 6000000001L;

	/**
	 * Creates a new <code>EpProductInBundleException</code> object with the given message.
	 * 
	 * @param message the reason for this exception
	 */
	public EpProductInUseException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EpProductInBundleException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this exception
	 * @param cause the <code>Throwable</code> that caused this exception
	 */
	public EpProductInUseException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
