/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.service.shoppingcart.WishListService;

/**
 * The Spring MVC controller for removing a product from the shopping cart.
 */
public class RemoveWishListItemControllerImpl extends SimplePageControllerImpl {

	private static final Logger LOG = Logger.getLogger(RemoveWishListItemControllerImpl.class);

	private String successView;

	private WishListService wishListService;

	/**
	 * Return the ModelAndView for the configured static view page.
	 * 
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		LOG.debug("entering 'handleRequestInternal' method...");

		long itemUid = ServletRequestUtils.getLongParameter(request, "itemUid", 0);

		if (itemUid > 0) {
			final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
			WishList wishList = wishListService.findOrCreateWishListByShopper(customerSession.getShopper());
			wishList.removeItem(itemUid);
			wishListService.save(wishList);
		} else {
			LOG.debug("No valid item UID passed to RemoveWishListItemController");
		}

		return new ModelAndView(this.getSuccessView());
	}

	/**
	 * Sets the static view name.
	 * 
	 * @param successView name of the success view
	 */
	public final void setSuccessView(final String successView) {
		this.successView = successView;
	}

	/**
	 * Sets the success view name.
	 * 
	 * @return name of the success view
	 */
	public String getSuccessView() {
		return this.successView;
	}

	/**
	 * Set wish list service.
	 * 
	 * @param wishListService the wish list service
	 */
	public void setWishListService(final WishListService wishListService) {
		this.wishListService = wishListService;
	}

}
