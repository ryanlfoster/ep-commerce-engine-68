package com.elasticpath.commons.exception;

import com.elasticpath.domain.EpDomainException;

/**
 * General exception for missing currency when trying to calculate Shipping Cost.  
 */
public class SCCMCurrencyMissingException extends EpDomainException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>SCCMCurrencyMissingException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>SCCMCurrencyMissingException</code>.
	 */
	public SCCMCurrencyMissingException(final String message) {
		super(message);
	}
	
	/**
	 * Creates a new <code>SCCMCurrencyMissingException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>SCCMCurrencyMissingException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>SCCMCurrencyMissingException</code>.
	 */
	public SCCMCurrencyMissingException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
