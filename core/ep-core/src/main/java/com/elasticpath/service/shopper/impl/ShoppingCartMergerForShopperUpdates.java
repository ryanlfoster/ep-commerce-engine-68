/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.shopper.impl;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.customer.CustomerSessionShopperUpdateHandler;
import com.elasticpath.service.shoppingcart.ShoppingCartMerger;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * Handles what happens to the {@link ShoppingCart} when a {@Shopper} is switched out.
 */
public final class ShoppingCartMergerForShopperUpdates implements CustomerSessionShopperUpdateHandler {

	private final ShoppingCartService shoppingCartService;
	private final ShoppingCartMerger shoppingCartMerger;

	/**
	 * @param shoppingCartService shopping cart service
	 * @param shoppingCartMerger the shopping cart merger routines
	 */
	public ShoppingCartMergerForShopperUpdates(final ShoppingCartService shoppingCartService,
			final ShoppingCartMerger shoppingCartMerger) {
		this.shoppingCartService = shoppingCartService;
		this.shoppingCartMerger = shoppingCartMerger;
	}

	@Override
	public void invalidateShopper(final CustomerSession customerSession, final Shopper invalidShopper) {

		final Shopper currentShopper = customerSession.getShopper();
		if (currentShopper.equals(invalidShopper)) {
			copyTransientData(currentShopper, invalidShopper);
			return;
		}

		// Keep anonymous cart if a guest checkout is causing the shopper update.
		if (isGuestPerformingCheckOut(customerSession)) {
			keepAnonymousCart(currentShopper, invalidShopper.getCurrentShoppingCart());
		} else {
			mergeShoppingCart(currentShopper, invalidShopper);
		}
	}

	private boolean isGuestPerformingCheckOut(final CustomerSession customerSession) {
		return customerSession.isCheckoutSignIn();
	}

	private void copyTransientData(final Shopper currentShopper, final Shopper invalidShopper) {
		final ShoppingCart shoppingCart = invalidShopper.getCurrentShoppingCart();
		currentShopper.setCurrentShoppingCart(shoppingCart);
		if (shoppingCart != null) {
			shoppingCart.setShopper(currentShopper);
		}
	}

	private void keepAnonymousCart(final Shopper currentShopper, final ShoppingCart keptCart) {

		// Remove any old shopping carts that have previously been persisted using the current shopping context
		// and replace it with the current shopping cart.
		removeExistingShoppingCart(currentShopper);

		attachCartToShopperAndPersist(currentShopper, keptCart);
	}

	private void removeExistingShoppingCart(final Shopper shopper) {
		final ShoppingCart shoppingCart = shoppingCartService.findOrCreateByShopper(shopper);
		shoppingCartService.remove(shoppingCart);
	}

	private void mergeShoppingCart(final Shopper incomingShopper, final Shopper invalidShopper) {

		final ShoppingCart anonymousCart = invalidShopper.getCurrentShoppingCart();
		final ShoppingCart preExistingPersistedCart = shoppingCartService.findOrCreateByShopper(incomingShopper);
		final ShoppingCart mergedShoppingCart = shoppingCartMerger.merge(preExistingPersistedCart, anonymousCart);

		shoppingCartService.remove(anonymousCart);

		attachCartToShopperAndPersist(incomingShopper, mergedShoppingCart);
	}

	private void attachCartToShopperAndPersist(final Shopper shopper, final ShoppingCart shoppingCart) {
		shoppingCart.setShopper(shopper);
		shoppingCartService.saveOrUpdate(shoppingCart);

		shopper.setCurrentShoppingCart(shoppingCart);
	}
}

