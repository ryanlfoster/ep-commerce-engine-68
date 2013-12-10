package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.FinalizeCheckoutActionContext;

/**
 * Container class for data required by finalizeCheckoutActions.
 */
public class FinalizeCheckoutActionContextImpl implements FinalizeCheckoutActionContext {

	private final Order order;
	private final ShoppingCart shoppingCart;
	private final OrderPayment orderPaymentTemplate;
	private final boolean orderExchange;
	private boolean emailFailed;

	/**
	 * Constructor for creating a FinalizeCheckoutActionContext object from a CheckoutActionContext object.
	 * @param checkoutActionContext the checkoutActionContext
	 */
	public FinalizeCheckoutActionContextImpl(final CheckoutActionContext checkoutActionContext) {
		this.order = checkoutActionContext.getOrder();
		this.shoppingCart = checkoutActionContext.getShoppingCart();
		this.orderPaymentTemplate = checkoutActionContext.getOrderPaymentTemplate();
		this.orderExchange = checkoutActionContext.isOrderExchange();
	}

	/**
	 * Gets the order.
	 * @return the order
	 * */
	public Order getOrder() {
		return order;
	}

	@Override
	public Shopper getShopper() {
		return shoppingCart.getShopper();
	}

	@Override
	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}

	/**
	 * Gets the order payment template.
	 * @return the order payment template
	 * */
	public OrderPayment getOrderPaymentTemplate() {
		return orderPaymentTemplate;
	}

	/**
	 * Gets the is order exchange.
	 * @return the is order exchange
	 * */
	public boolean isOrderExchange() {
		return orderExchange;
	}

	/**
	 * Sets email failed.
	 * @param emailFailed the email failed flag
	 * 
	 **/
	public void setEmailFailed(final boolean emailFailed) {
		this.emailFailed = emailFailed;
	}

	/**
	 * Gets email failed.
	 * @return is email failed
	 * */
	public boolean isEmailFailed() {
		return emailFailed;
	}
}