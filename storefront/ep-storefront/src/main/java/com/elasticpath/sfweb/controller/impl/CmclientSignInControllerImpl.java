package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.sfweb.service.WebCustomerSessionService;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestResponseFacade;
import com.elasticpath.sfweb.util.CookieHandler;

/**
 * The Spring MVC controller for RCP CM client sign in.
 */
public class CmclientSignInControllerImpl extends AbstractEpControllerImpl {

	private static final Logger LOG = Logger.getLogger(LocaleControllerImpl.class);

	private WebCustomerSessionService webCustomerSessionService;

	private CmUserService cmUserService;

	private CustomerService customerService;

	private CookieHandler cookieHandler;

	private String failureView;

	private String successView;

	private String logoutView;

	/**
	 * Gets the success view if log in successfully.
	 * 
	 * @return the url to the success view
	 */
	public String getSuccessView() {
		return successView;
	}

	/**
	 * Sets the success view.
	 * 
	 * @param successView the url to the success view
	 */
	public void setSuccessView(final String successView) {
		this.successView = successView;
	}

	/**
	 * Gets the cm user log in fail view.
	 * 
	 * @return String the url to the fail view
	 */
	public String getFailureView() {
		return failureView;
	}

	/**
	 * Sets the cm user log in fail view.
	 * 
	 * @param failureView the url to the fail view
	 */
	public void setFailureView(final String failureView) {
		this.failureView = failureView;
	}

	
	// ---- DOChandleRequestInternal
	/**
	 * Return the ModelAndView for the configured static view page.
	 * 
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		LOG.debug("entering 'handleRequestInternal' method...");

		// if csr tries to access the store front through menu or tool bar item, then make sure any previously created
		// http session is erased first by redirecting to the logout page.
		if (ServletRequestUtils.getStringParameter(request, WebConstants.CREATE_USER) != null) {
			return new ModelAndView(logoutView);
		}

		// Gets the validation parameters in the url
		final String cmUserID = ServletRequestUtils.getStringParameter(request, WebConstants.CMUSER_ID);
		final String cmUserPassword = ServletRequestUtils.getStringParameter(request, WebConstants.CMUSER_PASSWORD);
		final String customerUID = ServletRequestUtils.getStringParameter(request, WebConstants.CUSTOMER_UID);

		// validate the cmuser using password
		final CmUser cmUser = cmUserService.findByUserName(cmUserID);
		final String dbPassword = cmUser.getPassword();

		// if password does not match
		if (!dbPassword.equals(cmUserPassword) || !cmUser.isEnabled()) {
			return new ModelAndView(failureView);
		}

		// save the cmuser's uidpk to shopping cart
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		shoppingCart.setCmUserUID(cmUser.getUidPk());

		// logs the customer in
		final Customer csrCustomer = customerService.findByGuid(customerUID);
		final UsernamePasswordAuthenticationToken authResult = new UsernamePasswordAuthenticationToken(csrCustomer, csrCustomer
				.getClearTextPassword(), csrCustomer.getAuthorities());
		authResult.setDetails(new WebAuthenticationDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authResult);

		final HttpServletFacadeFactory facadeFactory = getBean("httpServletFacadeFactory");
		final HttpServletRequestResponseFacade requestResponseFacade = facadeFactory.createRequestResponseFacade(request, response);

		webCustomerSessionService.handleCustomerSignIn(requestResponseFacade, csrCustomer);

		// go to index.ep
		return new ModelAndView(successView);
	}
	// ---- DOChandleRequestInternal

	/**
	 * Gets the CmUser service.
	 * 
	 * @return CmUserService
	 */
	public CmUserService getCmUserService() {
		return cmUserService;
	}

	/**
	 * Sets the CmUser service.
	 * 
	 * @param cmUserService the CmUser service
	 */
	public void setCmUserService(final CmUserService cmUserService) {
		this.cmUserService = cmUserService;
	}

	/**
	 * Gets the customer service.
	 * 
	 * @return CustomerService
	 */
	public CustomerService getCustomerService() {
		return customerService;
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
	 * Gets the web customer session service.
	 * 
	 * @return WebCustomerSessionService the service
	 */
	public WebCustomerSessionService getWebCustomerSessionService() {
		return webCustomerSessionService;
	}

	/**
	 * Sets the web customer session service.
	 * 
	 * @param webCustomerSessionService the session service
	 */
	public void setWebCustomerSessionService(final WebCustomerSessionService webCustomerSessionService) {
		this.webCustomerSessionService = webCustomerSessionService;
	}

	/**
	 * Gets the store cookie handler.
	 * 
	 * @return the cookie handler instance
	 */
	protected CookieHandler getCookieHandler() {
		return cookieHandler;
	}

	/**
	 * Sets the store cookie handler.
	 * 
	 * @param cookieHandler the instance to set
	 */
	public void setCookieHandler(final CookieHandler cookieHandler) {
		this.cookieHandler = cookieHandler;
	}

	protected String getLogoutView() {
		return logoutView;
	}

	public void setLogoutView(final String logoutView) {
		this.logoutView = logoutView;
	}
}
