/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPriceFactory;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.sfweb.formbean.DeliveryOptionsFormBean;
import com.elasticpath.sfweb.formbean.impl.DeliveryOptionsFormBeanImpl;

/**
 * The Spring MVC controller for selecting a delivery option.
 */
public class DeliveryOptionsFormControllerImpl extends AbstractEpFormController {

	private static final Logger LOG = Logger.getLogger(DeliveryOptionsFormControllerImpl.class);

	private CheckoutService checkoutService;

	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();

		if (shoppingCart.getShippingAddress() == null) {
			return new ModelAndView("redirect:" + "/shipping-address.ep");
		}

		return super.handleRequestInternal(request, response);
	}

	/**
	 * Handle the address-add form submit.
	 * 
	 * @param request -the request
	 * @param response -the response
	 * @param command -the command object
	 * @param errors - will be written back in case of any business error happens
	 * @return return the view
	 */
	public ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response, final Object command,
			final BindException errors) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("DeliveryOptionsFormController: entering 'onSubmit' method...");
		}
		return new ModelAndView(getSuccessView());
	}

	/**
	 * Prepare the command object (the shopping cart).
	 * 
	 * @param request -the request
	 * @return the command object
	 */
	public Object formBackingObject(final HttpServletRequest request) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		if (shoppingCart.getShippingServiceLevelList().isEmpty()) {
			checkoutService.retrieveShippingOption(shoppingCart);
		}
		
		DeliveryOptionsFormBean dofb = new DeliveryOptionsFormBeanImpl();
		dofb.setShoppingCart(shoppingCart);
		dofb.setFrequencyMap(new FrequencyAndRecurringPriceFactory().getFrequencyMap(shoppingCart.getCartItems()));
		return dofb;
	}

	/**
	 * Set the checkout service.
	 * 
	 * @param checkoutService the customer service.
	 */
	public void setCheckoutService(final CheckoutService checkoutService) {
		this.checkoutService = checkoutService;
	}
}
