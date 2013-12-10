package com.elasticpath.service.shopper.impl;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.customer.CustomerSessionShopperUpdateHandler;

/**
 * Update the shopping cart with the customer session.
 * TODO - this should be removed once customer session is removed from cart.
 */
public class ShoppingCartCustomerSessionUpdater implements CustomerSessionShopperUpdateHandler {

	@Override
	public void invalidateShopper(final CustomerSession customerSession, final Shopper shopper) {
		customerSession.getShopper().getCurrentShoppingCart().setCustomerSession(customerSession);
	}

}
