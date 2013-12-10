/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */

package com.elasticpath.commons.exception;

import com.elasticpath.domain.EpDomainException;

/**
 * General exception for non consistent fields in domain objects.
 * For example, throw it if expiry date greater than start date.  
 */
public class EpNonConsistentDomainFieldException extends EpDomainException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>EpNonConsistentDomainFieldException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>EpNonConsistentDomainFieldException</code>.
	 */
	public EpNonConsistentDomainFieldException(final String message) {
		super(message);
	}
	
	/**
	 * Creates a new <code>EpNonConsistentDomainFieldException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>EpNonConsistentDomainFieldException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>EpNonConsistentDomainFieldException</code>.
	 */
	public EpNonConsistentDomainFieldException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
