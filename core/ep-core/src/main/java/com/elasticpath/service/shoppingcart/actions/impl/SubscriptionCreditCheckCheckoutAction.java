package com.elasticpath.service.shoppingcart.actions.impl;

import java.math.BigDecimal;
import java.util.Date;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.payment.CreditCardPaymentGateway;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.payment.PaymentServiceException;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;
import com.elasticpath.service.store.StoreService;

/**
 * CheckoutAction to trigger preCheckout events registered through checkoutEventHandler.
 */
public class SubscriptionCreditCheckCheckoutAction implements ReversibleCheckoutAction {

	private BeanFactory beanFactory;
	private StoreService storeService;

	@Override
	public void execute(final CheckoutActionContext context) {
		
		final ShoppingCart shoppingCart = context.getShoppingCart();
		final Order order = context.getOrder();
		final OrderPayment orderPaymentTemplate = context.getOrderPaymentTemplate();
		
		if (BigDecimal.ZERO.compareTo(shoppingCart.getTotal()) == 0 && shoppingCart.hasRecurringPricedShoppingItems()) {
			final OrderPayment authorizationOrderPayment = createOrderPaymentForRecurringChargePurchaseOnly(
					orderPaymentTemplate, OrderPayment.AUTHORIZATION_TRANSACTION);

			final PaymentGateway paymentGateway = findPaymentGateway(order, authorizationOrderPayment.getPaymentMethod());

			paymentGateway.preAuthorize(authorizationOrderPayment, order.getBillingAddress());
			authorizationOrderPayment.setStatus(OrderPaymentStatus.APPROVED);
			order.getOrderPayments().add(authorizationOrderPayment);

			paymentGateway.reversePreAuthorization(authorizationOrderPayment);
			OrderPayment reversalOrderPayment = createOrderPaymentForRecurringChargePurchaseOnly(orderPaymentTemplate,
					OrderPayment.REVERSE_AUTHORIZATION);
			reversalOrderPayment.setAuthorizationCode(authorizationOrderPayment.getAuthorizationCode());
			reversalOrderPayment.setStatus(OrderPaymentStatus.APPROVED);
			order.getOrderPayments().add(reversalOrderPayment);
		}
	}

	private PaymentGateway findPaymentGateway(final Order order, final PaymentType paymentType) {
		final Store store = getStoreService().findStoreWithCode(order.getStoreCode());
		PaymentGateway paymentGateway = store.getPaymentGatewayMap().get(paymentType);
		if (paymentGateway == null) {
			throw new PaymentServiceException("No payment gateway is defined for payment type: " + paymentType);
		}

		if (paymentGateway.getPaymentType() == PaymentType.CREDITCARD) {
			((CreditCardPaymentGateway) paymentGateway).setValidateCvv2(store.isCreditCardCvv2Enabled());
		}

		return paymentGateway;
	}

	private OrderPayment createOrderPaymentForRecurringChargePurchaseOnly(final OrderPayment templateOrderPayment, final String transactionType) {
		OrderPayment orderPayment = getBeanFactory().getBean(ContextIdNames.ORDER_PAYMENT);
		orderPayment.copyCreditCardInfo(templateOrderPayment);
		orderPayment.copyTransactionFollowOnInfo(templateOrderPayment);
		orderPayment.setPaymentForSubscriptions(true);
		orderPayment.setGatewayToken(templateOrderPayment.getGatewayToken());
		orderPayment.setGiftCertificate(templateOrderPayment.getGiftCertificate());
		orderPayment.setAmount(BigDecimal.ONE);
		orderPayment.setTransactionType(transactionType);
		orderPayment.setCreatedDate(new Date());
		orderPayment.usePaymentToken(templateOrderPayment.extractPaymentToken());

		return orderPayment;
	}

	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		//do nothing, it is ok
	}

	/**
	 * Sets the bean factory.
	 * @param beanFactory the bean factory
	 * */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	protected StoreService getStoreService() {
		return storeService;
	}
}