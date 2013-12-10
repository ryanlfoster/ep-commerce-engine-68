/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.domain.misc.WishListEmailPropertyHelper;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.misc.EmailService;
import com.elasticpath.service.shoppingcart.WishListEmailService;

/** Service for sending customer wishlists. */
public class WishListEmailServiceImpl implements WishListEmailService {

	private EmailService emailService;
	private WishListEmailPropertyHelper wishListEmailPropertyHelper;
	
	@Override
	public void sendWishList(final WishListMessage wishListMessage, final WishList wishList, final Store store, final Locale locale) {
		
		EmailProperties emailProperties = wishListEmailPropertyHelper.getWishListEmailProperties(wishListMessage, wishList, store, locale); 
		List<String> recipients = Arrays.asList(wishListMessage.getRecipientEmails().split(","));
		// We don't care about the recipient address of email properties being overwritten.
		// Don't iterate ourselves so that we send one request to the remote server rather than
		// multiple small ones.

		emailService.sendMail(recipients, emailProperties);
	}

	/**
	 * Set the emailService.
	 * 
	 * @param emailService the emailService
	 */
	public void setEmailService(final EmailService emailService) {
		this.emailService = emailService;
	}

	/**
	 * @param wishListEmailPropertyHelper the wishListEmailPropertyHelper to set
	 */
	public void setWishListEmailPropertyHelper(final WishListEmailPropertyHelper wishListEmailPropertyHelper) {
		this.wishListEmailPropertyHelper = wishListEmailPropertyHelper;
	}

}
