/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer;

import com.elasticpath.persistence.api.Entity;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * A <code>CustomerCreditCard</code> is a credit card stored by a store-front customer.
 */
public interface CustomerCreditCard extends Entity, PaymentMethod {

	/**
	 * @return the billingAddress
	 */
	CustomerAddress getBillingAddress();

	/**
	 * @param billingAddress the billingAddress to set
	 */
	void setBillingAddress(final CustomerAddress billingAddress);

	/**
	 * @return the cardHolderName
	 */
	String getCardHolderName();

	/**
	 * @param cardHolderName the cardHolderName to set
	 */
	void setCardHolderName(final String cardHolderName);

	/**
	 * @return the cardNumber
	 */
	String getCardNumber();

	/**
	 * @param cardNumber the cardNumber to set
	 */
	void setCardNumber(final String cardNumber);

	/**
	 * @return the cardType
	 */
	String getCardType();

	/**
	 * @param cardType the cardType to set
	 */
	void setCardType(final String cardType);

	/**
	 * @return the defaultCard
	 */
	boolean isDefaultCard();

	/**
	 * @param defaultCard the defaultCard to set
	 */
	void setDefaultCard(final boolean defaultCard);

	/**
	 * @return the expiryMonth
	 */
	String getExpiryMonth();

	/**
	 * @param expiryMonth the expiryMonth to set
	 */
	void setExpiryMonth(final String expiryMonth);

	/**
	 * @return the expiryYear
	 */
	String getExpiryYear();

	/**
	 * @param expiryYear the expiryYear to set
	 */
	void setExpiryYear(final String expiryYear);

	/**
	 * @return the issueNumber
	 */
	int getIssueNumber();

	/**
	 * @param issueNumber the issueNumber to set
	 */
	void setIssueNumber(final int issueNumber);

	/**
	 * @return the startMonth
	 */
	String getStartMonth();

	/**
	 * @param startMonth the startMonth to set
	 */
	void setStartMonth(final String startMonth);

	/**
	 * @return the startYear
	 */
	String getStartYear();

	/**
	 * @param startYear the startYear to set
	 */
	void setStartYear(final String startYear);

	/**
	 * Decrypts and returns the full credit card number. Access to this method should be restricted!
	 * 
	 * @return the decrypted credit card number
	 */
	String getUnencryptedCardNumber();

	/**
	 * Decrypts and returns the masked credit card number: ************5381. Useful for displaying in receipts, GUI, order history, etc.
	 * 
	 * @return the masked credit card number
	 */
	String getMaskedCardNumber();

	/**
	 * Encrypts the credit card number.
	 */
	void encrypt();
	
	/**
	 * Set the 3 or 4 digit security code from the back of the card.
	 * This value IS NOT persistent.
	 * @param securityCode the security code
	 */
	void setSecurityCode(final String securityCode);
	
	/**
	 * Get the 3 or 4 digit security code from the back of the card.
	 * This value cannot be persisted and will not be available unless
	 * the user has specified it.
	 * @return the security code
	 */
	String getSecurityCode();

	/**
	 * Copies all the fields from another credit card into this credit card.
	 * Card number is optional, as it may have already been encrypted.
	 * 
	 * @param creditCard the credit card from which to copy fields
	 * @param includeNumber specify whether to include the card number in the copy
	 */
	void copyFrom(final CustomerCreditCard creditCard, boolean includeNumber);
	
}
