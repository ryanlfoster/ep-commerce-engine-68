package com.elasticpath.plugin.payment.exceptions;


/**
 * Exception to throw if the amount authorized is exceeded in a follow on transaction.
 */
public class AuthorizedAmountExceededException extends PaymentProcessingException {
	
	/** Serial version id. */
	public static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * @param message error message
	 */
	public AuthorizedAmountExceededException(final String message) {
		super(message);
	}
}
