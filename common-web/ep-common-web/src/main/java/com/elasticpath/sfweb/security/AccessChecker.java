/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.security;

import javax.servlet.http.HttpServletRequest;

/**
 * Define methods for checking access for a store.
 */
public interface AccessChecker {

	/**
	 * Determine whether the store is accessible based on what is provided in the request.
	 * 
	 * @param request the request coming in from a browser
	 * @return true if the store is accessible
	 */
	boolean isAccessible(HttpServletRequest request);
}
