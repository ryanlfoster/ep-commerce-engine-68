package com.elasticpath.plugin.payment.dto;

import java.util.Date;

/**
 * Represents a payment method that consists of card details.
 */
public interface CardDetailsPaymentMethod extends PaymentMethod {

	/**
	 * Get the type/brand of the credit card (e.g. VISA).
	 *
	 * @return the cardType
	 */
	String getCardType();

	/**
	 * Set the vendor/brand of the credit card (e.g. VISA).
	 *
	 * @param cardType the cardType
	 */
	void setCardType(final String cardType);

	/**
	 * Set the card holder name.
	 *
	 * @return the name on the card
	 */
	String getCardHolderName();

	/**
	 * Get the card holder name.
	 *
	 * @param cardHolderName the name on the card
	 */
	void setCardHolderName(final String cardHolderName);

	/**
	 * Set the credit card number.
	 *
	 * @param number the credit card number
	 */
	void setUnencryptedCardNumber(final String number);

	/**
	 * Decrypts and returns the full credit card number. Access to this method should be restricted.
	 *
	 * @return the decrypted credit card number
	 */
	String getUnencryptedCardNumber();

	/**
	 * Get the two-digit expiry date year.
	 *
	 * @return the expiry date year
	 */
	String getExpiryYear();

	/**
	 * Set the two-digit expiry date year.
	 *
	 * @param expiryYear the expiry date year
	 */
	void setExpiryYear(final String expiryYear);

	/**
	 * Get the two-digit expiry date month.
	 *
	 * @return the two-digit expiry date month
	 */
	String getExpiryMonth();

	/**
	 * Set the expiry two-digit date month.
	 *
	 * @param expiryMonth the two digit expiry date month
	 */
	void setExpiryMonth(final String expiryMonth);

	/**
	 * Get the card start date Used by some U.K. cards.
	 *
	 * @return the start date
	 */
	Date getStartDate();

	/**
	 * Set the cart start date used by some U.K. cards.
	 *
	 * @param startDate the start date
	 */
	void setStartDate(final Date startDate);

	/**
	 * Get the issue number used by some U.K. cards.
	 *
	 * @return the issue number
	 */
	String getIssueNumber();

	/**
	 * Set the issue number used by some U.K. cards.
	 *
	 * @param issueNumber the issue number
	 */
	void setIssueNumber(final String issueNumber);

	/**
	 * Get the card security code (found near the signature on the back of the card).
	 *
	 * @return the card cvv2Code
	 */
	String getCvv2Code();

	/**
	 * Set the security code (found near the signature on the back of the card).
	 *
	 * @param cvv2Code the security code
	 */
	void setCvv2Code(final String cvv2Code);

	/**
	 * Get the reference id. The reference ID is basically a merchant reference code, and is usually set to the Order Number associated with this
	 * payment.
	 *
	 * @return the reference id.
	 */
	String getReferenceId();

	/**
	 * Set the reference id. The reference ID is basically a merchant reference code, and is usually set to the Order Number
	 * or shipment number associated with this payment.
	 *
	 * @param referenceId the reference id.
	 */
	void setReferenceId(final String referenceId);

	/**
	 * Get the customer's email address (Required for card processing).
	 *
	 * @return the customer email address
	 */
	String getEmail();

	/**
	 * Set the customer's email address (Required for card processing).
	 *
	 * @param email the customer's email address
	 */
	void setEmail(final String email);

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 *
	 * @return the payment transaction type
	 */
	String getTransactionType();

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 *
	 * @param transactionType the payment transaction type
	 */
	void setTransactionType(final String transactionType);

	/**
	 * Store the temporary token needed by some payment gateway, i.e. PayPal to complete the process.
	 *
	 * @param token payment gateway token
	 */
	void setGatewayToken(final String token);

	/**
	 * Return the payment gateway token.
	 *
	 * @return the temproary payment gateway token.
	 */
	String getGatewayToken();

	/**
	 * Get the ipAddress of the user from the Order Payment.
	 *
	 * @return the ipAddress
	 */
	String getIpAddress();

	/**
	 * Set the users ip Address into the Order Payment.
	 *
	 * @param ipAddress the ipAddress of the user.
	 */
	void setIpAddress(final String ipAddress);

	/**
	 * Get the encrypted credit cart number.
	 *
	 * @return the credit card number.
	 */
	String getCardNumber();

	/**
	 * Get Payer Authentication Validate value for transaction.
	 *
	 * @return the payerAuthValidationValue.
	 */
	PayerAuthValidationValueDto getPayerAuthValidationValueDto();

	/**
	 * Set the payerAuthValidationValue for order payment.
	 *
	 * @param payerAuthValidationValue the payerAuthValidationValue
	 */
	void setPayerAuthValidationValueDto(final PayerAuthValidationValueDto payerAuthValidationValue);
}
