/**
 * 
 */
package com.elasticpath.domain.geoip.provider.impl;

import org.junit.Test;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.elasticpath.domain.geoip.location.GeoIpLocation;

/**
 * Test for demo geo ip provider impl.
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public class NullGeoIpProviderTest extends AbstractDependencyInjectionSpringContextTests {

	/**
	 * Get the location of the spring configuration.
	 *
	 * @return the spring configuration file for this test
	 */
	@Override
	protected String[] getConfigLocations() {
		return new String[] { "classpath:/testNullGeoIp.xml" };
	}

	private NullGeoIpProviderImpl nullGeoIpProvider;
	
	/** The data below is configured in testNullGeoIp.xml config file. Don't change it. **/
	private static final String IP_ADDRESS_1 = "65.79.32.12";
	private static final String IP_ADDRESS_2 = "12.68.78.1";
	private static final String IP_ADDRESS_3 = "127.23.56.111";
	private static final String IP_ADDRESS_NOT_EXISTS = "10.24.36.32";
	
	private static final String GEO_LOCATION_1_CITY = "Kiev";
	private static final Float GEO_LOCATION_1_TIMEZONE = 2.0f;
	
	private static final String GEO_LOCATION_2_CARRIER_NAME = "coaxial";
	private static final String GEO_LOCATION_2_TOP_LEVEL_DOMAIN = "ru";
	
	private static final String GEO_LOCATION_3_PHONE_NUMBER = "134869786";
	private static final String GEO_LOCATION_3_CONNECTION_SPEED = "10 Mb\\s";
	
	/**
	 * Sets demo geoIp provider. 
	 * 
	 * @param nullGeoIpProvider demo geoIp provider
	 */
	public void setNullGeoIpProvider(final NullGeoIpProviderImpl nullGeoIpProvider) {
		this.nullGeoIpProvider = nullGeoIpProvider;
	}

	/**
	 * Test method for {@link com.elasticpath.domain.geoip.provider.impl.NullGeoIpProviderImpl#resolveIP(java.lang.String)}.
	 */
	@Test
	public void testResolveIP() {
		GeoIpLocation location1 = nullGeoIpProvider.resolveIPAddress(IP_ADDRESS_1);
		GeoIpLocation location2 = nullGeoIpProvider.resolveIPAddress(IP_ADDRESS_2);
		GeoIpLocation location3 = nullGeoIpProvider.resolveIPAddress(IP_ADDRESS_3);
		
		assertNotNull(location1);
		assertNotNull(location2);
		assertNotNull(location3);
		
		assertEquals(location1.getCity(), GEO_LOCATION_1_CITY);
		assertEquals(location1.getGmtTimeZone(), GEO_LOCATION_1_TIMEZONE);
		
		assertEquals(location2.getCarrierName(), GEO_LOCATION_2_CARRIER_NAME);
		assertEquals(location2.getTopLevelDomain(), GEO_LOCATION_2_TOP_LEVEL_DOMAIN);
		
		assertEquals(location3.getPhoneNumber(), GEO_LOCATION_3_PHONE_NUMBER);
		assertEquals(location3.getConnectionSpeed(), GEO_LOCATION_3_CONNECTION_SPEED);
		
		GeoIpLocation location4 = nullGeoIpProvider.resolveIPAddress(IP_ADDRESS_NOT_EXISTS);
		assertNull(location4);
	}

}
