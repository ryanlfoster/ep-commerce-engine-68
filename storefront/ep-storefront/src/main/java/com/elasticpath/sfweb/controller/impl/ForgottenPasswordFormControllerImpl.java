/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.exception.UserIdNonExistException;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.sfweb.EpSfWebException;
import com.elasticpath.sfweb.formbean.ForgottenPasswordFormBean;

/**
 * The Spring MVC controller for customer forgotten-password page.
 */
public class ForgottenPasswordFormControllerImpl extends AbstractEpFormController {
	private static final Logger LOG = Logger.getLogger(ForgottenPasswordFormControllerImpl.class);

	private CustomerService customerService;

	/**
	 * Handle the forgotten password form submit.
	 *
	 * @param request -the request
	 * @param response -the response
	 * @param command -the command object
	 * @param errors - will be written back in case of any business error happens
	 * @return return the view
	 * @throws EpSfWebException in case of any error happens
	 */
	@Override
	protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response, final Object command,
			final BindException errors) throws EpSfWebException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("ForgottenPasswordController: entering 'onSubmit' method...");
		}
		final ForgottenPasswordFormBean forgottenPasswordFormBean = (ForgottenPasswordFormBean) command;

		ModelAndView nextView = new ModelAndView(getSuccessView());
		try {
			this.customerService.resetPassword(forgottenPasswordFormBean.getEmail(), getRequestHelper().getStoreConfig().getStore().getCode());
		} catch (final UserIdNonExistException e) {
			errors.reject(e.getClass().getName(), new Object[] {}, e.getMessage());
			try {
				nextView = super.showForm(request, response, errors);
			} catch (Exception e1) {
				throw new EpSfWebException("Caught an exception.", e1); // NOPMD
			}
		}
		return nextView;
	}

	/**
	 * Set the customer service.
	 * @param customerService the customer service
	 */
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

}
