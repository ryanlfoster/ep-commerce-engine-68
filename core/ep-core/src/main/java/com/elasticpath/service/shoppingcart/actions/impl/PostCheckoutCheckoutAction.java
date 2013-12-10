package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.service.shoppingcart.CheckoutEventHandler;
import com.elasticpath.service.shoppingcart.actions.FinalizeCheckoutAction;
import com.elasticpath.service.shoppingcart.actions.FinalizeCheckoutActionContext;

/**
 * CheckoutAction to trigger postCheckout events registered through checkoutEventHandler.
 */
public class PostCheckoutCheckoutAction implements FinalizeCheckoutAction {
	private CheckoutEventHandler checkoutEventHandler;

	@Override
	public void execute(final FinalizeCheckoutActionContext context) throws EpSystemException {
		checkoutEventHandler.postCheckout(context.getShoppingCart(), context.getOrderPaymentTemplate(),
				context.getOrder());
	}

	protected CheckoutEventHandler getCheckoutEventHandler() {
		return checkoutEventHandler;
	}

	public void setCheckoutEventHandler(final CheckoutEventHandler checkoutEventHandler) {
		this.checkoutEventHandler = checkoutEventHandler;
	}
}