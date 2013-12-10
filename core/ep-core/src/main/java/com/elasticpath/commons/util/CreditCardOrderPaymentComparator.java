/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.commons.util;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;

import com.elasticpath.domain.order.OrderPayment;

/**
 * {@link Comparator} for {@link OrderPayment}s with payment methods of type  {@link com.elasticpath.plugin.payment.PaymentType#CREDITCARD}.
 */
public class CreditCardOrderPaymentComparator implements Comparator<OrderPayment> {
	@Override
	public int compare(final OrderPayment payment1, final OrderPayment payment2) {
		return new CompareToBuilder()
				.append(payment1.getCardType(), payment2.getCardType())
				.append(payment1.getDisplayValue(), payment2.getDisplayValue())
				.append(payment1.getCardNumber(), payment2.getCardNumber())
				.append(payment1.getExpiryYear(), payment2.getExpiryYear())
				.append(payment1.getExpiryMonth(), payment2.getExpiryMonth())
				.toComparison();
	}

}
