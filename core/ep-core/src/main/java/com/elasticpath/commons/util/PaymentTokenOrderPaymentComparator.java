package com.elasticpath.commons.util;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;

import com.elasticpath.domain.order.OrderPayment;

/**
 * {@link Comparator} for {@link OrderPayment}s with payment methods of type {@link com.elasticpath.plugin.payment.PaymentType#PAYMENT_TOKEN}.
 */
public class PaymentTokenOrderPaymentComparator implements Comparator<OrderPayment> {
	@Override
	public int compare(final OrderPayment payment1, final OrderPayment payment2) {
		return new CompareToBuilder()
				.append(payment1.getDisplayValue(), payment2.getDisplayValue())
				.toComparison();
	}
}