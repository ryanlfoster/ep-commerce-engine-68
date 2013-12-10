/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.util.impl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.LocaleResolver;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.sfweb.exception.EpRequestParameterBindingException;
import com.elasticpath.sfweb.exception.EpWebException;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.web.security.EsapiServletUtils;

/**
 * The default implementation of <code>RequestHelper</code>.
 */
public class RequestHelperImpl implements SfRequestHelper {

	private CustomerSessionService customerSessionService;

	private StoreConfig storeConfig;

	private LocaleResolver localeResolver;

	private static final Logger LOG = Logger.getLogger(RequestHelperImpl.class);

	/**
	 * Intended for transitional purposes only.  Remove at earliest possible opportunity.
	 *
	 * Required for various Velocity templates. Remove once it has been purged from the templates.
	 *
	 * @param request
	 * @param shopper
	 */
	@Deprecated
	private void updateTransitionalProxy(final HttpServletRequest request, final Shopper shopper) {
		final TransitionalShoppingCartProxy shoppingCartProxy
			= new TransitionalShoppingCartProxy(shopper, getStoreConfig().getStore().getDefaultLocale());
		request.getSession().setAttribute(WebConstants.SHOPPING_CART, shoppingCartProxy);
	}

	/**
	 * {@inheritDoc}
	 * @throws EpWebException when request is null
	 */
	@Override
	public CustomerSession getCustomerSession(final HttpServletRequest request) throws EpWebException {
		if (request == null) {
			throw new EpWebException("Null request is not allowed.");
		}

		return  (CustomerSession) request.getSession().getAttribute(WebConstants.CUSTOMER_SESSION);
	}


	/**
	 * {@inheritDoc}
	 * @throws EpWebException when request or customerSession is null
	 */
	@Override
	public CustomerSession getPersistedCustomerSession(final HttpServletRequest request) throws EpWebException {
		if (request == null) {
			throw new EpWebException("Null request is not allowed.");
		}

		CustomerSession customerSession = null;
		final String existingCustomerSessionGuid = getCookieValue(request, WebConstants.CUSTOMER_SESSION_GUID);

		// If Cookie exists, attempt to retrieve customer session
		if (!StringUtils.isEmpty(existingCustomerSessionGuid)) {
			customerSession = getCustomerSessionService().findByGuid(existingCustomerSessionGuid);
			if (customerSession == null) {
				LOG.warn("Customer session cookie set but not found in database");
			}
		}
		return customerSession;
	}

	/**
	 * Finds the customer session in the request session or looks it up by cookie.
	 *
	 * @param request the request
	 * @return the customer session, or null if it's not found
	 * @throws EpWebException when request or customerSession is null
	 */
	protected CustomerSession findCustomerSession(final HttpServletRequest request) throws EpWebException {
		CustomerSession customerSession = getCustomerSession(request);
		if (customerSession == null) {
			customerSession = getPersistedCustomerSession(request);
		}
		return customerSession;
	}

	/**
	 * {@inheritDoc}
	 * @throws EpWebException when request or customerSession is null
	 */
	@Override
	public void setCustomerSession(final HttpServletRequest request, final CustomerSession customerSession) throws EpWebException {
		if (customerSession == null) {
			throw new EpWebException("Null customer session is not allowed.");
		}
		if (request == null) {
			throw new EpWebException("Null request is not allowed.");
		}

		request.getSession().setAttribute(WebConstants.CUSTOMER_SESSION, customerSession);

		updateTransitionalProxy(request, customerSession.getShopper());
	}

	/**
	 * Retrieve the given parameter as <code>Long</code> from the given request. Effectively identical to
	 * {@code ServletRequestUtils#getLongParameter(javax.servlet.ServletRequest, String)}, except will throw a
	 * {@code EpRequestParameterBindingException} instead of a {@code ServletRequestBindingException}.
	 *
	 * @param request the request
	 * @param parameterName the parameter name
	 * @return a <code>Long</code> type instance if the parameter is set, otherwise null
	 */
	@Override
	public Long getLongParameter(final HttpServletRequest request, final String parameterName) {
		if (!EsapiServletUtils.hasParameter(request, parameterName)) {
			return null;
		}
		try {
			return ServletRequestUtils.getLongParameter(request, parameterName);
		} catch (final ServletRequestBindingException e) {
			throw new EpRequestParameterBindingException("Invalid request parameter.", e);
		}
	}

	/**
	 * Retrieve the given parameter as <code>Long</code> from the given request with a fallback value.
	 *
	 * @param request the request
	 * @param parameterName the parameter name
	 * @param fallback the fallback value
	 * @return a <code>Long</code> type instance if the parameter is set, otherwise null
	 */
	@Override
	public Long getLongParameter(final HttpServletRequest request, final String parameterName, final Long fallback) {
		return ServletRequestUtils.getLongParameter(request, parameterName, fallback);
	}

