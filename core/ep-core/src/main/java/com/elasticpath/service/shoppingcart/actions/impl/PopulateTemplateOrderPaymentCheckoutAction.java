package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;
import com.elasticpath.service.store.StoreService;

/**
 * CheckoutAction to populate the template orderPayment object using order details.
 */
public class PopulateTemplateOrderPaymentCheckoutAction implements ReversibleCheckoutAction {

	private StoreService storeService;

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		final OrderPayment templateOrderPayment = context.getOrderPaymentTemplate();
		final Order order = context.getOrder();
		final Store store = getStoreService().findStoreWithCode(order.getStoreCode());

		// Determine whether or not to store the encrypted credit card number, based on a Store setting
		templateOrderPayment.setShouldStoreEncryptedCreditCard(store.isStoreFullCreditCardsEnabled());

		// sets some properties of the order payment object
		// haven't they been set yet?
		templateOrderPayment.setReferenceId(order.getOrderNumber());
		templateOrderPayment.setCurrencyCode(order.getCurrency().getCurrencyCode());
		templateOrderPayment.setEmail(order.getCustomer().getEmail());
		templateOrderPayment.setIpAddress(context.getCustomerIpAddress());
	}

	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		// NO OP
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	public StoreService getStoreService() {
		return storeService;
	}
}