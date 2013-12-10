package com.elasticpath.service.geoip;

import com.elasticpath.domain.geoip.location.GeoIpLocation;
import com.elasticpath.domain.geoip.provider.GeoIpProvider;

/**
 * Interface for service that resolves location information using ip address provided.
 */
public interface GeoIpService {

	/**
	 * Setter of the property <tt>provider</tt>.
	 * 
	 * @param provider The provider to set.
	 */
	void setProvider(final GeoIpProvider provider);

	/**
	 * 
	* Resolves IP into location data. 
	* @return resolved location
	* @param ipAddress  ip address to be resolved.
	*/
	GeoIpLocation resolveIpAddress(final String ipAddress);

}
