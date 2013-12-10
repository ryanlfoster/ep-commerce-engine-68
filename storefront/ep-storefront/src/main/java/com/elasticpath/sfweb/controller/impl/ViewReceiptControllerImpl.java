/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sfweb.util.CookieHandler;

/**
 * The Spring MVC controller for viewing the order receipt after a successful checkout.
 */
public class ViewReceiptControllerImpl extends AbstractViewOrderControllerImpl {
	private CookieHandler cookieHandler;

	/**
	 * Process request for displaying the order receipt.
	 * 
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModelAndView instance
	 * @throws Exception if anything goes wrong.
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);	
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final Order completedOrder = shoppingCart.getCompletedOrder();
		
		// If the customer is anonymous then we need to clear their session cookie and log them out
		if (customerSession.getShopper().getCustomer().isAnonymous()) {
			// Remove the cookie so the customer won't be remembered
			cookieHandler.removeCookie(response, WebConstants.CUSTOMER_SESSION_GUID);

			SecurityContextHolder.clearContext();
		}

		final Map<String, Object> modelMap = new HashMap<String, Object>();	
		
		modelMap.put("orderItemFormBeanMap", createOrderItemFormBeanMap(completedOrder));		
		modelMap.put("order", completedOrder);
		modelMap.put("frequencyMap", getOrderPresentationHelper().getFrequencyMap(completedOrder));
		
		return new ModelAndView(getSuccessView(), modelMap);
	}

	/**
	 * Gets the store cookie handler.
	 * 
	 * @return the cookie handler instance
	 */
	protected CookieHandler getCookieHandler() {
		return cookieHandler;
	}

	/**
	 * Sets the store cookie handler.
	 * 
	 * @param cookieHandler the instance to set
	 */
	public void setCookieHandler(final CookieHandler cookieHandler) {
		this.cookieHandler = cookieHandler;
	}

}
