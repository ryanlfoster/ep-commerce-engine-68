package com.elasticpath.domain.localization.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.elasticpath.commons.util.impl.LocaleUtils;
import com.elasticpath.domain.localization.LocaleFallbackPolicy;

/**
 * A policy which broadens all preferred locales.
 */
public class LocaleBroadeningFallbackPolicy implements LocaleFallbackPolicy   {

	private final List <Locale> locales = new ArrayList<Locale>();

	@Override
	public void setPreferredLocales(final Locale... locales) {
		this.locales.clear();
		this.locales.addAll(Arrays.asList(locales));
	}

	@Override
	public void addLocale(final Locale locale) {
			this.locales.add(locale);
	}
	/**
	 * @return list of broadened locales.
	 */
	@Override
	public List<Locale> getLocales() {
		if (locales.isEmpty()) {
			throw new MissingLocaleException("Did not specify locales in fallback policy.");
		}
		return broadenEachLocale(locales);
	}

	@Override
	public Locale getPrimaryLocale() {
		if (locales.isEmpty()) {
			throw new MissingLocaleException("Did not specify locales in fallback policy.");
		}
		return locales.get(0);
	}
	/**
	 * creates new list of locales by adding the locale then 
	 * the broadened locales by removing first the variant, then the country,
	 * and finally just the language will remain.
	 * 
	 * @param locales 
	 * @return a list of the locales which have been broadened
	 */
	private List <Locale> broadenEachLocale(final List <Locale> locales) {
		List <Locale> broadenedLocales = new ArrayList<Locale>();
		
		for (Locale locale : locales) {
			broadenedLocales.add(locale);
			//remove the variant if present
			Locale countryBroadenedLocale = LocaleUtils.broadenLocale(locale);
			if (!countryBroadenedLocale.equals(locale)) {
				broadenedLocales.add(countryBroadenedLocale);
				// remove the country, if present
				Locale languageBroadenedLocale = LocaleUtils.broadenLocale(countryBroadenedLocale);
				if (!languageBroadenedLocale.equals(countryBroadenedLocale)) {
					broadenedLocales.add(languageBroadenedLocale);
				}
			}
		}
		return broadenedLocales;
	}

}
