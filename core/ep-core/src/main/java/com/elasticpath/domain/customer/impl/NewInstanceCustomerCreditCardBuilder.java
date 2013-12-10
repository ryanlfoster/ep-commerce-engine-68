package com.elasticpath.domain.customer.impl;

import com.elasticpath.domain.customer.CustomerCreditCard;

/**
 * {@link CustomerCreditCard} Builder that uses new to create instances.
 */
public class NewInstanceCustomerCreditCardBuilder extends AbstractCustomerCreditCardBuilder {

	@Override
	protected CustomerCreditCard create() {
		return new CustomerCreditCardImpl();
	}

}