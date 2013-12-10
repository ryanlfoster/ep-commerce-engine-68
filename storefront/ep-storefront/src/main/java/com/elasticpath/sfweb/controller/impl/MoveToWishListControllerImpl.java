/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.base.exception.EpServiceException;

/**
 * The Spring MVC controller for adding a product to the shopping cart.
 */
public class MoveToWishListControllerImpl extends SimplePageControllerImpl {

	private static final Logger LOG = Logger.getLogger(SimplePageControllerImpl.class);

	private String successView;

	private CartDirector cartDirector;

	private String errorView;

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
		final Shopper shopper = customerSession.getShopper();
		final ShoppingCart shoppingCart = shopper.getCurrentShoppingCart();

		final String skuCode = ServletRequestUtils.getStringParameter(request, "skuCode");
		final long itemUid = ServletRequestUtils.getLongParameter(request, "itemUid", 0);

		// remove the wishlist item from the cart if itemUid is provided
		if (itemUid > 0) {
			shoppingCart.removeCartItem(itemUid);
		}

		if (skuCode == null) {
			LOG.debug("No valid skuCode passed to AddToWishListController");
		} else {
			try {
				cartDirector.addSkuToWishList(skuCode, shopper, shoppingCart.getStore());
				cartDirector.saveShoppingCart(shoppingCart);
			} catch (EpServiceException exc) {
				LOG.warn("Cannot move a SKU to the wish list: " + skuCode, exc);
				final Map<String, Object> model = new HashMap<String, Object>();
				model.put("error.message", "product.unavailable");
				return new ModelAndView(getErrorView(), model);
			}
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
	 * Sets the shopping service.
	 * 
	 * @param cartDirector the car director to set
	 */
	public void setCartDirector(final CartDirector cartDirector) {
		this.cartDirector = cartDirector;
	}

	/**
	 * Gets the error view.
	 * 
	 * @return the error view
	 */
	protected String getErrorView() {
		return this.errorView;
	}

	/**
	 * Sets the error view.
	 * 
	 * @param errorView the error view
	 */
	public void setErrorView(final String errorView) {
		this.errorView = errorView;
	}

}
