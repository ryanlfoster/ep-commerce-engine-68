package com.elasticpath.persistence.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.drools.util.StringUtils;
import org.springframework.core.io.Resource;

import com.elasticpath.persistence.dao.LocalePropertyLoaderAware;

/**
 * Extension of {@link PropertiesDaoLoaderFactoryImpl} in order to produce {@link LocalePropertyLoaderAware} objects.
 * <p>
 * Locale properties are determined by their filename and must not contain an underscore except to delimit the locale.
 * That is, a all properties defined in a particular file will be loaded for a single locale. The locale is determined
 * by the end of the file name (expecting a '.properties' extension). The format is precisely described in
 * {@link Locale#toString()}:
 * <ul>
 * <li>&lt;resourceNamePrefix&gt; '_' &lt;language&gt; '_' &lt;country&gt; '_' &lt;variant&gt;</li>
 * <li>&lt;resourceNamePrefix&gt; '_' &lt;language&gt; '_' &lt;country&gt;</li>
 * <li>&lt;resourceNamePrefix&gt; '_' &lt;language&gt;</li>
 * <li>&lt;resourceNamePrefix&gt; for the default locale</li>
 * </ul>
 * where &lt;resourceNamePrefix&gt; is determined by the value given in {@link #setResourceNamePrefix(String)}. It is
 * valid for a variant to be non-empty with an empty country.
 * </p>
 * 
 * @param <T> type of {@link LocalePropertyLoaderAware}
 */
public class LocalePropertiesDaoLoaderFactoryImpl<T extends LocalePropertyLoaderAware> extends PropertiesDaoLoaderFactoryImpl<T> {

	private String resourceNamePrefix = StringUtils.EMPTY;
	private final Map<Locale, Properties> localeProperties = new HashMap<Locale, Properties>();

	/**
	 * Returns the properties from {@link InputStream} which were originally defined in {@link Resource}. This is mainly
	 * used as an extension point in case properties need to be mutated before being loaded. If this returns
	 * {@code null} then the properties are not added.
	 * 
	 * @param resource the resource the properties came from
	 * @param locale {@link Locale} which the properties are defined for
	 * @param stream {@link InputStream} of the resource
	 * @throws IOException in case of errors
	 * @return properties
	 */
	protected Properties readAndMutateLocaleProperties(final Resource resource, final Locale locale, final InputStream stream)
			throws IOException {
		Properties properties = new Properties();
		properties.load(stream);
		return properties;
	}

	@Override
	protected void loadProperties(final Resource resource, final InputStream stream) throws IOException {
		Locale locale = findLocale(resource.getFilename(), getResourcePrefix(resource));

		if (locale == null) {
			// no locale, its the default
			super.loadProperties(resource, stream);
			return;
		}

		Properties properties = localeProperties.get(locale);
		if (properties == null) {
			properties = new Properties();
			/*
			 * We don't want to add properties to the map here, just in case the mutate step removes all properties.
			 * This is a memory optimization.
			 */
		}

		Properties mutatedProperties = readAndMutateLocaleProperties(resource, locale, stream);
		if (mutatedProperties != null) {
			properties.putAll(mutatedProperties);

			if (!localeProperties.containsKey(locale)) {
				localeProperties.put(locale, properties);
			}
		}
	}

	/**
	 * Gets the prefix that is not part of the localization bit.
	 * 
	 * @param resource {@link Resource} that we are getting the prefix for
	 * @return the actual prefix of the resource
	 */
	protected String getResourcePrefix(final Resource resource) {
		return resourceNamePrefix;
	}

	/**
	 * Utility method to remove the file extension. Currently assumes the extension is {@code .properties}.
	 * 
	 * @param filename a filename
	 * @return filename without the extension
	 */
	protected static String removeFileExtension(final String filename) {
		String result = filename;
		if (result.endsWith(".properties")) {
			result = result.substring(0, result.length() - ".properties".length());
		}
		return result;
	}

	/**
	 * Attempts to find the locale from the given filename.
	 * 
	 * @param filename a filename
	 * @param prefix filename prefix, anything after this is assumed to be part of the locale
	 * @return a {@link Locale} or {@code null} if there is no loacle
	 */
	private static Locale findLocale(final String filename, final String prefix) {
		String working = removeFileExtension(filename);
		working = working.substring(prefix.length());

		if (working.isEmpty()) {
			return null;
		}

		// we know we have a locale now, remove the leading underscore
		working = working.substring(1);

		// commons-lang LocaleUtils doesn't accept a variant without a country (and throws IllegalArgumentExceptions)
		int countryUIdx = working.indexOf('_');
		String language = working;
		String country = "", variant = "";
		if (countryUIdx >= 0) {
			language = working.substring(0, countryUIdx);
			int variantUIdx = working.indexOf('_', countryUIdx + 1);
			if (variantUIdx >= 0) {
				country = working.substring(countryUIdx + 1, variantUIdx);
				variant = working.substring(variantUIdx + 1);
			} else {
				country = working.substring(countryUIdx + 1);
			}
		}

		return new Locale(language, country, variant);
	}

	@Override
	protected void setupObject(final T object) {
		super.setupObject(object);
		object.setInitializingLocaleOverideProperties(localeProperties);
	}


	public void setResourceNamePrefix(final String resourceNamePrefix) {
		this.resourceNamePrefix = resourceNamePrefix;
	}
}
