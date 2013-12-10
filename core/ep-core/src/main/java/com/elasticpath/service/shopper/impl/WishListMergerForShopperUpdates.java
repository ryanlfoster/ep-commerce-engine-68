/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.shopper.impl;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.service.customer.CustomerSessionShopperUpdateHandler;
import com.elasticpath.service.shoppingcart.WishListService;

/**
 * Handles what happens to the {@link WishList} when a {@Shopper} is switched out.
 */
public final class WishListMergerForShopperUpdates implements CustomerSessionShopperUpdateHandler {

	private final WishListService wishListService;

	/**
	 * @param wishListService wishlist service
	 */
	public WishListMergerForShopperUpdates(final WishListService wishListService) {
		this.wishListService = wishListService;
	}

	@Override
	public void invalidateShopper(final CustomerSession customerSession, final Shopper invalidShopper) {
		
		final WishList currentWishList = wishListService.findOrCreateWishListByShopper(customerSession.getShopper());
		final WishList invalidatedWishList = wishListService.findOrCreateWishListByShopper(invalidShopper);
		
		if (invalidatedWishList.equals(currentWishList)) {
			return;
		}
		
		currentWishList.addAllItems(invalidatedWishList.getAllItems());
		wishListService.save(currentWishList);

		wishListService.remove(invalidatedWishList);
	}
}