	/**
	 * Retrieve the absolute url from the given request.
	 *
	 * @param request the request
	 * @return the absolute url
	 */
	@Override
	public String getUrl(final HttpServletRequest request) {
		final String requestUri = request.getRequestURI();
		final String requestUrl = request.getRequestURL().toString();
		final String contextPath = request.getContextPath();
		final String url = requestUrl.substring(0, requestUrl.lastIndexOf(requestUri));
		return url + contextPath;
	}

	/**
	 * Returns the value of a cookie with the specified name.
	 *
	 * @param request The Http request to retrieve cookies from
	 * @param cookieName The name of the cookie to be retrieved
	 * @return the value of the cookie as a string (or an empty string if the cookie is empty or not found).
	 */
	@Override
	public String getCookieValue(final HttpServletRequest request, final String cookieName) {
		Cookie cookie = getCookie(request, cookieName);
		if (cookie != null) {
			return cookie.getValue();
		}
		return "";
	}

	/**
	 * Returns a cookie with the specified name.
	 *
	 * @param request The Http request to retrieve cookies from
	 * @param cookieName The name of the cookie to be retrieved
	 * @return the cookie with the given name
	 */
	private Cookie getCookie(final HttpServletRequest request, final String cookieName) {
		Cookie[] cookie = request.getCookies();
		if (cookie != null) {
			for (int i = 0; i < cookie.length; i++) {
				if (cookie[i].getName().equalsIgnoreCase(cookieName)) {
					return cookie[i];
				}
			}
		}
		return null;
	}

	/**
	 * Get an int parameter or attribute, with a fallback value. Never throws an exception. Can pass a distinguished value as default to enable
	 * checks of whether it was supplied.
	 *
	 * @param request current HTTP request
	 * @param name the name of the parameter
	 * @param defaultVal the default value to use as fallback
	 * @return the int value of parameter or attribute if it exists, otherwise, returns a fallback value
	 */
	@Override
	public int getIntParameterOrAttribute(final HttpServletRequest request, final String name, final int defaultVal) {
		Integer intValue = getIntegerParameter(request, name);
		if (intValue != null) {
			return intValue.intValue();
		}

		intValue = getIntegerAttribute(request, name);
		if (intValue != null) {
			return intValue.intValue();
		}

		return defaultVal;
	}

	private Integer getIntegerParameter(final HttpServletRequest request, final String name) {
		if (!EsapiServletUtils.hasParameter(request, name)) {
			return null;
		}
		String value = request.getParameter(name);
		if (value == null) {
			return null;
		}
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Integer getIntegerAttribute(final HttpServletRequest request, final String name) {
		Object value = request.getAttribute(name);
		if (value == null) {
			return null;
		}
		if (!(value instanceof String)) {
			return null;
		}

		try {
			return Integer.valueOf((String) value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Get a string parameter or attribute, with a fallback value. Never throws an exception.
	 *
	 * @param request current HTTP request
	 * @param name the name of the parameter
	 * @param defaultVal the default value to use as fallback
	 * @return the int value of parameter or attribute if it exists, otherwise, returns a fallback value
	 */
	@Override
	public String getStringParameterOrAttribute(final HttpServletRequest request, final String name, final String defaultVal) {
		if (EsapiServletUtils.hasParameter(request, name)) {
			String value = request.getParameter(name);
			if (value != null && !value.trim().equals("")) {
				return value;
			}
		}

		Object attrValue = request.getAttribute(name);
		if (attrValue instanceof String) {
			return (String) attrValue;
		}

		return defaultVal;
	}

	/**
	 * Sets the customer session service.
	 *
	 * @param customerSessionService the customerSessionService to set
	 */
	public void setCustomerSessionService(final CustomerSessionService customerSessionService) {
		this.customerSessionService = customerSessionService;
	}

	/**
	 * Gets the customer session service.
	 *
	 * @return the customer session service instance
	 */
	protected CustomerSessionService getCustomerSessionService() {
		return customerSessionService;
	}

	/**
	 * Returns the store configuration that provides context for the actions of this service.
	 *
	 * @return the store configuration.
	 */
	@Override
	public StoreConfig getStoreConfig() {
		return this.storeConfig;
	}

	/**
	 * Sets the store configuration that provides context for the actions of this service.
	 *
	 * @param storeConfig the store configuration.
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}

	/**
	 * Gets the locale resolver.
	 *
	 * @return the locale resolver instance
	 */
	protected LocaleResolver getLocaleResolver() {
		return localeResolver;
	}

	/**
	 * Sets the locale resolver.
	 *
	 * @param localeResolver the new locale resolver
	 */
	public void setLocaleResolver(final LocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}

}
