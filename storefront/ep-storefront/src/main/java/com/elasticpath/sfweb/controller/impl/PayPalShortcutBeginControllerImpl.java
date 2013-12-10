/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.payment.PayPalExpressPaymentGateway;
import com.elasticpath.domain.payment.PayPalExpressSession;
import com.elasticpath.domain.payment.impl.PayPalExpressSessionImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Spring MVC controller for beginning a checkout using PayPal EC Shortcut.
 */
public class PayPalShortcutBeginControllerImpl extends AbstractEpControllerImpl {

	private String returnUrl;

	private String cancelUrl;

	/**
	 * @param request the current <code>HttpServletRequest</code>
	 * @param response the <code>HttpServletResponse</code>
	 * @return the <code>ModelAndView</code> instance for the page to be displayed.
	 * @throws Exception if anything goes wrong
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final PayPalExpressPaymentGateway payPalExpressPayment = (PayPalExpressPaymentGateway) getRequestHelper().getStoreConfig().getStore()
				.getPaymentGatewayMap().get(PaymentType.PAYPAL_EXPRESS);

		final String curRequestUrl = request.getRequestURL().toString();
		PayPalExpressSession payPalSession = (PayPalExpressSession) request.getSession().getAttribute(WebConstants.PAYPAL_EXPRESS_CHECKOUT_SESSION);
		final StringBuffer urlSB = new StringBuffer("redirect:");
		String tokenStr = "";

		if (request.getParameter("continue") == null || payPalSession == null || payPalSession.getToken() == null) {
			final String baseUrl = curRequestUrl.substring(0, curRequestUrl.lastIndexOf('/') + 1);
			final String returnUrl = baseUrl.concat(this.returnUrl);
			final String cancelUrl = baseUrl.concat(this.cancelUrl);

			tokenStr = payPalExpressPayment.setExpressShortcutCheckout(shoppingCart, returnUrl, cancelUrl);
			if (payPalSession == null) {
				payPalSession = new PayPalExpressSessionImpl(tokenStr);
			} else {
				payPalSession.clearSessionInformation();
				payPalSession.setToken(tokenStr);
			}
			request.getSession().setAttribute(WebConstants.PAYPAL_EXPRESS_CHECKOUT_SESSION, payPalSession);
		} else {
			tokenStr = payPalSession.getToken();
		}

		urlSB.append(payPalExpressPayment.getPropertiesMap().get("paypalExpressCheckoutURL").getValue());
		urlSB.append(tokenStr);

		return new ModelAndView(urlSB.toString());
	}

	/**
	 * @param cancelUrl the cancelUrl to set
	 */
	public void setCancelUrl(final String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}

	/**
	 * @param returnUrl the returnUrl to set
	 */
	public void setReturnUrl(final String returnUrl) {
		this.returnUrl = returnUrl;
	}
}
