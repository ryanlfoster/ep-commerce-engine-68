/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.formbean;

/**
 * A form bean for storing the credit card data.
 */
public interface OrderPaymentFormBean extends EpFormBean {

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


}
