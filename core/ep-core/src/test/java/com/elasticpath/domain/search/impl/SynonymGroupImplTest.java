package com.elasticpath.domain.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.search.Synonym;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test case for {@link SynonymGroupImpl}.
 */
public class SynonymGroupImplTest extends AbstractEPTestCase {

	private static final String CONCEPT_TERM = "concept term which no one should use";


	private SynonymGroupImpl synonymGroupImpl;

	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception in case of any errors
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		stubGetBean(ContextIdNames.SYNONYM, SynonymImpl.class);
		
		synonymGroupImpl = new SynonymGroupImpl();
		synonymGroupImpl.setConceptTerm(CONCEPT_TERM);
	}

	/**
	 * Test method for {@link SynonymGroupImpl#removeSynonym(String...)}.
	 */
	@Test
	public void testRemoveSynonym() {
		final Synonym mockSynonym1 = context.mock(Synonym.class);
		final String synonymStr1 = "some string";
		context.checking(new Expectations() {
			{
				allowing(mockSynonym1).getSynonym();
				will(returnValue(synonymStr1));
			}
		});
		final Synonym mockSynonym2 = context.mock(Synonym.class, "second synonym");
		final String synonymStr2 = "another string";
		context.checking(new Expectations() {
			{
				allowing(mockSynonym2).getSynonym();
				will(returnValue(synonymStr2));
			}
		});

		Set<Synonym> synonymSet = new HashSet<Synonym>(Arrays.asList(mockSynonym1, mockSynonym2));
		synonymGroupImpl.setSynonymsInternal(synonymSet);

		// test removing one
		assertSame(2, synonymGroupImpl.getSynonymsInternal().size());
		synonymGroupImpl.removeSynonyms(synonymStr2);
		assertSame(1, synonymGroupImpl.getSynonymsInternal().size());
		assertSame(mockSynonym1, synonymGroupImpl.getSynonymsInternal().iterator().next());

		final Synonym mockSynonym3 = context.mock(Synonym.class, "third synonym");
		final String synonymStr3 = "one more string";
		context.checking(new Expectations() {
			{
				allowing(mockSynonym3).getSynonym();
				will(returnValue(synonymStr3));
			}
		});

		synonymSet = new HashSet<Synonym>(Arrays.asList(mockSynonym1, mockSynonym2, mockSynonym3));
		synonymGroupImpl.setSynonymsInternal(synonymSet);

		// test removing two
		final int three = 3;
		assertSame(three, synonymGroupImpl.getSynonymsInternal().size());
		synonymGroupImpl.removeSynonyms(synonymStr3, synonymStr1);
		assertSame(1, synonymGroupImpl.getSynonymsInternal().size());
		assertSame(mockSynonym2, synonymGroupImpl.getSynonymsInternal().iterator().next());

		// test remove non-existing synonyms
		synonymGroupImpl.removeSynonyms(synonymStr1, synonymStr2, synonymStr3,
				"this is clearly not a synonym that we previously put in");
		assertTrue(synonymGroupImpl.getSynonymsInternal().isEmpty());
	}

	/**
	 * Test method for {@link SynonymGroupImpl#containsSynonym(String...)} with 0 or 1 synonyms.
	 */
	@Test
	public void testContainSynonym() {
		assertTrue("All synonyms contains the empty set.", new SynonymGroupImpl().containsSynonyms());
		assertTrue("All synonyms contains the empty set.", synonymGroupImpl.containsSynonyms());
		assertTrue("All synonyms contains the empty set.", synonymGroupImpl.containsSynonyms());

		final String equals = "my equals method";
		final String notEquals = "notEquals";
		final Synonym mockSynonym = context.mock(Synonym.class);
		context.checking(new Expectations() {
			{
				allowing(mockSynonym).getSynonym();
				will(returnValue(equals));
			}
		});

		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>(Arrays.asList(mockSynonym)));
		assertTrue("Should contain the equal value", synonymGroupImpl.containsSynonyms(equals));
		assertFalse("Should not contain the not equal value", synonymGroupImpl.containsSynonyms(notEquals));
		assertFalse("Doesn't contain both values", synonymGroupImpl.containsSynonyms(equals, notEquals));
	}

	/**
	 * Test method for {@link SynonymGroupImpl#containsSynonyms(String...)} with 2 synonyms.
	 */
	@Test
	public void testContainsWith2Synonyms() {
		final String equals = "my equals method";
		final String equals2 = "a second equals";
		final String notEquals = "notEquals";
		final Synonym mockSynonym = context.mock(Synonym.class);
		context.checking(new Expectations() {
			{
				allowing(mockSynonym).getSynonym();
				will(returnValue(equals));
			}
		});

		final Synonym mockSynonym2 = context.mock(Synonym.class, "second synonym");
		context.checking(new Expectations() {
			{
				allowing(mockSynonym2).getSynonym();
				will(returnValue(equals2));
			}
		});

		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>(Arrays.<Synonym> asList(mockSynonym,
				mockSynonym2)));
		assertTrue("Should contain the equal value", synonymGroupImpl.containsSynonyms(equals));
		assertTrue("Should contain the second equal value", synonymGroupImpl.containsSynonyms(equals2));
		assertFalse("Should not contain the not equal value", synonymGroupImpl.containsSynonyms(notEquals));
		assertTrue("Should contain the both equal values", synonymGroupImpl.containsSynonyms(equals, equals2));
		assertFalse("Doesn't contain notEquals", synonymGroupImpl.containsSynonyms(equals, notEquals));
		assertFalse(synonymGroupImpl.containsSynonyms(notEquals, equals));
		assertFalse("Doesn't contain notEquals", synonymGroupImpl.containsSynonyms(equals2, notEquals));
		assertFalse(synonymGroupImpl.containsSynonyms(notEquals, equals2));
		assertFalse(synonymGroupImpl.containsSynonyms(notEquals, notEquals));
	}

	/**
	 * Test method for {@link SynonymGroupImpl#containsSynonym(String...)}. Testing that this
	 * contains the concept term.
	 */
	@Test
	public void testContainsSynonymConceptTerm() {
		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>());
		assertTrue(synonymGroupImpl.containsSynonyms(CONCEPT_TERM));

		final Synonym mockSynonym = context.mock(Synonym.class);
		context.checking(new Expectations() {
			{
				allowing(mockSynonym).getSynonym();
				will(returnValue("Something weird"));
			}
		});
		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>(Arrays.<Synonym> asList(mockSynonym)));
		assertTrue(synonymGroupImpl.containsSynonyms(CONCEPT_TERM));
	}

	/**
	 * Test method for {@link SynonymGroupImpl#addSynonyms(String...)} where we are adding the
	 * concept term.
	 */
	@Test
	public void testAddSynonymsWithConceptTerm() {
		final String anotherTerm = "aaaaaaa";

		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>());

		assertEquals(0, synonymGroupImpl.getSynonymsInternal().size());
		synonymGroupImpl.addSynonyms(CONCEPT_TERM);
		assertEquals("Concept term should not have been added", 0, synonymGroupImpl.getSynonymsInternal().size());

		synonymGroupImpl.addSynonyms(anotherTerm, CONCEPT_TERM);
		assertEquals("Concept term should not have been added", 1, synonymGroupImpl.getSynonymsInternal().size());
		assertFalse("Added synonym should not be the concept term", synonymGroupImpl.getSynonymsInternal().iterator().next().getSynonym()
				.equals(CONCEPT_TERM));
	}

	/**
	 * Test method for {@link SynonymGroupImpl#addSynonyms(String...)} where we are adding an
	 * existing term.
	 */
	@Test
	public void testAddSynonymsExisting() {
		final String anotherTerm = "aaaaaaa";

		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>());

		assertEquals(0, synonymGroupImpl.getSynonymsInternal().size());
		synonymGroupImpl.addSynonyms(anotherTerm);
		assertEquals(1, synonymGroupImpl.getSynonymsInternal().size());
		
		synonymGroupImpl.addSynonyms(anotherTerm);
		assertEquals(1, synonymGroupImpl.getSynonymsInternal().size());
		
		synonymGroupImpl.addSynonyms(anotherTerm, anotherTerm, anotherTerm);
		assertEquals(1, synonymGroupImpl.getSynonymsInternal().size());
	}
	
	/**
	 * Test method for {@link SynonymGroupImpl#addSynonyms(String...)} with adding one after the
	 * other.
	 */
	@Test
	public void testAddSynonymsOneAfterOther() {
		final String term1 = "qqqqqqq";
		final String term2 = "33333333";
		
		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>());
		synonymGroupImpl.addSynonyms(term1);
		assertEquals(1, synonymGroupImpl.getSynonymsInternal().size());
		assertEquals(term1, synonymGroupImpl.getSynonymsInternal().iterator().next().getSynonym());
		
		synonymGroupImpl.addSynonyms(term2);
		assertEquals(2, synonymGroupImpl.getSynonymsInternal().size());
		
		boolean containsTerm1 = false;
		for (Synonym synonym : synonymGroupImpl.getSynonymsInternal()) {
			if (synonym.getSynonym().equals(term1)) {
				containsTerm1 = true;
				break;
			}
		}
		assertTrue("Group should contain term 1", containsTerm1);
		
		boolean containsTerm2 = false;
		for (Synonym synonym : synonymGroupImpl.getSynonymsInternal()) {
			if (synonym.getSynonym().equals(term2)) {
				containsTerm2 = true;
				break;
			}
		}
		assertTrue("Group should contain term 2", containsTerm2);
	}
	
	/**
	 * Test {@link SynonymGroupImpl#getSynonyms()} is readonly.
	 */
	@Test
	public void testGetSynonymsIsReadOnly() {
		try {
			synonymGroupImpl.getSynonyms().add(null);
			fail("Expected UnsupportedOperationException");
		} catch (UnsupportedOperationException e) { //NOPMD -- AvoidEmptyCatchBlocks
			// success
		}
	}
	
	/**
	 * Test method for {@link SynonymGroupImpl#addSynonyms(String...)} with adding 2 terms at the
	 * same time.
	 */
	@Test
	public void testAddSynonymsTogether() {
		final String term1 = "qqqqqqq";
		final String term2 = "33333333";
		
		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>());
		synonymGroupImpl.addSynonyms(term1, term2);
		assertEquals(2, synonymGroupImpl.getSynonymsInternal().size());
		
		boolean containsTerm1 = false;
		for (Synonym synonym : synonymGroupImpl.getSynonymsInternal()) {
			if (synonym.getSynonym().equals(term1)) {
				containsTerm1 = true;
				break;
			}
		}
		assertTrue("Group should contain term 1", containsTerm1);
		
		boolean containsTerm2 = false;
		for (Synonym synonym : synonymGroupImpl.getSynonymsInternal()) {
			if (synonym.getSynonym().equals(term2)) {
				containsTerm2 = true;
				break;
			}
		}
		assertTrue("Group should contain term 2", containsTerm2);
	}
	
	/**
	 * Test method for {@link SynonymGroupImpl#setSynonyms(String...)}.
	 */
	@Test
	public void testSetSynonyms() {
		final Synonym mockSynonymToKeep = context.mock(Synonym.class, "to keep");
		final Synonym mockSynonymToRemove = context.mock(Synonym.class, "to remove");
		final String toKeep = "existing string xxxxxxx";
		final String toAdd = "non existing string ssssssss";
		final String toRemove = "existing string to remove";
		context.checking(new Expectations() {
			{
				allowing(mockSynonymToKeep).getSynonym();
				will(returnValue(toKeep));

				allowing(mockSynonymToRemove).getSynonym();
				will(returnValue(toRemove));
			}
		});
		
		synonymGroupImpl.setSynonymsInternal(new LinkedHashSet<Synonym>(Arrays.<Synonym> asList(mockSynonymToKeep
				, mockSynonymToRemove)));
		assertEquals(2, synonymGroupImpl.getSynonymsInternal().size());
		
		synonymGroupImpl.setSynonyms(toKeep, toAdd);
		
		// check for removed term
		for (Synonym synonym : synonymGroupImpl.getSynonymsInternal()) {
			if (synonym.getSynonym().equals(toRemove)) {
				fail("Term " + toRemove + " should have been removed");
			}
		}
		
		// check for added term
		boolean termAdded = false;
		for (Synonym synonym : synonymGroupImpl.getSynonymsInternal()) {
			if (synonym.getSynonym().equals(toAdd)) {
				termAdded = true;
				break;
			}
		}
		assertTrue(toAdd + " should have been added", termAdded);
		
		// check for existing term
		boolean existingTermStillThere = false;
		for (Synonym synonym : synonymGroupImpl.getSynonymsInternal()) {
			if (synonym.getSynonym().equals(toKeep)) {
				existingTermStillThere = true;
				break;
			}
		}
		assertTrue(toKeep + " should have left in the set", existingTermStillThere);
	}
	
	/**
	 * Test that {@link SynonymGroupImpl#setSynonyms(String...)} maintains ordering. This is
	 * helpful for UI purposes.
	 */
	@Test
	public void testSetSynonymsOrder() {
		final Synonym mockSynonymToKeep = context.mock(Synonym.class, "synonym to keep");
		final Synonym mockSynonymToRemove = context.mock(Synonym.class, "synonym to add");
		final String toKeep = "existing string bbbbbbbbbb";
		final String toAdd = "non existing string sssssssss";
		final String toRemove = "existing string to remove";
		context.checking(new Expectations() {
			{
				allowing(mockSynonymToKeep).getSynonym();
				will(returnValue(toKeep));

				allowing(mockSynonymToRemove).getSynonym();
				will(returnValue(toRemove));
			}
		});

		final Synonym synonymToKeep = mockSynonymToKeep;
		final Synonym synonymToRemove = mockSynonymToRemove;

		synonymGroupImpl.setSynonymsInternal(new LinkedHashSet<Synonym>(Arrays.asList(synonymToKeep, synonymToRemove)));
		assertEquals(2, synonymGroupImpl.getSynonymsInternal().size());

		synonymGroupImpl.setSynonyms(toAdd, toKeep);

		final Iterator<Synonym> synonymIter = synonymGroupImpl.getSynonymsInternal().iterator();
		assertEquals(toAdd, synonymIter.next().getSynonym());
		assertEquals(synonymToKeep, synonymIter.next());
		assertFalse("Other elements should have been removed", synonymIter.hasNext());
	}
	
	/**
	 * Tests method for {@link SynonymGroupImpl#addSynonyms(java.util.List)}.
	 */
	@Test
	public void testAddSynonymsList() {
		final Synonym mockSynonymToKeep = context.mock(Synonym.class, "synonym to keep");
		final Synonym mockSynonymToAdd = context.mock(Synonym.class, "synonym to add");
		final String toKeep = "existing string";
		final String toAdd = "non existing string";
		context.checking(new Expectations() {
			{
				allowing(mockSynonymToKeep).getSynonym();
				will(returnValue(toKeep));

				allowing(mockSynonymToAdd).getSynonym();
				will(returnValue(toAdd));
			}
		});

		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>(Arrays.<Synonym> asList(mockSynonymToKeep)));
		assertEquals(1, synonymGroupImpl.getSynonymsInternal().size());
		synonymGroupImpl.addSynonyms(Arrays.<Synonym> asList(mockSynonymToAdd));
		assertEquals(2, synonymGroupImpl.getSynonymsInternal().size());
	}
	
	/**
	 * Test method for {@link SynonymGroupImpl#setSynonyms(java.util.List)}.
	 */
	@Test
	public void testSetSynonymsList() {
		final Synonym mockSynonymToKeep = context.mock(Synonym.class, "synonym to keep");
		final Synonym mockSynonymToAdd = context.mock(Synonym.class, "synonym to add");
		final String toKeep = "existing string";
		final String toAdd = "non existing string";
		context.checking(new Expectations() {
			{
				allowing(mockSynonymToKeep).getSynonym();
				will(returnValue(toKeep));

				allowing(mockSynonymToAdd).getSynonym();
				will(returnValue(toAdd));
			}
		});

		final Synonym synonymToAdd = mockSynonymToAdd;

		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>(Arrays.<Synonym> asList(mockSynonymToKeep)));
		assertEquals(1, synonymGroupImpl.getSynonymsInternal().size());
		synonymGroupImpl.setSynonyms(Arrays.asList(synonymToAdd));
		assertEquals(1, synonymGroupImpl.getSynonymsInternal().size());
		assertEquals(synonymToAdd, synonymGroupImpl.getSynonymsInternal().iterator().next());
	}
	
	/**
	 * Test method for {@link SynonymGroupImpl#setSynonyms(java.util.List)} maintains ordering.
	 * This is useful for UI purposes.
	 */
	@Test
	public void testSetSynonymListOrder() {
		final Synonym mockSynonym1 = context.mock(Synonym.class, "first");
		final Synonym mockSynonym2 = context.mock(Synonym.class, "second");
		final String toKeep = "existing string";
		final String toAdd = "non existing string";
		context.checking(new Expectations() {
			{
				allowing(mockSynonym1).getSynonym();
				will(returnValue(toKeep));

				allowing(mockSynonym2).getSynonym();
				will(returnValue(toAdd));
			}
		});

		final Synonym synonym1 = mockSynonym1;
		final Synonym synonym2 = mockSynonym2;

		synonymGroupImpl.setSynonyms(Arrays.asList(synonym1, synonym2));
		assertEquals(2, synonymGroupImpl.getSynonymsInternal().size());

		final Iterator<Synonym> synonymIterator = synonymGroupImpl.getSynonymsInternal().iterator();
		assertEquals(synonym1, synonymIterator.next());
		assertEquals(synonym2, synonymIterator.next());
		assertFalse(synonymIterator.hasNext());
	}
	
	/**
	 * Test that {@link SynonymGroupImpl#setSynonyms(String...)} will clear the synonyms with
	 * null/empty list.
	 */
	@Test
	public void testSetSynonymWillClear() {
		final Synonym mockSynonym = context.mock(Synonym.class);
		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>(Arrays.<Synonym> asList(mockSynonym)));
		assertEquals(1, synonymGroupImpl.getSynonymsInternal().size());
		synonymGroupImpl.setSynonyms((String[]) null);
		assertTrue(synonymGroupImpl.getSynonymsInternal().isEmpty());
		
		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>(Arrays.<Synonym> asList(mockSynonym)));
		assertEquals(1, synonymGroupImpl.getSynonymsInternal().size());
		synonymGroupImpl.setSynonyms();
		assertTrue(synonymGroupImpl.getSynonymsInternal().isEmpty());
	}
	
	/**
	 * Test that {@link SynonymGroupImpl#addSynonyms(String...)} doesn't add an empty string or
	 * strings that are just whitespace to it's synonyms.
	 */
	@Test
	public void testAddSynonymWhitespace() {
		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>());
		assertTrue(synonymGroupImpl.getSynonymsInternal().isEmpty());
		synonymGroupImpl.addSynonyms("", "         ", "\t");
		assertTrue(synonymGroupImpl.getSynonymsInternal().isEmpty());
	}
	
	/**
	 * Test method for {@link SynonymGroupImpl#addSynonyms(String...)} passing in a {@code null}.
	 * This should do nothing and not throw exceptions.
	 */
	@Test
	public void testAddSynonymWithNull() {
		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>());
		assertTrue(synonymGroupImpl.getSynonymsInternal().isEmpty());
		synonymGroupImpl.addSynonyms((String[]) null);
		assertTrue(synonymGroupImpl.getSynonymsInternal().isEmpty());
	}
	
	/**
	 * Test method for {@link SynonymGroupImpl#removeSynonyms(String...)} passing in a
	 * {@code null}. This should do nothing and not throw exceptions.
	 */
	@Test
	public void testRemoveSynonymWithNull() {
		synonymGroupImpl.setSynonymsInternal(new HashSet<Synonym>());
		assertTrue(synonymGroupImpl.getSynonymsInternal().isEmpty());
		synonymGroupImpl.removeSynonyms((String[]) null);
		assertTrue(synonymGroupImpl.getSynonymsInternal().isEmpty());
	}
}
