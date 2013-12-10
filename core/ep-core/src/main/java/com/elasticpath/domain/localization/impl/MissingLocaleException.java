package com.elasticpath.domain.localization.impl;

import com.elasticpath.domain.EpDomainException;

/**
 * Thrown if no locale has been specified in the preferred list of the locale fallback policy.
 */
public class MissingLocaleException extends EpDomainException {

	private static final long serialVersionUID = 641L;
	/**
	 * Creates a new <code>NoLocaleSpecifiedException</code> object with the given message and cause.
	 * @param message the reason for this <code>NoLocaleSpecifiedException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>NoLocaleSpecifiedException</code>.
	 */
	public MissingLocaleException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Creates a new <code>NoLocaleSpecifiedException</code> object with the given message.
	 * @param message the reason for this <code>NoLocaleSpecifiedException</code>.
	 */
	public MissingLocaleException(final String message) {
		super(message);
	}

}
