/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.sfweb.util;

import javax.servlet.http.HttpServletResponse;

/**
 * A handler for dealing with cookies.
 */
public interface CookieHandler {

	/**
	 * Adds a cookie by using the response.
	 * 
	 * @param response the servlet response
	 * @param name the cookie name
	 * @param value the value of the cookie
	 */
	void addCookie(HttpServletResponse response, String name, String value);
	
	/**
	 * Removes the cookie from the client using the response.
	 * 
	 * @param response the response
	 * @param name the cookie name
	 */
	void removeCookie(HttpServletResponse response, String name);

}
