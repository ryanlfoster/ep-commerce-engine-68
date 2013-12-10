package com.elasticpath.service.search.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;

/**
 * Test cases for <code>AbstractSearchCriteriaImpl</code>.
 */
public class AbstractSearchCriteriaTest {


	private AbstractSearchCriteriaImpl searchCriteria;

	/**
	 * Prepare for the tests.
	 * 
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		this.searchCriteria = new AbstractSearchCriteriaImpl() {
			private static final long serialVersionUID = 1373296206083222462L;

			public void optimize() {
				// nothing for now
			}
			
			public IndexType getIndexType() {
				return null;
			}
		};
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AbstractSearchCriteriaImpl.getLocale()'.
	 */
	@Test
	public void testGetLocale() {
		assertNull(this.searchCriteria.getLocale());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AbstractSearchCriteriaImpl.setLocale(Locale)'.
	 */
	@Test
	public void testSetLocale() {
		this.searchCriteria.setLocale(Locale.ENGLISH);
		assertEquals(Locale.ENGLISH, this.searchCriteria.getLocale());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.ProductSearchCriteriaImpl.isInActiveOnly()'.
	 */
	@Test
	public void testIsFuzzySearchDisabled() {
		assertFalse(this.searchCriteria.isFuzzySearchDisabled());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.ProductSearchCriteriaImpl.setInActive(boolean)'.
	 */
	@Test
	public void testSetFuzzySearchDisabled() {
		this.searchCriteria.setFuzzySearchDisabled(true);
		assertTrue(this.searchCriteria.isFuzzySearchDisabled());
	}
}
