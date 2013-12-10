package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.order.Order;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shoppingcart.CheckoutEventHandler;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * CheckoutAction to persist (update) the order after checkout processing.
 */
public class UpdateOrderCheckoutAction implements ReversibleCheckoutAction {

	private CheckoutEventHandler checkoutEventHandler;

	private OrderService orderService;

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		// Notify the checkout event handler before the order is persisted
		// No part of the order should be saved until after this point so that
		// the checkoutEventHandler can reject an order by throwing an exception
		checkoutEventHandler.preCheckoutOrderPersist(context.getShoppingCart(),
				context.getOrderPaymentList(), context.getOrder());

		//process and update order - should limit our updates to once
		final Order updatedOrder = orderService.processOrderOnCheckout(context.getOrder(),
				context.getShoppingCart().isExchangeOrderShoppingCart());
		context.setOrder(updatedOrder);
		context.setOrderPaymentList(updatedOrder.getOrderPayments());
	}

	@Override
	public void rollback(final CheckoutActionContext context)
	throws EpSystemException {
		// NO OP
	}

	protected CheckoutEventHandler getCheckoutEventHandler() {
		return checkoutEventHandler;
	}

	public void setCheckoutEventHandler(final CheckoutEventHandler checkoutEventHandler) {
		this.checkoutEventHandler = checkoutEventHandler;
	}

	protected OrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}
}