/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.util.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.sfweb.util.StoreResolver;

/**
 * Resolves a store from an xml configuration file.
 */
public class StoreResolverImpl implements StoreResolver {
	
	private StoreService storeService;

	private boolean savingStoreCodeInSessionEnabled;

	/**
	 * Resolves the store code from a header containing a domain.
	 * 
	 * @param request the request to determine the store from.
	 * @param headerName the request header name to examine.
	 * @return the store code
	 */
	public String resolveDomainHeader(final HttpServletRequest request, final String headerName) {
		if (StringUtils.isEmpty(headerName)) {
			return null;
		}
		return resolveStoreCodeFromDomain(request.getHeader(headerName));
	}

	/**
	 * Resolves the store code from a parameter containing a domain.
	 * 
	 * @param request the request to determine the store from.
	 * @param paramName the request parameter name to examine.
	 * @return the store code
	 */
	public String resolveDomainParam(final HttpServletRequest request, final String paramName) {
		if (StringUtils.isEmpty(paramName)) {
			return null;
		}
		String storeCode = resolveStoreCodeFromDomain(request.getParameter(paramName));
		if (storeCode != null && isSavingStoreCodeInSessionEnabled()) {
			saveStoreCodeInSession(request, paramName, storeCode);
		}
		return storeCode;
	}

	/**
	 * Resolves the store code from a request header.
	 * 
	 * @param request the request to determine the store from.
	 * @param headerName the request header name to examine.
	 * @return the store code
	 */
	public String resolveStoreCodeHeader(final HttpServletRequest request, final String headerName) {
		if (StringUtils.isEmpty(headerName)) {
			return null;
		}
		return validateStoreCode(request.getHeader(headerName));
	}

	/**
	 * Resolves the store code from its request parameter.
	 * 
	 * @param request the request to determine the store from.
	 * @param paramName the request parameter name to examine.
	 * @return the store code
	 */
	public String resolveStoreCodeParam(final HttpServletRequest request, final String paramName) {
		if (StringUtils.isEmpty(paramName)) {
			return null;
		}
		String storeCode = validateStoreCode(request.getParameter(paramName));
		if (storeCode != null && isSavingStoreCodeInSessionEnabled()) {
			saveStoreCodeInSession(request, paramName, storeCode);
		}
		return storeCode;
	}
	
	/**
	 * Resolves the store code from a session attribute.
	 * 
	 * @param request the request to determine the store from.
	 * @param attributeName the session attribute name to examine.
	 * @return the store code
	 */
	public String resolveStoreCodeSession(final HttpServletRequest request, final String attributeName) {
		if (StringUtils.isEmpty(attributeName)) {
			return null;
		}
		return validateStoreCode((String) request.getSession().getAttribute(attributeName));
	}

	/**
	 * Resolves the store code from a session attribute.
	 * 
	 * @param request the request to determine the store from.
	 * @param attributeName the session attribute name to examine.
	 * @return the store code
	 */
	public String resolveDomainSession(final HttpServletRequest request, final String attributeName) {
		if (StringUtils.isEmpty(attributeName)) {
			return null;
		}
		return resolveStoreCodeFromDomain((String) request.getSession().getAttribute(attributeName));
	}

	/**
	 * Resolves a store code from a domain.
	 * 
	 * @param domain the domain to resolve a store from.
	 * @return the <code>Store</code> instance, or null if the store
	 *         cannot be resolved.
	 */
	protected String resolveStoreCodeFromDomain(final String domain) {
		if (null == domain) {
			return null;
		}
		String requestedHost = stripAnyPortNumber(domain);
		return getDomainToStoreMapping().get(requestedHost);
	}
	
	/**
	 * Validate the given store code.
	 * 
	 * @param storeCode the store code to validate
	 * @return a valid store code or null if the given store code is invalid
	 */
	protected String validateStoreCode(final String storeCode) {
		if (getStoreCodes().contains(storeCode)) {
			return storeCode;
		}
		return null;
	}
	
	/**
	 * Get the set of valid store codes.
	 * 
	 * @return a collection of all codes for complete stores.
	 */
	protected Collection<String> getStoreCodes() {
		Set<String> storeCodes = new HashSet<String>();
		List<Store> stores = storeService.findAllCompleteStores();
		for (Store store : stores) {
			storeCodes.add(store.getCode());
		}
		return storeCodes;
	}

	/**
	 * Creates a map with all the store domains mapped to their store codes.
	 * 
	 * @return a map populated with all store domains mapped to 
	 * 		   their corresponding store codes.
	 */
	protected Map<String, String> getDomainToStoreMapping() {
		Map<String, String> storeCodeMap = new HashMap<String, String>();
		List<Store> stores = storeService.findAllCompleteStores();
		for (Store store : stores) {
			String domainName = extractDomainName(store.getUrl());
			if (domainName != null) {
				storeCodeMap.put(domainName, store.getCode());
			}
		}
		return storeCodeMap;
	}
	
	/**
	 * Extracts the domain name from a URL string.
	 * 
	 * @param urlString the URL string
	 * @return the domain name
	 */
	protected String extractDomainName(final String urlString) {
		
		try {
			return new URL(urlString).getHost();
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * The 'host' header can include a port number - discard this (return just the host).
	 * 
	 * @param requestedHost the host:port string
	 * @return the host only
	 */
	protected String stripAnyPortNumber(final String requestedHost) {
		// pre-req: requestedHost cannot be null;
		int colonIndex = requestedHost.indexOf(':');
		if (colonIndex == -1) {
			return requestedHost;
		}
		return requestedHost.substring(0, colonIndex);
	}
	
	/**
	 * Save the store code in the named session attribute.
	 * 
	 * @param request the request that holds the session
	 * @param attributeName the name of the session attribute to use
	 * @param storeCode the storecode
	 */
	protected void saveStoreCodeInSession(final HttpServletRequest request, final String attributeName, final String storeCode) {
		request.getSession().setAttribute(attributeName, storeCode);
	}

	/**
	 * Set a flag indicating whether the store code should be saved in the session.
	 *  
	 * @param savingStoreCodeInSessionEnabled set true to save the store code in the session
	 */
	public void setSavingStoreCodeInSessionEnabled(final boolean savingStoreCodeInSessionEnabled) {
		this.savingStoreCodeInSessionEnabled = savingStoreCodeInSessionEnabled;
	}

	/**
	 * Get the flag indicating whether store code should be saved in the session.
	 * 
	 * @return true if the store code should be saved in the session
	 */
	protected boolean isSavingStoreCodeInSessionEnabled() {
		return this.savingStoreCodeInSessionEnabled;
	}

	/**
	 * Sets the store service instance.
	 * 
	 * @param storeService the service to retrieve the store from.
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}
	
}
