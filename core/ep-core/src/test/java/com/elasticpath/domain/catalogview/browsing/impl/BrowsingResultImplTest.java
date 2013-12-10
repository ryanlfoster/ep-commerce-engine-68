package com.elasticpath.domain.catalogview.browsing.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.browsing.BrowsingRequest;
import com.elasticpath.domain.catalogview.browsing.BrowsingResult;

/**
 * Test <code>BrowsingResultImpl</code>.
 */
public class BrowsingResultImplTest {

	private BrowsingResultImpl browsingResult;
	
	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {
		this.browsingResult = new BrowsingResultImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.BrowsingResultImpl.setCategory(Category)'.
	 */
	@Test
	public void testSetCategory() {
		assertNull(this.browsingResult.getCategory());
		final Category category = new CategoryImpl();
		this.browsingResult.setCategory(category);
		assertSame(category, this.browsingResult.getCategory());
	}

	/**
	 * Test replicateData(CatalogViewResult).
	 */
	@Test
	public void testReplicateData() {
		BrowsingRequest browsingRequest = new BrowsingRequestImpl();
		this.browsingResult.setCatalogViewRequest(browsingRequest);
		this.browsingResult.setProducts(new ArrayList<StoreProduct>());
		this.browsingResult.setFeaturedProducts(new ArrayList<StoreProduct>());

		final Category category = new CategoryImpl();
		this.browsingResult.setCategory(category);

		// Create new result
		BrowsingResult newBrowsingResult = new BrowsingResultImpl();

		newBrowsingResult.replicateData(this.browsingResult);
		assertSame(category, newBrowsingResult.getCategory());
		assertSame(browsingResult.getProducts(), newBrowsingResult.getProducts());
		assertSame(browsingResult.getFeaturedProducts(), newBrowsingResult.getFeaturedProducts());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalogview.browsing.impl.BrowsingResultImpl.setTopSellers()'.
	 */
	@Test
	public void testSetTopSellers() {
		assertNull(this.browsingResult.getTopSellers());
		final List<StoreProduct> topSellers = new ArrayList<StoreProduct>();
		this.browsingResult.setTopSellers(topSellers);
		assertSame(topSellers, this.browsingResult.getTopSellers());
	}
	
}
