package com.elasticpath.persistence.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.elasticpath.domain.misc.impl.GeographyImpl;

/**
 * Test class for {@link GeographyPropertiesDaoLoaderFactoryImpl}.
 */
public class GeographyPropertiesDaoLoaderFactoryImplTest {

	private ApplicationContext appContext;

	/** Test initialization. */
	@Before
	public void initialize() {
		appContext = new ClassPathXmlApplicationContext("/properties-dao/geography-property-dao-beans.xml");
	}

	/** Tests loading only countries. */
	@Test
	public void testLoadWithOnlyCountries() {
		TestGeography geography = appContext.getBean("countryNoOverrides", TestGeography.class);
		assertNotNull(geography.localeProperties);
		assertNotNull(geography.defaultProperties);

		Properties props = new Properties();
		props.setProperty("AB", "aye");
		props.setProperty("IT", "mate");
		
		assertEquals(props, geography.defaultProperties);
	}

	/** Tests loading is as expected when there are sub-countries attached to countries. */
	@Test
	public void testLoadOnlyCountriesWithOverrides() {
		TestGeography geography = appContext.getBean("countryOverrides", TestGeography.class);
		assertNotNull(geography.localeProperties);
		assertNotNull(geography.defaultProperties);

		Properties props = new Properties();
		props.setProperty("AB", "aye");
		props.setProperty("IT", "mate");
		
		assertEquals(props, geography.defaultProperties);

		Properties overrides = new Properties();
		overrides.setProperty("AB", "good day");
		overrides.setProperty("IT", "pal");
		assertEquals(Collections.singletonMap(new Locale("fr", "", ""), overrides), geography.localeProperties);
	}

	/** Tests loading is as expected when there are countries/subcountries with locale overrides. */
	@Test
	public void testLoadWithLocaleOverrides() {
		TestGeography geography = appContext.getBean("countryWithSubCountries", TestGeography.class);
		assertNotNull(geography.localeProperties);
		assertNotNull(geography.defaultProperties);

		Properties props = new Properties();
		props.setProperty("AB", "aye");
		props.setProperty("IT", "mate");
		props.setProperty("subcountry.AB.LI", "linked in");
		
		assertEquals(props, geography.defaultProperties);

		Properties overrides = new Properties();
		overrides.setProperty("AB", "good day");
		overrides.setProperty("IT", "pal");
		overrides.setProperty("subcountry.AB.LI", "linked");
		assertEquals(Collections.singletonMap(new Locale("fr", "", ""), overrides), geography.localeProperties);

		
	}

	/** Implementation for tests. */
	public static class TestGeography extends GeographyImpl {
		private static final long serialVersionUID = 1L;
		private Map<Locale, Properties> localeProperties;
		private Properties defaultProperties;

		@Override
		public void setInitializingLocaleOverideProperties(final Map<Locale, Properties> properties) {
			localeProperties = properties;
		}

		@Override
		public void setInitializingProperties(final Properties properties) {
			defaultProperties = properties;
		}
	}
}
