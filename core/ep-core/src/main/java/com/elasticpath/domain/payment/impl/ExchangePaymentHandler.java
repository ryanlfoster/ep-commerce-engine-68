/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment.impl;

import java.math.BigDecimal;
import java.util.Collection;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.payment.PaymentServiceException;

/**
 * Exchange order payment handler.
 */
public class ExchangePaymentHandler extends AbstractPaymentHandler {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	@Override
	protected Collection<OrderPayment> getPreAuthorizedPayments(final OrderPayment templateOrderPayment,
			final OrderShipment orderShipment, final BigDecimal amount) {
		
		if (BigDecimal.ONE.compareTo(amount) != 0) {
			throw new PaymentServiceException("Exchange order can only be authorized for $1. Passed value: " + amount);
		}
		
		return super.getPreAuthorizedPayments(templateOrderPayment, orderShipment, amount);
		}
	
	@Override
	protected PaymentType getPaymentType() {		
		return PaymentType.RETURN_AND_EXCHANGE; 
	}
}
