package com.elasticpath.commons.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test that the Pair class works as expected.
 */
@SuppressWarnings("PMD.UseAssertEqualsInsteadOfAssertTrue")
public class PairTest {

	/**
	 * Test that equals and hashCode works as expected - based on the equality of
	 * first and second.
	 */
	@Test
	public void testEqualsHashcode() {
		final Pair<Integer, String> pair1 = new Pair<Integer, String>(1, "fred");
		final Pair<Integer, String> pair2 = new Pair<Integer, String>(1, "fred");
		final Pair<Integer, String> pairDifferentFirst = new Pair<Integer, String>(1, "geoff");
		final Pair<Integer, String> pairDifferentSecond = new Pair<Integer, String>(2, "fred");

		final Pair<Integer, char[]> pairDifferentType = new Pair<Integer, char[]>(1, new char[] { 'f', 'r', 'e', 'd' });

		assertTrue(pair1.equals(pair1));
		assertEquals(pair1.hashCode(), pair1.hashCode());
		assertTrue(pair1.equals(pair2));
		assertTrue(pair2.equals(pair1));
		assertEquals(pair1.hashCode(), pair2.hashCode());

		assertFalse(pair1.equals(pairDifferentFirst));
		assertFalse(pair1.equals(pairDifferentSecond));

		assertFalse(pair1.equals(pairDifferentType));

	}

}
