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
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sellingchannel.director.CartDirector;

/**
 * The Spring MVC controller for removing a product from the shopping cart.
 */
public class RemoveCartItemControllerImpl extends AbstractEpControllerImpl {

	private static final Logger LOG = Logger.getLogger(RemoveCartItemControllerImpl.class);

	private String successView;

	private CartDirector cartDirector;

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

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();

		final long itemUid = ServletRequestUtils.getLongParameter(request, "itemUid", 0);
		if (itemUid > 0) {
			shoppingCart.removeCartItem(itemUid);
			final ShoppingCart updatedShoppingCart = cartDirector.saveShoppingCart(shoppingCart);
			customerSession.setShoppingCart(updatedShoppingCart);
		} else {
			LOG.debug("No valid item UID passed to RemoveCartItemController.");
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
	 * @param cartDirector the cart director to set
	 */
	public void setCartDirector(final CartDirector cartDirector) {
		this.cartDirector = cartDirector;
	}
}
