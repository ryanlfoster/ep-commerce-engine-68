package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.UserIdExistException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.sfweb.EpSfWebException;
import com.elasticpath.sfweb.formbean.EditAccountFormBean;

/**
 * The Spring MVC controller for customer account management page.
 */
public class EditAccountFormControllerImpl extends AbstractEpFormController {

	private static final Logger LOG = Logger.getLogger(EditAccountFormControllerImpl.class);

	private String unauthorizedView;

	private CustomerService customerService;

	private CustomerSessionService customerSessionService;

	/**
	 * Handle the create account form submit.
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
		LOG.debug("entering 'onSubmit' method...");

		ModelAndView nextView = new ModelAndView(getSuccessView());

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final Customer customer = customerSession.getShopper().getCustomer();

		final EditAccountFormBean editAccountFormBean = (EditAccountFormBean) command;

		customer.setFirstName(editAccountFormBean.getFirstName());
		customer.setLastName(editAccountFormBean.getLastName());
		customer.setEmail(editAccountFormBean.getEmail());
		customer.setPhoneNumber(editAccountFormBean.getPhoneNumber());

		try {
			// Update the email address first, which may throw an exception if the email address isn't accepted
			Customer updatedCustomer = customerService.update(customer);

			// Then update the password if required, which will send email to the valid address
			if (StringUtils.isNotBlank(editAccountFormBean.getPassword())) {
				updatedCustomer = customerService.changePasswordAndSendEmail(updatedCustomer, editAccountFormBean.getPassword());
			}
			customerSessionService.updateCustomerAndSave(customerSession, updatedCustomer);
		} catch (UserIdExistException e) {
			errors.rejectValue("email", e.getClass().getName(), new Object[] {}, e.getMessage());
			try {
				nextView = super.showForm(request, response, errors);
			} catch (Exception e1) {
				throw new EpSfWebException("Caught an exception.", e1); // NOPMD
			}
		}

		return nextView;
	}

	/**
	 * Prepare the command object for the create account form.
	 * 
	 * @param request -the current request.
	 * @return the command object.
	 */
	@Override
	protected Object formBackingObject(final HttpServletRequest request) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final Customer customer = customerSession.getShopper().getCustomer();
		return createEditAccountFormBean(customer);
	}

	/**
	 * @param customer
	 * @return
	 */
	private EditAccountFormBean createEditAccountFormBean(final Customer customer) {
		final EditAccountFormBean editAccountFormBean = getBean(ContextIdNames.EDIT_ACCOUNT_FORM_BEAN);

		editAccountFormBean.setFirstName(customer.getFirstName());
		editAccountFormBean.setLastName(customer.getLastName());
		editAccountFormBean.setEmail(customer.getEmail());
		editAccountFormBean.setPhoneNumber(customer.getPhoneNumber());

		return editAccountFormBean;
	}

	/**
	 * Sets the customer service.
	 * 
	 * @param customerService the customer service
	 */
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	/**
	 * Sets the customer session service.
	 * 
	 * @param customerSessionService the customer session service
	 */
	public void setCustomerSessionService(final CustomerSessionService customerSessionService) {
		this.customerSessionService = customerSessionService;
	}

	/**
	 * Sets the unauthorized view name.
	 * 
	 * @param unauthorizedView name of the unauthorized view
	 */
	public final void setUnauthorizedView(final String unauthorizedView) {
		this.unauthorizedView = unauthorizedView;
	}

	/**
	 * Gets the unauthorized view name.
	 * 
	 * @return name of the unauthorized view
	 */
	public String getUnauthorizedView() {
		return this.unauthorizedView;
	}
}
