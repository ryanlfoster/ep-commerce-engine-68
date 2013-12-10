package com.elasticpath.sfweb.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.sfweb.util.CookieHandler;

/**
 * Removes the customer session's identifier cookie.
 */
public class LogoutCustomerSessionHandler implements LogoutHandler {

	private CookieHandler cookieHandler;

	/**
	 * Removes the customer session's identifier cookie.
	 * @param request the request
	 * @param response the response
	 * @param authentication the authentication object
	 */
	public void logout(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
		//Remove the cookie so the customer won't be remembered
		cookieHandler.removeCookie(response, WebConstants.CUSTOMER_SESSION_GUID);
	}

	/**
	 * Gets the cookie handler.
	 * 
	 * @return the cookie handler instance
	 */
	protected CookieHandler getCookieHandler() {
		return cookieHandler;
	}

	/**
	 * Sets the cookie handler.
	 * 
	 * @param cookieHandler the instance to set
	 */
	public void setCookieHandler(final CookieHandler cookieHandler) {
		this.cookieHandler = cookieHandler;
	}

}
