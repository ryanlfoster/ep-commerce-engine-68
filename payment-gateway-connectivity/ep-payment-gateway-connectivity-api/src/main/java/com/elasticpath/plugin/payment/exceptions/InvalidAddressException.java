package com.elasticpath.plugin.payment.exceptions;


/**
 * Exception to throw if the shipping/billing address is invalid.
 */
public class InvalidAddressException extends PaymentProcessingException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * @param message error message
	 */
	public InvalidAddressException(final String message) {
		super(message);
	}
}
