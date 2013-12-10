/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.sfweb.listeners;



import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.geoip.location.GeoIpLocation;
import com.elasticpath.service.geoip.GeoIpService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.NewHttpSessionEventListener;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * Sets GEO IP information tags for current user once a session is created for a customer.
 */
public class GeoIpTagger implements NewHttpSessionEventListener {

	private static final Logger LOG = Logger.getLogger(GeoIpTagger.class);

	private GeoIpService geoProviderService;
	
	private static final String GEOIP_ENABLE = "COMMERCE/SYSTEM/GEOIP/enable";

	private SettingsReader settingsReader;
	
	/** Zip or post code tag name. */
	static final String GEOIP_ZIP_OR_POST_CODE = "GEOIP_ZIP_OR_POST_CODE";

	/** Country code tag name. */
	static final String GEOIP_COUNTRY_CODE = "GEOIP_COUNTRY_CODE";

	/** State or province name tag name. */
	static final String GEOIP_STATE_OR_PROVINCE = "GEOIP_STATE_OR_PROVINCE";

	/** Connection type tag name. */
	static final String GEOIP_CONNECTION_TYPE = "GEOIP_CONNECTION_TYPE";

	/** Routing type tag name. */
	static final String GEOIP_ROUTING_TYPE = "GEOIP_ROUTING_TYPE";

	/** City name tag name. */
	static final String GEOIP_CITY = "GEOIP_CITY";
	
	/** First level domain name. For example com, net, edu.*/
	static final String GEOIP_FIRST_LEVEL_DOMAIN = "GEOIP_FIRST_LEVEL_DOMAIN";
	
	/** Second level domain name. For example oracle from www.oracle.com .*/
	static final String GEOIP_SECOND_LEVEL_DOMAIN = "GEOIP_SECOND_LEVEL_DOMAIN";
	
	/** GMT time zone. */
	static final String GEOIP_GMT_TIME_ZONE = "GEOIP_GMT_TIME_ZONE";
	
	/** Continent.  */
	static final String GEOIP_CONTINENT = "GEOIP_CONTINENT";

