package com.elasticpath.web.util;

import javax.servlet.http.HttpServletRequest;


/**
 * A helper for HTTP requests.
 */
public interface RequestHelper {
	/**
	 * Retrive the absolute url from the given request.
	 *
	 * @param request the request
	 * @return the absolute url
	 */
	String getUrl(final HttpServletRequest request);

	/**
	 * Get an int parameter or attribute, with a fallback value. Never throws an exception. Can pass a distinguished value as default to enable
	 * checks of whether it was supplied.
	 *
	 * @param request current HTTP request
	 * @param name the name of the parameter
	 * @param defaultVal the default value to use as fallback
	 * @return the int value of parameter or attribute if it exists, otherwise, returns a fallback value
	 * @deprecated Use Spring ServletRequestUtils instead.
	 *    Note that in general, falling back from a parameter to an attribute is a bad idea anyways...
	 */
	@Deprecated
	int getIntParameterOrAttribute(HttpServletRequest request, String name, int defaultVal);

}
