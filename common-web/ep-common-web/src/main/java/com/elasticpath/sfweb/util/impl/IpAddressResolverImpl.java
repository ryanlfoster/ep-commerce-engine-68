/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.sfweb.util.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.sfweb.util.IpAddressResolver;

/**
 * Obtain remote client ip address handler implementation.
 * Application server can be behind of proxy or load balancer or can be used stand alone.
 * For obtain remote ip address need to support not RFC feature X-Forwarded-For (See http://en.wikipedia.org/wiki/X-Forwarded-For 
 * for more detail). Different proxy can have different header name for  X-Forwarded-For feature, so httpp header name shall 
 * be explicitly configured.
 */
public class IpAddressResolverImpl implements IpAddressResolver {
	
	private static final String IP_ADDRESS_DELIMITER = ",";
	
	private String forwardedHeaderName;

	/**
	 * 
	 * Get the Internet Protocol (IP) address of the client. The result not depends from proxy / application server configuration.
	 * @param httpServletRequest - the http servlet request. 
	 * @return a String containing the IP address of the client that sent the request.
	 *  
	 */
	public String getRemoteAddr(final HttpServletRequest httpServletRequest) {
		String clientIpAddress = httpServletRequest.getRemoteAddr();		
		if (!StringUtils.isBlank(forwardedHeaderName)) {
			final String xForwardHeaderValue = httpServletRequest.getHeader(forwardedHeaderName);
			if (!StringUtils.isBlank(xForwardHeaderValue)) {
				clientIpAddress = getClientIpAddr(xForwardHeaderValue);
			}
		}
		return clientIpAddress;
	}

	/**
	 * Get the client ip adders. Typical value for header X-Forwarded-For: client1, proxy1, proxy2
	 * @param xForwardHeaderValue value of x-forward-for header
	 * @return client ip address from header or null if it can be resolved via header
	 */
	String getClientIpAddr(final String xForwardHeaderValue) {
		String clientIpAddress = null;
		String[] ipAddresses = xForwardHeaderValue.split(IP_ADDRESS_DELIMITER);
		if (ipAddresses.length > 0) {
			clientIpAddress = ipAddresses[0].trim();
		}
		return clientIpAddress;
	}

	/**
	 * Set the X-Forwarded-For header name.
	 * @param forwardedHeaderName X-Forwarded-For header name.
	 */	
	public void setForwardedHeaderName(final String forwardedHeaderName) {
		this.forwardedHeaderName = forwardedHeaderName;
	}



}
