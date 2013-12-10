/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart;

import java.util.List;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.WishList;
/**
 * Provide wish list related business service.
 */
public interface WishListService {

	/**
	 * Get the wish list by uidPk.
	 * 
	 * @param uid the uidPk
	 * @return the wish list found
	 */
	WishList get(final long uid);

	/**
	 * Save the wish list.
	 * 
	 * @param wishList the wish list to be saved
	 * @return the saved wish list
	 */
	WishList save(final WishList wishList);

    /**
     * Create the wish list by the {@link Shopper}.
     * 
     * @param shopper the Shopper
     * @return the wish list created
     */
    WishList createWishList(Shopper shopper);
	
    /**
     * Find the wish list by shopper.
     * 
     * @param shopper the shopper
     * @return the wish list found
     */
    WishList findOrCreateWishListByShopper(Shopper shopper);

	/**
	 * Find the wish list with prices.
	 * 
	 * @param customerSession the customer session
	 * @return the wish list found
	 */
	WishList findOrCreateWishListWithPrice(CustomerSession customerSession);

	/**
	 * Remove one wish list.
	 * 
	 * @param wishList the wish list to be removed
	 */
	void remove(WishList wishList);

	/**
	 * Delete all Empty WishLists that are associated with the list of Shoppers.
	 * 
	 * @param shopperUids the uids of the associated Shoppers.
	 * @return the number of deleted WishLists
	 */
    int deleteEmptyWishListsByShopperUids(List<Long> shopperUids);

	/**
	 * Delete all WishLists that are associated with the list of Shoppers.
	 * Even the ones that are not empty.
	 * 
	 * @param shopperUids the uids of the associated Shoppers.
	 * @return the number of deleted WishLists
	 */
    int deleteAllWishListsByShopperUids(List<Long> shopperUids);

}
