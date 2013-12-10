package com.elasticpath.domain.subscriptions.exceptions;

import com.elasticpath.domain.EpDomainException;

/**
 * Payment Schedule exception with different root causes.
 */
public class PaymentScheduleException extends EpDomainException {

	private static final long serialVersionUID = -7834854965044648533L;

	/** Error message for Duration amount cannot be less than zero.*/
	public static final String DURATION_AMOUNT_CANNOT_BE_LESS_THAN_ZERO_ERROR = "Duration amount cannot be less than zero.";
	
	/** Error message for Frequency amount cannot be less than zero.*/
	public static final String FREQUENCY_AMOUNT_CANNOT_BE_LESS_THAN_ZERO_ERROR = "Frequency amount cannot be less than zero.";
	
	/** Error message for A payment schedule with the same name already exists.*/
	public static final String DUPLICATE_NAME = "A payment schedule with the same name already exists.";

	/** Error message for Invalid payment schedule name. */
	public static final String INVALID_NAME = "Invalid payment schedule name.";

	/** Error message for Frequency unit cannot be blank. */
	public static final String FREQUENCY_UNIT_CANNOT_BE_BLANK = "Frequency unit cannot be blank.";

	/** Error message for Duration unit cannot be blank. */
	public static final String DURATION_UNIT_CANNOT_BE_BLANK = "Duration unit cannot be blank.";

	/** Error message for Frequency cannot be null. */
	public static final String FREQUENCY_CANNOT_BE_NULL = "Frequency cannot be null.";

	/**
	 * Creates a new <code>PaymentScheduleException</code> object with the given message.
	 * @param message error message
	 */
	public PaymentScheduleException(final String message) {
		super(message);
	}

}
