package com.elasticpath.persistence.dao.impl;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.elasticpath.persistence.dao.LocalePropertyLoaderAware;

/** Test class for {@link LocalePropertiesDaoLoaderFactoryImpl}. */
public class LocalePropertiesDaoLoaderFactoryImplTest {
	private static final String LANGUAGE = "lang";
	private static final String DEFAULT_BEAN = "localeBean";
	private static final String PROPERTY_NAME = "property";
	private ApplicationContext appContext;

	/** Test initialization. */
	@Before
	public void initialize() {
		appContext = new ClassPathXmlApplicationContext("/properties-dao/locale-property-dao-beans.xml");

	}

	/** Getting the default property. */
	@Test
	public void testGetDefault() {
		LocalePropertyLoaderAware aware = appContext.getBean(DEFAULT_BEAN, LocalePropertyLoaderAware.class);
		assertEquals("default", aware.getProperty(PROPERTY_NAME));
	}

	/** Getting properties with a locale that is defined. */
	@Test
	public void testGetLang() {
		LocalePropertyLoaderAware aware = appContext.getBean(DEFAULT_BEAN, LocalePropertyLoaderAware.class);
		assertEquals("lang_coun_var", aware.getProperty(new Locale(LANGUAGE, "coun", "var"), PROPERTY_NAME));
		assertEquals("lang_coun", aware.getProperty(new Locale(LANGUAGE, "coun"), PROPERTY_NAME));
		assertEquals("_lang", aware.getProperty(new Locale(LANGUAGE), PROPERTY_NAME));
	}

	/** Getting properties when not all overrides are defined. */
	@Test
	public void testGetLangNoCountry() {
		LocalePropertyLoaderAware aware = appContext.getBean(DEFAULT_BEAN, LocalePropertyLoaderAware.class);
		assertEquals("lang2__posix", aware.getProperty(new Locale("lang2", "", "posix"), PROPERTY_NAME));
		assertEquals("_lang2", aware.getProperty(new Locale("lang2", ""), PROPERTY_NAME));
		assertEquals("_lang2", aware.getProperty(new Locale("lang2"), PROPERTY_NAME));
	}

	/** A missing locale should fallback to the default. */
	@Test
	public void testGetMissingLocale() {
		LocalePropertyLoaderAware aware = appContext.getBean(DEFAULT_BEAN, LocalePropertyLoaderAware.class);
		assertEquals("default", aware.getProperty(new Locale("non-existing"), PROPERTY_NAME));
	}

	/** When there are multiple properties files with the same locale, they should be merged. */
	@Test
	public void testMultiplePropertiesWithSameLocale() {
		LocalePropertyLoaderAware aware = appContext.getBean("multipleProperties", LocalePropertyLoaderAware.class);
		assertEquals("_lang 2", aware.getProperty(new Locale(LANGUAGE), "property2"));
		assertEquals("_lang", aware.getProperty(new Locale(LANGUAGE), PROPERTY_NAME));
	}

	/** Implementation for tests. */
	public static class TestLocalePropertyLoaderAwareBean extends AbstractLocalePropertyLoaderAwareImpl {
		private static final long serialVersionUID = 1L;
	}
}
