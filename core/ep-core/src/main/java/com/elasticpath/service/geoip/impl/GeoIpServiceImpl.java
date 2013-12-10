package com.elasticpath.service.geoip.impl;

import com.elasticpath.domain.geoip.location.GeoIpLocation;
import com.elasticpath.domain.geoip.provider.GeoIpProvider;
import com.elasticpath.service.geoip.GeoIpService;

/**
 * Class is a service that resolves location information using ip address provided. 
 */
public class GeoIpServiceImpl implements GeoIpService {

	private GeoIpProvider geoProvider;

	@Override
	public GeoIpLocation resolveIpAddress(final String ipAddress) {
		return  geoProvider.resolveIPAddress(ipAddress);
	}

	@Override
	public void setProvider(final GeoIpProvider geoProvider) {
		this.geoProvider = geoProvider;
	}

}
