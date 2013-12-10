/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart;

import java.util.Locale;

import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.store.Store;
/**
 * Provide wish list related business service.
 */
public interface WishListEmailService {

	/**
	 * Sends the customers wishlist via e-mail to a list of recipients.
	 * 
	 * @param wishListMessage the wishListMessage to send, includes the list of recipients
	 * @param wishList the wish list
	 * @param store the store
	 * @param locale the locale
	 */
	void sendWishList(final WishListMessage wishListMessage, final WishList wishList, final Store store, final Locale locale);


}
