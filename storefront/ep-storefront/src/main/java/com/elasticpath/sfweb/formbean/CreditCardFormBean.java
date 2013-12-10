/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean;

/**
 * Form bean for creating and editing customer credit cards.
 */
public interface CreditCardFormBean extends EpFormBean {

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
	 * @return the masked cardNumber
	 */
	String getMaskedCardNumber();

	/**
	 * @param maskedCardNumber the masked cardNumber to set
	 */
	void setMaskedCardNumber(final String maskedCardNumber);
	
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
	 * Gets the unique identifier of the credit card represented by this form bean.
	 * 
	 * @return the unique identifier.
	 */
	long getCardUidPk();

	/**
	 * Sets the unique identifier of the credit card represented by this form bean.
	 * 
	 * @param cardUidPk the unique identifier for the credit card.
	 */
	void setCardUidPk(final long cardUidPk);

	/**
	 * @return the securityCode
	 */
	String getSecurityCode();

	/**
	 * @param securityCode the securityCode to set
	 */
	void setSecurityCode(final String securityCode);
	
	/**
	 * Returns true if the credit card update request is from the checkout page.
	 * @return true if the credit card update request is from the checkout page.
	 */
	boolean isRequestFromCheckout();
	
	/**
	 * Set whether or not the credit card update request is from the checkout page.
	 * @param requestFromCheckout set to true to indicate that the request is from the checkout page
	 */
	void setRequestFromCheckout(final boolean requestFromCheckout);
	
}
