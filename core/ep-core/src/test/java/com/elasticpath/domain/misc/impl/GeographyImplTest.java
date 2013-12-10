/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.misc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.misc.Geography;

/**
 * Test for {@link GeographyImpl}.
 */
public class GeographyImplTest {

	private GeographyImpl geography;

	private static final String COUNTRY1_CODE = "country1";
	private static final String COUNTRY1_NAME = "country 1";
	private static final String COUNTRY1_NAME_FR = "country 1 fr";
	private static final String COUNTRY1_NAME_FR_POSIX = "country 1 fr posix";
	private static final String COUNTRY1_SUBCOUNTRY1_CODE = "country1subCountry1";
	private static final String COUNTRY1_SUBCOUNTRY1_NAME = "country 1 subCountry 1";
	private static final String COUNTRY1_SUBCOUNTRY1_NAME_FR = "country 1 subCountry 1 fr";
	// no FR_POSIX here for country 1 sub-country 1
	private static final String COUNTRY1_SUBCOUNTRY2_CODE = "country1subCountry2";
	private static final String COUNTRY1_SUBCOUNTRY2_NAME = "country 1 subCountry 2";
	private static final String COUNTRY1_SUBCOUNTRY2_NAME_FR = "country 1 subCountry 2 fr";
	private static final String COUNTRY1_SUBCOUNTRY2_NAME_FR_POSIX = "country 1 subCountry 2 fr posix";
	private static final String COUNTRY2_CODE = "country2";
	private static final String COUNTRY2_NAME = "country 2";
	private static final String COUNTRY2_SUBCOUNTRY1_CODE = "country2subCountry1";
	private static final String COUNTRY2_SUBCOUNTRY1_NAME = "country 2 subCountry 1";
	// no locale overrides
	private static final String COUNTRY3_CODE = "country3";
	private static final String COUNTRY3_NAME = "country 3";
	// no locale overrides

	private static final Locale FRENCH_LOCALE = new Locale("french", "", "");
	private static final Locale FRENCH_POSIX_LOCALE = new Locale("french", "", "posix");

	private static String subCountryPropertyKey(final String countryCode, final String subCountryCode) {
		return String.format("%s.%s.%s", Geography.SUB_COUNTRY_PROPERTY_PREFIX, countryCode, subCountryCode);
	}

	/** Test initialization. */
	@Before
	public void initialize() {
		Properties defaultProperties = new Properties();
		defaultProperties.setProperty(COUNTRY1_CODE, COUNTRY1_NAME);
		defaultProperties.setProperty(COUNTRY2_CODE, COUNTRY2_NAME);
		defaultProperties.setProperty(COUNTRY3_CODE, COUNTRY3_NAME);
		defaultProperties.setProperty(subCountryPropertyKey(COUNTRY1_CODE, COUNTRY1_SUBCOUNTRY1_CODE), COUNTRY1_SUBCOUNTRY1_NAME);
		defaultProperties.setProperty(subCountryPropertyKey(COUNTRY1_CODE, COUNTRY1_SUBCOUNTRY2_CODE), COUNTRY1_SUBCOUNTRY2_NAME);
		defaultProperties.setProperty(subCountryPropertyKey(COUNTRY2_CODE, COUNTRY2_SUBCOUNTRY1_CODE), COUNTRY2_SUBCOUNTRY1_NAME);

		Properties frenchOverrides = new Properties();
		frenchOverrides.setProperty(COUNTRY1_CODE, COUNTRY1_NAME_FR);
		frenchOverrides.setProperty(subCountryPropertyKey(COUNTRY1_CODE, COUNTRY1_SUBCOUNTRY1_CODE), COUNTRY1_SUBCOUNTRY1_NAME_FR);
		frenchOverrides.setProperty(subCountryPropertyKey(COUNTRY1_CODE, COUNTRY1_SUBCOUNTRY2_CODE), COUNTRY1_SUBCOUNTRY2_NAME_FR);

		Properties frenchPosixOverrides = new Properties();
		frenchPosixOverrides.setProperty(COUNTRY1_CODE, COUNTRY1_NAME_FR_POSIX);
		frenchPosixOverrides.setProperty(subCountryPropertyKey(COUNTRY1_CODE, COUNTRY1_SUBCOUNTRY2_CODE), COUNTRY1_SUBCOUNTRY2_NAME_FR_POSIX);

		Map<Locale, Properties> localeProperties = new HashMap<Locale, Properties>();
		localeProperties.put(FRENCH_LOCALE, frenchOverrides);
		localeProperties.put(FRENCH_POSIX_LOCALE, frenchPosixOverrides);

		geography = new GeographyImpl();
		geography.setInitializingProperties(defaultProperties);
		geography.setInitializingLocaleOverideProperties(localeProperties);
	}

