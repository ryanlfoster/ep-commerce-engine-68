package com.elasticpath.domain.geoip.provider.handler;


import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import com.elasticpath.domain.geoip.location.GeoIpLocation;
import com.elasticpath.domain.geoip.location.impl.GeoIpLocationImpl;
import com.quova.webservices.ondemand.geopoint.v1.IpInfo;

/**
 * 
 */
public final class QuovaGeoIpLocationHandler {
	
	private static final Logger LOG = Logger.getLogger(QuovaGeoIpLocationHandler.class);

	private static  QuovaGeoIpLocationHandler instance = null;
	
	private QuovaGeoIpLocationHandler() {
		
	}
	
	/**
	 * Returns instance of {@link QuovaGeoIpLocationHandler}.
	 *
	 * @return  instance of {@link QuovaGeoIpLocationHandler}
	 */
	public static QuovaGeoIpLocationHandler getInstance() {
		synchronized (QuovaGeoIpLocationHandler.class) {
		if (null == instance) {
				instance = new QuovaGeoIpLocationHandler();
			}
		}
		return instance;
	}
	/**
	 * Resolves {@link GeoIpLocation} from {@link IpInfo}.
	 * 
	 * @param ipInfo {@link IpInfo} class to be used in convertion.
	 * @return {@link GeoIpLocation} class.
	 */
	public GeoIpLocation resolveLocation(final IpInfo ipInfo) {
		GeoIpLocation geoLocation = new GeoIpLocationImpl();
		if (null == ipInfo) {
			return geoLocation;
		}
		if (null != ipInfo.getLocation()) {
			fillContinentInformation(ipInfo, geoLocation);
			fillCountryInformation(ipInfo, geoLocation);
			fillStateInformation(ipInfo, geoLocation);
			fillCityInformation(ipInfo, geoLocation);
		}
		fillNetworkInformation(ipInfo, geoLocation);
		return geoLocation;
	}

	private void fillNetworkInformation(final IpInfo ipInfo,
			final GeoIpLocation geoLocation) {
		if (null != ipInfo.getNetwork()) {
			String ipRoutingType = ipInfo.getNetwork().getIPRoutingType();
			geoLocation.setIpRoutingType(ipRoutingType);
			String connectionType = ipInfo.getNetwork().getConnection();
			geoLocation.setConnectionType(connectionType);
		}
	}

	private void fillContinentInformation(final IpInfo ipInfo,
			final GeoIpLocation geoLocation) {
		if (null != ipInfo.getLocation().getContinent()) {
			String continent = ipInfo.getLocation().getContinent().getName();
			geoLocation.setContinent(continent);
		}
	}

	private void fillCountryInformation(final IpInfo ipInfo,
			final GeoIpLocation geoLocation) {
		if (null != ipInfo.getLocation().getCountry()) {
			String countryCode = ipInfo.getLocation().getCountry().getName();
			geoLocation.setCountryCode(countryCode);
		}
	}

	private void fillStateInformation(final IpInfo ipInfo,
			final GeoIpLocation geoLocation) {
		if (null != ipInfo.getLocation().getState()) {
			String state = ipInfo.getLocation().getState().getName();
			geoLocation.setState(state);
		}
	}

	private  void fillCityInformation(final IpInfo ipInfo,
			final GeoIpLocation geoLocation) {
		if (null != ipInfo.getLocation().getCity()) {
			String city = ipInfo.getLocation().getCity().getName();
			geoLocation.setCity(city);
			String areaCode = ipInfo.getLocation().getCity().getPostalCode();
			geoLocation.setZipCode(areaCode);
			String timeZone = ipInfo.getLocation().getCity().getTimeZone();
			if (StringUtils.isNotBlank(timeZone)) {
				try {
					geoLocation.setGmtTimeZone(Float.valueOf(timeZone));
				} catch (NumberFormatException e) {
					LOG.warn("Unexpected non float value for time zone : " + timeZone);
				}
			}
		}
	}

}
