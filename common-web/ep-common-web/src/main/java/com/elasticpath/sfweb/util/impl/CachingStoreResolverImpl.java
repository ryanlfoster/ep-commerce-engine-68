/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.util.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.sfweb.util.StoreResolver;

/**
 * Resolves a store from an xml configuration file.
 */
public class CachingStoreResolverImpl implements StoreResolver {
	
	private static final long DEFAULT_CACHE_TIMEOUT_VALUE = 300000;

	private StoreResolver delegate;

	private final SimpleTimeoutCache<String, String> domainCache = new SimpleTimeoutCache<String, String>(DEFAULT_CACHE_TIMEOUT_VALUE);

	private final SimpleTimeoutCache<String, String> storeCodeCache = new SimpleTimeoutCache<String, String>(DEFAULT_CACHE_TIMEOUT_VALUE);

	/**
	 * Resolves the store code from a header containing a domain, looking in the cache first.
	 * 
	 * @param request the request to determine the store from.
	 * @param headerName the request header name to examine.
	 * @return the store code
	 */
	public String resolveDomainHeader(final HttpServletRequest request, final String headerName) {
		if (StringUtils.isEmpty(headerName)) {
			return null;
		}
		String domain = request.getHeader(headerName);
		if (domain == null) {
			return null;
		}
		
		String storeCode = domainCache.get(domain);

		if (storeCode == null) {
			storeCode = delegate.resolveDomainHeader(request, headerName);
			domainCache.put(domain, storeCode);
		}
		
		return storeCode;
	}

	/**
	 * Resolves the store code from a parameter containing a domain, looking in the cache first.
	 * 
	 * @param request the request to determine the store from.
	 * @param paramName the request parameter name to examine.
	 * @return the store code
	 */
	public String resolveDomainParam(final HttpServletRequest request, final String paramName) {
		if (StringUtils.isEmpty(paramName)) {
			
			return null;
		}
		String domain = request.getParameter(paramName);
		if (domain == null) {
			return null;
		}
		
		String storeCode = domainCache.get(domain);

		if (storeCode == null) {
			storeCode = delegate.resolveDomainParam(request, paramName);
			domainCache.put(domain, storeCode);
		}
		
		return storeCode;
	}

	/**
	 * Resolves the store code from a request header, looking in the cache first.
	 * 
	 * @param request the request to determine the store from.
	 * @param headerName the request header name to examine.
	 * @return the store code
	 */
	public String resolveStoreCodeHeader(final HttpServletRequest request, final String headerName) {
		if (StringUtils.isEmpty(headerName)) {
			return null;
		}
		String storeCode = request.getHeader(headerName);
		if (storeCode == null) {
			return null;
		}
		
		if (storeCodeCache.get(storeCode) == null) {
			storeCode = delegate.resolveStoreCodeHeader(request, headerName);
			storeCodeCache.put(storeCode, storeCode);
		} 
		
		return storeCode;
	}

	/**
	 * Resolves the store code from its request parameter, looking in the cache first.
	 * 
	 * @param request the request to determine the store from.
	 * @param paramName the request parameter name to examine.
	 * @return the store code
	 */
	public String resolveStoreCodeParam(final HttpServletRequest request, final String paramName) {
		if (StringUtils.isEmpty(paramName)) {
			return null;
		}
		String storeCode = request.getParameter(paramName);
		if (storeCode == null) {
			return null;
		}
		
		if (storeCodeCache.get(storeCode) == null) {
			storeCode = delegate.resolveStoreCodeParam(request, paramName);
			storeCodeCache.put(storeCode, storeCode);
		} else {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.setAttribute(paramName, storeCode);
			}
		}
		
		return storeCode;
	}

	/**
	 * Resolves the store code from a session attribute, looking in the cache first.
	 * 
	 * @param request the request to determine the store from.
	 * @param attributeName the session attribute name to examine.
	 * @return the store code
	 */
	public String resolveStoreCodeSession(final HttpServletRequest request, final String attributeName) {
		if (StringUtils.isEmpty(attributeName)) {
			return null;
		}
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		String storeCode = (String) session.getAttribute(attributeName);
		if (storeCode == null) {
			return null;
		}
		if (storeCodeCache.get(storeCode) == null) {
			storeCode = delegate.resolveStoreCodeSession(request, attributeName);
			storeCodeCache.put(storeCode, storeCode);
		} 
		
		return storeCode;
	}

	/**
	 * Resolves the store code from a session attribute containing a domain, looking in the cache first..
	 * 
	 * @param request the request to determine the store from.
	 * @param attributeName the session attribute name to examine.
	 * @return the store code
	 */
	public String resolveDomainSession(final HttpServletRequest request, final String attributeName) {
		if (StringUtils.isEmpty(attributeName)) {
			return null;
		}
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		
		String domain = (String) session.getAttribute(attributeName); 
		String storeCode = domainCache.get(domain);

		if (storeCode == null) {
			storeCode = delegate.resolveDomainSession(request, attributeName);
			domainCache.put(domain, storeCode);
		}
		
		return storeCode;
	}

	/**
	 * Sets the delegate for resolving the store code.
	 *
	 * @param delegate - The delegate to set.
	 */
	public void setDelegate(final StoreResolver delegate) {
		this.delegate = delegate;
	}
	
	/**
	 * Sets the timeout.
	 * 
	 * @param timeout the timeout in milliseconds
	 */
	public void setCacheTimeoutMillis(final long timeout) {
		domainCache.setTimeout(timeout);
		storeCodeCache.setTimeout(timeout);
	}

}
