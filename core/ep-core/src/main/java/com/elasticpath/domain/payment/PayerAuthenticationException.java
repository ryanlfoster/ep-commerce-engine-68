package com.elasticpath.domain.payment;

import com.elasticpath.domain.EpDomainException;
/**
 * Exception to throw if the payer authentication is invalid.
 */
public class PayerAuthenticationException extends EpDomainException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * @param message error message
	 */
	public PayerAuthenticationException(final String message) {
		super(message);
	}
}
