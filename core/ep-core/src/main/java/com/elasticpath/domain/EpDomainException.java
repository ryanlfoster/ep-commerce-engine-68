/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain;

import com.elasticpath.base.exception.EpSystemException;

/**
 * The generic exception class for the <code>com.elasticpath.domain</code> package.
 */
public class EpDomainException extends EpSystemException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>EpDomainException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>EpDomainException</code>.
	 */
	public EpDomainException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EpDomainException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this <code>EpDomainException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>EpDomainException</code>.
	 */
	public EpDomainException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
