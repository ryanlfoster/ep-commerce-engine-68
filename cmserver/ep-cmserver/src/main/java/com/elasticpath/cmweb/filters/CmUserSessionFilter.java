/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.filters;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import com.elasticpath.cmweb.util.CmRequestHelper;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.CmUserSession;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.web.ajax.dwrconverter.EpCurrencyConverter;

/**
 * This filter makes sure to set the valid cmUser instance into the cmUserSession <br>
 * after spring security framework's successful authentication. <br>
 */
public class CmUserSessionFilter implements Filter, InitializingBean {
	private static final Logger LOG = Logger.getLogger(CmUserSessionFilter.class);

	private CmRequestHelper requestHelper;

	private CmUserService cmUserService;

	/**
	 * Invoked by a BeanFactory after it has set all bean properties supplied (and satisfied BeanFactoryAware and ApplicationContextAware).
	 *
	 * @throws Exception on error
	 */
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.requestHelper, "requestHelper must be specified");
		Assert.notNull(this.cmUserService, "cmUserService must be specified");
	}

	/**
	 * Initialize the filter.
	 *
	 * @param arg0 not used
	 * @throws ServletException in case of error.
	 */
	public void init(final FilterConfig arg0) throws ServletException {
		// No init required
	}

	/**
	 * Filter the request.
	 *
	 * @param inRequest the request
	 * @param inResponse the response
	 * @param inFilterChain the filter chain
	 * @throws IOException in case of error
	 * @throws ServletException in case of error
	 */
	public void doFilter(final ServletRequest inRequest, final ServletResponse inResponse, final FilterChain inFilterChain) throws IOException,
			ServletException {

		if (!(inRequest instanceof HttpServletRequest)) {
			inFilterChain.doFilter(inRequest, inResponse);
			return;
		}

		// Try to check if the cmUser has just passed spring security framework's authentication.
		// If it is, try to get the valid CmUser instance from spring SecurityContextHolder and
		// set it into the CmUserSession.
		final HttpServletRequest httpRequest = (HttpServletRequest) inRequest;
		final CmUserSession cmUserSession = this.requestHelper.getCmUserSession(httpRequest);

		// Handle license key check
		LOG.debug("Url: " + httpRequest.getRequestURL());
		if (httpRequest.getRequestURL().indexOf("/licensing-error.ep") == -1) {
			EpCurrencyConverter licenseChecker = new EpCurrencyConverter();
			licenseChecker.checkLicense();
		}

		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			cmUserSession.setCmUser(null);
		} else if (SecurityContextHolder.getContext().getAuthentication() != null && cmUserSession.getCmUser() == null) {
			final CmUser validCmUser = (CmUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			// Update the lastLoginDate for this cmUser.
			validCmUser.setLastLoginDate(new Date());
			this.cmUserService.update(validCmUser);

			// update the cmUser in the user session
			cmUserSession.setCmUser(validCmUser);
			this.requestHelper.setCmUserSession(httpRequest, cmUserSession);
		}

		inFilterChain.doFilter(inRequest, inResponse);
	}

	/**
	 * Destroy the filter.
	 */
	public void destroy() {
		// Not sure what goes here yet.
	}

	/**
	 * Set the CmUserService.
	 *
	 * @param cmUserService the CmUser service
	 */
	public void setCmUserService(final CmUserService cmUserService) {
		this.cmUserService = cmUserService;
	}

	/**
	 * Sets the requestHelper instance.
	 *
	 * @param requestHelper -the requesthelper instance.
	 */
	public void setRequestHelper(final CmRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}

}
