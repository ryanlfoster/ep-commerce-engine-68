package com.elasticpath.persistence.dao.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.elasticpath.persistence.dao.LocalePropertyLoaderAware;

/**
 * Helper implementation of {@link LocalePropertyLoaderAware}.
 */
public class AbstractLocalePropertyLoaderAwareImpl extends AbstractPropertyLoaderAwareImpl implements LocalePropertyLoaderAware {

	private static final long serialVersionUID = 1L;

	/*
	 * Unlike the parent, this property shouldn't be visible to sub-classes, this class has assumptions that it doesn't
	 * change once its set.
	 */
	private Map<Locale, Properties> localeProperties = Collections.emptyMap();

	@Override
	public void setInitializingLocaleOverideProperties(final Map<Locale, Properties> properties) {
		this.localeProperties = properties;
	}

	@Override
	public String getProperty(final Locale locale, final String propertyName) {
		return getProperty(locale, propertyName, true);
	}

	@Override
	public String getProperty(final Locale locale, final String propertyName, final boolean fallback) {
		if (fallback) {
			return getPropertyFallback(locale, propertyName, localeProperties, getProperties());
		} else if (localeProperties != null && localeProperties.containsKey(locale)) {
			return localeProperties.get(locale).getProperty(propertyName);
		}
		return null;
	}

	/**
	 * Utility method for getting a property from the given locale-based property map with a {@link Locale} following
	 * the fallback rules.
	 * 
	 * @param locale {@link Locale} to start from or {@code null} for the default property
	 * @param propertyName name of the property to search for
	 * @param localeMapProperties locale-based property map to search in or {@code null} if there are no locale
	 *            overrides
	 * @param defaultProperties the default properties in case there isn't a locale specific one
	 * @return the best locale match following locale fallback rules or {@code null} if there was no property
	 */
	protected static String getPropertyFallback(final Locale locale, final String propertyName,
			final Map<Locale, Properties> localeMapProperties, final Properties defaultProperties) {
		if (localeMapProperties != null && locale != null) {
			if (localeMapProperties.containsKey(locale) && localeMapProperties.get(locale).containsKey(propertyName)) {
				return localeMapProperties.get(locale).getProperty(propertyName);
			}
			
			Locale fallbackLocale = new Locale(locale.getLanguage(), locale.getCountry());
			if (localeMapProperties.containsKey(fallbackLocale) && localeMapProperties.get(fallbackLocale).containsKey(propertyName)) {
				return localeMapProperties.get(fallbackLocale).getProperty(propertyName);
			}

			fallbackLocale = new Locale(locale.getLanguage());
			if (localeMapProperties.containsKey(fallbackLocale) && localeMapProperties.get(fallbackLocale).containsKey(propertyName)) {
				return localeMapProperties.get(fallbackLocale).getProperty(propertyName);
			}
		}

		if (defaultProperties == null || propertyName == null) {
			return null;
		}

		return defaultProperties.getProperty(propertyName);
	}

	/**
	 * Gets all the property keys for the given locale. Although keys are not localized in general, a localization may
	 * have more keys than the default.
	 * 
	 * @param locale the locale to get keys for
	 * @return all the available property keys
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Set<String> getAllPropertyKeys(final Locale locale) {
		Set<String> propertyKeys = new HashSet<String>();
		propertyKeys.addAll((Set) getProperties().keySet());
		if (localeProperties.containsKey(locale)) {
			propertyKeys.addAll((Set) localeProperties.get(locale).keySet());
		}
		return propertyKeys;
	}
}
