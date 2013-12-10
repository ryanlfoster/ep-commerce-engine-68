package com.elasticpath.service.search.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Test <code>ProductSearchCriteriaImpl</code>.
 */
public class ProductSearchCriteriaTest {


	private ProductSearchCriteria productSearchCriteria;

	@Before
	public void setUp() throws Exception {
		this.productSearchCriteria = new ProductSearchCriteria();
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.search.impl.ProductSearchCriteriaImpl.getBrandCode()'.
	 */
	@Test
	public void testGetBrandCode() {
		assertNull(this.productSearchCriteria.getBrandCode());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.search.impl.ProductSearchCriteriaImpl.setBrandCode(String)'.
	 */
	@Test
	public void testSetBrandCode() {
		final String brandCode = "brandCode";
		this.productSearchCriteria.setBrandCode(brandCode);
		assertEquals(brandCode, this.productSearchCriteria.getBrandCode());

	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.search.impl.ProductSearchCriteriaImpl.isActiveOnly()'.
	 */
	@Test
	public void testIsActiveOnly() {
		assertFalse(this.productSearchCriteria.isActiveOnly());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.search.impl.ProductSearchCriteriaImpl.setActive(boolean)'.
	 */
	@Test
	public void testSetActiveOnly() {
		this.productSearchCriteria.setActiveOnly(true);
		assertTrue(this.productSearchCriteria.isActiveOnly());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.search.impl.ProductSearchCriteriaImpl.getCategoryUid()'.
	 */
	@Test
	public void testGetCategoryUid() {
		assertNull(this.productSearchCriteria.getAncestorCategoryUids());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.search.impl.ProductSearchCriteriaImpl.setCategoryUid(long)'.
	 */
	@Test
	public void testSetCategoryUid() {
		final long categoryUid = Long.MAX_VALUE;
		Set<Long> categoryUids = new HashSet<Long>(Arrays.asList(new Long[] { new Long(categoryUid) }));
		this.productSearchCriteria.setAncestorCategoryUids(categoryUids);
		assertEquals(categoryUids, this.productSearchCriteria.getAncestorCategoryUids());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.search.impl.ProductSearchCriteriaImpl.isInActiveOnly()'.
	 */
	@Test
	public void testIsInActiveOnly() {
		assertFalse(this.productSearchCriteria.isInActiveOnly());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.search.impl.ProductSearchCriteriaImpl.setInActive(boolean)'.
	 */
	@Test
	public void testSetInActiveOnly() {
		this.productSearchCriteria.setInActiveOnly(true);
		assertTrue(this.productSearchCriteria.isInActiveOnly());
	}
}
