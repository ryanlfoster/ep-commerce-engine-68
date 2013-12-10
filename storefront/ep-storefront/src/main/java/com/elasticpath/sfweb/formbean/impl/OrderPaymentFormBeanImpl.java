/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.formbean.impl;

import com.elasticpath.sfweb.formbean.OrderPaymentFormBean;

/**
 * Order payment form bean for holding the credit card attributes.
 */
public class OrderPaymentFormBeanImpl extends EpFormBeanImpl implements OrderPaymentFormBean {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private String cvv2Code;
	private String expiryYear;
	private String expiryMonth;
	private String cardType;
	private String cardHolderName;
	private String unencryptedCardNumber;

	/**
	 * Get the card security code (found near the signature on the back of the card).
	 * 
	 * @return the card security code
	 */
	public String getCvv2Code() {
		return this.cvv2Code;
	}

	/**
	 * Set the security code (found near the signature on the back of the card).
	 * 
	 * @param cvv2Code the security code
	 */
	public void setCvv2Code(final String cvv2Code) {
		this.cvv2Code = cvv2Code;
	}

	/**
	 * Get the two-digit expiry date year.
	 * 
	 * @return the expiry date year
	 */
	public String getExpiryYear() {
		return this.expiryYear;
	}

	/**
	 * Set the two-digit expiry date year.
	 * 
	 * @param expiryYear the expiry date year
	 */
	public void setExpiryYear(final String expiryYear) {
		this.expiryYear = expiryYear;
	}

	/**
	 * Get the two-digit expiry date month.
	 * 
	 * @return the two-digit expiry date month
	 */
	public String getExpiryMonth() {
		return this.expiryMonth;
	}

	/**
	 * Set the expiry two-digit date month.
	 * 
	 * @param expiryMonth the two digit expiry date month
	 */
	public void setExpiryMonth(final String expiryMonth) {
		this.expiryMonth = expiryMonth;
	}
	
	/**
	 * Get the vendor/brand of the credit card (e.g. VISA).
	 * 
	 * @return the type
	 */
	public String getCardType() {
		return this.cardType;
	}

	/**
	 * Set the vendor/brand of the credit card (e.g. VISA).
	 * 
	 * @param cardType the card type
	 */
	public void setCardType(final String cardType) {
		this.cardType = cardType;
	}

	/**
	 * Set the card holder name.
	 * 
	 * @return the name on the card
	 */
	public String getCardHolderName() {
		return this.cardHolderName;
	}

	/**
	 * Get the card holder name.
	 * 
	 * @param cardHolderName the name on the card
	 */
	public void setCardHolderName(final String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	/**
	 * Decrypts and returns the full credit card number. Access to this method should be restricted.
	 * 
	 * @return the decrypted credit card number
	 */
	public String getUnencryptedCardNumber() {
		return this.unencryptedCardNumber;
	}

	/**
	 * Set the credit card number.
	 * 
	 * @param number the credit card number
	 */
	public void setUnencryptedCardNumber(final String number) {
		this.unencryptedCardNumber = number;
	}

}
