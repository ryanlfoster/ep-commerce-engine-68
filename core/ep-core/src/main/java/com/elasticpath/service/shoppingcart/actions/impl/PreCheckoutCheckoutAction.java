package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.service.shoppingcart.CheckoutEventHandler;
import com.elasticpath.service.shoppingcart.actions.CheckoutAction;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;

/**
 * CheckoutAction to trigger preCheckout events registered through checkoutEventHandler.
 */
public class PreCheckoutCheckoutAction implements CheckoutAction {
	private CheckoutEventHandler checkoutEventHandler;

	@Override
	public void execute(final CheckoutActionContext context) {
		checkoutEventHandler.preCheckout(context.getShoppingCart(), context.getOrderPaymentTemplate());
	}

	protected CheckoutEventHandler getCheckoutEventHandler() {
		return checkoutEventHandler;
	}

	public void setCheckoutEventHandler(final CheckoutEventHandler checkoutEventHandler) {
		this.checkoutEventHandler = checkoutEventHandler;
	}
}