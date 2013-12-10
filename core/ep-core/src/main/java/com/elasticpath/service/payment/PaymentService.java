/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.payment;

import java.math.BigDecimal;
import java.util.Collection;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.service.EpService;

/**
 * Payment service that handles all the cases when an interaction with the payment gateways is needed.
 */
public interface PaymentService extends EpService {

	/**
	 * Creates authorization order payments and processes them using the appropriate payment gateways.
	 * 
	 * @param order the order
	 * @param templateOrderPayment order payment with info on the payment method
	 * @param giftCertificates gift certificates to be applied to this order
	 * @throws PaymentServiceException on error
	 * @return PaymentResult
	 */
	PaymentResult initializePayments(Order order, OrderPayment templateOrderPayment, Collection<GiftCertificate> giftCertificates)
			throws PaymentServiceException;
	
	/**
	 * Handles payments on adding a new shipment.
	 * 
	 * @param orderShipment the order shipment
	 * @param templateOrderPayment order payment with info on the payment method
	 * @throws PaymentServiceException on error
	 * @return PaymentResult
	 */
	PaymentResult initializeNewShipmentPayment(OrderShipment orderShipment, OrderPayment templateOrderPayment) throws PaymentServiceException;

	/**
	 * Handles payments on completing a shipment. Order adjustment is fulfiled before capture to authorize shipment if required.
	 * 
	 * @param orderShipment the order shipment
	 * @throws PaymentServiceException on error
	 * @return PaymentResult
	 */
	PaymentResult processShipmentPayment(OrderShipment orderShipment) throws PaymentServiceException;

	/**
	 * Cancel all the authorization payments on an order.
	 * 
	 * @param order the order
	 * @throws PaymentServiceException if the order state is inappropriate or runtime error occurs
	 * @return PaymentResult
	 */
	PaymentResult cancelOrderPayments(Order order) throws PaymentServiceException;
	
	/**
	 * Cancels an order shipment payment.
	 * 
	 * @param orderShipment the orderShipment
	 * @throws PaymentServiceException on error
	 * @return PaymentResult
	 */
	PaymentResult cancelShipmentPayment(OrderShipment orderShipment) throws PaymentServiceException;

	/**
	 * Determines additional authorization amount. If re-auth isn't required, zero will be returned.
	 * 
	 * @param orderShipment the order shipment the adjustment is based on
	 * @return additional auth amount
	 */
	BigDecimal getAdditionalAuthAmount(final OrderShipment orderShipment);
	
	/**
	 * Gets the list of all active authorization payments which are either
	 * active conventional payment or/and list of active gift certificate payments.
	 * Specifically first will go gift certificate active auth payments ordered by creation date,
	 * then conventional payment will go.
	 * 
	 * @param orderShipment the order shipment
	 * @return collection of all active authorization payments
	 */
	Collection<OrderPayment> getAllActiveAutorizationPayments(OrderShipment orderShipment);
	
	/**
	 * Gets the last of all active authorization payments which are either
	 * active conventional payment or/and list of active gift certificate payments.
	 * 
	 * @param orderShipment the order shipment
	 * @return the last of all active authorization payments
	 */
	OrderPayment getLastAuthorizationPayments(final OrderShipment orderShipment);
		
	/**
	 * Adjusts a payment shipment by reversing all the auth transactions which can be gift certificates and/or conventional payment
	 * (returned by getAllActiveAutorizationPayments(...) method ) and creating new auths.
	 * 
	 * @param orderShipment the order shipment the adjustment is based on
	 * @param templateOrderPayment template payment for new authorization.
	 * @return PaymentResult
	 * @throws PaymentServiceException on error
	 */
	PaymentResult adjustShipmentPayment(final OrderShipment orderShipment, final OrderPayment templateOrderPayment) throws PaymentServiceException;

	/**
	 * Handles shipment amount changes. If the shipment amount has been changed the old authorization will be reversed and a new one created.
	 * 
	 * The most old gift certificate auth payment if present or auth conventional payment will be used
	 * as template for reauthorization.
	 * 
	 * @param orderShipment the order shipment
	 * @throws PaymentServiceException on error
	 * @return PaymentResult
	 */
	PaymentResult adjustShipmentPayment(OrderShipment orderShipment) throws PaymentServiceException; // valid for physical order shipment
	
	/**
	 * Rolls back the specified payments.
	 * 
	 * @param payments the payments to be rolled back
	 * @throws PaymentServiceException on error
	 */
	void rollBackPayments(Collection<OrderPayment> payments) throws PaymentServiceException;

	// void refund(Order order, OrderPayment payment); // TODO

	/**
	 * Tells the payment service that all payment activities have been completed for the specified shipment and any finalization actions should now
	 * occur, this must be called once payment activities have been completed on the order shipment. For example: When using external checkout
	 * systems they may need to finalize an order and send shipment confirmation emails.
	 * 
	 * @param orderShipment <CODE>OrderShipment</CODE> to be finalized.
	 */
	void finalizeShipment(final OrderShipment orderShipment);
	
	/**
	 * Determines if an {@link OrderPayment} can be refunded.
	 * @param orderPayment the capture payment to be refunded
	 * @return true if the {@link OrderPayment} can be refunded, false otherwise.
	 */
	boolean isOrderPaymentRefundable(final OrderPayment orderPayment);
}
