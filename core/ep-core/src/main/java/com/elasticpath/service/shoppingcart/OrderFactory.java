package com.elasticpath.service.shoppingcart;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Creates Orders from ShoppingCarts.
 */
public interface OrderFactory {

	/**
	 * Creates an {@code Order} from the items in a {@code ShoppingCart}.
	 * @param customer the customer
	 * @param shoppingCart the shopping cart
	 * @param isOrderExchange whether this order is created as a result of an Exchange
	 * @param awaitExchangeCompletion whether the order should wait for completion of the exchange before being fulfilled
	 * @param exchange the applicable {@code OrderReturn}, if any
	 * @return the Order that's created
	 */
	Order createAndPersistNewOrderFromShoppingCart(final Customer customer, final ShoppingCart shoppingCart,
			final boolean isOrderExchange,
			final boolean awaitExchangeCompletion, final OrderReturn exchange);
}
