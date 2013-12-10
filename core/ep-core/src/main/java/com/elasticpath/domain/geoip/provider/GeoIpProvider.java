package com.elasticpath.domain.geoip.provider;

import com.elasticpath.domain.geoip.location.GeoIpLocation;

/**
 * GeoIp provider interface .
 */
public interface GeoIpProvider {

	/**
	 * @return  Returns the name.
	 */
	String getName();

	/**
	 * Setter of the property <tt>name</tt>.
	 * @param name  The name to set.
	 */
	void setName(String name);

	/**
	 * @return  Returns the description.
	 */
	String getDescription();

	/**
	 * Setter of the property <tt>description</tt>.
	 * @param description  The description to set.
	 */
	void setDescription(String description);

		
		/**
		 * resolves location from specified ip.
		 * @return resolved location.
		 * @param ipAddress  - ip to be resolved
		 */
	GeoIpLocation resolveIPAddress(String ipAddress);
		

}
