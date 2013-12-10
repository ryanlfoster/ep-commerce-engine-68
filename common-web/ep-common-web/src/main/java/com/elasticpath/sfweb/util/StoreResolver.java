/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.util;

import javax.servlet.http.HttpServletRequest;


/**
 * Resolves a store using its request domain, usually extracted from an 
 * HTTP request.
 */
public interface StoreResolver {

	/**
	 * Resolves the store code from its request parameter.
	 * 
	 * @param request the request to determine the store from.
	 * @param paramName the request parameter name to examine.
	 * @return the store code
	 */
	String resolveStoreCodeParam(final HttpServletRequest request, final String paramName);
	
	/**
	 * Resolves the store code from a request header.
	 * 
	 * @param request the request to determine the store from.
	 * @param headerName the request header name to examine.
	 * @return the store code
	 */
	String resolveStoreCodeHeader(final HttpServletRequest request, final String headerName);
	
	/**
	 * Resolves the store code from a parameter containing a domain.
	 * 
	 * @param request the request to determine the store from.
	 * @param paramName the request parameter name to examine.
	 * @return the store code
	 */
	String resolveDomainParam(final HttpServletRequest request, final String paramName);

	/**
	 * Resolves the store code from a header containing a domain.
	 * 
	 * @param request the request to determine the store from.
	 * @param headerName the request header name to examine.
	 * @return the store code
	 */
	String resolveDomainHeader(final HttpServletRequest request, final String headerName);
	
	/**
	 * Resolves the store code from a session attribute.
	 * 
	 * @param request the request to determine the store from.
	 * @param attributeName the session attribute name to examine.
	 * @return the store code
	 */
	String resolveStoreCodeSession(final HttpServletRequest request, final String attributeName);
	
	/**
	 * Resolves the store code from a session attribute.
	 * 
	 * @param request the request to determine the store from.
	 * @param attributeName the session attribute name to examine.
	 * @return the store code
	 */
	String resolveDomainSession(final HttpServletRequest request, final String attributeName);
	

}
