/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment.impl;

import com.elasticpath.plugin.payment.PaymentType;

/**
 * Credit card payment handler.
 */
public class CreditCardPaymentHandler extends AbstractPaymentHandler {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	@Override
	protected PaymentType getPaymentType() {	
		return PaymentType.CREDITCARD;
	}
}
