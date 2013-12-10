/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.PaymentsComparatorFactory;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.payment.CreditCardPaymentGateway;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.PaymentHandler;
import com.elasticpath.domain.payment.PaymentHandlerFactory;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.exceptions.InsufficientFundException;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.order.InvalidShipmentStateException;
import com.elasticpath.service.payment.PaymentGatewayService;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.payment.PaymentServiceException;
import com.elasticpath.service.store.StoreService;

/**
 * Payment service implementation.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class PaymentServiceImpl extends AbstractEpServiceImpl implements PaymentService {

	private static final String PAYMENT_FATAL_FAIL = "Exception occurred while processing payments";

	private static final Logger LOG = Logger.getLogger(PaymentServiceImpl.class);

	private PaymentHandlerFactory paymentHandlerFactory;

	private PaymentGatewayService paymentGatewayService;
	private StoreService storeService;

	/**
	 * Creates authorization order payments and processes them using the appropriate payment gateways.
	 *
	 * @param order the order
	 * @param templateOrderPayment order payment with info on the payment method
	 * @param giftCertificates gift certificates to be applied to this order
	 * @throws PaymentServiceException on error
	 * @return PaymentResult
	 */
	public PaymentResult initializePayments(final Order order, final OrderPayment templateOrderPayment,
			final Collection<GiftCertificate> giftCertificates) throws PaymentServiceException {
		/** transform raw gift certificates to the list of gc template payments. */
		final List<OrderPayment> allTemplatePayments = createGiftCertificatePayments(giftCertificates);
		/** add conventional template payment to the list of template payments. */
		allTemplatePayments.add(templateOrderPayment);

		final PaymentResult result = getBean(ContextIdNames.PAYMENT_RESULT);
		final PaymentHandler paymentHandler = this.getPaymentHandler(templateOrderPayment.getPaymentMethod());

		/** Pre-authorize the order, if necessary */
		final Collection<OrderPayment> initOrderPayments = paymentHandler.beforeInitializePayments(templateOrderPayment, order);
		if (initOrderPayments != null) {
		preAuthorize(order, initOrderPayments, result);
		if (result.getResultCode() != PaymentResult.CODE_OK) {
			return result;
		}
		}

		/** Pre-authorize each shipment */
		for (final OrderShipment orderShipment : order.getAllShipments()) {
			/** collect auth payments using collection of template payments. */
			Collection<OrderPayment> shipmentAuthPayments = new ArrayList<OrderPayment>();
            for (OrderPayment tempOrderPayment : allTemplatePayments) {
            	final PaymentHandler templatePaymentHandler = this.getPaymentHandler(tempOrderPayment.getPaymentMethod());
            	templatePaymentHandler.beforePreAuthorizePayment(tempOrderPayment, orderShipment, shipmentAuthPayments);
            }
			preAuthorize(orderShipment, shipmentAuthPayments, result, !paymentHandler.canAuthorizePartly(orderShipment));
		}

		return result;
	}

	/**
	 * Handles payments on adding a new shipment.
	 *
	 * @param orderShipment the order shipment
	 * @param templateOrderPayment order payment with info on the payment method
	 * @throws PaymentServiceException on error
	 * @return PaymentResult
	 */
	public PaymentResult initializeNewShipmentPayment(final OrderShipment orderShipment, final OrderPayment templateOrderPayment)
			throws PaymentServiceException {
		final PaymentResult result = getBean(ContextIdNames.PAYMENT_RESULT);

		final PaymentHandler paymentHandler = this.getPaymentHandler(templateOrderPayment.getPaymentMethod());

		/** let the handler customize the payments */
		final Collection<OrderPayment> orderPayments = paymentHandler.beforePreAuthorizePayment(templateOrderPayment, orderShipment, null);

		/** authorize the shipment. */
		preAuthorize(orderShipment, orderPayments, result, true);

		return result;
	}

	/**
	 * Determines additional authorization amount. If re-auth isn't required, zero will be returned.
	 *
	 * @param orderShipment the order shipment the adjustment is based on.
	 * @return additional auth amount.
	 */
	public BigDecimal getAdditionalAuthAmount(final OrderShipment orderShipment) {
		final BigDecimal requiredAuthAmount = OrderPaymentHelper.calculateFullAuthorizationAmount(orderShipment);
		final BigDecimal authByGC = OrderPaymentHelper.getAuthorizedAmountByGCPayments(orderShipment);
		final BigDecimal authByConventional = OrderPaymentHelper.getAuthorizedByConventional(orderShipment);
		BigDecimal additionalAuthAmount = requiredAuthAmount.subtract(authByGC.add(authByConventional));

		if (additionalAuthAmount.compareTo(BigDecimal.ZERO) > 0) {
			OrderPayment activeConvPayment = OrderPaymentHelper.findActiveConventionalAuthorizationPayment(orderShipment);
			if (activeConvPayment != null) {
				final PaymentHandler paymentHandler = paymentHandlerFactory.getPaymentHandler(activeConvPayment.getPaymentMethod());
				if (paymentHandler.canCapture(activeConvPayment, requiredAuthAmount.subtract(authByGC))) { // NOPMD
					return BigDecimal.ZERO;
				}
			}
			return additionalAuthAmount;
		}
		return BigDecimal.ZERO;
	}

	/**
	 * Gets the list of all active authorization payments which are either
	 * active conventional payment or/and list of active gift certificate payments.
	 * Specifically first will go gift certificate active auth payments ordered by creation date,
	 * then conventional payment will go.
	 *
	 * @param orderShipment the order shipment
	 * @return collection of all active authorization payments
	 */
	public Collection<OrderPayment> getAllActiveAutorizationPayments(final OrderShipment orderShipment) {
		final Collection<OrderPayment> allActiveAuthPayments = new ArrayList<OrderPayment>();

		Collection<OrderPayment> gcAuths = OrderPaymentHelper.findActiveGiftCertificateAuthPayments(orderShipment);

		return sortPaymentList(orderShipment, allActiveAuthPayments, gcAuths);
	}

	private Collection<OrderPayment> sortPaymentList(
			final OrderShipment orderShipment,
			final Collection<OrderPayment> allActiveAuthPayments,
			final Collection<OrderPayment> gcAuths) {
		/** sorting is required to make capture (and may be some other) operations consistent. Capture ordering should coincide with auth date. */
		List<OrderPayment> gcList = new ArrayList<OrderPayment>(gcAuths);
		Collections.sort(gcList, PaymentsComparatorFactory.getOrderPaymentDateCompatator());
		allActiveAuthPayments.addAll(gcList);

		OrderPayment activeConventionalAuthPayment = OrderPaymentHelper.findActiveConventionalAuthorizationPayment(orderShipment);

		if (activeConventionalAuthPayment != null) {
			allActiveAuthPayments.add(activeConventionalAuthPayment);
		}

		return allActiveAuthPayments;
	}

	/**
	 * Gets the list of all authorization payments which are either
	 * conventional payment or/and list of non-reverted gift certificate payments.
	 * Specifically first will go gift certificate non-reverted auth payments ordered by creation date,
	 * then conventional payment will go.
	 *
	 * @param orderShipment the order shipment
	 * @return collection of all authorization payments
	 */
	public Collection<OrderPayment> getAllAuthorizationPayments(final OrderShipment orderShipment) {
		final Collection<OrderPayment> allActiveAuthPayments = new ArrayList<OrderPayment>();

		Collection<OrderPayment> gcAuths = OrderPaymentHelper.findNonRevertedGiftCertificateAuthPayments(orderShipment);

		return sortPaymentList(orderShipment, allActiveAuthPayments, gcAuths);
	}

	/**
	 * Gets the last of all authorization payments which are either
	 * conventional payment or/and list of non-reverted gift certificate payments.
	 *
	 * @param orderShipment the order shipment
	 * @return the last of all authorization payments or null if there are no authorization payments for this shipment
	 */
	public OrderPayment getLastAuthorizationPayments(final OrderShipment orderShipment) {
		List<OrderPayment> allActiveAuthPayments = new ArrayList<OrderPayment>(getAllAuthorizationPayments(orderShipment));
		Collections.sort(allActiveAuthPayments, PaymentsComparatorFactory.getOrderPaymentDateCompatator());
		if (!allActiveAuthPayments.isEmpty()) {
			return allActiveAuthPayments.get(0);
		}
		return null;
	}

	/**
	 * Adjusts a payment shipment by reversing all the auth transactions which can be gift certificates and/or conventional payment
	 * (returned by getAllActiveAutorizationPayments(...) method ) and creating new auths.
	 *
	 * @param orderShipment the order shipment the adjustment is based on
	 * @param templateOrderPayment template payment for new authorization.
	 * @return PaymentResult
	 * @throws PaymentServiceException on error
	 */
	public PaymentResult adjustShipmentPayment(final OrderShipment orderShipment, final OrderPayment templateOrderPayment)
			throws PaymentServiceException {
		logDebugMessage("Enter adjustShipmentPayment(). Order shipment: " + orderShipment);
		final PaymentResult result = getBean(ContextIdNames.PAYMENT_RESULT);

		logDebugMessage("Start search for the authorization payment");

		final PaymentType paymentType = templateOrderPayment.getPaymentMethod();
		final PaymentHandler paymentHandler = this.getPaymentHandler(paymentType);

		if (getAdditionalAuthAmount(orderShipment).compareTo(BigDecimal.ZERO) > 0) {
			/** request creation of new auth(s) */
			final Collection<OrderPayment> newAuthPayments = paymentHandler.beforePreAuthorizePayment(templateOrderPayment, orderShipment, null);

			/** request the list of payments to-be-reversed BEFORE new authorization. */
			Collection<OrderPayment> allAuthPayments = getAllActiveAutorizationPayments(orderShipment);
			final Collection<OrderPayment> allReversePayments = new ArrayList<OrderPayment>();
			for (OrderPayment authPayment : allAuthPayments) {
				this.getPaymentHandler(authPayment.getPaymentMethod()).beforeReverseAuthorizePayment(authPayment, orderShipment, allReversePayments);
			}

			/** authorize the shipment. */
			preAuthorize(orderShipment, newAuthPayments, result, true);

			/** if auth failed, prevent reversing. */
			if (result.getResultCode() != PaymentResult.CODE_OK) {
				return result;
			}

			/** now new auth advanced, reverse all previous authorizations */
			reverse(orderShipment.getOrder(), allReversePayments, result);
		}
		return result;
	}

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
	public PaymentResult adjustShipmentPayment(final OrderShipment orderShipment) throws PaymentServiceException {
		logDebugMessage("Enter adjustShipmentPayment(). Order shipment: " + orderShipment);

		if (orderShipment.getTotal() == null || orderShipment.getTotal().doubleValue() == 0) {
			return null;
		}

		Collection<OrderPayment> allActiveAuthPayments = getAllActiveAutorizationPayments(orderShipment);
		if (allActiveAuthPayments.isEmpty()) {
			// no payment to be used as adjustment template.
			throw new PaymentServiceException("No matching authorization payment found");
		}

		// FIXME: get last active gc or conventional. Should I try next payment in case if first failed?
		OrderPayment lastAuth = allActiveAuthPayments.iterator().next();

		return adjustShipmentPayment(orderShipment, lastAuth);
	}

	/**
	 * Cancel all the authorization payments on an order.
	 *
	 * @param order the order
	 * @throws PaymentServiceException if the order state is inappropriate or runtime error occurs
	 * @return PaymentResult
	 */
	public PaymentResult cancelOrderPayments(final Order order) throws PaymentServiceException {

		PaymentResult result = getBean(ContextIdNames.PAYMENT_RESULT);

		// 1. Validate that the order can be canceled.
		if (!order.isCancellable()) {
			throw new PaymentServiceException("Order is not cancellable");
		}

		// 2. reverse the preAuth
		for (OrderShipment orderShipment : order.getAllShipments()) {
			PaymentResult shipmentPaymentResult = null;
			try {
				shipmentPaymentResult = this.cancelShipmentPayment(orderShipment);
			} finally {
				if (shipmentPaymentResult != null) {
					result.addProcessedPayments(shipmentPaymentResult.getProcessedPayments());
				}
			}
		}

		return result;
	}

	/**
	 * Cancels an order shipment payment.
	 *
	 * @param orderShipment the orderShipment
	 * @throws PaymentServiceException on error
	 * @return PaymentResult
	 */
	public PaymentResult cancelShipmentPayment(final OrderShipment orderShipment) throws PaymentServiceException {

		PaymentResult result = getBean(ContextIdNames.PAYMENT_RESULT);

		// 1. Validate that the orderShipment can be canceled.
		if (!orderShipment.isCancellable()) {
			throw new PaymentServiceException("The shipment is not in a state that allows it to be cancelled: " + orderShipment.getShipmentStatus());
		}

		final Collection<OrderPayment> allReversePayments = new ArrayList<OrderPayment>();

		final Collection<OrderPayment> allAuthPayments = getAllActiveAutorizationPayments(orderShipment);
		for (OrderPayment authPayment : allAuthPayments) {
			/** collect all reverse payments. */
			this.getPaymentHandler(authPayment.getPaymentMethod()).beforeReverseAuthorizePayment(authPayment, orderShipment, allReversePayments);
		}
		// 2. reverse all auth payments.
		reverse(orderShipment.getOrder(), allReversePayments, result);
		return result;
	}

	/**
	 * Captures funds for a shipment.
	 *
	 * @param orderShipment the order shipment
	 * @return PaymentResult
	 * @throws PaymentServiceException if there was an error in the payment service
	 * @throws InvalidShipmentStateException if the shipment is not in a state that allows funds to be captured
	 */
	public PaymentResult processShipmentPayment(final OrderShipment orderShipment) throws PaymentServiceException {
		if (orderShipment == null) {
			throw new IllegalArgumentException("Order shipment cannot be null.");
		}

		if (!orderShipment.isReadyForFundsCapture()) {
			throw new InvalidShipmentStateException("Can't capture funds on the orderShipment since it is not in a valid state: "
					+ orderShipment.getShipmentStatus());
		}

		final PaymentResult result = getBean(ContextIdNames.PAYMENT_RESULT);

		/** adjust shipment's authorizations first. */
		PaymentResult adjustPaymentResult = adjustShipmentPayment(orderShipment);
		if (adjustPaymentResult != null && adjustPaymentResult.getResultCode() == PaymentResult.CODE_OK) {
			result.addProcessedPayments(adjustPaymentResult.getProcessedPayments());
		} else {
			return adjustPaymentResult;
		}


		/** now ready for capture.*/
		final Collection<OrderPayment> allAuthPayments = getAllActiveAutorizationPayments(orderShipment);
		final Collection<OrderPayment> allCapturePayments = new ArrayList<OrderPayment>();
		for (OrderPayment authPayment : allAuthPayments) {
			/** collect all capture payments. */
			this.getPaymentHandler(authPayment.getPaymentMethod()).beforeCapturePayment(authPayment, orderShipment, allCapturePayments);
		}

		capture(orderShipment.getOrder(), allCapturePayments, result);

		if (result.getResultCode() != PaymentResult.CODE_OK) {
			return result;
		}

		//TODO: reverse when capture only GC?
		/** reverse unused gift certificate payments. The GC can become unused when user paid by several gift certificates,
		 * but then CSR decreased shipment's total and just few of all certificates are enough to capture money. */
		final Collection<OrderPayment> gcReversePayments = new ArrayList<OrderPayment>();
		final Collection<OrderPayment> activeGCPayments = OrderPaymentHelper.findActiveGiftCertificateAuthPayments(orderShipment);
		for (OrderPayment authPayment : activeGCPayments) {
			this.getPaymentHandler(authPayment.getPaymentMethod()).beforeReverseAuthorizePayment(authPayment, orderShipment, gcReversePayments);
		}
		/** Reverse unused GCs. */
		reverse(orderShipment.getOrder(), gcReversePayments, result);

		return result;
	}

	/**
	 * Rolls back order payments.
	 *
	 * @param payments the payments to be rolled back
	 * @throws PaymentServiceException on error
	 */
	public void rollBackPayments(final Collection<OrderPayment> payments) throws PaymentServiceException {
		if (payments == null) {
			return;
		}
		for (final OrderPayment orderPayment : payments) {
			try {
				if (orderPayment.getStatus() == OrderPaymentStatus.APPROVED) {
					final PaymentGateway paymentGateway = findPaymentGateway(orderPayment.getOrderShipment().getOrder(), orderPayment
							.getPaymentMethod());
					if (orderPayment.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)) {
						logDebugMessage("Call PaymentGateway.reversePreAuthorization() with param: " + orderPayment);
						paymentGateway.reversePreAuthorization(orderPayment);
					} else if (orderPayment.getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION)) {
						logDebugMessage("Call PaymentGateway.voidCaptureOrCredit() with param: " + orderPayment);
						paymentGateway.voidCaptureOrCredit(orderPayment);
					}
				}
			} catch (final Exception exc) {
				LOG.error("Can not rollback the order payment. " + orderPayment, exc);
			}
		}
	}

	/**
	 * Find the payment gateway used to process the shipment and pass on the finalize message.
	 *
	 * @param orderShipment <CODE>OrderShipment</CODE> to be finalized.
	 */
	public void finalizeShipment(final OrderShipment orderShipment) {
		final OrderPayment authPayment = OrderPaymentHelper.findActiveConventionalAuthorizationPayment(orderShipment);
		if (authPayment != null) {
			final PaymentGateway paymentGateway = findPaymentGateway(orderShipment.getOrder(), authPayment.getPaymentMethod());
			paymentGateway.finalizeShipment(orderShipment);
		}
	}
	
	@Override
	public boolean isOrderPaymentRefundable(final OrderPayment orderPayment) {
		if (orderPayment == null) { 
			return false;
		}

		if (!OrderPayment.CAPTURE_TRANSACTION.equals(orderPayment.getTransactionType())) {
			return false;
		}

		return (!orderPayment.getPaymentMethod().equals(PaymentType.CREDITCARD) 
				|| orderPayment.isEncryptedCreditCardStored());

	}

	private void preAuthorize(final Order order, final Collection<OrderPayment> authPayments, final PaymentResult result) {
		// iterate over the newly created order payments
			for (final OrderPayment orderPayment : authPayments) {
				try {
					// trying to pre-auth all the payments returned by the payment handler
					final PaymentGateway paymentGateway = findPaymentGateway(order, orderPayment.getPaymentMethod());
					logDebugMessage("Call PaymentGateway.preAuthorize() with param: " + orderPayment);
					// call payment gateway preAuthorize
					paymentGateway.preAuthorize(orderPayment, order.getBillingAddress());
					// set status approved if no exception occurred
					orderPayment.setStatus(OrderPaymentStatus.APPROVED);
					// TODO: the next was left for pay pal
					// templateOrderPayment.setAuthorizationCode(orderPayment.getAuthorizationCode());
				} catch (final PaymentProcessingException ppe) {
					LOG.debug("Authorization was unsuccessful", ppe);
					orderPayment.setStatus(OrderPaymentStatus.FAILED);
					result.setCause(ppe);
					return;
				} catch (final Exception runtimeExc) {
					throw new PaymentServiceException(PAYMENT_FATAL_FAIL, runtimeExc);
				} finally {
					order.addOrderPayment(orderPayment);
					result.addProcessedPayment(orderPayment);
				}
			}
		}

	/**
	 * Authorizes a list of auth payments.
	 *
	 * @param orderShipment the shipment to be authorized.
	 * @param authPayments payments used to authorize.
	 * @param result payment result.
	 * @param fullAuth true if auth payments' total should be not less than shipment's total. For example GC can have less auth than required,
	 * mark the error in this case.
	 */
	private void preAuthorize(final OrderShipment orderShipment, final Collection<OrderPayment> authPayments, final PaymentResult result,
			final boolean fullAuth) {
		if (authPayments != null) {
			if (fullAuth) {
				BigDecimal newAuthAmount = OrderPaymentHelper.calculateFullAuthorizationAmount(orderShipment);

				BigDecimal authAmount = BigDecimal.ZERO;
				for (OrderPayment payment : authPayments) {
					authAmount = authAmount.add(payment.getAmount());
				}
				if (authAmount.compareTo(newAuthAmount) < 0) {
					result.setResultCode(PaymentResult.CODE_FAILED);
					result.setCause(new InsufficientFundException("Not enough balance to process the payment."));
					return;
				}
			}
			preAuthorize(orderShipment.getOrder(), authPayments, result);
		}
	}

	private void capture(final Order order, final Collection<OrderPayment> payments, final PaymentResult result) {
		if (payments != null) {
			for (final OrderPayment payment : payments) {
				if (payment.getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION)) {
					try {
						PaymentGateway paymentGateway = findPaymentGateway(payment.getOrderShipment().getOrder(), payment.getPaymentMethod());
						// process the payment using the gateway
						logDebugMessage("Capture payment: " + payment);
						paymentGateway.capture(payment);
						logDebugMessage("Capture successful");
						payment.setStatus(OrderPaymentStatus.APPROVED);
					} catch (final PaymentProcessingException ppe) {
						LOG.debug("Payment was unsuccessful", ppe);
						payment.setStatus(OrderPaymentStatus.FAILED);
						result.setCause(ppe);
						return;
					} catch (final Exception exc) {
						throw new PaymentServiceException(PAYMENT_FATAL_FAIL, exc);
					} finally {
						order.addOrderPayment(payment);
						result.addProcessedPayment(payment);
					}
				}
			}
		}
	}

	private void reverse(final Order order, final Collection<OrderPayment> payments, final PaymentResult result) {
		if (payments != null) {
			for (final OrderPayment payment : payments) {
				if (payment.getTransactionType().equals(OrderPayment.REVERSE_AUTHORIZATION)) {
					try {
						PaymentGateway paymentGateway = findPaymentGateway(order, payment.getPaymentMethod());
						// process the payment using the gateway
						logDebugMessage("Reverse GC payment: " + payment);
						paymentGateway.reversePreAuthorization(payment);
						logDebugMessage("Reverse successful");
						payment.setStatus(OrderPaymentStatus.APPROVED);
					} catch (final PaymentProcessingException ppe) {
						LOG.debug("Payment was unsuccessful", ppe);
						payment.setStatus(OrderPaymentStatus.FAILED);
						result.setCause(ppe);
						return;
					} catch (final Exception exc) {
						throw new PaymentServiceException(PAYMENT_FATAL_FAIL, exc);
					} finally {
						order.addOrderPayment(payment);
						result.addProcessedPayment(payment);
					}
				}
			}
		}
	}

	/**
	 * Finds a payment gateway. If none found for the specified payment type - runtime exception is thrown.
	 */
	private PaymentGateway findPaymentGateway(final Order order, final PaymentType paymentType) {
		PaymentGateway paymentGateway;
		Store store = getStoreService().findStoreWithCode(order.getStoreCode());
		if (paymentType == PaymentType.RETURN_AND_EXCHANGE) {
			paymentGateway = getExchangePaymentGateway(store);
		} else {
			paymentGateway = store.getPaymentGatewayMap().get(paymentType);
			if (paymentGateway != null && paymentGateway.getPaymentType() == PaymentType.CREDITCARD) {
				((CreditCardPaymentGateway) paymentGateway).setValidateCvv2(store.isCreditCardCvv2Enabled());
			}
		}

		if (paymentGateway == null) {
			throw new PaymentServiceException(
					"No payment gateway is defined for payment type [" + paymentType + "] in store [" + store.getCode() + "]");
		}
		return paymentGateway;
	}

	/**
	 * Gets the exchange payment gateway defined for this store. If none, try to find it using the gateway service.
	 */
	private PaymentGateway getExchangePaymentGateway(final Store store) {
		Map<PaymentType, PaymentGateway> paymentGatewayMap = store.getPaymentGatewayMap();
		PaymentGateway exchangePaymentGateway = paymentGatewayMap.get(PaymentType.RETURN_AND_EXCHANGE);
		if (exchangePaymentGateway == null) {
			for (PaymentGateway pg : paymentGatewayService.findAllPaymentGateways()) {
				if (pg.getPaymentType() == PaymentType.RETURN_AND_EXCHANGE) {
					return pg;
				}
			}
		}
		return exchangePaymentGateway;
	}

	/**
	 * Gets the payment handler for a specific payment type.
	 *
	 * @param paymentType {@link PaymentType}
	 * @return PaymentHandler
	 */
	private PaymentHandler getPaymentHandler(final PaymentType paymentType) {
		PaymentHandler paymentHandler = paymentHandlerFactory.getPaymentHandler(paymentType);
		if (paymentHandler == null) {
			throw new PaymentServiceException("Payment handler does not exist for payment type: " + paymentType);
		}
		return paymentHandler;
	}

	private void logDebugMessage(final String message) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(message);
		}
	}

	private List<OrderPayment> createGiftCertificatePayments(final Collection<GiftCertificate> giftCertificates) {
		if (giftCertificates == null) {
			return new ArrayList<OrderPayment>();
		}
		List<OrderPayment> gcPayments = new ArrayList<OrderPayment>(giftCertificates.size());
		for (GiftCertificate giftCertificate : giftCertificates) {
			OrderPayment orderPayment = getBean(ContextIdNames.ORDER_PAYMENT);
			orderPayment.setGiftCertificate(giftCertificate);
			orderPayment.setPaymentMethod(PaymentType.GIFT_CERTIFICATE);
			gcPayments.add(orderPayment);
		}
		return gcPayments;
	}

	/**
	 * Sets the payment handler factory.
	 *
	 * @param paymentHandlerFactory the factory to set
	 */
	public void setPaymentHandlerFactory(final PaymentHandlerFactory paymentHandlerFactory) {
		this.paymentHandlerFactory = paymentHandlerFactory;
	}

	/**
	 * Sets the {@link PaymentGatewayService} instance to use.
	 *
	 * @param paymentGatewayService the {@link PaymentGatewayService} instance to use
	 */
	public void setPaymentGatewayService(final PaymentGatewayService paymentGatewayService) {
		this.paymentGatewayService = paymentGatewayService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	protected StoreService getStoreService() {
		return storeService;
	}
}
