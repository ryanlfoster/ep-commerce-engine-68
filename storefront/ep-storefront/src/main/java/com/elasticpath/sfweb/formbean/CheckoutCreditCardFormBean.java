/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean;

import java.util.List;

import com.elasticpath.domain.customer.CustomerCreditCard;

/**
 * Form bean for creating new credit cards during the checkout.
 */
public interface CheckoutCreditCardFormBean extends EpFormBean {

	/**
	 * Get a new <code>CustomerCreditCard</code> object to store a newly entered credit card.
	 * 
	 * @return a new credit card object
	 */
	CustomerCreditCard getNewCreditCard();

	/**
	 * Set the credit card object to be used to record a new credit card.
	 * 
	 * @param creditCard the new credit cardCreditCard
	 */
	void setNewCreditCard(final CustomerCreditCard creditCard);

	/**
	 * Set the available credit cards that the customer may choose to pay with.
	 * 
	 * @param existingCreditCards the list of credit cards
	 */
	void setExistingCreditCards(final List<CustomerCreditCard> existingCreditCards);

	/**
	 * Get the available credit cards that the customer may choose to pay with.
	 * 
	 * @return the list of credit cards
	 */
	List<CustomerCreditCard> getExistingCreditCards();
}