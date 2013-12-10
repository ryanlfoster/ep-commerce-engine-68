package com.elasticpath.commons.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

/**
 * Test that the {@link PairInsensitiveString} class works as expected.
 */
@SuppressWarnings("PMD.UseAssertEqualsInsteadOfAssertTrue")
public class PairInsensitiveStringTest {
	
	/**
	 * Test that equals and hashCode works in case insensitive mode when pairs keep 
	 * string in upper and lower cases.
	 */
	@Test
	public void testEqualsHashcode() {
		final Locale locale = new Locale("en");
		
		final PairInsensitiveString firstPair = new PairInsensitiveString("elastic", "path", locale);
		final PairInsensitiveString secondPair = new PairInsensitiveString("Elastic", "Path", locale);
		final PairInsensitiveString thrirdPair = new PairInsensitiveString("Flexible", "Path", locale);
		
		assertTrue(firstPair.equals(secondPair));
		assertTrue(secondPair.equals(firstPair));
		
		assertEquals(firstPair.hashCode(), secondPair.hashCode());
		
		assertFalse(firstPair.equals(thrirdPair));
		assertFalse(thrirdPair.equals(secondPair));
		
	}
	
	/**
	 * Test that equals and hashCode works as expected with null values.
	 */
	@Test
	public void testEqualsHashcodeWithNullValues() {
		final Locale locale = new Locale("en");
		
		final PairInsensitiveString thrirdPair = new PairInsensitiveString(null, null, locale);
		
		PairInsensitiveString firstPair = new PairInsensitiveString("elastic", null, locale);
		PairInsensitiveString secondPair = new PairInsensitiveString("Elastic", null, locale);
		
		
		assertTrue(firstPair.equals(secondPair));
		assertTrue(secondPair.equals(firstPair));
		
		assertEquals(firstPair.hashCode(), secondPair.hashCode());

		
		assertFalse(firstPair.equals(thrirdPair));
		assertFalse(thrirdPair.equals(secondPair));
		
		firstPair = new PairInsensitiveString(null, "path", locale);
		secondPair = new PairInsensitiveString(null, "PatH", locale);

		assertTrue(firstPair.equals(secondPair));
		assertTrue(secondPair.equals(firstPair));
		
		assertEquals(firstPair.hashCode(), secondPair.hashCode());
		
		
	}
	

}
