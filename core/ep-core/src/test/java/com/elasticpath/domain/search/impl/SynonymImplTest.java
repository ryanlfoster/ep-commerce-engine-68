package com.elasticpath.domain.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.search.Synonym;

/**
 * Test case for {@link SynonymImpl}.
 */
public class SynonymImplTest {

	private SynonymImpl synonymImpl;

	@Before
	public void setUp() throws Exception {
		synonymImpl = new SynonymImpl();
	}

	/**
	 * Test method for {@link SynonymImpl#compareTo(Synonym)}.
	 */
	@Test
	public void testCompareTo() {
		Synonym other = new SynonymImpl();

		synonymImpl.setSynonym(null);
		other.setSynonym(null);
		assertEquals(0, synonymImpl.compareTo(other));

		final String syn = "some String";
		synonymImpl.setSynonym(syn);
		other.setSynonym(syn);
		assertEquals(0, synonymImpl.compareTo(other));

		synonymImpl.setSynonym("some synonym");
		other.setSynonym(null);
		assertTrue(synonymImpl.compareTo(other) > 0);
		assertTrue(other.compareTo(synonymImpl) < 0);

		synonymImpl.setSynonym(null);
		other.setSynonym("another term");
		assertTrue(synonymImpl.compareTo(other) < 0);
		assertTrue(other.compareTo(synonymImpl) > 0);

		synonymImpl.setSynonym("a");
		other.setSynonym("z");
		assertTrue(synonymImpl.compareTo(other) < 0);
		assertTrue(other.compareTo(synonymImpl) > 0);
	}

	/**
	 * Test method for {@link SynonymImpl#equals(Object)} with the same object.
	 */
	@Test
	public void testEqualsSameObject() {
		assertEquals("Should be equal.", synonymImpl, synonymImpl);
	}

	/**
	 * Test method for {@link SynonymImpl#equals(Object)} test against {@code null}.
	 */
	@Test
	public void testEqualsWithNulls() {
		final SynonymImpl nullSynonym = null;
		final String nullString = null;
		assertFalse(synonymImpl.equals(nullSynonym));
		assertFalse(synonymImpl.equals(nullString));
	}

	/**
	 * Test method for {@link SynonymImpl#equals(Object)}.
	 */
	@Test
	public void testEquals() {
		synonymImpl = new SynonymImpl();
		final SynonymImpl other = new SynonymImpl();
		final String synonym = "some synonym";
		final String synonym2 = "a different synonym";

		assertEquals("New objects should be equal", synonymImpl, other);
		assertEquals("New objects should be equal", other, synonymImpl);

		other.setSynonym(synonym);
		assertFalse(synonymImpl.equals(other));
		assertFalse(other.equals(synonymImpl));

		synonymImpl.setSynonym(synonym);
		assertEquals("Should be the same", synonymImpl, other);
		assertEquals("Should be the same", other, synonymImpl);

		synonymImpl.setSynonym(synonym2);
		assertFalse(synonymImpl.equals(other));
		assertFalse(other.equals(synonymImpl));
	}

	/**
	 * Test method for {@link SynonymImpl#equals(Object)} with strings.
	 */
	@Test
	public void testEqualsAgainstString() {
		final String synonym = "synonym";
		synonymImpl.setSynonym(synonym);

		assertEquals("Synonyms should be equal to a string", synonymImpl, synonym);
	}

	/**
	 * Tests that {@link SynonymImpl#hashCode()} has the same hash as it's synonym string.
	 */
	@Test
	public void testHashCodeAgainstString() {
		final String synonym = "some sdf synonym";
		synonymImpl.setSynonym(synonym);
		assertEquals(synonym.hashCode(), synonymImpl.hashCode());
	}

	/**
	 * Test method for {@link SynonymImpl#hashCode()} where the synonym is persistent.
	 */
	@Test
	public void testHasCodePersistent() {
		synonymImpl.setSynonym(null);
		final int hashCode = synonymImpl.hashCode();
		synonymImpl.setUidPk(2L);
		assertFalse("Hash codes should not be the same for persistent objects", synonymImpl.hashCode() == hashCode);
	}

	/**
	 * Tests that {@link SynonymImpl#hashCode()} has the same hash as another different instance
	 * when nothing has been modified.
	 */
	@Test
	public void testHashCodeWithNewObjects() {
		assertEquals(new SynonymImpl().hashCode(), new SynonymImpl().hashCode());
	}

	/**
	 * Test method for {@link SynonymImpl#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		synonymImpl = new SynonymImpl();
		final SynonymImpl other = new SynonymImpl();
		final String synonym = "some fsdf synonym";

		synonymImpl.setSynonym(synonym);
		other.setSynonym(synonym);
		assertEquals("Object with same synonym should have same hash code", synonymImpl.hashCode(), other.hashCode());

		final Map<SynonymImpl, String> testMap = new HashMap<SynonymImpl, String>();
		testMap.put(synonymImpl, "1");
		testMap.put(other, "2");
		assertEquals("Objects hash to same value, and are equal", 1, testMap.size());
		assertEquals("other should overwritten synonymImpl value (same hash, and equal)", "2", testMap.get(synonymImpl));
		assertEquals("other should overwritten synonymImpl value (same hash, and equal)", "2", testMap.get(other));

		other.setSynonym(null);
		testMap.put(other, "3");
		assertEquals(2, testMap.size());

		assertEquals("2", testMap.get(synonymImpl));
		assertEquals("3", testMap.get(other));
	}

	/**
	 * Test that {@link SynonymImpl#setSynonym(String)} trims it's string.
	 */
	@Test
	public void testSetSynonymTrim() {
		final String untrimmedString = "          some       whitespace         string        ";
		synonymImpl.setSynonym(untrimmedString);
		assertFalse("String's should not be equal", untrimmedString.equals(synonymImpl.getSynonym()));
		assertEquals(untrimmedString.trim(), synonymImpl.getSynonym());
	}
}
