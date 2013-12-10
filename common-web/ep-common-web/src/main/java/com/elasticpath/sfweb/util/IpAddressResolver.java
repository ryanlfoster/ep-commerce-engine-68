/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.sfweb.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Obtain remote client ip address handler. 
 */
public interface IpAddressResolver {	

	/**
	 * 
	 * Get the Internet Protocol (IP) address of the client. The result not depends from proxy / application server configuration.
	 * @param httpServletRequest - the http servlet request.
	 * @return a String containing the IP address of the client that sent the request.
	 *  
	 */
	String getRemoteAddr(HttpServletRequest httpServletRequest);

}
