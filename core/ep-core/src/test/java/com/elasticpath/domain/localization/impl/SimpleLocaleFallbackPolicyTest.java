package com.elasticpath.domain.localization.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

/**
 * Test a simple locale fallback policy.
 */
public class SimpleLocaleFallbackPolicyTest {
	
	private Locale languageLocale; 
	private Locale countryLocale;
	private Locale variantLocale;
	private SimpleLocaleFallbackPolicy policy;
	
	/**
	 * Set up before each test.
	 */
	@Before
	public void setUp() {
		languageLocale = new Locale("la");
		countryLocale  = new Locale(languageLocale.getLanguage(), "co");
		variantLocale = new Locale(countryLocale.getLanguage(), countryLocale.getCountry(), "va");
		policy = new SimpleLocaleFallbackPolicy();
	}
	/**
	 * Test for setPreferredLocales on empty list.
	 */
	@Test
	public void testSetPreferredLocalesOnEmptyList() {
		policy.setPreferredLocales(variantLocale, countryLocale, languageLocale);

		List <Locale> expectedLocales = Arrays.asList(variantLocale, countryLocale, languageLocale);

		assertEquals("Expected Locales should be same as set preferred locales", policy.getLocales(), expectedLocales);
	}
	/**
	 * Test set preferred locales clears existing locales.
	 */
	@Test
	public void testSetPreferredLocalesOnExistingList() {
		policy.addLocale(variantLocale);
		policy.setPreferredLocales(countryLocale, languageLocale);
		List <Locale> expectedLocales = Arrays.asList(countryLocale, languageLocale);

		assertEquals("Expected Locales should be same as set preferred locales", policy.getLocales(), expectedLocales);
	}
	/**
	 * Test for adding locales to an empty list.
	 */
	@Test
	public void testAddLocalesLocalesToEmptyList() {
		policy.addLocale(variantLocale);
		policy.addLocale(countryLocale);
		policy.addLocale(languageLocale);

		List <Locale> expectedLocales = Arrays.asList(variantLocale, countryLocale, languageLocale);

		assertEquals("Expected Locales should be same as added locales", policy.getLocales(), expectedLocales);
	}
	/**
	 * Test for add locales to existing list of locales.
	 */
	@Test
	public void testAddLocalesLocalesToExistingList() {
		policy.setPreferredLocales(variantLocale);
		policy.addLocale(countryLocale);
		
		List <Locale> expectedLocales = Arrays.asList(variantLocale, countryLocale);
		assertEquals("Expected Locales should be same as added locales", policy.getLocales(), expectedLocales);
	}
	
	/**
	 * Test for getPrimaryLocale.
	 */
	@Test
	public void testGetPrimaryLocale() {
		policy.setPreferredLocales(variantLocale, countryLocale);
		
		assertEquals("Primary locale should be first locale in preferred locales", policy.getPrimaryLocale(), variantLocale);
	}
	/**
	 * Test for getPrimaryLocale.
	 */
	@Test(expected = MissingLocaleException.class)
	public void testGetPrimaryLocaleOnEmptyList() {
		policy.getPrimaryLocale();
	}
}
