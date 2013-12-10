/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb;

import com.elasticpath.base.exception.EpSystemException;



/**
 * The generic exception class for the <code>com.elasticpath.cmweb</code>
 * package.
 * 
 */
public class EpCmWebException extends EpSystemException {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>EpWebException</code> object with the given
	 * message.
	 * 
	 * @param message
	 *            the reason for this <code>EpWebException</code>.
	 */
	public EpCmWebException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EpWebException</code> object using the given
	 * message and cause exception.
	 * 
	 * @param message
	 *            the reason for this <code>EpWebException</code>.
	 * @param cause
	 *            the <code>Throwable</code> that caused this
	 *            <code>EpServiceException</code>.
	 */
	public EpCmWebException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
