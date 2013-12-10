package com.elasticpath.domain.localization.impl;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.localization.LocaleFallbackPolicy;

/**
 * Represents a locale fallback policy which returns what is inputed.
 */
public class SimpleLocaleFallbackPolicy implements LocaleFallbackPolicy {
	
	private final List <Locale> locales = new ArrayList<Locale>();
	
	@Override
	public void setPreferredLocales(final Locale ... locales) {
		this.locales.clear();
		this.locales.addAll(Arrays.asList(locales));
	}
	/**
	 * @return a list of locales as inputed
	 */
	@Override
	public List <Locale> getLocales() {
		return locales;
	}
	
	@Override
	public Locale getPrimaryLocale() {
		if (locales.isEmpty()) {
			throw new MissingLocaleException("Did not specify locales in fallback policy.");
		}
		return locales.get(0);
	}

	@Override
	public void addLocale(final Locale locale) {
		this.locales.add(locale);
	}
}
