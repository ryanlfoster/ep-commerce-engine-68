/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.exceptionhandler;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.WebConstants;

/**
 * <code>EpSystemExceptionHandler</code> is to handle any exception that is bubbled up to the web layer.
 */
public class EpSystemExceptionHandler implements HandlerExceptionResolver, Ordered {
	private static final Logger LOG = Logger.getLogger(EpSystemExceptionHandler.class.getName());

	private String viewName;

	private int order;

	/**
	 * Receive any exception that is not caught under controller layer. Log the exception and show the generic error page.
	 *
	 * @param request - the request
	 * @param response - the response
	 * @param handler - the current handler.
	 * @param exception - the exception to be handled.
	 * @return the model and view for display.
	 */
	public ModelAndView resolveException(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
			final Exception exception) {
		ModelAndView modelAndView = null;
		LOG.error("EpSystemExceptionHandler -", exception);

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put(WebConstants.ERROR_KEY, exception.getClass().getName());
		modelAndView = new ModelAndView(this.viewName, modelMap);

		// set error code so this page will not be cached and when the issue is resolved,
		// the correct page will be  displayed
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return modelAndView;
	}

	/**
	 * Gets the order of the current exception handler.
	 *
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * Sets the order of the current exception handler.
	 *
	 * @param order the order to set
	 */
	public void setOrder(final int order) {
		this.order = order;
	}

	/**
	 * Sets the static view name.
	 *
	 * @param viewName - the static view name.
	 */
	public final void setViewName(final String viewName) {
		this.viewName = viewName;
	}
}
