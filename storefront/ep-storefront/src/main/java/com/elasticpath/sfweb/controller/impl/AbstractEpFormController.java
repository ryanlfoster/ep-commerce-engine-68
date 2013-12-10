package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.sfweb.EpSfWebException;
import com.elasticpath.sfweb.controller.EpController;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * <code>AbstactEpFormControllerImpl</code> represents a controller in an Spring MVC
 * pattern dealing with form based requests.
 */
public abstract class AbstractEpFormController extends SimpleFormController implements EpController {
	private SfRequestHelper requestHelper;
	private String[] disallowedFields;
	private BeanFactory beanFactory;


	/**
	 * Sets the disallowed fields for the abstract form controller and all
	 * of its extenders.
	 *
	 * @param disallowedFields a comma separated list of disallowed fields to set
	 */
	public void setDisallowedFormFields(final String disallowedFields) {
		this.disallowedFields = disallowedFields.trim().split("[\\s]*,[\\s]*");
	}

	/**
	 * Sets the disallowed fields for the implementing controller.
	 * <p>
	 * Note: Make sure that if this method is overwritten it calls super.initBinder().
	 * 		Otherwise set disallowed fields won't work.
	 * @param request the http request
	 * @param binder the servlet request data binder instance
	 * @throws Exception if error occurs
	 */
	@Override
	protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) throws Exception {
		if (disallowedFields != null) {
			binder.setDisallowedFields(disallowedFields);
		}
		super.initBinder(request, binder);
	}

	/**
	 * Sets the requestHelper instance.
	 *
	 * @param requestHelper -the requesthelper instance.
	 */
	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}

	/**
	 * Gets the requestHelper instance.
	 *
	 * @return - the current requestHelper instance.
	 */
	public SfRequestHelper getRequestHelper() {
		return this.requestHelper;
	}

	/**
	 * Creates and error and returns a ModelAndView object to display the error on the form view.
	 *
	 * @param errorKey the properties file key for the error
	 * @param request the HttpServletRequest
	 * @param response the HttpServletResponse
	 * @param errors the <code>BindException</code>
	 * @return a <code>ModelAndView</code> for displaying the error
	 */
	protected ModelAndView getErrorView(final String errorKey, final HttpServletRequest request, final HttpServletResponse response,
			final BindException errors) {
		errors.reject(errorKey);
		ModelAndView nextView = null;
		try {
			nextView = super.showForm(request, response, errors);
		} catch (Exception e1) {
			throw new EpSfWebException("Caught an exception.", e1);
		}
		return nextView;
	}

	/**
	 * Return a bean specified by the beanName from the bean factory.
	 *
	 * @param <T> generic to allow castless assignment
	 * @param name the name of the bean to return.
	 * @return the bean, or null if it's not found.
	 */
	protected <T> T getBean(final String name) {
		return beanFactory.getBean(name);
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
