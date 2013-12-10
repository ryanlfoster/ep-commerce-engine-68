/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.shoppingcart;

import java.util.Collection;
import java.util.Locale;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Implementations know how to build an {@code OrderSku} from a {@code ShoppingItem}, {@code Customer}, and {@code Store}.
 */
public interface OrderSkuFactory {
	
	/**
	 * @param cartItems collection of shopping items
	 * @param locale the locale in which the OrderSku is being purchased
	 * @return collection of OrderSkus
	 */
	Collection<OrderSku> createOrderSkus(final Collection<ShoppingItem> cartItems,
			final Locale locale);

}
