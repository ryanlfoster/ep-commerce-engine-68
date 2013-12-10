/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.test.builders;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerCreditCard;

/**
* Builder that creates instances of {@link CustomerCreditCard}.
*/
public class CustomerCreditCardBuilder {
	private final BeanFactory beanFactory;

	private String cardHolderName;
	private String cardNumber;
	private String cardType;
	private String expiryMonth;
	private String expiryYear;
	private boolean defaultCard;

	/**
	 * Creates a new builder that uses the provided bean factory to create the initial CustomerCreditCard instance.
	 * @param beanFactory
	 */
	public CustomerCreditCardBuilder(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public CustomerCreditCardBuilder withCardHolderName(final String cardHolderName) {
		this.cardHolderName = cardHolderName;
		return this;
	}

	public CustomerCreditCardBuilder withCardNumber(final String cardNumber) {
		this.cardNumber = cardNumber;
		return this;
	}

	public CustomerCreditCardBuilder withCardType(final String cardType) {
		this.cardType = cardType;
		return this;
	}

	public CustomerCreditCardBuilder withExpiryMonth(final String expiryMonth) {
		this.expiryMonth = expiryMonth;
		return this;
	}

	public CustomerCreditCardBuilder withExpiryYear(final String expiryYear) {
		this.expiryYear = expiryYear;
		return this;
	}

	public CustomerCreditCardBuilder withDefaultCard(final boolean defaultCard) {
		this.defaultCard = defaultCard;
		return this;
	}

	public CustomerCreditCard build() {
		final CustomerCreditCard creditCard = beanFactory.getBean(ContextIdNames.CUSTOMER_CREDIT_CARD);
		creditCard.setCardHolderName(cardHolderName);
		creditCard.setCardNumber(cardNumber);
		creditCard.setCardType(cardType);
		creditCard.setExpiryMonth(expiryMonth);
		creditCard.setExpiryYear(expiryYear);
		creditCard.setDefaultCard(defaultCard);
		return creditCard;
	}
}
