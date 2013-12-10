package com.elasticpath.web.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;

/**
 * A security wrapped implementation of {@Link AbstractWizardFormController}.
 */
public abstract class AbstractSecurityWizardFormController extends AbstractWizardFormController {
	private final List<ParameterConfiguration> bindingConfig = new ArrayList<ParameterConfiguration>();

	/**
	 * Locked down implementation in order to secure the {@link HttpServletRequest}. Implement
	 * {@link #handleRequestInternal(HttpServletRequest, HttpServletResponse)} to define your own logic.
	 * 
	 * @param request {@inheritDoc}
	 * @param response {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws {@inheritDoc}
	 */
	@Override
	public final ModelAndView handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		return super.handleRequest(EsapiServletUtils.secureHttpRequest(request), response);
	}

	/**
	 * Creates a new {@link ServletRequestDataBinder} for the request and command.
	 * 
	 * @param request current HTTP request
	 * @param command the command to bind onto
	 * @return the new binder instance
	 * @throws Exception in case of invalid state or arguments
	 */
	@Override
	protected final ServletRequestDataBinder createBinder(final HttpServletRequest request, final Object command) throws Exception {
		ServletRequestDataBinder binder = new EsapiServletRequestDataBinder(command, getCommandName(), bindingConfig);
		prepareBinder(binder);
		initBinder(EsapiServletUtils.secureHttpRequest(request), binder);

		return binder;
	}

	@Override
	protected boolean isFinishRequest(final HttpServletRequest request) {
		return EsapiServletUtils.hasParameter(request, PARAM_FINISH);
	}

	@Override
	protected boolean isCancelRequest(final HttpServletRequest request) {
		return EsapiServletUtils.hasParameter(request, PARAM_CANCEL);
	}

	/**
	 * This is a copy-paste of the parent method with the exception of checking the page number. Due to the way a
	 * {@link org.owasp.esapi.filters.SecurityWrapperRequest} works, the parent implementation no longer works out of
	 * box.
	 *
	 * {@inheritDoc}
	 */
	@Override
	protected int getCurrentPage(final HttpServletRequest request) {
		// Check for overriding attribute in request.
		String pageAttrName = getPageSessionAttributeName(request);
		Integer pageAttr = (Integer) request.getAttribute(pageAttrName);
		if (pageAttr != null) {
			return pageAttr.intValue();
		}
		// Check for explicit request parameter.
		if (EsapiServletUtils.hasParameter(request, PARAM_PAGE)) {
			return Integer.parseInt(request.getParameter(PARAM_PAGE));
		}
		// Check for original attribute in session.
		if (isSessionForm()) {
			pageAttr = (Integer) request.getSession().getAttribute(pageAttrName);
			if (pageAttr != null) {
				return pageAttr.intValue();
			}
		}
		throw new IllegalStateException(
				"Page attribute [" + pageAttrName + "] neither found in session nor in request");
	}

	/**
	 * Sets the configuration to be used by the {@link org.springframework.validation.DataBinder DataBinder}.
	 * 
	 * @param bindingConfig list of {@link ParameterConfiguration} to pass to the binder
	 * @see ParameterConfiguration
	 */
	public void setBindingConfig(final List<ParameterConfiguration> bindingConfig) {
		// not allowed access to internal list
		this.bindingConfig.clear();
		if (bindingConfig != null) {
			this.bindingConfig.addAll(bindingConfig);
		}
	}
}
