package com.elasticpath.domain.localization.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

/**
 * Test LocaleBroadeningFallbackPolicy.
 */
public class LocaleBroadeningFallbackPolicyTest {

	private final Locale languageLocale = new Locale("la");
	private final Locale countryLocale = new Locale(languageLocale.getLanguage(), "co");
	private final Locale variantLocale = new Locale(countryLocale.getLanguage(), countryLocale.getCountry(), "va");
	private final Locale secondaryLanguageLocale = Locale.GERMAN;
	private final Locale secondaryCountryLocale = Locale.GERMANY;
	private final LocaleBroadeningFallbackPolicy policy = new LocaleBroadeningFallbackPolicy();
	
	/**
	 * Test that locales are broadened from variant to language.
	 */
	@Test
	public void testALocaleIsBroadenedFromVariantToLangauge() {
		policy.setPreferredLocales(variantLocale);
		List <Locale> expectedLocales = Arrays.asList(variantLocale, countryLocale, languageLocale);
		assertEquals("The Locale should be broadened from variant to language", expectedLocales, policy.getLocales());
	}
	/**
	 * Test that locales are broadened from country to language.
	 */
	@Test
	public void testAllLocalesAreBroadened() {
		policy.setPreferredLocales(countryLocale);
		policy.addLocale(secondaryCountryLocale);
		List <Locale> expectedLocales = Arrays.asList(countryLocale, languageLocale, secondaryCountryLocale, secondaryLanguageLocale);
		assertEquals("All locales should be broadened", expectedLocales, policy.getLocales());
	}
	/**
	 * Test that setting preferred locales overwrites any existing ones.
	 */
	@Test
	public void testSetPreferredLocalesOverwritesExistingLocales() {
		policy.addLocale(languageLocale); // The one we want to overwrite
		policy.setPreferredLocales(secondaryLanguageLocale);
		assertEquals("Setting preferred locales should overwrite existing locales", Arrays.asList(secondaryLanguageLocale), policy.getLocales());
	}

	/**
	 * Test that locales are added after existing locales.
	 */
	@Test
	public void testAddedLocalesAreAddedAfterExistingLocales() {
		policy.setPreferredLocales(languageLocale);
		policy.addLocale(secondaryLanguageLocale);
		assertEquals("Added locale should come after existing locales.", Arrays.asList(languageLocale, secondaryLanguageLocale), policy.getLocales());
	}
	
	/**
	 * Test getting primary locale returns first locale in list.
	 */
	@Test
	public void testGetPrimaryLocale() {
		policy.setPreferredLocales(variantLocale, countryLocale);
		assertEquals("Primary locale should be first locale in preferred locales", policy.getPrimaryLocale(), variantLocale);
	}
	
	/**
	 * Test that getting the primary locale on an empty list throws MissingLocaleException.
	 */
	@Test(expected = MissingLocaleException.class)
	public void testGetPrimaryLocaleOnEmptyList() {
		policy.getPrimaryLocale();
	}
}
