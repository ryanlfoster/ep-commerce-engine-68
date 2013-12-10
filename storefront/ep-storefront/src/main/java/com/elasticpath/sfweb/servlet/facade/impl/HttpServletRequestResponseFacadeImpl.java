package com.elasticpath.sfweb.servlet.facade.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestResponseFacade;
import com.elasticpath.sfweb.util.CookieHandler;
import com.elasticpath.sfweb.util.IpAddressResolver;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.web.security.EsapiServletUtils;

/**
 * Facade class to limit the access to the HttpServletRequest when the request must be passed outside of a filter. <br>
 * Also provides method for making accessing data contained within the request easier.
 */
public class HttpServletRequestResponseFacadeImpl implements HttpServletRequestResponseFacade {

	private final SfRequestHelper requestHelper;

	private final IpAddressResolver ipResolver;
	
	private final CookieHandler cookieHandler;

	private final HttpServletRequest request;

	private final HttpServletResponse response;
    
	/**
	 * Package scoped constructor to produce a <code>HttpServletRequestFacade</code>/<code>HttpServletResponseFacade</code> object.
	 *
	 * @param requestHelper the request helper.
	 * @param ipResolver the ip resolver.
	 * @param cookieHandler the cookie handler.
	 * @param request the http servlet request.
	 * @param response the http servlet response
	 */
	HttpServletRequestResponseFacadeImpl(final SfRequestHelper requestHelper, final IpAddressResolver ipResolver, 
			final CookieHandler cookieHandler, final HttpServletRequest request, final HttpServletResponse response) {
		this.requestHelper = requestHelper;
		this.ipResolver = ipResolver;
		this.cookieHandler = cookieHandler;
		this.request = request;
		this.response = response;
	}

	@Override
	public boolean isNewSession() {
		return request.getSession().isNew();
	}

	@Override
	public CustomerSession getPersistedCustomerSession() {
		return requestHelper.getPersistedCustomerSession(request);
	}
	
	@Override
	public CustomerSession getCustomerSession() {
		return requestHelper.getCustomerSession(request);
	}
	
	@Override
	public void setCustomerSession(final CustomerSession customerSession) {
		requestHelper.setCustomerSession(request, customerSession);
	}

	@Override
	public String getServletPath() {
		return request.getServletPath();
	}

	@Override
	public String getQueryString() {
		return request.getQueryString();
	}

	@Override
	public StringBuffer getRequestURL() {
		return request.getRequestURL();
	}

	@Override
	public String getHeader(final String name) {
		return request.getHeader(name);
	}

	@Override
	public String getParameterOrAttributeValue(final String name, final String defaultValue) {
		return requestHelper.getStringParameterOrAttribute(request, name, defaultValue);
	}

 	/**
 	 * Gets the given parameter value from the request.  Returns null if the parameter was not found.
 	 *
 	 * @param parameterName the name of the parameter top retrieve
 	 * @return The parameter's value, or null if the parameter is not on the request
 	 */
 	@Override
 	public String getParameterValue(final String parameterName) {
 		if (!EsapiServletUtils.hasParameter(request, parameterName)) {
 			return null;
 		}
 
 		return request.getParameter(parameterName);
  	}
	
	@Override
	public String getRemoteAddress() {
		return ipResolver.getRemoteAddr(request);
	}

	@Override
	public Store getStore() {
		return requestHelper.getStoreConfig().getStore();
	}

	@Override
	public String getStoreCode() {
		return requestHelper.getStoreConfig().getStoreCode();
	}

    @Override
	public void writeCookie(final String key, final String value) {
        cookieHandler.addCookie(response, key, value);
    }
}
