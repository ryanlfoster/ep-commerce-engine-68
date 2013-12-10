package com.elasticpath.test.persister.testscenarios.payment;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;

/**
 * OrderHandlerVerifier verifies payments, shipments, skus expectations.
 */
public class OrderHandlerVerifier {

	private static final Logger LOG = Logger.getLogger(OrderHandlerVerifier.class);

	private static PaymentComparator comparator = new PaymentComparator();

	/** Next is for debug purposes. */
	private static OrderPaymentPrinter paymentPrinter = new OrderPaymentPrinter();

	/**
	 * Checks actual order's payments (whole bunch of payments) against expected ones.
	 * 
	 * @param orderHandler the handler, actual payment will be gotten from.
	 * @param payments expected payments.
	 * @return true is expected payments are in actual and vice versa.
	 */
	public boolean checkPayments(final OrderHandler orderHandler, final OrderPayment... payments) {
		return checkPaymentsInternal(orderHandler.getRealOrder().getOrderPayments(), payments);
	}

	/**
	 * Checks actual shipment's payments against expected ones.
	 * 
	 * @param orderHandler the handler, actual payment will be gotten from.
	 * @param shipmentNumber the number of shipment to be compared.
	 * @param payments expected payments.
	 * @return true is expected payments for the specified shipment are in actual and vice versa.
	 */
	public boolean checkPayments(final OrderHandler orderHandler, final int shipmentNumber, final OrderPayment... payments) {
		long shipmentUidPk = orderHandler.getOrderShipment(shipmentNumber).getUidPk();
		Set<OrderPayment> actualPayments = new HashSet<OrderPayment>();
		for (OrderPayment orderPayment : orderHandler.getRealOrder().getOrderPayments()) {
			if (orderPayment.getOrderShipment() != null && shipmentUidPk == orderPayment.getOrderShipment().getUidPk()) {
				actualPayments.add(orderPayment);
			}
		}

		return checkPaymentsInternal(actualPayments, payments);
	}

	private boolean checkPaymentsInternal(final Set<OrderPayment> actualPayments, final OrderPayment... expectedOrderPayments) {
		OrderPayment[] actualOrderPayments = actualPayments.toArray(new OrderPayment[0]);
		Arrays.sort(expectedOrderPayments, comparator);
		Arrays.sort(actualOrderPayments, comparator);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Expected payments quantity: " + expectedOrderPayments.length + ". Actual payments quantuty: "
					+ actualOrderPayments.length);

			for (OrderPayment expectedPayment : expectedOrderPayments) {
				LOG.debug("Expected payment: " + paymentPrinter.toString(expectedPayment));
			}
			for (OrderPayment orderPayment : actualOrderPayments) {
				LOG.debug("Actual payment: " + paymentPrinter.toString(orderPayment));
			}
		}

		if (expectedOrderPayments.length != actualOrderPayments.length) {
			return false;
		}

		for (int i = 0; i < expectedOrderPayments.length; i++) {
			if (comparator.compare(expectedOrderPayments[i], actualOrderPayments[i]) != 0) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Encountered inconsistency (exp/actual): " + paymentPrinter.toString(expectedOrderPayments[i])
							+ paymentPrinter.toString(actualOrderPayments[i]));
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks skus quantity of the specified shipment.
	 * 
	 * @param orderHandler the handler
	 * @param shipmentNumber the number of shipment to be compared.
	 * @param skusQuantity the expected skus quantity.
	 * @return true if equals
	 */
	public boolean checkSkus(final OrderHandler orderHandler, final int shipmentNumber, final int skusQuantity) {
		OrderShipment orderShipment = orderHandler.getOrderShipment(shipmentNumber);
		return orderShipment.getShipmentOrderSkus().iterator().next().getQuantity() == skusQuantity;
	}

	/**
	 * Checks shipments quantity of the specified order.
	 * 
	 * @param orderHandler the handler.
	 * @param shipmentQuantity the expected number of shipments.
	 * @return true if equals
	 */
	public boolean checkShipments(final OrderHandler orderHandler, final int shipmentQuantity) {
		return orderHandler.getRealOrder().getAllShipments().size() == shipmentQuantity;
	}

	private static class OrderPaymentPrinter {
		String toString(final OrderPayment payment) {
			StringBuffer buffer = new StringBuffer("\nPayment wrapper: \n");
			buffer.append("Payment Type: ").append(payment.getPaymentMethod()).append("\n");
			buffer.append("Amount: ").append(payment.getAmount()).append("\n");
			buffer.append("Status: ").append(payment.getStatus()).append("\n");
			buffer.append("Transaction Type: ").append(payment.getTransactionType()).append("\n");
			return buffer.toString();

		}
	}

	private static class PaymentComparator implements Comparator<OrderPayment> {

		public int compare(final OrderPayment o1, final OrderPayment o2) {
			// Overkills the checkstyle, but rather declarative ).
			if (o1.getPaymentMethod().equals(o2.getPaymentMethod())) {
				if (o1.getStatus().equals(o2.getStatus())) {
					if (o1.getTransactionType().equals(o2.getTransactionType())) {
						if (o1.getAmount().compareTo(o2.getAmount()) == 0) {
							return 0;
						}
						return o1.getAmount().compareTo(o2.getAmount());
					}
					return o1.getTransactionType().compareTo(o2.getTransactionType());
				}
				return o1.getStatus().compareTo(o2.getStatus());
			}
			return o1.getPaymentMethod().compareTo(o2.getPaymentMethod());
		}
	}
}
