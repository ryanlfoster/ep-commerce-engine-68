/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.exception;

import com.elasticpath.base.exception.EpSystemException;

/**
 * This exception gets thrown when binding errors of request parameter happens. For example, a number is expected for category UID in the http
 * request, if we got a non-number string, this exception will get thrown.
 */
public class EpRequestParameterBindingException extends EpSystemException {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>EpWebException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>EpWebException</code>.
	 */
	public EpRequestParameterBindingException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EpWebException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this <code>EpWebException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>EpServiceException</code>.
	 */
	public EpRequestParameterBindingException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
