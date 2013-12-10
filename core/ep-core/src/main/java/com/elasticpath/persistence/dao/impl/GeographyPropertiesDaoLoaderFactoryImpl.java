package com.elasticpath.persistence.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.core.io.Resource;

import com.elasticpath.domain.misc.Geography;

/**
 * Property loader class for {@link Geography}. Sub-countries are passed in the same initializing properties in the
 * following format:
 * <p>
 * <code>{@link Geography#SUB_COUNTRY_PROPERTY_PREFIX &lt;prefix&gt;} '.' &lt;country code&gt; '.' &lt;sub country code&gt;</code>
 * </p>
 * <p>
 * Sub-country files are assumed to be in the following format:
 * </p>
 * <p>
 * <code>&lt;subCountryPrefix&gt; '.' &lt;country code&gt; ['_' &lt;locale &gt;]</code>
 * </p>
 * <p>
 * where locale follows the same conventions as in {@link LocalePropertiesDaoLoaderFactoryImpl}.
 * </p>
 */
public class GeographyPropertiesDaoLoaderFactoryImpl extends LocalePropertiesDaoLoaderFactoryImpl<Geography> {

	private String subCountryFilePrefix;

	@Override
	protected Properties readAndMutateLocaleProperties(final Resource resource, final Locale locale, final InputStream stream)
			throws IOException {
		return mutateSubCountryProperties(resource, super.readAndMutateLocaleProperties(resource, locale, stream));
	}

	@Override
	protected String getResourcePrefix(final Resource resource) {
		String filename = resource.getFilename();
		if (filename.startsWith(subCountryFilePrefix) && filename.length() > subCountryFilePrefix.length()) {
			filename = removeFileExtension(filename);

			// format is <prefix> '.' <code> ['_' locale]
			int languageIdx = filename.indexOf('_', subCountryFilePrefix.length()); // codes shouldn't have '_'
			if (languageIdx < 0) {
				return filename;
			}
			return filename.substring(0, languageIdx);
		}
		return super.getResourcePrefix(resource);
	}

	private Properties mutateSubCountryProperties(final Resource resource, final Properties properties) {
		String filename = resource.getFilename();
		if (filename.startsWith(subCountryFilePrefix)) {
			filename = removeFileExtension(filename);
			filename = filename.substring(subCountryFilePrefix.length());

			// format from parent <prefix> '.' <country code> [ '.' <locale>]
			filename = filename.substring(1); // remove the '.' after prefix

			// remove locale if present
			int trailingDot = filename.indexOf('_');
			if (trailingDot > 0) {
				filename = filename.substring(0, trailingDot);
			}
			String countryCode = filename;

			Properties newProperties = new Properties();
			for (Entry<Object, Object> entry : properties.entrySet()) {
				String subCountryCode = (String) entry.getKey();
				newProperties.put(String.format("%s.%s.%s", Geography.SUB_COUNTRY_PROPERTY_PREFIX, countryCode, subCountryCode),
						entry.getValue());
			}
			return newProperties;
		}
		return properties;
	}

	@Override
	protected Properties readAndMutateProperties(final Resource resource, final InputStream stream) throws IOException {
		return mutateSubCountryProperties(resource, super.readAndMutateProperties(resource, stream));
	}

	public void setSubCountryFilePrefix(final String subCountryFilePrefix) {
		this.subCountryFilePrefix = subCountryFilePrefix;
	}
}
