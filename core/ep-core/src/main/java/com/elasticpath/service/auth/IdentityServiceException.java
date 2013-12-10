package com.elasticpath.service.auth;

import com.elasticpath.base.exception.EpServiceException;

/**
 * This exception will be thrown if any errors occur in the <code>IdentityService</code> methods. 
 */
public class IdentityServiceException extends EpServiceException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>IdentityServiceException</code> object using the given message and cause exception.
	 * @param message the reason for this <code>IdentityServiceException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>IdentityServiveException</code>.
	 */
	public IdentityServiceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new <code>IdentityServiceException</code> object using the given message.
	 * @param message the reason for this <code>IdentityServiceException</code>.
	 */
	public IdentityServiceException(final String message) {
		super(message);
	}

}
