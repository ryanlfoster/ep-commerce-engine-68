package com.elasticpath.sfweb.servlet.facade;

/**
 * Facade class to limit the access to the HttpServletResponse when the response must be passed outside of a filter.
 *
 */
public interface HttpServletResponseFacade {

	/**
	 * Writes the key/value pair as a cookie in the response.
	 * @param key the cookie key
	 * @param value the cookie value
	 */
	void writeCookie(final String key, final String value);

}