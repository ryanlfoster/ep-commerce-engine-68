/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment;

import java.math.BigDecimal;
import java.util.Collection;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;

/**
 * A Payment Handler acts as an interface between the payment service and the payment gateway.
 * It is responsible for abstracting away the requirements of different types of payment gateways.
 * When an order with shipments is created, depending on the payment methods (gateways) chosen 
 * to pay for the order, different combinations of OrderPayment objects may be required. 
 * The Payment Handler will figure out how to create the different OrderPayments.
 * 
 */
public interface PaymentHandler extends EpDomain {

	/**
	 * Takes in an OrderPayment detailing the customer's payment information, and an Order
	 * that the customer is going to pay for.
	 * 
	 * Returns a set of populated OrderPayment objects that detail all the different payments that should
	 * be applied to this Shipment. The returned OrderPayment objects are of type "ORDER_TRANSACTION".
	 * 
	 * This method is primarily used for Paypal, because it needs to have a special OrderPayment sent
	 * to it before we can make a Pre-Auth against it.
	 * 
	 * @param order the order
	 * @param templateOrderPayment the OrderPayment containing the customer's payment information for the
	 * given Order
	 * @return a collection of "ORDER_TRANSACTION" OrderPayments
	 */
	Collection<OrderPayment> beforeInitializePayments(OrderPayment templateOrderPayment, Order order);
	
	/**
	 * Takes in an OrderPayment detailing the customer's payment information, and an OrderShipment
	 * that the customer is going to pay for. The auth amount will be calculated as order's total
	 * minus sum of amounts of the list of previous auth payments.
	 * 
	 * Returns a set of populated OrderPayment objects that detail all the different payments that should
	 * be applied to this Shipment.
	 * 
	 * @param templateOrderPayment the template order payment
	 * @param orderShipment the order shipment
	 * @param allAuthpayments the list of auth payments that was previously created to authorize the shipment. Payments
	 * processed by this handler will be appended to this collection. 
	 * @return collection of "AUTHORIZATION_TRANSACTION" OrderPayments
	 */
	Collection<OrderPayment> beforePreAuthorizePayment(OrderPayment templateOrderPayment, OrderShipment orderShipment,
			Collection<OrderPayment> allAuthpayments);
	
	/**
	 * Using specified AUTHORIZATION_TRANSACTION payment creates CAPTURE_TRANSACTION OrderPayments to hand off to payment
	 * gateways. The capture amount will be calculated as order's total minus sum of amounts of the list of previous capture payments.
	 * 
	 * @param authOrderPayment auth order payment capture payment should be created for.
	 * @param orderShipment the order shipment
	 * @param currentCapturePayments the list of capture payments that was previously created to capture the shipment. Payments
	 * processed by this handler will be appended to this collection. 
	 * @return a collection of order payments to-be-captured created by this handler.
	 */
	Collection<OrderPayment> beforeCapturePayment(final OrderPayment authOrderPayment, final OrderShipment orderShipment,
			final Collection<OrderPayment> currentCapturePayments);
		
	/**
	 * Using specified AUTHORIZATION_TRANSACTION payment creates REVERSE_AUTHORIZATION_TRANSACTION OrderPayments to hand off to payment
	 * gateways.
	 * 
	 * @param authOrderPayment auth order payment reverse payment should be created for.
	 * @param orderShipment the order shipment
	 * @param currentReversePayments the list of capture payments that was previously created to reverse the shipment. Payments
	 * processed by this handler will be appended to this collection. 
	 * @return a collection of order payments to be reversed created by this handler.
	 */
	Collection<OrderPayment> beforeReverseAuthorizePayment(final OrderPayment authOrderPayment, final OrderShipment orderShipment, 
			final Collection<OrderPayment> currentReversePayments);
	
	/**
	 * Determine if it possible to capture specified amount from the payment.
	 *
	 * @param orderPayment authorization payment. 
	 * @param amount amount which we want to capture.
	 * @return if it possible to capture specified amount from the payment.
	 */
	boolean canCapture(final OrderPayment orderPayment, final BigDecimal amount);
	
	/**
	 * Determine if it possible to for the handler to authorize amount less than shipment's or order's total. 
	 * PayPal for an instance allows this when authorization has previously been processed. 
	 * Default false. 
	 *
	 * @param orderShipment the shipment to be analyzed.
	 * @return true if auth total can be less than shipment's/order's total, false otherwise.
	 */
	boolean canAuthorizePartly(final OrderShipment orderShipment);
}
