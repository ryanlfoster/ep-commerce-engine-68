package com.elasticpath.plugin.payment.exceptions;


/**
 * Exception to throw if the amount specified exceeds allowable limit.
 */
public class AmountLimitExceededException extends PaymentProcessingException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Constructor.
	 * @param message error message
	 */
	public AmountLimitExceededException(final String message) {
		super(message);
	}
}
