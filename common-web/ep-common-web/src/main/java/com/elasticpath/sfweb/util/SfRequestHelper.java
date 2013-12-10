/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.util;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.sfweb.exception.EpRequestParameterBindingException;
import com.elasticpath.web.util.RequestHelper;

/**
 * <code>RequestHelper</code> represents a helper instance for http requests.
 */
public interface SfRequestHelper extends RequestHelper {
	/**
	 * Return a <code>CustomerSession</code> instance if there is one stored in http session.  If not, it will try to retrieve one
	 * based on the session GUID. 
	 * 
	 * By the time this is called, the request should already have a Customer Session so it will not be creating one.
	 * 
	 * @param request the http request
	 * @return a <code>CustomerSession</code> instance or null.
	 */
	CustomerSession getCustomerSession(final HttpServletRequest request);

	/**
	 * Retrieves the customer session identified by the session GUID existing
	 * in a cookie in the given request, or null if either the cookie, the guid,
	 * or the session cannot be found.
	 * 
	 * @param request the request
	 * @return a <code>CustomerSession</code> instance or null.
	 */
	CustomerSession getPersistedCustomerSession(final HttpServletRequest request);
	
	/**
	 * Set a <code>CustomerSession</code> into http session.
	 * 
	 * @param request the http request
	 * @param customerSession the customer session to save
	 */
	void setCustomerSession(final HttpServletRequest request, final CustomerSession customerSession);

	/**
	 * Retrieve the given parameter as <code>Long</code> from the given request.
	 * 
	 * @param request the request
	 * @param parameterName the parameter name
	 * @return a <code>Long</code> type instance if the parameter is set, otherwise null
	 * @throws EpRequestParameterBindingException if this method would normally throw a {@code ServletRequestBindingException}.
	 * @deprecated Use Spring ServletRequestUtils instead
	 */
	@Deprecated
	Long getLongParameter(final HttpServletRequest request, final String parameterName) throws EpRequestParameterBindingException;
	
	/**
	 * Retrieve the given parameter as <code>Long</code> from the given request with a fallback value.
	 * 
	 * @param request the request
	 * @param parameterName the parameter name
	 * @param fallback the fallback value
	 * @return a <code>Long</code> type instance if the parameter is set, otherwise null
	 * @deprecated Use Spring ServletRequestUtils instead
	 */
	@Deprecated
	Long getLongParameter(final HttpServletRequest request, final String parameterName, final Long fallback);

	/**
	 * Returns the value of a cookie with the specified name.
	 * 
	 * @param request The Http request to retrieve cookies from
	 * @param cookieName The name of the cookie to be retrieved
	 * @return the value of the cookie as a string
	 */
	String getCookieValue(final HttpServletRequest request, final String cookieName);

	/**
	 * Returns the store configuration that provides context for the actions of this service.
	 * 
	 * @return the store configuration.
	 */
	StoreConfig getStoreConfig();

	/**
	 * Get a string parameter or attribute, with a fallback value. Never throws an exception.
	 * 
	 * @param request current HTTP request
	 * @param name the name of the parameter
	 * @param defaultVal the default value to use as fallback
	 * @return the int value of parameter or attribute if it exists, otherwise, returns a fallback value
 	 * @deprecated Use Spring ServletRequestUtils instead.
 	 * 	  Note that in general, falling back from a parameter to an attribute is a bad idea anyways...
  	 */
 	@Deprecated
	String getStringParameterOrAttribute(HttpServletRequest request, String name, String defaultVal);
	
}
