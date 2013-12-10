package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

/**
 * <code>SimplePageControllerImpl</code> is the implemenetaion of spring
 * controller to load a static page.
 */
public class SimplePageControllerImpl extends AbstractEpControllerImpl {

	private String viewName;

	private static final Logger LOG = Logger
			.getLogger(SimplePageControllerImpl.class);

	/**
	 * Return the ModelAndView for the configured static view page.
	 *
	 * @param request -the current request.
	 * @param response -the current response.
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	protected ModelAndView handleRequestInternal(
			final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("entering 'handleRequest' method...");
		}
		return new ModelAndView(this.viewName);
	}

	/**
	 * Sets the static view name.
	 *
	 * @param viewName -
	 *            the static view name.
	 */
	public final void setViewName(final String viewName) {
		this.viewName = viewName;
	}

}
