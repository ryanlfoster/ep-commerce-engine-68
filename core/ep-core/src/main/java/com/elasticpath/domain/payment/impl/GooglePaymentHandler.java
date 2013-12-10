/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.payment.PaymentServiceException;

/**
 * Google checkout handler.
 */
public class GooglePaymentHandler extends AbstractPaymentHandler {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	@Override
	protected Collection<OrderPayment> getPreAuthorizedPayments(final OrderPayment templateOrderPayment,
			final OrderShipment orderShipment, final BigDecimal amount) {

		List<OrderPayment> orderPayments = new ArrayList<OrderPayment>(1);
		OrderPayment orderPayment = createAuthOrderPayment(orderShipment, templateOrderPayment, amount);
		orderPayment.setReferenceId(orderShipment.getOrder().getExternalOrderNumber());
		orderPayments.add(orderPayment);

		return orderPayments;
	}
	
	@Override
	protected Collection<OrderPayment> getReversePayments(final OrderPayment authPayment, final OrderShipment orderShipment) {	
		Collection<OrderPayment> reversePayments = new ArrayList<OrderPayment>();

		if (authPayment != null) {
			OrderPayment reversePayment = createAuthOrderPayment(orderShipment, authPayment, authPayment.getAmount());
			reversePayment.setTransactionType(OrderPayment.REVERSE_AUTHORIZATION);
			reversePayment.setAuthorizationCode(authPayment.getAuthorizationCode());
			reversePayment.setRequestToken(authPayment.getRequestToken());
			reversePayments.add(reversePayment);
		}
		return reversePayments;
	}

	/**
	 * @param orderShipment the order shipment
	 * @param templateOrderPayment the template order payment
	 * @param amount the money amount this payment should be
	 * @return OrderPayment
	 */
	public OrderPayment createAuthOrderPayment(final OrderShipment orderShipment, final OrderPayment templateOrderPayment, final BigDecimal amount) {

		if (amount.compareTo(BigDecimal.ZERO) > 0) {
			OrderPayment orderPayment = getNewOrderPayment();

			orderPayment.copyTransactionFollowOnInfo(templateOrderPayment);
			orderPayment.setGatewayToken(templateOrderPayment.getGatewayToken());
			orderPayment.setGiftCertificate(templateOrderPayment.getGiftCertificate());
			orderPayment.setAmount(amount);
			orderPayment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
			orderPayment.setCreatedDate(new Date());
			orderPayment.setOrderShipment(orderShipment);
			// orderPayment.setReferenceId(orderShipment.getOrder().getOrderNumber());
			orderPayment.setIpAddress(orderShipment.getOrder().getIpAddress());
			orderPayment.setPaymentMethod(getPaymentType());

			return orderPayment;
		}

		throw new PaymentServiceException("Can not create an authorization payment for less or equal 0");
	}

	@Override
	protected Collection<OrderPayment> getCapturePayments(final OrderPayment authPayment, 
			final OrderShipment orderShipment, final BigDecimal amount) {
		List<OrderPayment> orderPayments = new ArrayList<OrderPayment>(1);
		OrderPayment orderPayment = createCapturePayment(orderShipment, amount);
		orderPayment.setReferenceId(orderShipment.getOrder().getExternalOrderNumber());
		orderPayments.add(orderPayment);
		return orderPayments;
	}

	@Override
	protected PaymentType getPaymentType() {		
		return PaymentType.GOOGLE_CHECKOUT;
	}
}
