package com.elasticpath.service.shoppingcart.actions;

import java.util.Collection;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Container class for data required by setupCheckoutActions and reversibleCheckoutActions.
 */
public interface CheckoutActionContext {

	/**
	 * Gets the {@link Shopper}.
	 * 
	 * @return the {@link Shopper}
	 * */
	Shopper getShopper();
	
	/**
	 * Gets the {@link ShoppingCart}.
	 * 
	 * @return the {@link ShoppingCart}
	 * */
	ShoppingCart getShoppingCart();
	
	/**
	 * Gets the {@link Customer}.  If you need to save the Customer, you need to know where it comes from.
	 * 
	 * Look inside the implementation.
	 * 
	 * @return the {@link Customer}
	 * */
	Customer getCustomer();
	
	/**
	 * Sets the order.
	 * @param order the order
	 * */
	void setOrder(final Order order);

	/**
	 * Gets the order.
	 * @return the order
	 * */
	Order getOrder();

	/**
	 * Gets the order payment template.
	 * @return the order payment template
	 * */
	OrderPayment getOrderPaymentTemplate();

	/**
	 * Gets the is order exchange.
	 * @return the is order exchange
	 * */
	boolean isOrderExchange();

	/**
	 * Gets is Await Exchange Completion.
	 * @return is Await Exchange Completion
	 * */
	boolean isAwaitExchangeCompletion();

	/**
	 * Gets the order return exchange.
	 * @return order return exchange
	 * */
	OrderReturn getExchange();

	/**
	 * Sets the order payment list.
	 * @param orderPaymentList the order payment list
	 * */
	void setOrderPaymentList(final Collection<OrderPayment> orderPaymentList);

	/**
	 * Gets order payment list.
	 * @return the order payment list
	 * */
	Collection<OrderPayment> getOrderPaymentList();
	
	/**
	 * Returns the Ip Address of the Customer that is doing this checkout.
	 *
	 * @return the Customer IP Address.
	 */
	String getCustomerIpAddress();
}