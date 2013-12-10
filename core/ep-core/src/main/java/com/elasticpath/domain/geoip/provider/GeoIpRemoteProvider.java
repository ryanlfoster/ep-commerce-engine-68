package com.elasticpath.domain.geoip.provider;

/**
 * Remote GeoIP provider interface.
 * Use this interface for providers that provide their services using Web services.
 */
public interface GeoIpRemoteProvider extends GeoIpProvider {

	/**
	 * Sets end point for remote call.
	 *
	 * @param endpoint -  end point for remote call
	 */
	void setEndpoint(final String endpoint);

	/**
	 * Sets user name.
	 *
	 * @param username - user name.
	 */
	void setUsername(final String username);
	
	/** 
	 *	Sets password. 
	 *
	 * @param password - password.
	 */
	void setPassword(final String password);
	
}
