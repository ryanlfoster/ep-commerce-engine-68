package com.elasticpath.commons.util.impl;

import java.util.Locale;

/**
 * Static Utility class for Locale-related utility methods.
 */
public final class LocaleUtils {

	private LocaleUtils() {
		// Can not be instantiated
	}

	/**
	 * Attempts to broaden the given locale removing either the variant or the country in that
	 * order. If the variant and country are defined, the variant is always removed first. Locales
	 * that only have a language are returned as is.
	 *
	 * @param locale the locale to broaden
	 * @return a broadened locale
	 */
	public static Locale broadenLocale(final Locale locale) {
		if (locale == null) {
			throw new IllegalArgumentException("locale");
		}
		if (locale.getVariant() == null || "".equals(locale.getVariant())) {
			if (locale.getCountry() == null || "".equals(locale.getCountry())) {
				return locale;
			}
			return new Locale(locale.getLanguage());
		}
		return new Locale(locale.getLanguage(), locale.getCountry());
	}
}
