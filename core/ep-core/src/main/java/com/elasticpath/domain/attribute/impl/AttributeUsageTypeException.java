package com.elasticpath.domain.attribute.impl;

import com.elasticpath.domain.EpDomainException;

/**
 * An Exception class for reporting errors of usage type.
 * Thrown if a check against currently available types do not include the requested type id.
 *
 */
public class AttributeUsageTypeException extends EpDomainException {

	private static final long serialVersionUID = 2355382655047441384L;

	/**
	 * Creates a new <code>EpAttributeUsageTypeException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>EpDomainException</code>.
	 */
	public AttributeUsageTypeException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EpAttributeUsageTypeException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this <code>EpDomainException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>EpDomainException</code>.
	 */
	public AttributeUsageTypeException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
