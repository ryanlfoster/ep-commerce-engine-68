package com.elasticpath.domain.geoip.provider.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.geoip.location.GeoIpLocation;
import com.elasticpath.domain.geoip.provider.DomainNameResolver;

/**
 * Demo provider with configurable example geo locations.
 */
public class NullGeoIpProviderImpl extends AbstractGeoIpProviderImpl {
	
	private DomainNameResolver domainNameResolver;

	private Map<String, GeoIpLocation> locations = new HashMap<String, GeoIpLocation>();

	/**
	 * Sets dummy {@link GeoIpLocation} data.
	 * 
	 * @param locations {@link Map} with dummy {@link GeoIpLocation}s 
	 */
	public void setLocations(final Map<String, GeoIpLocation> locations) {
		this.locations = locations;
	}

	@Override
	public GeoIpLocation resolveIPAddress(final String ipAddr) {
		GeoIpLocation geoIpLocation = locations.get(ipAddr);
		if (null != domainNameResolver && null != geoIpLocation) {
			Pair<String, String> firstSecondDomainNames = domainNameResolver.getFirstAndSecondLevelDomainName(ipAddr);
			if (null != firstSecondDomainNames) {
				geoIpLocation.setTopLevelDomain(firstSecondDomainNames.getFirst());
				geoIpLocation.setSecondLevelDomain(firstSecondDomainNames.getSecond());				
			}
		}
		return geoIpLocation; 
	}
	

	/**
	 * Get the domainNameResolutionEnabled flag, if it true customer can use GEOIP_FIRST_LEVEL_DOMAIN 
	 * and GEOIP_SECOND_LEVEL_DOMAIN tags. 
	 * @return instance of DomainNameResolver if GEOIP_FIRST_LEVEL_DOMAIN & GEOIP_SECOND_LEVEL_DOMAIN usage enabled
	 */
	public DomainNameResolver getDomainNameResolver() {
		return domainNameResolver;
	}


	/**
	 * 
	 * Set the domainNameResolver if  GEOIP_FIRST_LEVEL_DOMAIN & GEOIP_SECOND_LEVEL_DOMAIN used. 
	 * Usage of this tags can impact performance, because to resolve name by ip address need interaction with DNS 
	 * and it be slow.
	 * 
	 * @param domainNameResolver instance of DomainNameResolver if GEOIP_FIRST_LEVEL_DOMAIN & GEOIP_SECOND_LEVEL_DOMAIN. Otherwise - null.
	 */
	public void setDomainNameResolver(final DomainNameResolver domainNameResolver) {
		this.domainNameResolver = domainNameResolver;
	}
	

}
