package com.elasticpath.service.shoppingcart.dao;

import java.util.List;

import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.WishList;

/**
 * The wish list dao interface.
 */
public interface WishListDao {
	
	/**
	 * find wish list by shopping context data.
	 * 
	 * @param shopper the customer context data
	 * @return the wish list found
	 */
	WishList findByShopper(Shopper shopper);

	/**
	 * Get wish list by uidpk.
	 * 
	 * @param uid the uidpk
	 * @return the wish list found
	 */
	WishList get(long uid);

	/**
	 * save or update wish list. 
	 * 
	 * @param wishList the wish list to be saved or updated.
	 * @return the saved/updated wish list
	 */
	WishList saveOrUpdate(WishList wishList);

	/**
	 * Remove the wish list.
	 * 
	 * @param wishList the wish list
	 */
	void remove(WishList wishList);
	
	/**
	 * Remove all empty WishLists associated with any of the given Shopper.
	 * 
	 * @param shopperUids the uids of the associated Shoppers
	 * @return the number of deleted WishLists.
	 */
	int deleteEmptyWishListsByShopperUids(final List<Long> shopperUids);

	/**
	 * Remove all WishLists associated with the given list of Shoppers.
	 * 
	 * @param shopperUids the uids of the associated Shoppers
	 * @return the number of deleted WishLists.
	 */
	int deleteAllWishListsByShopperUids(final List<Long> shopperUids);
}
