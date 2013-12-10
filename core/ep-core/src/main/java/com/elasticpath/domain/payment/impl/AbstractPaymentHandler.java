/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.payment.PaymentHandler;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.payment.PaymentServiceException;
import com.elasticpath.service.payment.impl.OrderPaymentHelper;

/**
 * Abstract default impl of Payment handler. Its main role is to handle gift certificate payments and call the abstract methods for retrieving the
 * conventional payments.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public abstract class AbstractPaymentHandler extends AbstractEpDomainImpl implements PaymentHandler {

	private static final long serialVersionUID = -1906539571355394905L;

	/**
	 * Initialize the order payments. For Paypal, we need make an order payment before we can process other payments. Also if we need change the
	 * payment template for different payment methods, do it here. Default implementation returns null.
	 * 
	 * @param order the order
	 * @param templateOrderPayment the order payment template
	 * @return a collection of order payments created
	 */
	public Collection<OrderPayment> beforeInitializePayments(final OrderPayment templateOrderPayment, final Order order) {
		return null;
	}
	
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
	 * @param currentAuthPayments the list of auth payments that was previously created to authorize the shipment. Payments
	 * processed by this handler will be appended to this collection. 
	 * @return collection of "AUTHORIZATION_TRANSACTION" OrderPayments
	 */
	public final Collection<OrderPayment> beforePreAuthorizePayment(final OrderPayment templateOrderPayment, final OrderShipment orderShipment,
			final Collection<OrderPayment> currentAuthPayments) {	

		BigDecimal currentAuthTotal = getCurrentAuthTotal(currentAuthPayments);

		/** calculate the amount left. */
		BigDecimal amountLeftToAuthorize = OrderPaymentHelper.calculateFullAuthorizationAmount(orderShipment).subtract(currentAuthTotal);
		 
		Collection<OrderPayment> orderPayments = null;
		if (amountLeftToAuthorize.compareTo(BigDecimal.ZERO) > 0) {
			/** this handler need to create auth payment. */
			orderPayments = getPreAuthorizedPayments(templateOrderPayment, orderShipment, amountLeftToAuthorize);
			if (currentAuthPayments != null) {
				currentAuthPayments.addAll(orderPayments);
			}
		}

		return orderPayments;
	}

	/**
	 * Get the currently authorized total, based on the supplied collection of auth payments.
	 * @param currentAuthPayments the list of auth payments that was previously created to authorize the shipment.
	 * @return the currently total amount currently authorized 
	 */
	protected BigDecimal getCurrentAuthTotal(final Collection<OrderPayment> currentAuthPayments) {
		BigDecimal currentAuthTotal = BigDecimal.ZERO;
		if (currentAuthPayments != null) {
			for (OrderPayment currentAuthPayment : currentAuthPayments) {
				if (!OrderPayment.AUTHORIZATION_TRANSACTION.equals(currentAuthPayment.getTransactionType())) {
					throw new PaymentServiceException("Not an auth payment. ");
				}
				currentAuthTotal = currentAuthTotal.add(currentAuthPayment.getAmount());
			}			
		}
		return currentAuthTotal;
	}

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
	public final Collection<OrderPayment> beforeCapturePayment(final OrderPayment authOrderPayment, final OrderShipment orderShipment,
			final Collection<OrderPayment> currentCapturePayments) {		
		BigDecimal currentCaptureTotal = BigDecimal.ZERO;
		if (currentCapturePayments != null) {
			for (OrderPayment capturePayment : currentCapturePayments) {
				if (!OrderPayment.CAPTURE_TRANSACTION.equals(capturePayment.getTransactionType())) {
					throw new PaymentServiceException("Not a capture payment. ");
				}
				currentCaptureTotal = currentCaptureTotal.add(capturePayment.getAmount());
			}			
		}
		BigDecimal amountLeftToCapture = OrderPaymentHelper.adjustExchangeOrderCaptureAmount(orderShipment).subtract(currentCaptureTotal);
		Collection<OrderPayment> orderPayments = null;
		if (amountLeftToCapture.compareTo(BigDecimal.ZERO) > 0) {
			/** this handler need to create capture payment. */
			orderPayments = getCapturePayments(authOrderPayment, orderShipment, amountLeftToCapture);
			if (currentCapturePayments != null) {
				currentCapturePayments.addAll(orderPayments);
			}
		}
		return orderPayments;
	}
	
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
	public final Collection<OrderPayment> beforeReverseAuthorizePayment(final OrderPayment authOrderPayment, 
			final OrderShipment orderShipment, final Collection<OrderPayment> currentReversePayments) {				
		Collection<OrderPayment> reversePayments = getReversePayments(authOrderPayment, orderShipment);
		if (currentReversePayments != null && reversePayments != null) {
			//For future usage.
			//authOrderPayment.setStatus(OrderPaymentStatus.REVERSED);
			currentReversePayments.addAll(reversePayments);
		}
		return reversePayments;
	}

	/**
	 * Gets all the auth payments for the specified template order payment.
	 * 
	 * @param templateOrderPayment payment, auth payment should be created for.
	 * @param orderShipment the order shipment
	 * @param amount the amount to be authorized by the payment
	 * @return an order payment
	 */
	protected Collection<OrderPayment> getPreAuthorizedPayments(final OrderPayment templateOrderPayment,
			final OrderShipment orderShipment, final BigDecimal amount) {

		List<OrderPayment> orderPayments = new ArrayList<OrderPayment>(1);
		OrderPayment orderPayment = createAuthOrderPayment(orderShipment, templateOrderPayment, amount);
		orderPayments.add(orderPayment);

		return orderPayments;
	}

	/**
	 * Gets all the reverse payments for the specified auth order payment.
	 * 
	 * @param authPayment auth order payment capture payment should be created for.
	 * @param orderShipment the order shipment
	 * @return an order payment
	 */
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
	 * Gets all the capture payments for the specified auth order payment.
	 * 
	 * @param authPayment auth order payment capture payment should be created for.
	 * @param orderShipment the order shipment
	 * @param amount the amount to be captured by the payment
	 * @return an order payment
	 */
	protected Collection<OrderPayment> getCapturePayments(final OrderPayment authPayment,
			final OrderShipment orderShipment, final BigDecimal amount) {
		List<OrderPayment> orderPayments = new ArrayList<OrderPayment>(1);
		OrderPayment orderPayment = createCapturePayment(orderShipment, amount);
		orderPayments.add(orderPayment);
		return orderPayments;
	}

	
	/**
	 * Determine if it possible to capture specified amount from the payment.
	 * 
	 * @param orderPayment authorization payment.
	 * @param amount amount which we want to capture.
	 * @return if it possible to capture specified amount from the payment.
	 */
	public boolean canCapture(final OrderPayment orderPayment, final BigDecimal amount) {
		return orderPayment.getAmount().compareTo(amount) >= 0;
	}
	/**
	 * Determine if it possible to for the handler to authorize amount less than shipment's or order's total. 
	 * PayPal for an instance allows this when authorization has previously been processed. 
	 * Default false. 
	 *
	 * @param orderShipment the shipment to be analyzed.
	 * @return true if auth total can be less than shipment's/order's total, false otherwise.
	 */
	public boolean canAuthorizePartly(final OrderShipment orderShipment) {
		return false;
	}
	
	/**
	 * @param orderShipment the order shipment
	 * @param templateOrderPayment the template order payment
	 * @param amount the money amount this payment should be
	 * @return OrderPayment
	 */
	protected OrderPayment createAuthOrderPayment(final OrderShipment orderShipment, 
			final OrderPayment templateOrderPayment, final BigDecimal amount) {

		if (amount.compareTo(BigDecimal.ZERO) > 0) {
			OrderPayment orderPayment = getNewOrderPayment();

			orderPayment.copyCreditCardInfo(templateOrderPayment);
			orderPayment.copyTransactionFollowOnInfo(templateOrderPayment);
			orderPayment.setGatewayToken(templateOrderPayment.getGatewayToken());
			orderPayment.setGiftCertificate(templateOrderPayment.getGiftCertificate());
			orderPayment.setAmount(amount);
			orderPayment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
			orderPayment.setCreatedDate(new Date());
			orderPayment.setOrderShipment(orderShipment);
			orderPayment.setReferenceId(orderShipment.getShipmentNumber());
			orderPayment.setIpAddress(orderShipment.getOrder().getIpAddress());
			orderPayment.setPaymentMethod(getPaymentType());
			
			String email = templateOrderPayment.getEmail();
			if (email == null || email.trim().length() == 0) {
				
				Order order = orderShipment.getOrder(); 
				if (order.getCustomer() != null) { //NOPMD
					email = order.getCustomer().getEmail();
				}
			}
			orderPayment.setEmail(email);

			return orderPayment;
		}

		throw new PaymentServiceException("Can not create an authorization payment for less or equal 0");
	}
	
	/**
	 * @param orderShipment the order shipment
	 * @param amount the amount to be captured
	 * @return a collection of OrderPayment objects
	 */
	protected OrderPayment createCapturePayment(final OrderShipment orderShipment, final BigDecimal amount) {

		OrderPayment authorizationPayment = OrderPaymentHelper.findActiveConventionalAuthorizationPayment(orderShipment);
		if (authorizationPayment == null) {
			throw new PaymentServiceException("No authorization payment found");
		}

		OrderPayment capturePayment = getNewOrderPayment();
		capturePayment.copyCreditCardInfo(authorizationPayment);
		capturePayment.copyTransactionFollowOnInfo(authorizationPayment);
		capturePayment.setGatewayToken(authorizationPayment.getGatewayToken());
		capturePayment.setGiftCertificate(authorizationPayment.getGiftCertificate());
		capturePayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		capturePayment.setCreatedDate(new Date());
		capturePayment.setAuthorizationCode(authorizationPayment.getAuthorizationCode());
		capturePayment.setOrderShipment(orderShipment);
		capturePayment.setPaymentMethod(getPaymentType());
		capturePayment.setRequestToken(authorizationPayment.getRequestToken());
		capturePayment.setReferenceId(authorizationPayment.getReferenceId());
		// for some reason the last modified entity listener is not firing when this is cascade persisted from order
		capturePayment.copyLastModifiedDate(authorizationPayment);
		
		String email = authorizationPayment.getEmail();
		if (email == null || email.trim().length() == 0) {
			
			Order order = orderShipment.getOrder(); 
			if (order.getCustomer() != null) {
				email = order.getCustomer().getEmail();
			}
		}
		capturePayment.setEmail(email);

		// set the total amount of the capture payment to be the order shipment total
		capturePayment.setAmount(amount);

		return capturePayment;
	}
	
	/**
	 * Create the payments with ORDER_TRANSACTION type.
	 * 
	 * @param order the order
	 * @param templateOrderPayment the template order payment
	 * @param amount the money amount this payment should be
	 * @return OrderPayment
	 */
	protected OrderPayment createOrderPayment(final Order order, final OrderPayment templateOrderPayment, final BigDecimal amount) {

		if (amount.compareTo(BigDecimal.ZERO) > 0) {
			OrderPayment orderPayment = getNewOrderPayment();

			orderPayment.copyCreditCardInfo(templateOrderPayment);
			orderPayment.copyTransactionFollowOnInfo(templateOrderPayment);
			orderPayment.setGatewayToken(templateOrderPayment.getGatewayToken());
			orderPayment.setGiftCertificate(templateOrderPayment.getGiftCertificate());
			orderPayment.setAmount(amount);
			orderPayment.setTransactionType(OrderPayment.ORDER_TRANSACTION);
			orderPayment.setCreatedDate(new Date());
			orderPayment.setOrderShipment(null);
			orderPayment.setReferenceId(order.getOrderNumber());
			orderPayment.setIpAddress(order.getIpAddress());
			orderPayment.setPaymentMethod(getPaymentType());

			String email = templateOrderPayment.getEmail();
			if (email == null || email.trim().length() == 0) {
				
				Customer customer = order.getCustomer();
				if (customer != null) { //NOPMD
					email = customer.getEmail();
				}
			}
			orderPayment.setEmail(email);
			
			return orderPayment;
		}

		throw new PaymentServiceException("Can not create an authorization payment for less or equal 0");
	}

	/**
	 * Gets the payment type supported by this handler.
	 * 
	 * @return {@link PaymentType}
	 */
	protected abstract PaymentType getPaymentType();

	/**
	 * Creates a new orde payment.
	 * 
	 * @return OrderPayment instance
	 */
	protected OrderPayment getNewOrderPayment() {
		return getBean(ContextIdNames.ORDER_PAYMENT);
	}
	

	// --- private methods section ---

	
}
