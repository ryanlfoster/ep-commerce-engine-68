/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sellingchannel.ProductNotPurchasableException;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.base.exception.EpServiceException;

/**
 * The Spring MVC controller for adding a product to the shopping cart.
 */
public class MoveToCartControllerImpl extends AbstractCartControllerImpl {

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
		final String skuCode = ServletRequestUtils.getStringParameter(request, "skuCode");
		final int qty = ServletRequestUtils.getIntParameter(request, "qty", 1);

		if (skuCode == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("No valid skuCode passed to " + getClass());
			}
		} else {
			try {
				final ShoppingItemDto dto = getShoppingItemDtoFactory().createDto(skuCode, qty);
				getCartDirector().moveItemFromWishListToCart(shoppingCart, dto);
			} catch (ProductUnavailableException exc) {
				LOG.warn("Product SKU[" + skuCode + "] is not available.", exc);
				final Map<String, Object> model = new HashMap<String, Object>();
				model.put("error.message", "product.unavailable");
				return new ModelAndView(getErrorView(), model);
			} catch (ProductNotPurchasableException exc) {
				LOG.warn("Product SKU[" + skuCode + "] is not available for purchase.", exc);
				final Map<String, Object> model = new HashMap<String, Object>();
				model.put("error.message", "product.unavailable");
				return new ModelAndView(getErrorView(), model);
			} catch (EpServiceException exc) {
				LOG.warn("Cannot add SKU to shopping cart: " + skuCode, exc);
				final Map<String, Object> model = new HashMap<String, Object>();
				model.put("error.message", "product.unavailable");
				return new ModelAndView(getErrorView(), model);
			}
		}

		return new ModelAndView(this.getSuccessView());
	}

}
