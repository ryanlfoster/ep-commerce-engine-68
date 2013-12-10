/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.commons.util;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;

import com.elasticpath.domain.order.OrderPayment;

/**
 * {@link Comparator} for {@link OrderPayment}s with payment methods of type {@link com.elasticpath.plugin.payment.PaymentType#GIFT_CERTIFICATE}.
 */
public class GiftCertificateOrderPaymentComparator implements Comparator<OrderPayment> {
	@Override
	public int compare(final OrderPayment payment1, final OrderPayment payment2) {
		return new CompareToBuilder()
				.append(payment1.getGiftCertificate().getGiftCertificateCode(), payment2.getGiftCertificate().getGiftCertificateCode())
				.toComparison();

	}
}