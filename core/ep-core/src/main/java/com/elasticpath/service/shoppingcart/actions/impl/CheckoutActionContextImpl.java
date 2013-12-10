package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.Collection;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;

/**
 * Container class for data required by setupCheckoutActions and reversibleCheckoutActions.
 */
public class CheckoutActionContextImpl implements CheckoutActionContext {

	private Order order;
	private Collection<OrderPayment> orderPaymentList;
	private final ShoppingCart shoppingCart;
	private final OrderPayment orderPaymentTemplate;
	private final boolean orderExchange;
	private final boolean awaitExchangeCompletion;
	private final OrderReturn exchange;

	/**
	 * Constructor for creating the initial CheckoutActionContext object.
	 * 
	 * @param shoppingCart the customer session
	 * @param orderPaymentTemplate the orderPaymentTemplate
	 * @param isOrderExchange indicates whether or not the cart is for an order exchange
	 * @param awaitExchangeCompletion indicates whether or not the cart should wait for exchange completion
	 * @param exchange the orderReturn object which is used for exchanges
	 */
	public CheckoutActionContextImpl(final ShoppingCart shoppingCart,
			final OrderPayment orderPaymentTemplate, final boolean isOrderExchange,
			final boolean awaitExchangeCompletion, final OrderReturn exchange) {
		this.shoppingCart = shoppingCart;
		this.orderPaymentTemplate = orderPaymentTemplate;
		this.orderExchange = isOrderExchange;
		this.awaitExchangeCompletion = awaitExchangeCompletion;
		this.exchange = exchange;
	}

	@Override
	public void setOrder(final Order order) {
		this.order = order;
	}

	@Override
	public Order getOrder() {
		return order;
	}

	@Override
	public OrderPayment getOrderPaymentTemplate() {
		return orderPaymentTemplate;
	}

	@Override
	public boolean isOrderExchange() {
		return orderExchange;
	}

	@Override
	public boolean isAwaitExchangeCompletion() {
		return awaitExchangeCompletion;
	}

	@Override
	public OrderReturn getExchange() {
		return exchange;
	}

	@Override
	public void setOrderPaymentList(final Collection<OrderPayment> orderPaymentList) {
		this.orderPaymentList = orderPaymentList;
	}

	@Override
	public Collection<OrderPayment> getOrderPaymentList() {
		return orderPaymentList;
	}

	@Override
	public Shopper getShopper() {
		return shoppingCart.getShopper();
	}
	
	@Override
	public Customer getCustomer() {
		return getShopper().getCustomer();
	}
	
	@Override
	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}

	@Override
	public String getCustomerIpAddress() {
		return getShoppingCart().getIpAddress();
	}
}