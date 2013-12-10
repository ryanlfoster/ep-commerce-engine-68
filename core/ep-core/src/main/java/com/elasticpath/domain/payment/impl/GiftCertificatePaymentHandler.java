/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.catalog.GiftCertificateService;

/**
 * Gift certificate payment handler.
 */
public class GiftCertificatePaymentHandler extends AbstractPaymentHandler {
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private GiftCertificateService giftCertificateService;
	
	@Override
	protected Collection<OrderPayment> getReversePayments(final OrderPayment authPayment, final OrderShipment orderShipment) {
		final Collection<OrderPayment> reversePayments = new ArrayList<OrderPayment>();

		if (authPayment != null) {
			OrderPayment reversePayment = createGiftCertificatePayment(orderShipment, authPayment.getAmount(), authPayment.getGiftCertificate());
			reversePayment.setTransactionType(OrderPayment.REVERSE_AUTHORIZATION);
			reversePayment.setAuthorizationCode(authPayment.getAuthorizationCode());
			reversePayments.add(reversePayment);
		}

		return reversePayments;
	}

	@Override
	protected Collection<OrderPayment> getPreAuthorizedPayments(final OrderPayment templateOrderPayment,
			final OrderShipment orderShipment, final BigDecimal amount) {
		final List<OrderPayment> orderPayments;
		if (templateOrderPayment.getGiftCertificate() == null) {
			orderPayments = Collections.emptyList();
		} else {
			orderPayments = new ArrayList<OrderPayment>(1);
			Collection<GiftCertificate> giftCertificates = new ArrayList<GiftCertificate>(1);
			giftCertificates.add(templateOrderPayment.getGiftCertificate());
			addGiftCertificatePayments(orderShipment, amount, giftCertificates, orderPayments);
		}
		return orderPayments;
	}

	@Override
	protected Collection<OrderPayment> getCapturePayments(final OrderPayment authPayment, 
			final OrderShipment orderShipment, final BigDecimal amount) {

		List<OrderPayment> capturePayments = new ArrayList<OrderPayment>();
		BigDecimal gcBalance = authPayment.getAmount();
		final BigDecimal currGCCapture;
		if (gcBalance.compareTo(amount) >= 0) {
			currGCCapture = amount;
		} else {
			currGCCapture = gcBalance;
		}
		if (currGCCapture.compareTo(BigDecimal.ZERO) > 0) {
			OrderPayment capturePayment = createGiftCertificatePayment(orderShipment, currGCCapture, authPayment.getGiftCertificate());
			capturePayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
			capturePayment.setAuthorizationCode(authPayment.getAuthorizationCode());
			capturePayments.add(capturePayment);
		}

		return capturePayments;
	}

	@Override
	protected PaymentType getPaymentType() {
		return PaymentType.GIFT_CERTIFICATE;
	}
	
	/**
	 * Process the gift certificate payments.
	 * 
	 * @param orderShipment the order shipment
	 * @param giftCertificates the applied gift certificates
	 * @param orderPaymentList the payment list
	 * @param amount the required amount
	 * @return the total amount that gift certificate can cover.
	 */
	private BigDecimal addGiftCertificatePayments(final OrderShipment orderShipment, final BigDecimal amount,
			final Collection<GiftCertificate> giftCertificates, final List<OrderPayment> orderPaymentList) {
		BigDecimal captureTotal = amount;
		BigDecimal authTotal = BigDecimal.ZERO;

		for (GiftCertificate giftCertificate : giftCertificates) {
			BigDecimal authAmount = BigDecimal.ZERO;
			if (captureTotal.compareTo(BigDecimal.ZERO) == 0) {
				break; // When enough money is captured and the limit is reached for GC discount, end the process.
			}

			BigDecimal gcBalance = getGiftCertificateService().getBalance(giftCertificate);

			if (gcBalance.compareTo(captureTotal) >= 0) {
				authAmount = captureTotal;
				captureTotal = BigDecimal.ZERO;
			} else {
				authAmount = gcBalance;
				captureTotal = captureTotal.subtract(gcBalance);
			}
			if (authAmount.compareTo(BigDecimal.ZERO) > 0) {
				authTotal = authTotal.add(authAmount);
				final OrderPayment authPayment = createGiftCertificatePayment(orderShipment, authAmount, giftCertificate);
				authPayment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
				orderPaymentList.add(authPayment);
			}
		}
		return authTotal;
	}

	private OrderPayment createGiftCertificatePayment(final OrderShipment orderShipment, final BigDecimal amount,
			final GiftCertificate giftCertificate) {
		final OrderPayment giftCertificatePayment = getNewOrderPayment();
		Order order = orderShipment.getOrder();
		giftCertificatePayment.setPaymentMethod(getPaymentType());
		giftCertificatePayment.setGiftCertificate(giftCertificate);
		giftCertificatePayment.setCurrencyCode(order.getCurrency().getCurrencyCode());
		giftCertificatePayment.setEmail(order.getCustomer().getEmail());
		giftCertificatePayment.setCreatedDate(new Date());
		giftCertificatePayment.setOrderShipment(orderShipment);
		giftCertificatePayment.setAmount(amount);
		return giftCertificatePayment;
	}

	/**
	 * Get the gift certificate service.
	 * 
	 * @return GiftCertificateService
	 */
	private GiftCertificateService getGiftCertificateService() {
		if (giftCertificateService == null) {
			giftCertificateService = getBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE);
		}
		return giftCertificateService;
	}
}
