package com.elasticpath.service.search.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Map.Entry;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.lang.NullArgumentException;

import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.query.FilteredSearchCriteria.Relationship;

/**
 * Test case for {@link FilteredSearchCriteria}.
 */
public class FilteredSearchCriteriaTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private FilteredSearchCriteria<SearchCriteria> searchCriteria;
	
	/**
	 * Prepare for the tests.
	 * 
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		searchCriteria = new FilteredSearchCriteria<SearchCriteria>();
	}
	
	/**
	 * Test case for empty criteria.
	 */
	@Test
	public void testEmptyCriteria() {
		assertTrue(searchCriteria.isEmpty());
		assertSame(0, searchCriteria.size());
	}
	
	/**
	 * Test method for {@link FilteredSearchCriteria#addCriteria(SearchCriteria, SearchCriteria...)}.
	 */
	@Test
	public void testAddCriteria() {
		final SearchCriteria mockSearchCriteria1 = context.mock(SearchCriteria.class);
		context.checking(new Expectations() {
			{
				allowing(mockSearchCriteria1).getIndexType();
				will(returnValue(IndexType.PRODUCT));
			}
		});
		final SearchCriteria searchCriteria1 = mockSearchCriteria1;
		
		final SearchCriteria mockSearchCriteria2 = context.mock(SearchCriteria.class, "second search crit");
		context.checking(new Expectations() {
			{
				allowing(mockSearchCriteria2).getIndexType();
				will(returnValue(IndexType.CATEGORY));
			}
		});
		final SearchCriteria searchCriteria2 = mockSearchCriteria2;
		
		final SearchCriteria mockSearchCriteria3 = context.mock(SearchCriteria.class, "third search crit");
		context.checking(new Expectations() {
			{
				allowing(mockSearchCriteria3).getIndexType();
				will(returnValue(IndexType.CUSTOMER));
			}
		});
		final SearchCriteria searchCriteria3 = mockSearchCriteria3;
		
		searchCriteria.addCriteria(searchCriteria1);
		
		try {
			searchCriteria.addCriteria(searchCriteria2);
			fail("IllegalArgumentException excepted");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
		
		try {
			searchCriteria.addCriteria(searchCriteria2, searchCriteria3);
			fail("IllegalArgumentException excepted");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
		
		try {
			searchCriteria.addCriteria(searchCriteria1, searchCriteria3);
			fail("IllegalArgumentException excepted");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
		
		searchCriteria.addCriteria(searchCriteria1, searchCriteria1);
		searchCriteria.addCriteria(searchCriteria1);
		
		final int three = 3;
		assertFalse(searchCriteria.isEmpty());
		assertSame(three, searchCriteria.size());
		for (Entry<SearchCriteria, Collection<SearchCriteria>> entry : searchCriteria) {
			assertEquals(searchCriteria1, entry.getKey());
		}
	}
	
	/**
	 * Test method for {@link FilteredSearchCriteria#removeCriteria(SearchCriteria)}.
	 */
	@Test
	public void testRemoveCriteria() {
		final SearchCriteria mockSearchCriteria = context.mock(SearchCriteria.class);
		context.checking(new Expectations() {
			{
				allowing(mockSearchCriteria).getIndexType();
				will(returnValue(IndexType.PRODUCT));
			}
		});
		final SearchCriteria searchCriteria = mockSearchCriteria;
		
		this.searchCriteria.addCriteria(searchCriteria);
		assertTrue(this.searchCriteria.removeCriteria(searchCriteria));
		assertTrue(this.searchCriteria.isEmpty());
		assertSame(0, this.searchCriteria.size());
		
		for (Entry<SearchCriteria, Collection<SearchCriteria>> entry : this.searchCriteria) {
			assertNotSame(searchCriteria, entry.getKey());
		}
	}
	
	/**
	 * Test method for {@link FilteredSearchCriteria#getCriteria(int)}.
	 */
	@Test
	public void testGetCriteria() {
		final SearchCriteria mockSearchCriteria1 = context.mock(SearchCriteria.class);
		context.checking(new Expectations() {
			{
				allowing(mockSearchCriteria1).getIndexType();
				will(returnValue(IndexType.PRODUCT));
			}
		});
		final SearchCriteria searchCriteria1 = mockSearchCriteria1;
		
		final SearchCriteria mockSearchCriteria2 = context.mock(SearchCriteria.class, "second search crit");
		context.checking(new Expectations() {
			{
				allowing(mockSearchCriteria2).getIndexType();
				will(returnValue(IndexType.PRODUCT));
			}
		});
		final SearchCriteria searchCriteria2 = mockSearchCriteria2;
		
		searchCriteria.addCriteria(searchCriteria1);
		searchCriteria.addCriteria(searchCriteria2);
		
		assertSame(searchCriteria1, searchCriteria.getCriteria(0).getKey());
		assertSame(searchCriteria2, searchCriteria.getCriteria(1).getKey());
	}
	
	/**
	 * Test method for {@link FilteredSearchCriteria#setRelationship(Relationship)}.
	 */
	@Test
	public void testSetRelationship() {
		final Relationship and = Relationship.AND;
		searchCriteria.setRelationship(and);
		assertSame(and, searchCriteria.getRelationship());
		
		try {
			searchCriteria.setRelationship(null);
			fail("NullArgumentException expected");
		} catch (NullArgumentException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 * Test method for {@link FilteredSearchCriteria#getIndexType()}.
	 */
	@Test
	public void testGetIndexType() {
		assertNull(searchCriteria.getIndexType());
		final IndexType indexType = IndexType.PRODUCT;
		
		final SearchCriteria mockSearchCriteria1 = context.mock(SearchCriteria.class);
		context.checking(new Expectations() {
			{
				allowing(mockSearchCriteria1).getIndexType();
				will(returnValue(indexType));
			}
		});
		final SearchCriteria searchCriteria1 = mockSearchCriteria1;
		
		searchCriteria.addCriteria(searchCriteria1);
		assertSame(indexType, searchCriteria.getIndexType());
		
		searchCriteria.removeCriteria(searchCriteria1);
		assertNull(searchCriteria.getIndexType());
	}
}
