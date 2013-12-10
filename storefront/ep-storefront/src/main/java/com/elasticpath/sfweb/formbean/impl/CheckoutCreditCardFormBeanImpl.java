/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean.impl;

import java.util.List;

import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.sfweb.formbean.CheckoutCreditCardFormBean;

/**
 * Form bean for creating new addresses during the checkout.
 */
public class CheckoutCreditCardFormBeanImpl extends EpFormBeanImpl implements CheckoutCreditCardFormBean {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private CustomerCreditCard newCreditCard;

	private List<CustomerCreditCard> existingCreditCards;

	/**
	 * Get a new <code>CustomerCreditCard</code> object to store a newly entered credit card.
	 * 
	 * @return a new credit card object
	 */
	public CustomerCreditCard getNewCreditCard() {
		return newCreditCard;
	}

	/**
	 * Set the credit card object to be used to record a new credit card.
	 * 
	 * @param creditCard the new credit card
	 */
	public void setNewCreditCard(final CustomerCreditCard creditCard) {
		this.newCreditCard = creditCard;
	}

	/**
	 * Set the available credit cards that the customer may choose to pay with.
	 * 
	 * @param existingCreditCards the list of credit cards
	 */
	public void setExistingCreditCards(final List<CustomerCreditCard> existingCreditCards) {
		this.existingCreditCards = existingCreditCards;
	}

	/**
	 * Get the available credit cards that the customer may choose to pay with.
	 * 
	 * @return the list of credit cards
	 */
	public List<CustomerCreditCard> getExistingCreditCards() {
		return existingCreditCards;
	}
}
