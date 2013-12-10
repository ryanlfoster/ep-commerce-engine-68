package com.elasticpath.domain.catalogview.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;

/**
 * Test <code>SearchResultImpl</code>.
 */
public class SearchResultImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SearchRequestImpl searchRequest;

	private SearchResultImpl searchResult;


	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		// Create a search request
		SearchRequestImpl searchRequestImpl = new SearchRequestImpl();
		this.searchRequest = searchRequestImpl;

		// Create a search result
		this.searchResult = new SearchResultImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.SearchResultImpl.getProducts()'.
	 */
	@Test
	public void testGetProducts() {
		assertNull(this.searchResult.getProducts());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.SearchResultImpl.setProducts(List)'.
	 */
	@Test
	public void testSetProducts() {
		final List<StoreProduct> products = new ArrayList<StoreProduct>();
		this.searchResult.setProducts(products);
		assertSame(products, searchResult.getProducts());

		// Products can only be set once.
		try {
			this.searchResult.setProducts(products);
			fail("EpDomainException expected!");
		} catch (EpDomainException e) {
			// succeed
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.SearchResultImpl.getCatalogViewRequest()'.
	 */
	@Test
	public void testGetSearchRequest() {
		assertNull(this.searchResult.getCatalogViewRequest());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.SearchResultImpl.setCatalogViewRequest(List)'.
	 */
	@Test
	public void testSetSearchRequest() {
		this.searchResult.setCatalogViewRequest(searchRequest);
		assertSame(searchRequest, searchResult.getCatalogViewRequest());
	}

	/**
	 * Test replicateData(CatalogViewResult).
	 */
	@Test
	public void testReplicateData() {
		// Mock a request
		CatalogViewRequest mockCatalogViewRequest = context.mock(CatalogViewRequest.class);

		// Create new result
		CatalogViewResult newCatalogViewResult = new SearchResultImpl();
		newCatalogViewResult.setCatalogViewRequest(mockCatalogViewRequest);

		final List<StoreProduct> products = new ArrayList<StoreProduct>();
		this.searchResult.setCatalogViewRequest(this.searchRequest);
		this.searchResult.setProducts(products);

		StoreProduct featureProduct = new StoreProductImpl(new ProductImpl());
		final List<StoreProduct> featuredProducts = new ArrayList<StoreProduct>();
		featuredProducts.add(featureProduct);

		this.searchResult.setFeaturedProducts(featuredProducts);

		newCatalogViewResult.replicateData(this.searchResult);
		assertSame(searchResult.getProducts(), newCatalogViewResult.getProducts());
	}

	/**
	 * Test getSuggestions & setSuggestions.
	 */
	@Test
	public void testGetSetSuggestions() {
		assertNull(this.searchResult.getSuggestions());
		List<String> emptyList = Collections.emptyList();
		this.searchResult.setSuggestions(emptyList);
		assertEquals(emptyList, this.searchResult.getSuggestions());
	}
} // NOPMD
