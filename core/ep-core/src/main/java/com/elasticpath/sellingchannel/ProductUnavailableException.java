/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sellingchannel;

import com.elasticpath.base.exception.EpSystemException;

/**
 * Thrown when the user attempts to view a product that is not currently available.
 */
public class ProductUnavailableException extends EpSystemException {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	
	/**
	 * Creates a new <code>EpWebException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>EpWebException</code>.
	 */
	public ProductUnavailableException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EpWebException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this <code>EpWebException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>EpServiceException</code>.
	 */
	public ProductUnavailableException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
