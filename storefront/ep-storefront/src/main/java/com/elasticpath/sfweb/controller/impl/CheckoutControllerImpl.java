/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Spring MVC controller for beginning the checkout process.
 */
public class CheckoutControllerImpl extends AbstractEpControllerImpl {

	private static final Logger LOG = Logger.getLogger(CheckoutControllerImpl.class);

	private String shippingAddressView;

	private String billingAddressView;

	private String orderView;

	/**
	 * Return the ModelAndView for the configured static view page.
	 * 
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		LOG.debug("entering 'handleRequestInternal' method...");

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final Customer customer = customerSession.getShopper().getCustomer();

		String nextView = this.shippingAddressView;

		if (!shoppingCart.requiresShipping()) {
			shoppingCart.setShippingAddress(null);
			if (customer.getPreferredBillingAddress() == null) {
				nextView = billingAddressView;
			} else {
				shoppingCart.setBillingAddress(customer.getPreferredBillingAddress());
				nextView = orderView;
			}
		}

		request.getSession().removeAttribute(WebConstants.IS_CHECKOUT_SIGN_IN);

		customerSession.setCheckoutSignIn(false);

		return new ModelAndView(nextView);

	}

	/**
	 * Sets the name of the view for specifying addresses.
	 * 
	 * @param addressView name of the view
	 */
	public final void setShippingAddressView(final String addressView) {
		this.shippingAddressView = addressView;
	}

	/**
	 * Sets the name of the view for specifying addresses.
	 * 
	 * @param addressView name of the view
	 */
	public final void setBillingAddressView(final String addressView) {
		this.billingAddressView = addressView;
	}

	/**
	 * Sets the name of the view for reviewing the order.
	 * 
	 * @param orderView name of the view
	 */
	public final void setOrderView(final String orderView) {
		this.orderView = orderView;
	}

}
