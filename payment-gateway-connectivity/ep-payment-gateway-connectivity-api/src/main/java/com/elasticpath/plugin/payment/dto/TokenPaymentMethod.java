package com.elasticpath.plugin.payment.dto;

/**
 * Represents a token payment method.
 */
public interface TokenPaymentMethod extends PaymentMethod {
	/**
	 * Gets the token value as a String.
	 *
	 * @return the token value
	 */
	String getValue();
}
