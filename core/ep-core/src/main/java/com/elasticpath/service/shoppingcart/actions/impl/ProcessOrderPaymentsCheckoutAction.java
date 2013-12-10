package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * CheckoutAction to process order payments (pre-authorizations for all shipments, and capture for electronic shipments).
 */
public class ProcessOrderPaymentsCheckoutAction implements ReversibleCheckoutAction {
	private static final Logger LOG = Logger.getLogger(ProcessOrderPaymentsCheckoutAction.class);

	private PaymentService paymentService;

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {

		final List<OrderPayment> allPayments = new ArrayList<OrderPayment>();

		final PaymentResult orderPaymentResult = paymentService.initializePayments(context.getOrder(),
				context.getOrderPaymentTemplate(), context.getShoppingCart().getAppliedGiftCertificates());
		if (orderPaymentResult.getResultCode() != PaymentResult.CODE_OK) {
			LOG.debug("Payment service reported failed payments.", orderPaymentResult.getCause());
			if (orderPaymentResult.getCause() != null) {
				throw orderPaymentResult.getCause();
			}
			throw new EpServiceException("Payment service reported failed payments.");
		}
		allPayments.addAll(orderPaymentResult.getProcessedPayments());

		capturePaymentsForElectronicShipments(context.getOrder(), allPayments);

		context.setOrderPaymentList(allPayments);
	}

	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		if (context.getOrder() != null) {
			paymentService.rollBackPayments(context.getOrder().getOrderPayments());
		}
	}

	private void capturePaymentsForElectronicShipments(final Order order,
			final List<OrderPayment> allPayments) {
		// capture payments for electronic shipments
		for (final OrderShipment orderShipment : order.getElectronicShipments()) {
			if (orderHasPaymentForShipment(order, orderShipment)) {
				final PaymentResult capturePaymentResult = paymentService.processShipmentPayment(orderShipment);
				if (capturePaymentResult != null) {
					allPayments.addAll(capturePaymentResult.getProcessedPayments());
				}
			}
			// if we don't have to pay for electronically shipments, we should still put the status on SHIPPED
			orderShipment.setStatus(OrderShipmentStatus.SHIPPED);
		}
	}

	private boolean orderHasPaymentForShipment(final Order order,
			final OrderShipment orderShipment) {
		if (orderShipment == null) {
			return false;
		}

		for (OrderPayment orderPayment : order.getOrderPayments()) {
			if (orderShipment.equals(orderPayment.getOrderShipment()) || (orderPayment.getOrderShipment() == null)) {
				return true;
			}
		}
		return false;
	}

	protected PaymentService getPaymentService() {
		return paymentService;
	}

	public void setPaymentService(final PaymentService paymentService) {
		this.paymentService = paymentService;
	}
}