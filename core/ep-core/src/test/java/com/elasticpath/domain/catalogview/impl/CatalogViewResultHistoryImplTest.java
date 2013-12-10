package com.elasticpath.domain.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalogview.CatalogViewRequestUnmatchException;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.domain.catalogview.search.impl.SearchResultImpl;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test <code>SearchResultHistoryImplTest</code>.
 */
public class CatalogViewResultHistoryImplTest extends AbstractEPTestCase {


	private CatalogViewResultHistoryImpl catalogViewResultHistory;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		stubGetBean(ContextIdNames.SEARCH_RESULT, SearchResultImpl.class);
		
		this.catalogViewResultHistory = new CatalogViewResultHistoryImpl();
	}

	/**
	 * Test status changes of a <code>SearchResultHistory</code>.
	 */
	@Test
	public void testAddAndGet() { //NOPMD
		// Must returns <code>null</code> if not result is added.
		assertNull(this.catalogViewResultHistory.getLastResult());

		// Base search with only key words
		final SearchRequest mockBaseSearchRequest = context.mock(SearchRequest.class);
		CatalogViewResult baseSearchResult = this.catalogViewResultHistory.addRequest(mockBaseSearchRequest);
		assertNotNull(baseSearchResult);
		assertNull(baseSearchResult.getProducts());
		assertEquals(1, catalogViewResultHistory.size());
		baseSearchResult.setProducts(getProducts());
		baseSearchResult.setFeaturedProducts(baseSearchResult.getProducts());
		assertSame(baseSearchResult, this.catalogViewResultHistory.getLastResult());

		// Base search + one filter
		final SearchRequest mockSearchRequestWithOneFilter = context.mock(SearchRequest.class, "search request with one filter");
		context.checking(new Expectations() {
			{
				allowing(mockSearchRequestWithOneFilter).compare(mockBaseSearchRequest);
				will(returnValue(1));
			}
		});
		CatalogViewResult searchResultWithOneFilter = this.catalogViewResultHistory.addRequest(mockSearchRequestWithOneFilter);
		assertNotNull(searchResultWithOneFilter);
		assertEquals(2, catalogViewResultHistory.size());
		assertFalse(baseSearchResult.equals(searchResultWithOneFilter));
		assertFalse(baseSearchResult.getCatalogViewRequest().equals(searchResultWithOneFilter.getCatalogViewRequest()));
		assertSame(searchResultWithOneFilter, this.catalogViewResultHistory.getLastResult());

		// Base Search + one filter + sorter
		final SearchRequest mockSearchRequestWithFiltersAndSorter = context.mock(SearchRequest.class, "search request with one filter and sorter");
		context.checking(new Expectations() {
			{
				allowing(mockSearchRequestWithFiltersAndSorter).compare(mockSearchRequestWithOneFilter);
				will(returnValue(0));
			}
		});

		CatalogViewResult searchResultWithFiltersAndSorter = this.catalogViewResultHistory.addRequest(mockSearchRequestWithFiltersAndSorter);
		assertNotNull(searchResultWithFiltersAndSorter);
		assertEquals(2, catalogViewResultHistory.size());
		assertFalse(baseSearchResult.equals(searchResultWithFiltersAndSorter));
		assertFalse(baseSearchResult.getCatalogViewRequest().equals(searchResultWithFiltersAndSorter.getCatalogViewRequest()));
		assertSame(searchResultWithFiltersAndSorter, this.catalogViewResultHistory.getLastResult());

		// Base Search + one filter + sorter + max return number
		final SearchRequest mockSearchRequestWithFiltersAndSorterAndMaxReturnNumber = context.mock(SearchRequest.class,
				"search request with one filter and sorter and max return number");
		context.checking(new Expectations() {
			{
				allowing(mockSearchRequestWithFiltersAndSorterAndMaxReturnNumber).compare(mockSearchRequestWithFiltersAndSorter);
				will(returnValue(0));
			}
		});

		CatalogViewResult searchResultWithFiltersAndSorterAndMaxReturnNumber = this.catalogViewResultHistory
				.addRequest(mockSearchRequestWithFiltersAndSorterAndMaxReturnNumber);

		assertNotNull(searchResultWithFiltersAndSorterAndMaxReturnNumber);
		assertEquals(2, catalogViewResultHistory.size());
		assertFalse(baseSearchResult.equals(searchResultWithFiltersAndSorterAndMaxReturnNumber));
		assertFalse(baseSearchResult.getCatalogViewRequest().equals(searchResultWithFiltersAndSorterAndMaxReturnNumber.getCatalogViewRequest()));
		assertSame(searchResultWithFiltersAndSorterAndMaxReturnNumber, this.catalogViewResultHistory.getLastResult());

		// Base search + two filters
		final SearchRequest mockSearchRequestWithTwoFilters = context.mock(SearchRequest.class, "search request with two filters");
		context.checking(new Expectations() {
			{
				allowing(mockSearchRequestWithTwoFilters).compare(mockSearchRequestWithFiltersAndSorterAndMaxReturnNumber);
				will(returnValue(1));

//				allowing(mockSearchRequestWithTwoFilters).compare(searchRequestWithFiltersAndSorter);
//				will(returnValue(-1));
			}
		});

		CatalogViewResult searchResultWithTwoFilters = this.catalogViewResultHistory.addRequest(mockSearchRequestWithTwoFilters);
		assertNotNull(searchResultWithTwoFilters);
		final int size = 3;
		assertEquals(size, catalogViewResultHistory.size());
		assertFalse(searchResultWithFiltersAndSorterAndMaxReturnNumber.equals(searchResultWithTwoFilters));
		assertFalse(searchResultWithFiltersAndSorterAndMaxReturnNumber.getCatalogViewRequest().equals(
				searchResultWithTwoFilters.getCatalogViewRequest()));
		assertSame(searchResultWithTwoFilters, this.catalogViewResultHistory.getLastResult());

		// Return to one filter
		context.checking(new Expectations() {
			{
				allowing(mockSearchRequestWithFiltersAndSorterAndMaxReturnNumber).compare(mockSearchRequestWithTwoFilters);
				will(returnValue(-1));

				allowing(mockSearchRequestWithFiltersAndSorterAndMaxReturnNumber).compare(mockSearchRequestWithFiltersAndSorterAndMaxReturnNumber);
				will(returnValue(0));
			}
		});

		searchResultWithFiltersAndSorterAndMaxReturnNumber = this.catalogViewResultHistory
				.addRequest(mockSearchRequestWithFiltersAndSorterAndMaxReturnNumber);
		assertNotNull(searchResultWithFiltersAndSorterAndMaxReturnNumber);
		assertEquals(2, catalogViewResultHistory.size());
		assertFalse(searchResultWithTwoFilters.equals(searchResultWithFiltersAndSorterAndMaxReturnNumber));
		assertFalse(searchResultWithTwoFilters.getCatalogViewRequest().equals(
				searchResultWithFiltersAndSorterAndMaxReturnNumber.getCatalogViewRequest()));
		assertSame(searchResultWithFiltersAndSorterAndMaxReturnNumber, this.catalogViewResultHistory.getLastResult());

		// Base search with other key words
		final SearchRequest mockBaseSearchRequestWithOtherKeyWords = context.mock(SearchRequest.class, "search request with other keywords");
		context.checking(new Expectations() {
			{
				allowing(mockBaseSearchRequestWithOtherKeyWords).compare(mockSearchRequestWithFiltersAndSorterAndMaxReturnNumber);
				will(throwException(new CatalogViewRequestUnmatchException("Test!")));
			}
		});

		CatalogViewResult baseSearchResultWithOtherKeyWords = this.catalogViewResultHistory.addRequest(mockBaseSearchRequestWithOtherKeyWords);
		assertNotNull(baseSearchResultWithOtherKeyWords);
		assertNull(baseSearchResultWithOtherKeyWords.getProducts());
		assertEquals(1, catalogViewResultHistory.size());
		baseSearchResultWithOtherKeyWords.setProducts(getProducts());
		assertSame(baseSearchResultWithOtherKeyWords, this.catalogViewResultHistory.getLastResult());
	}

	private List<StoreProduct> getProducts() {
		return new ArrayList<StoreProduct>();
	}

	/**
	 * Test getSearchResultStack() method of a <code>SearchResultHistory</code>.
	 */
	@Test
	public void testGetSearchResultStack() {
		// Base search with only key words
		final SearchRequest mockBaseSearchRequest = context.mock(SearchRequest.class);

		CatalogViewResult baseSearchResult = this.catalogViewResultHistory.addRequest(mockBaseSearchRequest);
		baseSearchResult.setProducts(getProducts());
		baseSearchResult.setFeaturedProducts(baseSearchResult.getProducts());

		// Base search + one filter
		final SearchRequest mockSearchRequestWithOneFilter = context.mock(SearchRequest.class, "search request with one filter");
		context.checking(new Expectations() {
			{
				allowing(mockSearchRequestWithOneFilter).compare(mockBaseSearchRequest);
				will(returnValue(1));
			}
		});
		CatalogViewResult searchResultWithOneFilter = this.catalogViewResultHistory.addRequest(mockSearchRequestWithOneFilter);

		assertEquals(2, catalogViewResultHistory.size());

		List<CatalogViewResult> searchResultList = catalogViewResultHistory.getResultList();
		assertEquals(2, searchResultList.size());
		assertSame(baseSearchResult, searchResultList.get(0));
		assertSame(searchResultWithOneFilter, searchResultList.get(1));

		// The returned search result stack should be a copy
		searchResultList.clear();
		assertEquals(0, searchResultList.size());
		assertEquals(2, catalogViewResultHistory.size());
	}
}
