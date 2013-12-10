package com.elasticpath.service.search.query;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test <code>CategorySearchCriteriaImpl</code>.
 */
public class CategorySearchCriteriaTest {


	private CategorySearchCriteria categorySearchCriteria;

	@Before
	public void setUp() throws Exception {
		this.categorySearchCriteria = new CategorySearchCriteria();
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.CategorySearchCriteriaImpl.isActiveOnly()'.
	 */
	@Test
	public void testIsActiveOnly() {
		assertFalse(this.categorySearchCriteria.isActiveOnly());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.CategorySearchCriteriaImpl.setActive(boolean)'.
	 */
	@Test
	public void testSetActiveOnly() {
		this.categorySearchCriteria.setActiveOnly(true);
		assertTrue(this.categorySearchCriteria.isActiveOnly());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.CategorySearchCriteriaImpl.isInActiveOnly()'.
	 */
	@Test
	public void testIsInActiveOnly() {
		assertFalse(this.categorySearchCriteria.isInActiveOnly());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.CategorySearchCriteriaImpl.setInActive(boolean)'.
	 */
	@Test
	public void testSetInActiveOnly() {
		this.categorySearchCriteria.setInActiveOnly(true);
		assertTrue(this.categorySearchCriteria.isInActiveOnly());
	}
}
