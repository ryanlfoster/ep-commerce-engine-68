/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.test.integration.checkout;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Factory for creating {@link OrderPaymentMatcher}s for integration tests.
 */
public class OrderPaymentMatcherFactory {
	
	/**
	 * Gets a successful token capture matcher.
	 *
	 * @return the successful token capture
	 */
	public static OrderPaymentMatcher createSuccessfulTokenCapture() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.APPROVED)
				.withType(PaymentType.PAYMENT_TOKEN)
				.withTransaction(OrderPayment.CAPTURE_TRANSACTION)
				.build();
	}

	/**
	 * Gets a successful token authorization matcher.
	 *
	 * @return the successful token authorization
	 */
	public static OrderPaymentMatcher createSuccessfulTokenAuthorization() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.APPROVED)
				.withType(PaymentType.PAYMENT_TOKEN)
				.withTransaction(OrderPayment.AUTHORIZATION_TRANSACTION)
				.build();
	}
	
	/**
	 * Gets a successful token credit matcher.
	 *
	 * @return the successful token credit
	 */
	public static OrderPaymentMatcher createSuccessfulTokenCredit() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.APPROVED)
				.withType(PaymentType.PAYMENT_TOKEN)
				.withTransaction(OrderPayment.CREDIT_TRANSACTION)
				.build();
	}
	
	/**
	 * Gets a successful gift certificate capture matcher.
	 *
	 * @return the successful gift certificate capture
	 */
	public static OrderPaymentMatcher createSuccessfulGiftCertificateCapture() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.APPROVED)
				.withType(PaymentType.GIFT_CERTIFICATE)
				.withTransaction(OrderPayment.CAPTURE_TRANSACTION)
				.build();
	}
	
	/**
	 * Gets a successful gift certificate authorization matcher.
	 *
	 * @return the successful gift certificate authorization
	 */
	public static OrderPaymentMatcher createSuccessfulGiftCertificateAuthorization() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.APPROVED)
				.withType(PaymentType.GIFT_CERTIFICATE)
				.withTransaction(OrderPayment.AUTHORIZATION_TRANSACTION)
				.build();
	}

	/**
	 * Gets a failed token capture matcher.
	 *
	 * @return the failed token capture
	 */
	public static OrderPaymentMatcher createFailedTokenCapture() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.FAILED)
				.withType(PaymentType.PAYMENT_TOKEN)
				.withTransaction(OrderPayment.CAPTURE_TRANSACTION)
				.build();
	}
}
