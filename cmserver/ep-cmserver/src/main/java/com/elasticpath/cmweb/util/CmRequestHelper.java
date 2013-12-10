/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.util;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.domain.cmuser.CmUserSession;
import com.elasticpath.web.util.RequestHelper;

/**
 * <code>RequestHelper</code> represents a helper instance for http requests.
 */
public interface CmRequestHelper extends RequestHelper {

	/**
	 * Return a <code>ShoppingCart</code> instance if there is one stored in http session. Otherwise, create a new one and return it.
	 * 
	 * @param request the http request
	 * @return a <code>CmUserSession</code> instance
	 */
	CmUserSession getCmUserSession(final HttpServletRequest request);

	/**
	 * Sets a <code>CmUserSession</code> into http session.
	 * 
	 * @param request the http request
	 * @param cmUserSession the cm user session to save
	 */
	void setCmUserSession(final HttpServletRequest request, final CmUserSession cmUserSession);
	
}