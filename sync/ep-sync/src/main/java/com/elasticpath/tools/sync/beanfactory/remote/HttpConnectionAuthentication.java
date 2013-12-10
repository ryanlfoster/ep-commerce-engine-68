/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.beanfactory.remote;

import org.springframework.security.core.Authentication;

/**
 * A holder of the authentication object and the 
 * URL of the established HTTP connection between the sync tool 
 * and the source/target system.
 */
public class HttpConnectionAuthentication {
	
	private String connectionUrl;
	
	private Authentication authentication;
	
	/**
	 *
	 * @return the connectionUrl
	 */
	public String getConnectionUrl() {
		return connectionUrl;
	}
	
	/**
	 *
	 * @param connectionUrl the connectionUrl to set
	 */
	public void setConnectionUrl(final String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	
	/**
	 *
	 * @return the authentication
	 */
	public Authentication getAuthentication() {
		return authentication;
	}
	
	/**
	 *
	 * @param authentication the authentication to set
	 */
	public void setAuthentication(final Authentication authentication) {
		this.authentication = authentication;
	}
	
}
