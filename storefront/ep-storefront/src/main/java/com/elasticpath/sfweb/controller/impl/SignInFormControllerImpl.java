package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.sfweb.EpSfWebException;
import com.elasticpath.sfweb.formbean.SignInFormBean;

/**
 * The Spring MVC controller for customer sign-in page that is presented during a checkout. </br>
 * This controller works with a page that has three forms on it:
 * <ol>
 * <li>Regular sign-in form that is processed by this controller</li>
 * <li>Anonymous sign-in form processed by the AnonymousSignInFormController</li>
 * <li>A form for creating a new customer account that is processed by the CreateAccountFormController</li>
 * </ol>
 * Note that the form-backing beans for the anonymous sign in and new account forms are set in the <code>referenceData()</code> method.
 */
public class SignInFormControllerImpl extends AbstractEpFormController {

	private static final Logger LOG = Logger.getLogger(SignInFormControllerImpl.class);

	/** Form controller for anonymously logging in. */
	private AnonymousSignInFormControllerImpl anonymousSignInFormController;

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

		LOG.debug("SignInFormController: entering 'onSubmit' method...");

		ModelAndView nextView = new ModelAndView(getSuccessView());

		final SignInFormBean signInFormBean = (SignInFormBean) command;

		final Customer customer = getBean(ContextIdNames.CUSTOMER);
		customer.setAnonymous(false);
		customer.setEmail(signInFormBean.getJ_username());
		customer.setStoreCode(getRequestHelper().getStoreConfig().getStoreCode());

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
		final SignInFormBean signInFormBean = getBean(ContextIdNames.SIGN_IN_FORM_BEAN);
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		
		Shopper shopperFromCustomerSesion = customerSession.getShopper();
		Customer customerFromShopper = shopperFromCustomerSesion.getCustomer();
		
		signInFormBean.setJ_username(customerFromShopper.getEmail());
		return signInFormBean;
	}

	/**
	 * Prepare the reference data map.
	 * 
	 * @param request the current request.
	 * @return reference data map.
	 * @throws Exception if error occurs
	 */
	@Override
	protected Map<String, Object> referenceData(final HttpServletRequest request) throws Exception {
		final Map<String, Object> extraParamMap = new HashMap<String, Object>();

		// Spring Security
		if (request.getParameter(WebConstants.LOGIN_FAILED) != null && request.getParameter(WebConstants.LOGIN_FAILED).equals("1")) {
			extraParamMap.put(WebConstants.LOGIN_FAILED, "1");
		}

		// Prepare form bean for anonymous sign ins
		final Object anonymousSignInFormControllerBean = this.anonymousSignInFormController.formBackingObject(request);
		extraParamMap.put(ContextIdNames.ANONYMOUS_SIGN_IN_FORM_BEAN, anonymousSignInFormControllerBean);
		final String beanKey = anonymousSignInFormControllerBean.getClass().getName() + ".FORM." + anonymousSignInFormController.getCommandName();
		request.getSession().setAttribute(beanKey, anonymousSignInFormControllerBean);

		return extraParamMap;
	}

	/**
	 * Set the anonymous sign in controller that will handle anonymous sign-ins.
	 * 
	 * @param anonymousSignInFormController the controller that will handle anonymous sign-ins.
	 */
	public void setAnonymousSignInFormController(final AnonymousSignInFormControllerImpl anonymousSignInFormController) {
		this.anonymousSignInFormController = anonymousSignInFormController;
	}

}
