package com.elasticpath.tools.sync.beanfactory.remote;

import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

/**
 * <p>
 * Enhances the Spring HttpInvokerProxyFactoryBean by allowing one to set the serverURL prefix needed to find all the remote beans.
 * </p>
 * <p>
 * Also overrides the method that gets the RequestExecutor so that it by default uses Spring's AuthenticationSimpleHttpInvokerRequestExecutor, thereby
 * allowing the BASIC auth credentials to be set in the HTTP headers automatically. Rather than require injecting this bean in every remote bean
 * definition, it was easier to simply inject it programatically.
 * </p>
 * <p>
 * This class is designed so that after the application context has been loaded, once can set the server's URL at runtime.
 * </p>
 */
public class EpHttpInvokerProxyFactoryBean extends HttpInvokerProxyFactoryBean {
	
	private HttpConnectionAuthentication httpConnectionAuthentication;
	
	/**
	 * {@inheritDoc} This implementation prepends the serverUrlPrefix set at runtime.
	 */
	@Override
	public String getServiceUrl() {
		return httpConnectionAuthentication.getConnectionUrl().concat(super.getServiceUrl());
	}

	/**
	 * {@inheritDoc} This implementation returns an instance of the Spring Security's AuthenticationSimpleHttpInvokerRequestExecutor.
	 * 
	 * @return an instance of Spring Security's AuthenticationSimpleHttpInvokerRequestExecutor
	 */
	@Override
	public HttpInvokerRequestExecutor getHttpInvokerRequestExecutor() {
		return new EpAuthenticationSimpleHttpInvokerRequestExecutor(httpConnectionAuthentication.getAuthentication());
	}

	/**
	 *
	 * @return the httpConnectionAuthentication
	 */
	public HttpConnectionAuthentication getHttpConnectionAuthentication() {
		return httpConnectionAuthentication;
	}

	/**
	 *
	 * @param httpConnectionAuthentication the httpConnectionAuthentication to set
	 */
	public void setHttpConnectionAuthentication(final HttpConnectionAuthentication httpConnectionAuthentication) {
		this.httpConnectionAuthentication = httpConnectionAuthentication;
	}
}
