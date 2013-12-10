/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.Currency;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.customer.CustomerSessionService;

/**
 * The Spring MVC controller for locale change.
 */
public class LocaleControllerImpl extends AbstractEpControllerImpl {

	private static final Logger LOG = Logger.getLogger(LocaleControllerImpl.class);
	private CustomerSessionService customerSessionService;
	private String storefrontContextUrl;

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

		final String localeStr = ServletRequestUtils.getStringParameter(request, WebConstants.LOCALE_PARAMETER_NAME);
		final Locale locale = getLocale(localeStr);

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		customerSession.setLocale(locale);

		final String currencyStr = ServletRequestUtils.getStringParameter(request, WebConstants.CURRENCY);
		final Currency currency = getCurrency(currencyStr);
		customerSession.setCurrency(currency);

		customerSessionService.update(customerSession);
		customerSession.getShopper().getCurrentShoppingCart().fireRules();

		return new ModelAndView(new RedirectView(getReferrer(request)));
	}

	private Currency getCurrency(final String currencyStr) {
		Currency currency = null;
		for (Currency c : getRequestHelper().getStoreConfig().getStore().getSupportedCurrencies()) {
			if (c.getCurrencyCode().equals(currencyStr)) {
				currency = c;
			}
		}

		if (currency == null) {
			currency = getRequestHelper().getStoreConfig().getStore().getDefaultCurrency();
		}
		return currency;
	}

	private Locale getLocale(final String localeStr) {
		final Store store = getRequestHelper().getStoreConfig().getStore();
		for (Locale supportedLocale : store.getSupportedLocales()) {
			if (localeStr.equals(supportedLocale.toString())) {
				return supportedLocale;
			}
		}
		return store.getDefaultLocale();
	}

	private String getReferrer(final HttpServletRequest request) {
		// get the value of the destination param
		final String destinationStr = ServletRequestUtils.getStringParameter(request, WebConstants.DESTINATION_URL, null);
		String referer = request.getHeader("Referer");
		if (referer == null && destinationStr == null) {
			referer = "shop.ep";
		} else if (referer == null) {
			// adding / to make sure will go relative to our application
			referer = storefrontContextUrl + "/" + destinationStr;
		}

		return referer;
	}

	public void setCustomerSessionService(final CustomerSessionService customerSessionService) {
		this.customerSessionService = customerSessionService;
	}

	/**
	 * @param storefrontContextUrl The context url for the storefront.
	 */
	public void setStorefrontContextUrl(final String storefrontContextUrl) {
		this.storefrontContextUrl = storefrontContextUrl;
	}
}
