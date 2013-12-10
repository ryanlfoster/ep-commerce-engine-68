/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.util.impl;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.cmweb.EpCmWebException;
import com.elasticpath.cmweb.util.CmRequestHelper;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.cmuser.CmUserSession;
import com.elasticpath.web.security.EsapiServletUtils;

/**
 * The default implementation of <code>RequestHelper</code>.
 */
public class RequestHelperImpl implements CmRequestHelper {

	private BeanFactory beanFactory;

	/**
	 * Return a <code>CmUserSession</code> instance if there is one stored in http session. Otherwise, create a new one and return it.
	 *
	 * @param request the http request
	 * @return a <code>CmUserSession</code> instance
	 * @throws EpCmWebException when request is null
	 */
	@Override
	public CmUserSession getCmUserSession(final HttpServletRequest request) throws EpCmWebException {
		if (request == null) {
			throw new EpCmWebException("Null request is not allowed.");
		}
		CmUserSession cmUserSession = (CmUserSession) request.getSession().getAttribute(WebConstants.CM_USER_SESSION);
		if (cmUserSession == null) {
			cmUserSession = beanFactory.getBean(ContextIdNames.CM_USER_SESSION);
			this.setCmUserSession(request, cmUserSession);
		}
		return cmUserSession;
	}

	/**
	 * Sets a <code>CmUserSession</code> into http session.
	 *
	 * @param request the http request
	 * @param cmUserSession the cm user session to save
	 * @throws EpCmWebException when request or cm user session is null
	 */
	@Override
	public void setCmUserSession(final HttpServletRequest request, final CmUserSession cmUserSession) throws EpCmWebException {
		if (cmUserSession == null) {
			throw new EpCmWebException("Null cm user session is not allowed.");
		}
		if (request == null) {
			throw new EpCmWebException("Null request is not allowed.");
		}
		request.getSession().setAttribute(WebConstants.CM_USER_SESSION, cmUserSession);
	}

	/**
	 * Retrive the absolute url from the given request.
	 *
	 * @param request the request
	 * @return the absolute url
	 */
	@Override
	public String getUrl(final HttpServletRequest request) {
		final String requestUri = request.getRequestURI();
		final String requestUrl = request.getRequestURL().toString();
		final String contextPath = request.getContextPath();
		final String url = requestUrl.substring(0, requestUrl.lastIndexOf(requestUri));
		return url + contextPath;
	}

	/**
	 * Get an int parameter or attribute, with a fallback value. Never throws an exception. Can pass a distinguished value as default to enable
	 * checks of whether it was supplied.
	 *
	 * @param request current HTTP request
	 * @param name the name of the parameter
	 * @param defaultVal the default value to use as fallback
	 * @return the int value of parameter or attribute if it exists, otherwise, returns a fallback value
	 */
	@Override
	public int getIntParameterOrAttribute(final HttpServletRequest request, final String name, final int defaultVal) {
		Integer intValue = getIntegerParameter(request, name);
		if (intValue != null) {
			return intValue.intValue();
		}

		intValue = getIntegerAttribute(request, name);
		if (intValue != null) {
			return intValue.intValue();
		}

		return defaultVal;
	}

	private Integer getIntegerParameter(final HttpServletRequest request, final String name) {
		if (!EsapiServletUtils.hasParameter(request, name)) {
			return null;
		}
		String value = request.getParameter(name);
		if (value == null) {
			return null;
		}
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Integer getIntegerAttribute(final HttpServletRequest request, final String name) {
		Object value = request.getAttribute(name);
		if (value == null) {
			return null;
		}
		if (!(value instanceof String)) {
			return null;
		}

		try {
			return Integer.valueOf((String) value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
