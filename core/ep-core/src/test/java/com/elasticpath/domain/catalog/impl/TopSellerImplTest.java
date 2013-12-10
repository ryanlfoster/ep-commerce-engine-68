package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.TopSeller;
import com.elasticpath.domain.catalog.TopSellerProduct;

/**
 * Test <code>TopSellerImpl</code>.
 */
public class TopSellerImplTest {

	private TopSeller topSeller;

	@Before
	public void setUp() throws Exception {
		this.topSeller = new TopSellerImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.TopSellerImpl.getCategoryUid()'.
	 */
	@Test
	public void testGetCategoryUid() {
		assertEquals(0, topSeller.getCategoryUid());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.TopSellerImpl.setCategoryUid(long)'.
	 */
	@Test
	public void testSetCategoryUid() {
		topSeller.setCategoryUid(Long.MAX_VALUE);
		assertEquals(Long.MAX_VALUE, topSeller.getCategoryUid());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.TopSellerImpl.getTopSellerProducts()'.
	 */
	@Test
	public void testGetTopSellerProducts() {
		assertNotNull(topSeller.getTopSellerProducts());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.TopSellerImpl.setTopSellerProducts(Map)'.
	 */
	@Test
	public void testSetTopSellerProducts() {
		final Map<Long, TopSellerProduct> topSellerProducts = new HashMap<Long, TopSellerProduct>();
		final Long productUid1 = new Long(1L);
		final Long productUid2 = new Long(2L);

		topSellerProducts.put(productUid1, new TopSellerProductImpl());
		topSellerProducts.put(productUid2, new TopSellerProductImpl());
		topSeller.setTopSellerProducts(topSellerProducts);
		assertSame(topSellerProducts, topSeller.getTopSellerProducts());
		assertTrue(topSeller.getProductUids().contains(productUid1));
		assertTrue(topSeller.getProductUids().contains(productUid2));
	}
}
