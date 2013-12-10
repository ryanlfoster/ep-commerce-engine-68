package com.elasticpath.persistence.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.Locale;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

/** Test class for {@link AbstractLocalePropertyLoaderAwareImpl}. */
public class AbstractLocalePropertyLoaderAwareImplTest {

	private static final String NON_EXISTING_LOCALE_KEY = "non-existing";
	private static final String PROPERTY_EXISTS_KEY = "some property";
	private static final String PROPERTY_EXISTS_VALUE = "value";
	private static final String PROPERTY_EXISTS_LANGUAGE_VALUE = "some language";
	private static final String PROPERTY_EXISTS_COUNTRY_VALUE = "some country";
	private static final String PROPERTY_EXISTS_VARIANT_VALUE = "some variant";
	private static final String PROPERTY_NOEXISTS_KEY = "another property";
	private AbstractLocalePropertyLoaderAwareImpl loader;

	/** Test initialization. */
	@Before
	public void setUp() {
		loader = new TestLocalePropertyLoaderImpl();

		Properties defaults = new Properties();
		defaults.setProperty(PROPERTY_EXISTS_KEY, PROPERTY_EXISTS_VALUE);
		loader.setInitializingProperties(defaults);
	}

	/** Tests getting the default value. */
	@Test
	public void testGetDefault() {
		assertEquals(PROPERTY_EXISTS_VALUE, loader.getProperty(PROPERTY_EXISTS_KEY));
	}

	/** {@code null} should be returned for non-existing keys. */
	@Test
	public void testGetDefaultNoExists() {
		assertNull(loader.getProperty(PROPERTY_NOEXISTS_KEY));
	}

	/** We should get the default if there are no locale properties. */
	@Test
	public void testNoLocaleProperties() {
		assertEquals(PROPERTY_EXISTS_VALUE, loader.getProperty(PROPERTY_EXISTS_KEY));
	}

	/** Even if the property exists for other locales, there should be no fallback here. */
	@Test
	public void testGetMatchNoFallback() {
		assertNull(loader.getProperty(new Locale("non", "existing", "locale"), PROPERTY_EXISTS_KEY, false));
	}

	/** Tests getting a property with exact variant in initialized properties. */
	@Test
	public void testGetWithVariant() {
		Locale variant = new Locale("a", "b", "c");
		Properties overrides = new Properties();
		overrides.setProperty(PROPERTY_EXISTS_KEY, PROPERTY_EXISTS_VARIANT_VALUE);
		loader.setInitializingLocaleOverideProperties(Collections.singletonMap(variant, overrides));

		assertEquals(PROPERTY_EXISTS_VARIANT_VALUE, loader.getProperty(variant, PROPERTY_EXISTS_KEY));
		assertEquals(PROPERTY_EXISTS_VALUE,
				loader.getProperty(new Locale(variant.getLanguage(), variant.getCountry()), PROPERTY_EXISTS_KEY));
		assertEquals(PROPERTY_EXISTS_VALUE, loader.getProperty(new Locale(variant.getLanguage()), PROPERTY_EXISTS_KEY));
		assertEquals(PROPERTY_EXISTS_VALUE, loader.getProperty(new Locale(NON_EXISTING_LOCALE_KEY), PROPERTY_EXISTS_KEY));
	}

	/** Tests getting a property with exact country in initialized properties. */
	@Test
	public void testGetWithCountry() {
		Locale country = new Locale("d", "e");
		Properties overrides = new Properties();
		overrides.setProperty(PROPERTY_EXISTS_KEY, PROPERTY_EXISTS_COUNTRY_VALUE);
		loader.setInitializingLocaleOverideProperties(Collections.singletonMap(country, overrides));

		assertEquals(PROPERTY_EXISTS_COUNTRY_VALUE,
				loader.getProperty(new Locale(country.getLanguage(), country.getCountry(), NON_EXISTING_LOCALE_KEY), PROPERTY_EXISTS_KEY));
		assertEquals(PROPERTY_EXISTS_COUNTRY_VALUE,
				loader.getProperty(new Locale(country.getLanguage(), country.getCountry()), PROPERTY_EXISTS_KEY));
		assertEquals(PROPERTY_EXISTS_VALUE, loader.getProperty(new Locale(country.getLanguage()), PROPERTY_EXISTS_KEY));
		assertEquals(PROPERTY_EXISTS_VALUE, loader.getProperty(new Locale(NON_EXISTING_LOCALE_KEY), PROPERTY_EXISTS_KEY));
	}

	/** Tests getting a property with exact language in initialized properties. */
	@Test
	public void testGetWithLanguage() {
		Locale country = new Locale("f");
		Properties overrides = new Properties();
		overrides.setProperty(PROPERTY_EXISTS_KEY, PROPERTY_EXISTS_LANGUAGE_VALUE);
		loader.setInitializingLocaleOverideProperties(Collections.singletonMap(country, overrides));

		assertEquals(PROPERTY_EXISTS_LANGUAGE_VALUE, loader.getProperty(new Locale(country.getLanguage(), NON_EXISTING_LOCALE_KEY,
				NON_EXISTING_LOCALE_KEY), PROPERTY_EXISTS_KEY));
		assertEquals(PROPERTY_EXISTS_LANGUAGE_VALUE,
				loader.getProperty(new Locale(country.getLanguage(), NON_EXISTING_LOCALE_KEY), PROPERTY_EXISTS_KEY));
		assertEquals(PROPERTY_EXISTS_LANGUAGE_VALUE, loader.getProperty(new Locale(country.getLanguage()), PROPERTY_EXISTS_KEY));
		assertEquals(PROPERTY_EXISTS_VALUE, loader.getProperty(new Locale(NON_EXISTING_LOCALE_KEY), PROPERTY_EXISTS_KEY));
	}

	/** Implementation for tests. */
	private static class TestLocalePropertyLoaderImpl extends AbstractLocalePropertyLoaderAwareImpl {
		private static final long serialVersionUID = 1L;
	}
}
