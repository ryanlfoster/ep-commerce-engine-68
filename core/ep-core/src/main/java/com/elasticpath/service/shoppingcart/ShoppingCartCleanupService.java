/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.service.shoppingcart;

import java.util.Date;

/**
 * Service to clean up shopping cart usage.
 */
public interface ShoppingCartCleanupService {

	/**
	 * Delete abandoned shopping carts. <br>
	 * If the removal date is less than or equal to the shopping cart last modified date, the shopping cart is considered abandoned.<br>
	 *
	 * @param removalDate the date at or before that shopping carts are considered abandoned
	 * @param maxResults limits the number of abandoned carts to remove
	 * @return the number of abandoned carts removed
	 */
	int deleteAbandonedShoppingCarts(Date removalDate, int maxResults);

}
