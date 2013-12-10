package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

/**
 * Tests the {@link LocaleUtils} class.
 */
public class LocaleUtilsTest {
	/**
	 * Tests the broadenLocale method.
	 */
	@Test
	public void testBroadenLocale() {
		assertEquals("Trivial Case", Locale.ENGLISH, LocaleUtils.broadenLocale(Locale.ENGLISH));
		assertEquals("Remove Country", Locale.ENGLISH, LocaleUtils.broadenLocale(Locale.CANADA));
		assertEquals("Remove Country", Locale.FRENCH, LocaleUtils.broadenLocale(Locale.CANADA_FRENCH));
		assertEquals("Remove Variant", new Locale("es", "ES"), LocaleUtils.broadenLocale(new Locale("es", "ES", "Tradition_WIN")));
	}

	/**
	 * Tests that broadenLocale hates nulls.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testBroadenLocaleNullCase() {
		LocaleUtils.broadenLocale(null);
	}
}
