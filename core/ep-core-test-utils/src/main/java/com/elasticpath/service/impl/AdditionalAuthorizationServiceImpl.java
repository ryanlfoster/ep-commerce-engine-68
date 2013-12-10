package com.elasticpath.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.ReAuthorizationItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;
import com.elasticpath.service.AdditionalAuthorizationService;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.store.StoreService;

/**
 * Service provides methods for checking necessity and processing additional authorization of order shipments.
 */
public class AdditionalAuthorizationServiceImpl implements AdditionalAuthorizationService {

	private PaymentService paymentService;
	private StoreService storeService;
	private ElasticPath elasticPath;

	@Override
	public List<ReAuthorizationItem> getReAuthorizationItemList(final Order order) {
		final List<ReAuthorizationItem> reAuthorizationList = new ArrayList<ReAuthorizationItem>();
		for (final OrderShipment shipment : order.getAllShipments()) {
			BigDecimal additionalAmount = shipment.getTotal();
			OrderPayment originalPayment = paymentService.getLastAuthorizationPayments(shipment);

			if (originalPayment != null) {
				additionalAmount = paymentService.getAdditionalAuthAmount(shipment);
			}

			if (additionalAmount.compareTo(BigDecimal.ZERO) > 0) {
				final ReAuthorizationItem reAuthorizationItem = new ReAuthorizationItem();
				reAuthorizationItem.setShipment(shipment);
				reAuthorizationItem.setOldPayment(originalPayment);
				reAuthorizationList.add(reAuthorizationItem);
			}
		}
		return reAuthorizationList;
	}

	/**
	 * Process reauthorization of adjusted and new shipments.
	 * 
	 * @param reAuthorizationList the list of containers with information necessary for authorization
	 */
	public void authorizeOrder(final List<ReAuthorizationItem> reAuthorizationList) {
		for (final ReAuthorizationItem reAuthorizationItem : reAuthorizationList) {
			try {
				if (reAuthorizationItem.getNewPayment() == null) {
					throw new EpServiceException("Payment template for re-authorization must be specified.");
				}
				PaymentResult paymentResult;
				if (reAuthorizationItem.getOldPayment() == null) {
					paymentResult = paymentService.initializeNewShipmentPayment(reAuthorizationItem.getShipment(), reAuthorizationItem
							.getNewPayment());
				} else {
					paymentResult = paymentService.adjustShipmentPayment(reAuthorizationItem.getShipment(), reAuthorizationItem.getNewPayment());
				}

				for (OrderPayment proccessedPayment : paymentResult.getProcessedPayments()) {
					// Need to reinit shipment since PaymentService is a remote service and those changes done remotely
					// aren't affect local shipment.
					proccessedPayment.setOrderShipment(reAuthorizationItem.getShipment());
					reAuthorizationItem.getShipment().getOrder().addOrderPayment(proccessedPayment);
					if (OrderPayment.AUTHORIZATION_TRANSACTION.equals(proccessedPayment.getTransactionType())) {
						reAuthorizationItem.setNewPayment(proccessedPayment);
					}
				}

				if (paymentResult.getResultCode() != PaymentResult.CODE_OK && paymentResult.getCause() != null) {
					throw paymentResult.getCause();
				}
			} catch (final PaymentGatewayException gatewayException) {
				throw new EpServiceException("Exception with payment gateway", gatewayException);
			} catch (final PaymentProcessingException error) {
				reAuthorizationItem.setError(error);
			}
		}
	}

	/**
	 * Set reAuthItem.newPayment and fill it with information about credit card or gift certificate and payment gateways.
	 * 
	 * @param reAuthorizationItem container with payment information to be updated with new payment
	 * @param orderPayment order payment selected in UI
	 * @return reauthorization item
	 */
	public ReAuthorizationItem setNewPaymentInformation(final ReAuthorizationItem reAuthorizationItem, final OrderPayment orderPayment) {
		reAuthorizationItem.setNewPayment((OrderPayment) elasticPath.getBean(ContextIdNames.ORDER_PAYMENT));
		final Order order = reAuthorizationItem.getShipment().getOrder();
		final Store store = getStoreService().findStoreWithCode(order.getStoreCode());
		final PaymentGateway paymentGateway = store.getPaymentGatewayMap().get(orderPayment.getPaymentMethod());
		if (paymentGateway.getPaymentType() == PaymentType.CREDITCARD) {
			reAuthorizationItem.getNewPayment().copyCreditCardInfo(orderPayment);
		} else if (paymentGateway.getPaymentType() == PaymentType.GIFT_CERTIFICATE) {
			reAuthorizationItem.getNewPayment().setGiftCertificate(orderPayment.getGiftCertificate());
		}
		reAuthorizationItem.getNewPayment().copyTransactionFollowOnInfo(orderPayment);
		return reAuthorizationItem;
	}
	
	/**
	 * Sets payment service.
	 * 
	 * @param paymentService payment service.
	 */
	public void setPaymentService(final PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	/**
	 * Gets payment service.
	 * 
	 * @return payment service.
	 */
	public PaymentService getPaymentService() {
		return paymentService;
	}

	protected StoreService getStoreService() {
		return storeService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	/**
	 * Sets elastic path.
	 * 
	 * @param elasticPath elastic path instance.
	 */
	public void setElasticPath(final ElasticPath elasticPath) {
		this.elasticPath = elasticPath;
	}

	/**
	 * Gets elastic path.
	 * 
	 * @return elastic path instance.
	 */
	public ElasticPath getElasticPath() {
		return elasticPath;
	}
}
