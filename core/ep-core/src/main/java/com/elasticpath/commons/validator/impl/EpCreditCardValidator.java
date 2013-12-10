/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.validator.impl;

import java.util.List;

import org.apache.commons.validator.CreditCardValidator;
import org.apache.commons.validator.CreditCardValidator.CreditCardType;

/**
 * A bean that is used to add new credit card types to the underlying credit card validator
 * using a spring configuration file.
 */
public class EpCreditCardValidator {

	private final CreditCardValidator validator = new CreditCardValidator();
	
	/**
	 * Sets the credit card types for the validator.
	 * 
	 * @param creditCardTypes the credit card types list
	 */
	public void setCreditCardTypes(final List<CreditCardType> creditCardTypes) {
		for (CreditCardType type : creditCardTypes) {
			validator.addAllowedCardType(type);
		}
	}

	/**
	 * Checks whether a credit card number is valid.
	 * 
	 * @param cardNumber a card number
	 * @return true if the card number is valid
	 */
	public boolean isCreditCardValid(final String cardNumber) {
		return validator.isValid(cardNumber);
	}
}
