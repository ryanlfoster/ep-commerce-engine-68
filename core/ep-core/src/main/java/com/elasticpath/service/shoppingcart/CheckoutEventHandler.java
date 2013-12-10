/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart;

import java.util.Collection;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * This interface is implemented by services who wish to be notified
 * of events occurring during the checkout process so that an additional
 * action can be taken before or after the checkout.
 */
public interface CheckoutEventHandler {

	/**
	 * This event occurs before any action is taken as part of the checkout process.
	 * 
	 * @param shoppingCart the {@link ShoppingCart} being checked out
	 * @param orderPayment information about the method of payment
	 */
	void preCheckout(final ShoppingCart shoppingCart, final OrderPayment orderPayment);

	/**
	 * This event occurs after a checkout has been processed but before the order has been persisted.
	 * This event occurs between preCheckout and postCheckout.
	 * 
	 * @param shoppingCart the {@link ShoppingCart} being checked out
	 * @param orderPayment information about the method of payment
	 * @param completedOrder the order object resulting from the checkout
	 */
	void preCheckoutOrderPersist(final ShoppingCart shoppingCart, final Collection<OrderPayment> orderPayment, final Order completedOrder);

	/**
	 * This event occurs after a checkout process has completed.
	 * 
	 * @param shoppingCart with the {@link ShoppingCart} being checked out
	 * @param orderPayment information about the method of payment
	 * @param completedOrder the order object resulting from the checkout
	 */
	void postCheckout(final ShoppingCart shoppingCart, final OrderPayment orderPayment, final Order completedOrder);
}
