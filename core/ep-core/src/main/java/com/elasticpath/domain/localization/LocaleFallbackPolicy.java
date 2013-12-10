package com.elasticpath.domain.localization;

import java.util.List;
import java.util.Locale;

/**
 * Represents an object with a fallback policy for locales.
 *
 */
public interface LocaleFallbackPolicy {
	
	/**
	 * @param locales list of Locales in order of fallback preference
	 */
	void setPreferredLocales(Locale ... locales);
	
	/**
	 * Adds a locale to the list of preferred fallback locales.
	 * 
	 * @param locale the lcoale to add
	 */
	void addLocale(Locale locale);
	
	/**
	 * @return list of locales in order defined by policy
	 */
	List <Locale> getLocales();
	
	/**
	 * @return returns the first locale in the list of preferred fallback locales
	 */
	Locale getPrimaryLocale();
	
}