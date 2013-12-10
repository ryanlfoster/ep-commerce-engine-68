/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * The Spring MVC controller for adding a product to the shopping cart. <br/>
 * <b>Interface</b> <br/>
 * Input<br/>
 * <ul>
 * <li>Request contains 'skuCode' parameter with the SKU code (guid).</li>
 * <li>Request contains 'qty' parameter with the quantity of items requested. Defaults to 1.</li>
 * <li>Http Session contains reference to {@code ShoppingCart}.</li>
 * </ul>
 */
public class AddToCartSimpleControllerImpl extends AbstractCartControllerImpl {

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

		CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final String skuCode = ServletRequestUtils.getStringParameter(request, "skuCode");
		int qty = ServletRequestUtils.getIntParameter(request, "qty", 1);

		return addSkuToCart(request, shoppingCart, skuCode, qty);
	}
}
