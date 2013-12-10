package com.elasticpath.domain.store;

import com.elasticpath.persistence.api.Persistable;

/**
 * Represents a type of credit card.
 */
public interface CreditCardType extends Persistable {
	
	/**
	 * Gets the type of credit card.
	 *
	 * @return the type of credit card
	 */
	String getCreditCardType();
	
	/**
	 * Sets the type of credit card.
	 *
	 * @param creditCardType the type of credit card
	 */
	void setCreditCardType(final String creditCardType);
}
