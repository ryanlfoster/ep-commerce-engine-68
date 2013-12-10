package com.elasticpath.domain.geoip.provider.impl;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import com.elasticpath.domain.geoip.location.GeoIpLocation;
import com.elasticpath.domain.geoip.provider.handler.QuovaGeoIpLocationHandler;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.quova.webservices.ondemand.geopoint.v1.GeoPoint;
import com.quova.webservices.ondemand.geopoint.v1.GeoPointSoap;
import com.quova.webservices.ondemand.geopoint.v1.IpInfo;
/**
 * Implementation of provider to resolve ip address using QUOVA Geo provider.  
 */
public class QuovaGeoIpProviderImpl extends AbstractGeoIpRemoteProviderImpl {

	private final GeoPointSoap geoPointPort;

	private SettingsReader settingsReader;
	
	/**
	 * Constructor.
	 */
	public QuovaGeoIpProviderImpl() {
		GeoPoint geoPointService = new GeoPoint();
		geoPointPort = geoPointService.getGeoPointSoap();
	}

	@Override
	public GeoIpLocation resolveIPAddress(final String ipAddress) {
		IpInfo ipInfo = geoPointPort.getIpInfo(ipAddress);
		return QuovaGeoIpLocationHandler.getInstance().resolveLocation(ipInfo);
	}

	@Override
	protected void putPropertyIntoRequestContext(final String property, final String value) {
		BindingProvider bindinProvider = (BindingProvider) geoPointPort;
		SettingValue settingValue = this.settingsReader.getSettingValue(value);
		bindinProvider.getRequestContext().put(property, settingValue.getValue());
	}

	/**
	 * Set the settings reader for getting username. password, service url. 
	 * @param settingsReader the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
	
	/**
	 * Set integer parameter values into JAXWS binding provide context. 
	 *
	 * @param properties map of values
	 */
	public void setAdditionalIntegerRequestProperties(final Map<String, Integer> properties) {
		BindingProvider bindingProvider = (BindingProvider) geoPointPort;

		for (String key : properties.keySet()) {
			bindingProvider.getRequestContext().put(key, properties.get(key));
		}
	}

}
