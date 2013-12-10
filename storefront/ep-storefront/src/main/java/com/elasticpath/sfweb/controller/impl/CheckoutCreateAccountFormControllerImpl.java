/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.UserIdExistException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.sfweb.EpSfWebException;
import com.elasticpath.sfweb.formbean.CustomerFormBean;
import com.elasticpath.sfweb.service.WebCustomerSessionService;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestResponseFacade;


/**
 * Form controller for a form used to create an account during the checkout process.
 */
public class CheckoutCreateAccountFormControllerImpl extends AbstractEpFormController {
	private static final Logger LOG = Logger.getLogger(CheckoutCreateAccountFormControllerImpl.class);

	private CustomerService customerService;
	
	private WebCustomerSessionService webCustomerSessionService;


	/**
	 * Handle the customer add account form submit.
	 *
	 * @param request -the request
	 * @param response -the response
	 * @param command -the command object
	 * @param errors - will be written back in case of any business error happens
	 * @return return the view
	 * @throws EpSfWebException in case of any error happens
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response, final Object command,
			final BindException errors) throws EpSfWebException {
		LOG.debug("CheckoutCreateAccountFormController: entering 'onSubmit'");

		ModelAndView nextView = new ModelAndView(getSuccessView());

		final CustomerFormBean customerFormBean = (CustomerFormBean) command;
		final Customer customer = createCustomerFromBean(customerFormBean);
		try {
			customer.setAnonymous(false);
			customer.setStoreCode(getRequestHelper().getStoreConfig().getStoreCode());
			
			final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
			final ShoppingCart shoppingCart = customerSession.getShoppingCart();
			
			customer.setPreferredLocale(shoppingCart.getLocale());
			
			Customer savedCustomer = null;
            if (request.getUserPrincipal() == null)  {
            	savedCustomer = customerService.add(customer);
            } else {
                customer.setUserId(request.getUserPrincipal().getName());
                savedCustomer = customerService.addByAuthenticate(customer, true);
            }

			final HttpServletFacadeFactory facadeFactory = getBean("httpServletFacadeFactory");
			final HttpServletRequestResponseFacade requestResponseFacade = facadeFactory.createRequestResponseFacade(request, response);

			webCustomerSessionService.handleCreateNewAccount(requestResponseFacade, savedCustomer);

			// To integrate with spring security framework:
			// save the add the Authentication object into the SecurityContextHoder, which could be used to
			// evaluate the following invocation.
			UsernamePasswordAuthenticationToken authResult = new UsernamePasswordAuthenticationToken(savedCustomer, 
					savedCustomer.getClearTextPassword(),
					savedCustomer.getAuthorities());
			authResult.setDetails(new WebAuthenticationDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authResult);

			// Redirect the user to the page they were trying to access before they were brought to the login page by Spring Security
			HttpSessionRequestCache httpSessionRequestCache = new HttpSessionRequestCache();
			final SavedRequest savedRequest = httpSessionRequestCache.getRequest(request, response);
			String targetUrl = null;
			if (savedRequest != null) {
				targetUrl = savedRequest.getRedirectUrl();
			}
			httpSessionRequestCache.removeRequest(request, response);
			if (targetUrl != null) {
				nextView = new ModelAndView("redirect:" + targetUrl.substring(targetUrl.lastIndexOf('/')));
			}

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
	 * Creates a new customer object of type {@link Customer} initialized with values
	 * from the {@link CustomerFormBean}.
	 *  
	 * @param customerFormBean customer form bean
	 * @return instance of {@link Customer}
	 */
	protected Customer createCustomerFromBean(final CustomerFormBean customerFormBean) {
		final Customer customer = getBean(ContextIdNames.CUSTOMER);
		
		customer.setClearTextPassword(customerFormBean.getClearTextPassword());
		customer.setEmail(customerFormBean.getEmail());
		customer.setFirstName(customerFormBean.getFirstName());
		customer.setLastName(customerFormBean.getLastName());
		customer.setPhoneNumber(customerFormBean.getPhoneNumber());
		customer.setToBeNotified(customerFormBean.isToBeNotified());
		
		return customer;
	}

	/**
	 * Set the customer service.
	 *
	 * @param customerService the customer service to set.
	 */
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	/**
	 * Set the customer session service.
	 *
	 * @param webCustomerSessionService the web customer session service to set.
	 */
	public void setWebCustomerSessionService(final WebCustomerSessionService webCustomerSessionService) {
		this.webCustomerSessionService = webCustomerSessionService;
	}

}
