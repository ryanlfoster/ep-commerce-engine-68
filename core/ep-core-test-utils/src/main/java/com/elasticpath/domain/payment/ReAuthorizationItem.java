package com.elasticpath.domain.payment;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;

/**
 * Utility container for information related to reauthorization process.
 */
public class ReAuthorizationItem {

	/**
	 * The order shipment for which reauthorization should be made.
	 */
	private OrderShipment shipment;

	/**
	 * The last authorization transaction for the shipment. Can be null in case of new shipment.
	 */
	private OrderPayment oldPayment;

	/**
	 * Reauthorization payment.
	 */
	private OrderPayment newPayment;

	/**
	 * Exception which was thrown on reauthorization. Can be null if reauthorization was successful.
	 */
	private PaymentProcessingException error;

	/**
	 * @return the shipment
	 */
	public OrderShipment getShipment() {
		return shipment;
	}

	/**
	 * @param shipment the shipment to set
	 */
	public void setShipment(final OrderShipment shipment) {
		this.shipment = shipment;
	}

	/**
	 * @return the oldPayment
	 */
	public OrderPayment getOldPayment() {
		return oldPayment;
	}

	/**
	 * @param oldPayment the oldPayment to set
	 */
	public void setOldPayment(final OrderPayment oldPayment) {
		this.oldPayment = oldPayment;
	}

	/**
	 * @return the newPayment
	 */
	public OrderPayment getNewPayment() {
		return newPayment;
	}

	/**
	 * @param newPayment the newPayment to set
	 */
	public void setNewPayment(final OrderPayment newPayment) {
		this.newPayment = newPayment;
	}

	/**
	 * @return the error
	 */
	public PaymentProcessingException getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(final PaymentProcessingException error) {
		this.error = error;
	}
}
