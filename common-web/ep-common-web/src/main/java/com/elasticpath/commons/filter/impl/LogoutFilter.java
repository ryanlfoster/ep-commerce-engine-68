package com.elasticpath.commons.filter.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.apache.log4j.Logger;

/**
 *	Replacement for the Spring Security's <code>LogoutFilter</code> using properties rather than constructor args.
 */
public class LogoutFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(LogoutFilter.class);
	
	private String filterProcessesUrl = "/j_acegi_logout";
	private String logoutSuccessUrl;
	private List<LogoutHandler> handlers;
	
    /**
     * Not called as we use spring IoC.
     */
	public void destroy() {
		// NOPMD
	}

	/**
	 * Called by the container whenever a request/response pair is passed through the chain.
	 * @param request - the servlet request
	 * @param response - the servlet response
	 * @param chain - the filter chain
	 * @throws IOException - an IO exception
	 * @throws ServletException - a servlet exception
	 */
	public void doFilter(final ServletRequest request, final ServletResponse response,
			final FilterChain chain) throws IOException, ServletException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("LogoutFilter initializing with " + handlers.size() + " handlers");
		}
		LogoutHandler[] logoutHandlers = handlers.toArray(new LogoutHandler[handlers.size()]);
		org.springframework.security.web.authentication.logout.LogoutFilter filter = 
			new org.springframework.security.web.authentication.logout.LogoutFilter(logoutSuccessUrl, logoutHandlers);
		filter.setFilterProcessesUrl(filterProcessesUrl);
		filter.doFilter(request, response, chain);
	}

	/**
	 * Not called as we use spring IoC.
	 * @param filterConfig ignored
	 * @throws ServletException ignored
	 */
	public void init(final FilterConfig filterConfig) throws ServletException {
		// NOPMD
	}

	 /**
	 * Set the URL this filter applies to.
	 * @param filterProcessesUrl the filterProcessesUrl to set
	 */
	public void setFilterProcessesUrl(final String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}

	/**
	 * Set the list of logout handlers.
	 * @param handlers the handlers to set
	 */
	public void setHandlers(final List<LogoutHandler> handlers) {
		this.handlers = handlers;
	}

	/**
	 * Set the URL to redirect to on successful logout.
	 * @param logoutSuccessUrl the logoutSuccessUrl to set
	 */
	public void setLogoutSuccessUrl(final String logoutSuccessUrl) {
		this.logoutSuccessUrl = logoutSuccessUrl;
	}

}
