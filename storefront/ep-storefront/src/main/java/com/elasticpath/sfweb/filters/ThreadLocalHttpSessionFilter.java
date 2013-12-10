/**
 * 
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

import com.elasticpath.commons.httpsession.HttpSessionHandle;

/**
 * Sets the HttpSession into ThreadLocal storage.
 */
public class ThreadLocalHttpSessionFilter implements Filter {

	private HttpSessionHandle httpSessionHandle;
	
	/**
	 * Sets the threadlocal's HttpSession to null.
	 */
	public void destroy() {
		httpSessionHandle.setHttpSession(null);
	}

	/**
	 * Sets the threadlocal's HttpSession to the request session.
	 * @param request the request
	 * @param response the response
	 * @param chain the filter chain
	 * @throws IOException on error
	 * @throws ServletException on error
	 */
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) 
	throws IOException, ServletException {
		httpSessionHandle.setHttpSession(((HttpServletRequest) request).getSession());
		chain.doFilter(request, response);
	}

	/**
	 * {@inheritDoc}
	 * This implementation does nothing.
	 */
	public void init(final FilterConfig filterConfig) throws ServletException {
		//no init required
	}

	public void setHttpSessionHandle(final HttpSessionHandle httpSessionHandle) {
		this.httpSessionHandle = httpSessionHandle;
	}
}
