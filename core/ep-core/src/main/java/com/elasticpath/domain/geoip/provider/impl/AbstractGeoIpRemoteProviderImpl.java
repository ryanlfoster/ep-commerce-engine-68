package com.elasticpath.domain.geoip.provider.impl;

import javax.xml.ws.BindingProvider;

import com.elasticpath.domain.geoip.provider.GeoIpRemoteProvider;

/**
 * Abstract class that encapsulates basic functionality of remote provider class. 
 */
public abstract class AbstractGeoIpRemoteProviderImpl extends AbstractGeoIpProviderImpl implements GeoIpRemoteProvider {
	
	@Override
	public void setEndpoint(final String endpoint) {
		putPropertyIntoRequestContext(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
	}

	@Override
	public void setUsername(final String username) {
		putPropertyIntoRequestContext(BindingProvider.USERNAME_PROPERTY, username);
	}
	
	@Override
	public void setPassword(final String password) {
		putPropertyIntoRequestContext(BindingProvider.PASSWORD_PROPERTY, password);
		
	}

	/**
	 * Puts provided property value into request context. 
	 * Implementation will depends on provider.
	 *
	 * @param property - property which value will be set.
	 * @param value - value of the property to be set.
	 */
	protected abstract void putPropertyIntoRequestContext(final String property, final String value);
}

