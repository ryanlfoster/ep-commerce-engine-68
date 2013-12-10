/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.misc.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.elasticpath.domain.misc.Geography;
import com.elasticpath.persistence.dao.impl.AbstractLocalePropertyLoaderAwareImpl;

/**
 * Default implementation of {@link Geography}. Assumes that backing data doesn't change at runtime.
 */
public class GeographyImpl extends AbstractLocalePropertyLoaderAwareImpl implements Geography {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private final Map<String, Map<Locale, Properties>> subCountriesLocale = new HashMap<String, Map<Locale, Properties>>();
	private final Map<String, Properties> subCountriesDefault = new HashMap<String, Properties>();

	private static final Pattern SUB_COUNTRY_PROPERTY_KEY_PATTERN = Pattern.compile(String.format("^\\Q%s\\E\\.(.*)\\.(.*)$",
			Geography.SUB_COUNTRY_PROPERTY_PREFIX));

	@Override
	public void setInitializingLocaleOverideProperties(final Map<Locale, Properties> properties) {
		// first filter out sub-country entries, we handle those in a special manner
		for (Entry<Locale, Properties> entry : properties.entrySet()) {
			for (Iterator<Entry<Object, Object>> iter = entry.getValue().entrySet().iterator(); iter.hasNext();) {
				Entry<Object, Object> subCountryEntry = iter.next();
				String code = (String) subCountryEntry.getKey();

				// check for a sub-country entry
				if (code.startsWith(Geography.SUB_COUNTRY_PROPERTY_PREFIX)) {
					Matcher matcher = SUB_COUNTRY_PROPERTY_KEY_PATTERN.matcher(code);
					matcher.find();
					String countryCode = matcher.group(1);
					String subCountryCode = matcher.group(2);

					Map<Locale, Properties> subCountryMap = subCountriesLocale.get(countryCode);
					if (subCountryMap == null) {
						subCountryMap = new HashMap<Locale, Properties>();
						subCountriesLocale.put(countryCode, subCountryMap);
					}

					Properties subCountryProps = subCountryMap.get(entry.getKey());
					if (subCountryProps == null) {
						subCountryProps = new Properties();
						subCountryMap.put(entry.getKey(), subCountryProps);
					}

					subCountryProps.put(subCountryCode, subCountryEntry.getValue());
					iter.remove();
				}
			}
		}

		super.setInitializingLocaleOverideProperties(properties);
	}

	@Override
	public void setInitializingProperties(final Properties properties) {
		// first filter out sub-country entries, we handle those in a special manner
		for (Iterator<Entry<Object, Object>> iter = properties.entrySet().iterator(); iter.hasNext();) {
			Entry<Object, Object> entry = iter.next();
			String code = (String) entry.getKey();

			// check for a sub-country entry
			if (code.startsWith(Geography.SUB_COUNTRY_PROPERTY_PREFIX)) {
				Matcher matcher = SUB_COUNTRY_PROPERTY_KEY_PATTERN.matcher(code);
				matcher.find();
				String countryCode = matcher.group(1);
				String subCountryCode = matcher.group(2);

				Properties subCountryProps = subCountriesDefault.get(countryCode);
				if (subCountryProps == null) {
					subCountryProps = new Properties();
					subCountriesDefault.put(countryCode, subCountryProps);
				}

				subCountryProps.put(subCountryCode, entry.getValue());
				iter.remove();
			}
		}
		super.setInitializingProperties(properties);
	}

	@Override
	public String getCountryDisplayName(final String code) {
		return getCountryDisplayName(code, Locale.getDefault());
	}

	@Override
	public String getCountryDisplayName(final String code, final Locale locale) {
		return getProperty(locale, code);
	}

	@Override
	public Set<String> getCountryCodes() {
		return getAllPropertyKeys(Locale.getDefault());
	}

	@Override
	public String getSubCountryDisplayName(final String countryCode, final String subcountryCode) {
		return getSubCountryDisplayName(countryCode, subcountryCode, Locale.getDefault());
	}

	@Override
	public String getSubCountryDisplayName(final String countryCode, final String subcountryCode, final Locale locale) {
		return getPropertyFallback(locale, subcountryCode, subCountriesLocale.get(countryCode), subCountriesDefault.get(countryCode));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Set<String> getSubCountryCodes(final String countryCode) {
		Set<String> subCountriesKeys = new HashSet<String>();

		if (subCountriesDefault.containsKey(countryCode)) {
			subCountriesKeys.addAll((Set) subCountriesDefault.get(countryCode).keySet());
		}

		// should never have more data for one locale versus the default (for geography), this is just in case there is
		Locale locale = Locale.getDefault();
		if (subCountriesLocale.containsKey(countryCode) && subCountriesLocale.get(countryCode).containsKey(locale)) {
			subCountriesKeys.addAll((Set) subCountriesLocale.get(countryCode).get(locale).keySet());
		}
		return subCountriesKeys;
	}
}