	/** Tests that country properties are properly parsed on initialization. */
	@Test
	public void testCountriesParsedCorrectly() {
		assertEquals("countries not setup correctly", new HashSet<String>(Arrays.asList(COUNTRY1_CODE, COUNTRY2_CODE, COUNTRY3_CODE)),
				geography.getCountryCodes());
		assertEquals("sub-countries for country 1 not setup correctly",
				new HashSet<String>(Arrays.asList(COUNTRY1_SUBCOUNTRY1_CODE, COUNTRY1_SUBCOUNTRY2_CODE)),
				geography.getSubCountryCodes(COUNTRY1_CODE));
		assertEquals("sub-countries for country 2 not setup correctly", new HashSet<String>(Arrays.asList(COUNTRY2_SUBCOUNTRY1_CODE)),
				geography.getSubCountryCodes(COUNTRY2_CODE));
		assertEquals("sub-countries for country 3 not setup correctly", Collections.emptySet(), geography.getSubCountryCodes(COUNTRY3_CODE));
	}

	/** Tests country display name when when there is no locale. */
	@Test
	public void testCountryDisplayNameNoLocale() {
		assertEquals(COUNTRY3_NAME, geography.getCountryDisplayName(COUNTRY3_CODE));
	}

	/** Tests getting country name when there is an exact locale available for the given country. */
	@Test
	public void testCountryDisplayNameLocalesExact() {
		assertEquals(COUNTRY1_NAME_FR, geography.getCountryDisplayName(COUNTRY1_CODE, FRENCH_LOCALE));
		assertEquals(COUNTRY1_NAME_FR_POSIX, geography.getCountryDisplayName(COUNTRY1_CODE, FRENCH_POSIX_LOCALE));
	}

	/** Tests getting the country name when the locale isn't an exact match. */
	@Test
	public void testCountryDisplayNameLocaleFallback() {
		assertEquals(COUNTRY1_NAME_FR,
				geography.getCountryDisplayName(COUNTRY1_CODE, new Locale(FRENCH_LOCALE.getLanguage(), "", "another variant")));
	}

	/** Tests sub-country display name when when there is no locale. */
	@Test
	public void testSubCountryDisplayNameNoLocale() {
		assertEquals(COUNTRY2_SUBCOUNTRY1_NAME, geography.getSubCountryDisplayName(COUNTRY2_CODE, COUNTRY2_SUBCOUNTRY1_CODE));
	}

	/** Tests getting sub-country name when there is an exact locale available for the given sub-country. */
	@Test
	public void testSubCountryDisplayNameLocalesExact() {
		assertEquals(COUNTRY1_SUBCOUNTRY1_NAME_FR,
				geography.getSubCountryDisplayName(COUNTRY1_CODE, COUNTRY1_SUBCOUNTRY1_CODE, FRENCH_LOCALE));
		assertEquals(COUNTRY1_SUBCOUNTRY2_NAME_FR,
				geography.getSubCountryDisplayName(COUNTRY1_CODE, COUNTRY1_SUBCOUNTRY2_CODE, FRENCH_LOCALE));
		assertEquals(COUNTRY1_SUBCOUNTRY2_NAME_FR_POSIX,
				geography.getSubCountryDisplayName(COUNTRY1_CODE, COUNTRY1_SUBCOUNTRY2_CODE, FRENCH_POSIX_LOCALE));
	}

	/** Tests getting the sub-country name when the locale isn't an exact match. */
	@Test
	public void testSubCountryDisplayNameLocaleFallback() {
		assertEquals(COUNTRY1_SUBCOUNTRY1_NAME_FR, geography.getSubCountryDisplayName(COUNTRY1_CODE, COUNTRY1_SUBCOUNTRY1_CODE, new Locale(
				FRENCH_LOCALE.getLanguage(), "", "another variant")));
	}

	/** We should get no errors if we attempt to use a {@code null} country. */
	@Test
	public void testCountyDisplayNameCountryNull() {
		assertNull(geography.getCountryDisplayName(null));
	}

	/**
	 * We should get not errors if we attempt to get use {@code null} in the country when fetching the sub country
	 * display name.
	 */
	@Test
	public void testSubCountryDisplayNameCountryNull() {
		assertNull(geography.getSubCountryDisplayName(null, COUNTRY1_SUBCOUNTRY1_CODE));
	}

	/**
	 * We should get not errors if we attempt to get use {@code null} in the country when fetching the sub country
	 * display name.
	 */
	@Test
	public void testSubCountryDisplayNameSubCountryNull() {
		assertNull(geography.getSubCountryDisplayName(COUNTRY1_CODE, null));
	}

	/** If the locale is {@code null} we should effectively get the default unlocalized value for country. */
	@Test
	public void testGetCountryDisplayNameLocaleNull() {
		assertEquals(COUNTRY1_NAME, geography.getCountryDisplayName(COUNTRY1_CODE, null));
	}

	/** If the locale is {@code null} we should effectively get the default unlocalized value for sub-country. */
	@Test
	public void testGetSubCountryDisplayNameLocaleNull() {
		assertEquals(COUNTRY1_SUBCOUNTRY1_NAME, geography.getSubCountryDisplayName(COUNTRY1_CODE, COUNTRY1_SUBCOUNTRY1_CODE));
	}
}
