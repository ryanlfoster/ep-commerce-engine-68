/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.base.exception;

/**
 * The generic exception class for the <code>com.elasticpath.service</code> package.
 */
public class EpServiceException extends EpSystemException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/** Returns the class not of the Exception. Used to identify a specific exception on client side,
	 * i.e. in JavaScript when using Ajax to invoke service layer method.
	 * @return the exception name, i.e. com.elasticpath.base.exception.EpServiceException, com.elasticpath.service.EmailExistException.
	 */
	public String getName() {
		return this.getClass().getName();
	}

	/**
	 * Creates a new <code>EpServiceException</code> object with the given message.
	 *
	 * @param message the reason for this <code>EpServiceException</code>.
	 */
	public EpServiceException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EpServiceException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>EpServiceException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>EpServiceException</code>.
	 */
	public EpServiceException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
