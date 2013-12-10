package com.elasticpath.geoip.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.geoip.location.GeoIpLocation;
import com.elasticpath.domain.geoip.provider.impl.QuovaGeoIpProviderImpl;
import com.elasticpath.service.geoip.GeoIpService;
import com.elasticpath.service.geoip.impl.GeoIpServiceImpl;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * This test is used to perform a Quova ip info request. It uses a live quova server
 * and a quova test account details. This test is not to be used for junit task on this
 * project - it must only be used for development process.
 * To run the test comment out @Ignore annotation on the tests.
 */
@Ignore("This test is for development only, must not be executed in build process")
@SuppressWarnings({ "PMD.AvoidUsingHardCodedIP" })
public class QuovaTest {

	private static final String PASSWORD = "COMMERCE/SYSTEM/GEOIP/QUOVA/Password";
	private static final String USERNAME = "COMMERCE/SYSTEM/GEOIP/QUOVA/Username";
	private static final String ENDPOINT_URL = "COMMERCE/SYSTEM/GEOIP/QUOVA/onDemandEndpointUrl";

	private GeoIpService geoProviderService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * setUp is for Spike testing purpose only.
	 * Everything configured here  should be performed by Spring container in production environment.
	 */
	@Before
	public void setUp() {
		final SettingValue valueForURL = context.mock(SettingValue.class, "url");
		final SettingValue valueForUsername = context.mock(SettingValue.class, "username");
		final SettingValue valueForPassword = context.mock(SettingValue.class, "password");
		
		final SettingsReader reader = context.mock(SettingsReader.class);
		
		context.checking(new Expectations() { {
			allowing(valueForURL).getValue(); will(returnValue("https://webservices.quova.com/OnDemand/GeoPoint/v1/default.asmx"));
			allowing(valueForUsername).getValue(); will(returnValue("ws706049"));
			allowing(valueForPassword).getValue(); will(returnValue("dd10c1aa-9d2b-4d6c-95e1-34f471462176"));

			allowing(reader).getSettingValue(ENDPOINT_URL); will(returnValue(valueForURL));
			allowing(reader).getSettingValue(USERNAME); will(returnValue(valueForUsername));
			allowing(reader).getSettingValue(PASSWORD); will(returnValue(valueForPassword));
		} });
		
		QuovaGeoIpProviderImpl geoProvider = new QuovaGeoIpProviderImpl();
		geoProvider.setSettingsReader(reader);
		geoProvider.setUsername(USERNAME);
		geoProvider.setPassword(PASSWORD);
		geoProvider.setEndpoint(ENDPOINT_URL);
		geoProviderService = new GeoIpServiceImpl();
		geoProviderService.setProvider(geoProvider);
	}

	/**
	 * Tests QUOVA geo ip resolver.
	 */
	@Test
	public void testCorrectGetIPInfo() {
		GeoIpLocation location = geoProviderService.resolveIpAddress("65.61.233.74");
		assertNotNull(location);
		assertEquals("north america", location.getContinent());
		assertEquals("v5g 4w9", location.getZipCode());
		assertEquals("burnaby", location.getCity());
	}
	
}
