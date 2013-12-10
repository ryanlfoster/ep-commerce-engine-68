/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.sfweb.listeners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.geoip.location.GeoIpLocation;
import com.elasticpath.domain.geoip.location.impl.GeoIpLocationImpl;
import com.elasticpath.domain.geoip.provider.GeoIpProvider;
import com.elasticpath.service.geoip.GeoIpService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.impl.CachedSettingsReaderImpl;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.facade.impl.HttpServletFacadeFactoryImpl;
import com.elasticpath.sfweb.util.impl.IpAddressResolverImpl;
import com.elasticpath.tags.TagSet;

/**
 * Tests that GEO IP tagger is setting all the required tags as expected.
 */
public class GeoIpTaggerTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private TagSet tagSet;

	private MockHttpServletRequest request;
	private HttpServletRequestFacade requestFacade;

	private CustomerSession session;

	private GeoIpTagger listener;

	private IpAddressResolverImpl ipAddressResolver;

	private GeoIpService geoIpService;

	private static final String ZIP_OR_POST_CODE = "123";

	private static final String COUNTRY_CODE = "CA";

	private static final String STATE_OR_PROVINCE = "BC";

	private static final String CONNECTION_TYPE = "dialup";

	private static final String ROUTING_TYPE = "fixed";

	private static final String CITY = "Vancouver";
	
	private static final String FIRST_LEVEL_DOMAIN = "com";
	
	private static final String SECOND_LEVEL_DOMAIN = "elasticpath";
	
	private static final Float  GMT_TIME_ZONE = +7.5f;
	
	private static final String CONTINENT = "America";
	

	/**
	 * Setting up instances.
	 */
	@Before
	public void setUp() {
		tagSet = new TagSet();
		request = new MockHttpServletRequest();
		session = context.mock(CustomerSession.class);
		ipAddressResolver = new IpAddressResolverImpl();
		ipAddressResolver.setForwardedHeaderName("X-Forwarded-For");
		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(null, ipAddressResolver, null);
		requestFacade = httpServletFacadeFactory.createRequestFacade(request);
	}

	/**
	 * Tests that if the data object return all the data for an IP city the listener properly populates the tags.
	 */
	@Test
	public void testThatIfAllDataIsProvidedAllGeoIpTagsAreSet() {

		final SettingValue settings = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				allowing(settings).getBooleanValue();
				will(returnValue(Boolean.TRUE));

			}
		});
		
		SettingsReader settingsReader = new CachedSettingsReaderImpl() {
			@Override
			public SettingValue getSettingValue(final String path) {
				if ("COMMERCE/SYSTEM/GEOIP/enable".equals(path)) {
					return 	settings;
				}
				 return null;
			}
		};
		
		listener = new GeoIpTagger();
		
		listener.setSettingsReader(settingsReader);
		
		geoIpService = new GeoIpService() {
			
			public GeoIpLocation resolveIpAddress(final String ipAddress) {
				GeoIpLocation geoIpLocation = new GeoIpLocationImpl();
				geoIpLocation.setZipCode(ZIP_OR_POST_CODE);
				geoIpLocation.setCountryCode(COUNTRY_CODE);
				geoIpLocation.setState(STATE_OR_PROVINCE);
				geoIpLocation.setConnectionType(CONNECTION_TYPE);
				geoIpLocation.setIpRoutingType(ROUTING_TYPE);
				geoIpLocation.setCity(CITY);
				geoIpLocation.setTopLevelDomain(FIRST_LEVEL_DOMAIN);
				geoIpLocation.setSecondLevelDomain(SECOND_LEVEL_DOMAIN);
				geoIpLocation.setGmtTimeZone(GMT_TIME_ZONE);
				geoIpLocation.setContinent(CONTINENT);
				return geoIpLocation;
			}
			
			public void setProvider(final GeoIpProvider provider) {
				// Does nothing.
			}

		};

		listener.setGeoProviderService(geoIpService);

		context.checking(new Expectations() {
			{
				allowing(session).getCustomerTagSet();
				will(returnValue(tagSet));
			}
		});

		listener.execute(session, requestFacade);

		assertTag(ZIP_OR_POST_CODE, GeoIpTagger.GEOIP_ZIP_OR_POST_CODE, tagSet);
		assertTag(COUNTRY_CODE, GeoIpTagger.GEOIP_COUNTRY_CODE, tagSet);
		assertTag(STATE_OR_PROVINCE, GeoIpTagger.GEOIP_STATE_OR_PROVINCE, tagSet);
		assertTag(CONNECTION_TYPE, GeoIpTagger.GEOIP_CONNECTION_TYPE, tagSet);
		assertTag(ROUTING_TYPE, GeoIpTagger.GEOIP_ROUTING_TYPE, tagSet);
		assertTag(CITY, GeoIpTagger.GEOIP_CITY, tagSet);
		assertTag(FIRST_LEVEL_DOMAIN, GeoIpTagger.GEOIP_FIRST_LEVEL_DOMAIN, tagSet);
		assertTag(SECOND_LEVEL_DOMAIN, GeoIpTagger.GEOIP_SECOND_LEVEL_DOMAIN, tagSet);
		assertTag(GMT_TIME_ZONE, GeoIpTagger.GEOIP_GMT_TIME_ZONE, tagSet);
		assertTag(CONTINENT, GeoIpTagger.GEOIP_CONTINENT, tagSet);

	}
	
	private void assertTag(final Number expected, final String tagName, final TagSet tagSet) {
		assertNotNull(nullMessage(tagName), tagSet.getTagValue(tagName));
		assertEquals(incorrectMessage(tagName), expected, tagSet.getTagValue(tagName).getValue());
	}
	

	private void assertTag(final String expected, final String tagName, final TagSet tagSet) {
		assertNotNull(nullMessage(tagName), tagSet.getTagValue(tagName));
		assertEquals(incorrectMessage(tagName), expected, tagSet.getTagValue(tagName).getValue());
	}

	private String nullMessage(final String tagName) {
		return "Tag not found in tag set: " + tagName;
	}

	private String incorrectMessage(final String tagName) {
		return "Incorrect value for tag: " + tagName;
	}

	/**
	 * Tests that if the data object returns as null for an IP the listener does not populates tags and does not crash.
	 */
	@Test
	public void testThatIfDataIsNullGeoIpTaggerIsNullSafe() {

		final SettingValue settings = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				allowing(settings).getBooleanValue();
				will(returnValue(Boolean.TRUE));

			}
		});
		
		SettingsReader settingsReader = new CachedSettingsReaderImpl() {
			@Override
			public SettingValue getSettingValue(final String path) {
				if ("COMMERCE/SYSTEM/GEOIP/enable".equals(path)) {
					return 	settings;
				}
				 return null;
			}
		};
		
		listener = new GeoIpTagger();
		listener.setSettingsReader(settingsReader);
		
		geoIpService = new GeoIpService() {
			public GeoIpLocation resolveIpAddress(final String ipAddress) {
				return null;
			}

			public void setProvider(final GeoIpProvider provider) {
				// Does nothing.
			}

		};

		listener.setGeoProviderService(geoIpService);

		context.checking(new Expectations() {
			{
				allowing(session).getCustomerTagSet();
				will(returnValue(tagSet));

			}
		});

		listener.execute(session, requestFacade);

		assertNullTag(GeoIpTagger.GEOIP_ZIP_OR_POST_CODE, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_COUNTRY_CODE, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_STATE_OR_PROVINCE, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_CONNECTION_TYPE, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_ROUTING_TYPE, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_CITY, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_FIRST_LEVEL_DOMAIN, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_SECOND_LEVEL_DOMAIN, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_GMT_TIME_ZONE, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_CONTINENT, tagSet);


	}

	private void assertNullTag(final String tagName, final TagSet tagSet) {
		assertNull(isNotNullMessage(tagName), tagSet.getTagValue(tagName));
	}

	private String isNotNullMessage(final String tagName) {
		return "Tag is not null: " + tagName;
	}

	/**
	 * Tests that geoip tags are empty in spite of existing stub values in case of GEO IP setting value is set to FALSE.
	 */
	@Test
	public void testGeoIpTaggingEventIsDisabled() {

		final SettingValue settings = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				allowing(settings).getBooleanValue();
				will(returnValue(Boolean.FALSE));

			}
		});
		
		SettingsReader settingsReader = new CachedSettingsReaderImpl() {
			@Override
			public SettingValue getSettingValue(final String path) {
				if ("COMMERCE/SYSTEM/GEOIP/enable".equals(path)) {
					return 	settings;
				}
				 return null;
			}
		};
		
		listener = new GeoIpTagger();		
		listener.setSettingsReader(settingsReader);
		
		geoIpService = new GeoIpService() {
			public GeoIpLocation resolveIpAddress(final String ipAddress) {
				GeoIpLocation geoIpLocation = new GeoIpLocationImpl();
				geoIpLocation.setZipCode(ZIP_OR_POST_CODE);
				geoIpLocation.setCountryCode(COUNTRY_CODE);
				geoIpLocation.setState(STATE_OR_PROVINCE);
				geoIpLocation.setConnectionType(CONNECTION_TYPE);
				geoIpLocation.setIpRoutingType(ROUTING_TYPE);
				geoIpLocation.setCity(CITY);
				geoIpLocation.setTopLevelDomain(FIRST_LEVEL_DOMAIN);
				geoIpLocation.setSecondLevelDomain(SECOND_LEVEL_DOMAIN);
				geoIpLocation.setGmtTimeZone(GMT_TIME_ZONE);
				geoIpLocation.setContinent(CONTINENT);
				return geoIpLocation;
			}

			public void setProvider(final GeoIpProvider provider) {
				// Does nothing.
			}

		};

		listener.setGeoProviderService(geoIpService);

		context.checking(new Expectations() {
			{
				allowing(session).getCustomerTagSet();
				will(returnValue(tagSet));
			}
		});

		listener.execute(session, requestFacade);

		assertNullTag(GeoIpTagger.GEOIP_ZIP_OR_POST_CODE, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_COUNTRY_CODE, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_STATE_OR_PROVINCE, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_CONNECTION_TYPE, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_ROUTING_TYPE, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_CITY, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_FIRST_LEVEL_DOMAIN, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_SECOND_LEVEL_DOMAIN, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_GMT_TIME_ZONE, tagSet);
		assertNullTag(GeoIpTagger.GEOIP_CONTINENT, tagSet);


	}
	
	
	/**
	 * Test that the tagger doesn't allow exceptions with the 
	 * geoip provider to leak out to the client.
	 */
	@Test
	public void testGeoIpTaggingEventCatchesExceptions() {

		SettingsReader settingsReader = new CachedSettingsReaderImpl() {
			@Override
			public SettingValue getSettingValue(final String path) {
				throw new IllegalStateException("Simulated geoip issue");
			}
		};
		
		listener = new GeoIpTagger();
		listener.setSettingsReader(settingsReader);

		try {
			listener.execute(session, requestFacade);
			// We should get here without an exception
		} catch (IllegalStateException e) {
			fail("Exception should not be received by tagger's client");
		}

	}
	
}
