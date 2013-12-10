/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elasticpath.sfweb.security.AccessChecker;

/**
 * Determines whether a store can be accessed by the requester.
 */
@SuppressWarnings("PMD.SimplifyStartsWith")
public class StoreAccessFilter implements Filter {

	private AccessChecker accessChecker;
	
	private String storeNotAccessibleView;

	private String storefrontContextUrl;
	
	/**
	 * Do the store access filtering. This will just pass through as normal if a store is accessible,
	 * otherwise it will redirect to a maintenance page.
	 * 
	 * @param request the request
	 * @param response the response
	 * @param filterChain the filter chain
	 * @throws IOException in case of an I/O error in the filter chain
	 * @throws ServletException in case of a servlet exception
	 */
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) 
		throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (isAccessible(httpRequest)) {
			filterChain.doFilter(request, response);
		} else {
			storeNotAccessible(httpRequest, httpResponse);
			return;
		}
		
	}

	/**
	 * Executed if the store is not accessible. Forces a redirect to a maintenance page.
	 * 
	 * @param request the response
	 * @param response the request
	 * @throws IOException if an I/O error occurs with the redirect
	 */
	protected void storeNotAccessible(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		response.sendRedirect(getRedirectUri(request));
	}

	/**
	 * Check if the store is accessible.
	 * 
	 * @param request the request to check
	 * @return true if the store is accessible
	 */
	protected boolean isAccessible(final HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		return getAccessChecker().isAccessible(request) || requestUri.equals(getRedirectUri(request));
	}
	
	/**
	 * Get the URI to redirect to.
	 * 
	 * @param request the request
	 * @return the URI that we should redirect to
	 */
	protected String getRedirectUri(final HttpServletRequest request) {		
		String contextPath = storefrontContextUrl;		
		String view = getStoreNotAccessibleView();
		StringBuffer uri = new StringBuffer(contextPath);
		if (!contextPath.endsWith("/") && !view.startsWith("/")) {
			uri.append('/');
		}
		uri.append(view);
		return uri.toString();
	}
	
	/**
	 * Initialize the filter. In this case no initialization is required.
	 * 
	 * @param filterConfig the filter config
	 * @throws ServletException in case of an error during initialization
	 */
	public void init(final FilterConfig filterConfig) throws ServletException {
		// nothing to do
	}

	/**
	 * Gets called when the filter is destroyed. In this case nothing needs to be done.
	 */
	public void destroy() {
		// nothing to do
	}

	/**
	 * Get the access checker used to check the access.
	 * 
	 * @return the accessChecker
	 */
	protected AccessChecker getAccessChecker() {
		return accessChecker;
	}

	/**
	 * Set the access checker used to check the access.
	 * 
	 * @param accessChecker the accessChecker to set
	 */
	public void setAccessChecker(final AccessChecker accessChecker) {
		this.accessChecker = accessChecker;
	}

	/**
	 * Get the view to redirect to if the store is not accessible.
	 * 
	 * @return the storeNotAccessibleView
	 */
	protected String getStoreNotAccessibleView() {
		return storeNotAccessibleView;
	}

	/**
	 * Set the view to redirect to if the store is not accessible.
	 * 
	 * @param storeNotAccessibleView the storeNotAccessibleView to set
	 */
	public void setStoreNotAccessibleView(final String storeNotAccessibleView) {
		this.storeNotAccessibleView = storeNotAccessibleView;
	}
	
	/**
	 * 
	 * @param storefrontContextUrl The context url for the storefront.
	 */
	public void setStorefrontContextUrl(final String storefrontContextUrl) {
		this.storefrontContextUrl = storefrontContextUrl;
	}
}

