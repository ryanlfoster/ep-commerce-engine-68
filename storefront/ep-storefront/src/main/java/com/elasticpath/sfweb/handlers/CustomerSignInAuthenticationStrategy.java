package com.elasticpath.sfweb.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.sfweb.service.WebCustomerSessionService;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestResponseFacade;

/**
 * An implementation of {@link SessionAuthenticationStrategy} that uses {@link WebCustomerSessionService} to handle customer
 * sign-in. Doing it as part of this strategy ensures we maintain the existing behaviour of being able to fall back 
 * from https to http without losing the session or the cookie to protect from Session Fixation attacks. Spring won't do that by default.
 */
public class CustomerSignInAuthenticationStrategy implements
		SessionAuthenticationStrategy {

	private WebCustomerSessionService webCustomerSessionService;
	private HttpServletFacadeFactory httpServletFacadeFactory;

	@Override
	public void onAuthentication(final Authentication authentication,
			final HttpServletRequest request, final HttpServletResponse response) {
		HttpServletRequestResponseFacade requestResponseFacade = httpServletFacadeFactory.createRequestResponseFacade(request, response);
		webCustomerSessionService.handleCustomerSignIn(requestResponseFacade, (Customer) authentication.getPrincipal());
	}

	public void setWebCustomerSessionService(
			final WebCustomerSessionService webCustomerSessionService) {
		this.webCustomerSessionService = webCustomerSessionService;
	}

	public void setHttpServletFacadeFactory(final HttpServletFacadeFactory httpServletFacadeFactory) {
		this.httpServletFacadeFactory = httpServletFacadeFactory;
	}

	protected HttpServletFacadeFactory getHttpServletFacadeFactory() {
		return httpServletFacadeFactory;
	}

}