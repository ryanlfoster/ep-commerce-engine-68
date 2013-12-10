/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc;

import java.util.Locale;

import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.store.Store;

/**
 * Helper for constructing email properties.
 */
public interface WishListEmailPropertyHelper {

	/**
	 * Sends the customers wishlist via e-mail to a list of recipients.
	 * 
	 * @param wishListMessage the wishListMessage to send, includes the list of recipients
	 * @param wishList the wish list
	 * @param store the store
	 * @param locale the locale
	 * @return the email properties
	 */
	EmailProperties getWishListEmailProperties(final WishListMessage wishListMessage, final WishList wishList, 
			final Store store, final Locale locale);

}