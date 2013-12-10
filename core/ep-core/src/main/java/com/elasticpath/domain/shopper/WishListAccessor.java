package com.elasticpath.domain.shopper;

import com.elasticpath.domain.shoppingcart.WishList;

/**
 * Gives access to a {@link WishList} on the implementing object. 
 */
public interface WishListAccessor {

	/**
	 * Gets the current {@link WishList}.
	 *
	 * @return a {@link WishList}.
	 */
	WishList getCurrentWishList();

	/**
	 * Sets the current {@link WishList}.
	 *
	 * @param wishList the {@link WishList}.
	 */
	void setCurrentWishList(WishList wishList);

}