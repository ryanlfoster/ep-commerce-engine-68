package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EmailExistException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.sfweb.EpSfWebException;
import com.elasticpath.sfweb.formbean.AnonymousSignInFormBean;
import com.elasticpath.sfweb.security.GuestAuthenticationToken;
import com.elasticpath.sfweb.service.WebCustomerSessionService;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestResponseFacade;

/**
 * The Spring MVC controller for customers who sign in anonymously. Customers can only sign in anonymously as part of the checkout process.
 */
public class AnonymousSignInFormControllerImpl extends AbstractEpFormController {

	private static final Logger LOG = Logger.getLogger(AnonymousSignInFormControllerImpl.class);

	private CustomerService customerService;

	/** Form controller for sign in. */
	private SignInFormControllerImpl checkoutSignInFormController;

	private WebCustomerSessionService webCustomerSessionService;

	/**
	 * Handle form submit.
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

		LOG.debug("AnonymousSignInFormController: entering 'onSubmit' method...");

		ModelAndView nextView = new ModelAndView(getSuccessView());
		final AnonymousSignInFormBean anonymousSignInFormBean = (AnonymousSignInFormBean) command;
		final Customer customer = getBean(ContextIdNames.CUSTOMER);
		customer.setAnonymous(true);
		customer.setEmail(anonymousSignInFormBean.getEmail());
		customer.setStoreCode(getRequestHelper().getStoreConfig().getStoreCode());
		try {
			this.customerService.validateNewCustomer(customer);

			// Anonymous customers are currently persisted before checkout so that their addresses can be
			// assigned UIDPKs, which is necessary for manipulating addresses during checkout.
			final Customer newCustomer = customerService.add(customer);

			final Customer retrievedCustomer = customerService.findByGuid(newCustomer.getGuid());

			final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
			final ShoppingCart shoppingCart = customerSession.getShoppingCart();

			// clear and reset shopping cart values
			shoppingCart.clearEstimates();
			shoppingCart.fireRules();
			
			final HttpServletFacadeFactory facadeFactory = getBean("httpServletFacadeFactory");
			final HttpServletRequestResponseFacade requestResponseFacade = facadeFactory.createRequestResponseFacade(request, response);

			webCustomerSessionService.handleGuestSignIn(requestResponseFacade, retrievedCustomer);

		} catch (final EmailExistException e) {
			errors.rejectValue("email", e.getClass().getName(), new Object[] {}, e.getMessage());
			try {
				nextView = super.showForm(request, response, errors);
			} catch (final Exception e1) {
				throw new EpSfWebException("Caught an exception.", e1); // NOPMD
			}
		}

		// Add the Authentication object into the SecurityContextHoder so that Spring Security knows the customer is signed in
		final Customer anonymousCustomer = getBean("anonymousCustomer");
		final GuestAuthenticationToken authentication = new GuestAuthenticationToken(customer.getEmail(), anonymousCustomer.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return nextView;
	}

	/**
	 * Prepare the command object for the create account form.
	 * 
	 * @param request -the current request.
	 * @return the command object.
	 * @throws Exception if error occurs
	 */
	@Override
	@SuppressWarnings({ "PMD.UselessOverridingMethod" })
	protected Object formBackingObject(final HttpServletRequest request) throws Exception {
		// delegate to the super implementation
		return super.formBackingObject(request);
	}

	/**
	 * Prepare the reference data map. <br>
	 * This controller displays on the checkout sign in form which requires a form backing bean for each of the three forms displayed on it.<br>
	 * This method adds the backing beans for the other two forms.
	 * 
	 * @param request the current request.
	 * @return reference data map.
	 */
	@Override
	protected Map<String, Object> referenceData(final HttpServletRequest request) {
		final Map<String, Object> extraParamMap = new HashMap<String, Object>();

		// Prepare the form bean for regular sign ins
		final Object checkoutSignInFormBean = this.checkoutSignInFormController.formBackingObject(request);
		extraParamMap.put(ContextIdNames.SIGN_IN_FORM_BEAN, checkoutSignInFormBean);
		final String beanKey = checkoutSignInFormBean.getClass().getName() + ".FORM." + checkoutSignInFormController.getCommandName();
		request.getSession().setAttribute(beanKey, checkoutSignInFormBean);

		return extraParamMap;
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
	 * Set the anonymous sign in controller that will handle anonymous sign-ins.
	 * 
	 * @param checkoutSignInFormController the controller that will handle anonymous sign-ins.
	 */
	public void setCheckoutSignInFormController(final SignInFormControllerImpl checkoutSignInFormController) {
		this.checkoutSignInFormController = checkoutSignInFormController;
	}

	/**
	 * Setters for {@link WebCustomerSessionService}.
	 * 
	 * @param webCustomerSessionService {@link WebCustomerSessionService}.
	 */
	public void setWebCustomerSessionService(final WebCustomerSessionService webCustomerSessionService) {
		this.webCustomerSessionService = webCustomerSessionService;
	}
}
