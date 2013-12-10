/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.exception.UserStatusInactiveException;
import com.elasticpath.domain.catalog.InsufficientInventoryException;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.payment.PayPalExpressSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.exceptions.AmountLimitExceededException;
import com.elasticpath.plugin.payment.exceptions.InsufficientFundException;
import com.elasticpath.service.shoppingcart.CheckoutService;

/**
 * Spring MVC controller for completing checkout upon returning from PayPal.
 */
public class PayPalExpressCheckoutController extends AbstractEpControllerImpl {

	private static final Logger LOG = Logger.getLogger(CheckoutControllerImpl.class);

	private CheckoutService checkoutService;

	private String successView;

	private String standardReturnView;

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
		LOG.debug("entering 'handleRequest' method...");

		PayPalExpressSession payPalSession = (PayPalExpressSession) request.getSession().getAttribute(WebConstants.PAYPAL_EXPRESS_CHECKOUT_SESSION);

		try {
			final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
			final OrderPayment orderPayment = getBean(ContextIdNames.ORDER_PAYMENT);
			orderPayment.setPaymentMethod(PaymentType.PAYPAL_EXPRESS);
			orderPayment.setGatewayToken(payPalSession.getToken());
			final Shopper shopper = customerSession.getShopper();

			checkoutService.checkout(shopper.getCurrentShoppingCart(), orderPayment);
			// Checkout was successful so we need to clear out any checkout-related session attributes that may have been set.
			request.getSession().removeAttribute(WebConstants.PAYPAL_EXPRESS_CHECKOUT_SESSION);
			return new ModelAndView(this.successView);
		} catch (InsufficientInventoryException iInvE) {
			payPalSession.setStatus(PayPalExpressSession.INSUFFICIENT_INVENTORY_ERROR);
		} catch (InsufficientFundException isfex) {
			payPalSession.setStatus(PayPalExpressSession.INSUFFICIENT_FUND_ERROR);
		} catch (AmountLimitExceededException alex) {
			payPalSession.setStatus(PayPalExpressSession.AMOUNT_LIMIT_EXCEEDED_ERROR);
		} catch (UserStatusInactiveException inactiveEx) {
			payPalSession.setStatus(PayPalExpressSession.USER_STATUS_INACTIVE_ERROR);
		} catch (EpServiceException eSrvE) {
			payPalSession.setStatus(PayPalExpressSession.PAYMENT_GATEWAY_ERROR);
		}
		// remove token
		payPalSession.clearSessionInformation();
		request.getSession().setAttribute(WebConstants.PAYPAL_EXPRESS_CHECKOUT_SESSION, payPalSession);

		return new ModelAndView(this.standardReturnView);
	}

	/**
	 * Set the checkout service for processing a checkout.
	 * 
	 * @param checkoutService the checkout service
	 */
	public void setCheckoutService(final CheckoutService checkoutService) {
		this.checkoutService = checkoutService;
	}

	/**
	 * Sets the name of the view upon successfully creating the order.
	 * 
	 * @param successView name of the view
	 */
	public final void setSuccessView(final String successView) {
		this.successView = successView;
	}

	/**
	 * Sets the name of the view when an error has occurred and OnePage is not enabled.
	 * 
	 * @param standardReturnView name of the view
	 */
	public final void setStandardReturnView(final String standardReturnView) {
		this.standardReturnView = standardReturnView;
	}
}