	/**
	 * Apply GEO IP tags to the given session.
	 * 
	 * @param session instance of CustomerSession
	 * @param request the originating HttpServletRequest
	 */
	public void execute(final CustomerSession session, final HttpServletRequestFacade request) {

		try {
			if (null == getSettingsReader().getSettingValue(GEOIP_ENABLE)) {
				LOG.info("Setting value " + GEOIP_ENABLE + " does not exist.");
				return;
			}

			if (!getSettingsReader().getSettingValue(GEOIP_ENABLE).getBooleanValue()) {
				LOG.debug("GEO Ip tagging event is disabled.");
				return;
			}

			final String customerIpAddress = request.getRemoteAddress();
			final GeoIpLocation data = geoProviderService.resolveIpAddress(customerIpAddress);
			final TagSet tagSet = session.getCustomerTagSet();

			if (data != null) {
				data.setIpAddress(customerIpAddress);
				addZipCodeTag(tagSet, data.getZipCode());
				addRoutingTypeTag(tagSet, data.getIpRoutingType());
				addStateOrProvinceTag(tagSet, data.getState());
				addCityTag(tagSet, data.getCity());
				addConnectionTypeTag(tagSet, data.getConnectionType());
				addCountryCodeTag(tagSet, data.getCountryCode());
				addFirstLevelDomainNameTag(tagSet, data.getTopLevelDomain());
				addSecondLevelDomainNameTag(tagSet, data.getSecondLevelDomain());
				addGmtTimeZoneTag(tagSet, data.getGmtTimeZone());
				addContinentTag(tagSet, data.getContinent());

			}
		} catch (Exception e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Error fetching/setting geoip tags in tag set.", e);
			}
		}
	}
	
	/**
	 * Add/overwrite tag with continent for current customer.
	 * 
	 * @param tagSet the customer tag set
	 * @param continent the post code for this connection
	 */
	private void addContinentTag(final TagSet tagSet, final String continent) {
		addTag(tagSet, GEOIP_CONTINENT, continent);
	}
	
	
	/**
	 * Add/overwrite tag with GMT time zone for current customer.
	 * 
	 * @param tagSet the customer tag set
	 * @param gmtTimeZone the post code for this connection
	 */
	private void addGmtTimeZoneTag(final TagSet tagSet, final Float gmtTimeZone) {
		addTag(tagSet, GEOIP_GMT_TIME_ZONE, gmtTimeZone);
	}
	

	
	/**
	 * Add/overwrite tag with first level domain name  for current customer.
	 * 
	 * @param tagSet the customer tag set
	 * @param firstLevelDomainName the post code for this connection
	 */
	private void addFirstLevelDomainNameTag(final TagSet tagSet, final String firstLevelDomainName) {
		addTag(tagSet, GEOIP_FIRST_LEVEL_DOMAIN, firstLevelDomainName);
	}
	
	/**
	 * Add/overwrite tag with first level domain name code for current customer.
	 * 
	 * @param tagSet the customer tag set
	 * @param secondLevelDomainName the post code for this connection
	 */
	private void addSecondLevelDomainNameTag(final TagSet tagSet, final String secondLevelDomainName) {
		addTag(tagSet, GEOIP_SECOND_LEVEL_DOMAIN, secondLevelDomainName);
	}
	
	

	/**
	 * Add/overwrite tag with zip/post code for current customer.
	 * 
	 * @param tagSet the customer tag set
	 * @param zipOrPostalCode the post code for this connection
	 */
	private void addZipCodeTag(final TagSet tagSet, final String zipOrPostalCode) {
		addTag(tagSet, GEOIP_ZIP_OR_POST_CODE, zipOrPostalCode);
	}

	/**
	 * Add/overwrite tag with routing type for current customer.
	 * 
	 * @param tagSet the customer tag set
	 * @param routingType the routing type for this connection
	 */
	private void addRoutingTypeTag(final TagSet tagSet, final String routingType) {
		addTag(tagSet, GEOIP_ROUTING_TYPE, routingType);
	}

	/**
	 * Add/overwrite tag with state or province name for current customer.
	 * 
	 * @param tagSet the customer tag set
	 * @param stateOrProvinceName the state or province name for this connection
	 */
	private void addStateOrProvinceTag(final TagSet tagSet, final String stateOrProvinceName) {
		addTag(tagSet, GEOIP_STATE_OR_PROVINCE, stateOrProvinceName);
	}

	/**
	 * Add/overwrite tag with city name for current customer.
	 * 
	 * @param tagSet the customer tag set
	 * @param cityName the city name for this connection
	 */
	private void addCityTag(final TagSet tagSet, final String cityName) {
		addTag(tagSet, GEOIP_CITY, cityName);
	}

	/**
	 * Add/overwrite tag with connection type for current customer.
	 * 
	 * @param tagSet the customer tag set
	 * @param connectionType the type for this connection
	 */
	private void addConnectionTypeTag(final TagSet tagSet, final String connectionType) {
		addTag(tagSet, GEOIP_CONNECTION_TYPE, connectionType);
	}

	/**
	 * Add/overwrite tag with connection type for current customer.
	 * 
	 * @param tagSet the customer tag set
	 * @param countryCode the two letter code as specified by ISO-3166-Alpha-2 code standards for this connection
	 */
	private void addCountryCodeTag(final TagSet tagSet, final String countryCode) {
		addTag(tagSet, GEOIP_COUNTRY_CODE, countryCode);
	}

	private void addTag(final TagSet tagSet, final String tagDefinition, final Object tagValue) {
		if (null == tagSet) {
			return;
		}
		if (StringUtils.isEmpty(tagDefinition)) {
			return;
		}
		if (null == tagValue) {
			return;
		}
		if ((tagValue instanceof String) && StringUtils.isEmpty((String) tagValue)) {
			return;
		}
		tagSet.addTag(tagDefinition, new Tag(tagValue));
		if (LOG.isDebugEnabled()) {
			LOG.debug("Updated customer tag cloud with " + tagDefinition + ":" + tagSet);
		}
	}

	/**
	 * Sets geo ip service provider.
	 * 
	 * @param geoProviderService - geo ip service provider.
	 */
	public void setGeoProviderService(final GeoIpService geoProviderService) {
		this.geoProviderService = geoProviderService;
	}
	
	/**
	 * Sets the Settings Reader.
	 * @param settingsReader the settings reader
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
	
	/**
	 * Gets the Settings Reader.
	 * @return the settings reader
	 */
	public SettingsReader getSettingsReader() {
		return this.settingsReader;
	}

}
