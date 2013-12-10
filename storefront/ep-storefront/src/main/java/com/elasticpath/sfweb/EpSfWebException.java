/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb;

import com.elasticpath.sfweb.exception.EpWebException;

/**
 * The generic exception class for the <code>com.elasticpath.sfweb</code>
 * package.
 * 
 * @deprecated use parent, {@link com.elasticpath.web.exception.EpWebException}
 */
@Deprecated
public class EpSfWebException extends EpWebException {
	
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
	public EpSfWebException(final String message) {
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
	public EpSfWebException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
