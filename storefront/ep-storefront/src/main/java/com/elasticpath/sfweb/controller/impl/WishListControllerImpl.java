/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.shoppingcart.WishListEmailService;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.sfweb.EpSfWebException;

/**
 * The Spring MVC controller for customer WishList sending page.
 */
public class WishListControllerImpl extends AbstractEpFormController {
	private static final Logger LOG = Logger.getLogger(WishListControllerImpl.class);

	private WishListService wishListService;

	private WishListEmailService wishListEmailService;

	/**
	 * Handle the wishlist form submit.
	 * 
	 * @param request -the request
	 * @param response -the response
	 * @param command -the command object
	 * @param errors - will be written back in case of any business error happens
	 * @return return the view
	 * @throws com.elasticpath.sfweb.EpSfWebException in case of any error happens
	 */
	@Override
	protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response, final Object command,
			final BindException errors) throws EpSfWebException {
		LOG.debug("WishListController: entering 'onSubmit' method...");

		final WishListMessage wishListMessage = (WishListMessage) command;
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final WishList wishList = wishListService.findOrCreateWishListByShopper(customerSession.getShopper());

		try {
			wishListEmailService.sendWishList(wishListMessage, wishList, shoppingCart.getStore(), shoppingCart.getLocale());
			return new ModelAndView(getSuccessView() + "?sendEmailConf=1");
		} catch (EpServiceException ep) {
			return new ModelAndView(getSuccessView() + "?sendEmailConf=0");
		}

	}

	/**
	 * Prepare the command object for the forgotten password form.
	 * 
	 * @param request -the request
	 * @return the command object
	 */
	@Override
	protected Object formBackingObject(final HttpServletRequest request) {
		final WishListMessage wishListMessage = this.getBean("wishListMessage");
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final Customer customer = customerSession.getShopper().getCustomer();
		if (customer != null && customer.getFirstName() != null
				&& customer.getLastName() != null) {
			final StringBuffer senderName = new StringBuffer();
			senderName.append(customer.getFirstName());
			senderName.append(' ');
			senderName.append(customer.getLastName());
			wishListMessage.setSenderName(senderName.toString());

		}
		return wishListMessage;
	}

	/**
	 * Set the wish list service.
	 * 
	 * @param wishListService the wish list service
	 */
	public void setWishListService(final WishListService wishListService) {
		this.wishListService = wishListService;
	}

	/**
	 * Set the wish list email service.
	 * 
	 * @param wishListEmailService the wish list email service
	 */
	public void setWishListEmailService(final WishListEmailService wishListEmailService) {
		this.wishListEmailService = wishListEmailService;
	}

}
