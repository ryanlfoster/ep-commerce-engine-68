package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.WebConstants;

/**
 * <code>InvalidAccessControllerImpl</code> is the implemenetaion of spring controller to handle http status code 403 (throw by spring security
 * framework on access denied), that is to say, remove the session attribute HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY.
 */
public class InvalidAccessControllerImpl extends AbstractEpControllerImpl {

	private String viewName;

	private static final Logger LOG = Logger.getLogger(InvalidAccessControllerImpl.class);

	private static final String ACCESS_DENIED_ERROR = "403";

	/**
	 * Remove the session attribute for spring security context on access denied.
	 *
	 * @param request -the current request.
	 * @param response -the current response.
	 * @return - the ModleAndView instance for the error page.
	 * @throws Exception if anything goes wrong.
	 */
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Handling http status code 403");
		}
		request.getSession().removeAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		return new ModelAndView(this.viewName, WebConstants.ERROR_KEY, ACCESS_DENIED_ERROR);
	}

	/**
	 * Sets the viewName.
	 *
	 * @param viewName - the viewName for error page.
	 */
	public final void setViewName(final String viewName) {
		this.viewName = viewName;
	}

}