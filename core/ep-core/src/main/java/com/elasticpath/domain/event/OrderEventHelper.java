/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.event;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;

/**
 * The helper on the <code>OrderEvent</code>.
 * Help to generate the event details to track the order changes.
 *
 */
public interface OrderEventHelper {

	/**
	 * Log the event when order placed.
	 *
	 * @param order the new order
	 */
	void logOrderPlaced(final Order order);

	/**
	 * Log the event when order canceled.
	 *
	 * @param order the order been canceled
	 */
	void logOrderCanceled(final Order order);

	/**
	 * Log the event when order shipment released.
	 *
	 * @param order the order
	 * @param shipment the order shipment
	 */
	void logOrderShipmentReleased(final Order order, final OrderShipment shipment);

	/**
	 * Log the event when order shipment canceled.
	 *
	 * @param order the order
	 * @param shipment the order shipment
	 */
	void logOrderShipmentCanceled(final Order order, final OrderShipment shipment);

	/**
	 * Log the event when notes added.
	 *
	 * @param order the order
	 * @param note the note
	 */
	void logOrderNote(final Order order, final String note);

	/**
	 * Log the event when payment captured.
	 *
	 * @param order the order
	 * @param orderPayment the order payment
	 */
	void logOrderPaymentCaptured(final Order order, final OrderPayment orderPayment);

	/**
	 * Log the event when payment refund.
	 *
	 * @param order the order
	 * @param orderPayment the order payment
	 */
	void logOrderPaymentRefund(final Order order, final OrderPayment orderPayment);

	/**
	 * Log the event when new sku added.
	 *
	 * @param shipment the order shipment
	 * @param orderSku the sku be added
	 */
	void logOrderSkuAdded(final OrderShipment shipment, final OrderSku orderSku);

	/**
	 * Log the event when new sku removed.
	 *
	 * @param shipment the order shipment
	 * @param orderSku the sku be removed
	 */
	void logOrderSkuRemoved(final OrderShipment shipment, final OrderSku orderSku);

	/**
	 * Log the event when sku moved to other shipments.
	 *
	 * @param shipment the shipment
	 * @param orderSku the sku be moved
	 */
	void logOrderSkuMoved(final OrderShipment shipment, final OrderSku orderSku);

	/**
	 * Log the event when sku quantity changed.
	 *
	 * @param shipment the order shipment
	 * @param orderSku the sku be changed
	 * @param quantity quantity changed on the order sku.
	 */
	void logOrderSkuQuantityChanged(final OrderShipment shipment, final OrderSku orderSku, final int quantity);

	/**
	 * Log the event when shipping method changed.
	 *
	 * @param order the order
	 * @param shipment the shipment.
	 */
	void logOrderShipmentMethodChanged(final Order order, final PhysicalOrderShipment shipment);

	/**
	 * Log the event when shipping address changed.
	 *
	 * @param order the order
	 * @param shipment the shipment.
	 */
	void logOrderShipmentAddressChanged(final Order order, final PhysicalOrderShipment shipment);

	/**
	 * Log the event when order is put on hold.
	 *
	 * @param order the order
	 */
	void logOrderOnHold(final Order order);

	/**
	 * Log the event when order hold is released.
	 *
	 * @param order the order
	 */
	void logOrderHoldReleased(final Order order);

	/**
	 * Log the event when order return is created.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	void logOrderReturnCreated(final Order order, final OrderReturn orderReturn);

	/**
	 * Log the event when order exchange is created.
	 *
	 * @param order the order
	 * @param orderExchange the order return
	 */
	void logOrderExchnageCreated(final Order order, final OrderReturn orderExchange);

	/**
	 * Log the event when receive the return item.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	void logOrderReturnReceived(final Order order, final OrderReturn orderReturn);

	/**
	 * Log the event when the order return is changed.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	void logOrderReturnChanged(final Order order, final OrderReturn orderReturn);

	/**
	 * Log the event when the order return is canceled.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	void logOrderReturnCanceled(final Order order, final OrderReturn orderReturn);

	/**
	 * Log the event when the order return is completed.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	void logOrderReturnCompleted(final Order order, final OrderReturn orderReturn);

	/**
	 * Log the event when exchange order is created.
	 *
	 * @param order the exchange order
	 */
	void logOrderExchangeCreated(final Order order);

	/**
	 * Log the event when the exchange order is canceled.
	 *
	 * @param order the exchange order
	 * @param orderExchange the orderExchange
	 */
	void logOrderExchangeCanceled(final Order order, final OrderReturn orderExchange);

	/**
	 * Log the event when the exchange order is completed.
	 *
	 * @param order the exchange order
	 * @param orderExchange the orderExchange
	 */
	void logOrderExchangeCompleted(final Order order, final OrderReturn orderExchange);

}
