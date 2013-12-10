package com.elasticpath.sfweb.servlet.facade.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestResponseFacade;
import com.elasticpath.sfweb.util.CookieHandler;
import com.elasticpath.sfweb.util.IpAddressResolver;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 *  Reference implementation of a <code>HttpServletFacadeFactory</code>. 
 */
public class HttpServletFacadeFactoryImpl implements HttpServletFacadeFactory {
	
	private final SfRequestHelper requestHelper;

	private final IpAddressResolver ipResolver;
	
	private final CookieHandler cookieHandler;

	/**
	 *
	 * @param requestHelper the request helper.
	 * @param ipResolver the ip resolver.
	 * @param cookieHandler the cookie handler.
	 */
	public HttpServletFacadeFactoryImpl(final SfRequestHelper requestHelper, final IpAddressResolver ipResolver, 
			final CookieHandler cookieHandler) {
		this.requestHelper = requestHelper;
		this.ipResolver = ipResolver;
		this.cookieHandler = cookieHandler;
	}

	@Override
	public HttpServletRequestFacade createRequestFacade(final HttpServletRequest request) {
		return new HttpServletRequestResponseFacadeImpl(requestHelper, ipResolver, cookieHandler, request, null);
	}

	@Override
	public HttpServletRequestResponseFacade createRequestResponseFacade(final HttpServletRequest request, final HttpServletResponse response) {
		return new HttpServletRequestResponseFacadeImpl(requestHelper, ipResolver, cookieHandler, request, response);
	}
}
