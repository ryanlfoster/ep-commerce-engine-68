package com.elasticpath.service.search.solr.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.impl.FilterOptionImpl;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexSearcherImpl;
import com.elasticpath.service.store.StoreService;

/**
 * Test case for {@link SolrIndexSearchResult}.
 */
public class SolrIndexSearchResultTest {

	/**
	 * Store code to use within all tests.
	 */
	private static final String TEST_STORE_CODE = "SAMPLE_STORECODE";
	
	/**
	 * Master catalog code.
	 */
	private static final String TEST_MASTER_CATALOG_CODE = "a master catalog code that no one would ever think of";

	private SolrIndexSearchResult solrIndexSearchResult;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private Searcher mockSearcher;

	private TestSolrSearcher testSolrSearcher;
	
	private CatalogService mockCatalogService;

	private StoreService mockStoreService;

	@Mock
	private ElasticPath elasticPath;

	
	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception in case of any errors
	 */
	@Before
	public void setUp() throws Exception {
		createMockCatalogService();
		
		solrIndexSearchResult = new SolrIndexSearchResult();
		testSolrSearcher = new TestSolrSearcher(elasticPath);
		solrIndexSearchResult.setIndexSearcher(testSolrSearcher);

		mockSearcher = context.mock(Searcher.class);
		testSolrSearcher.searcher = mockSearcher;
	}

	/**
	 * Test method for {@link SolrIndexSearchResult#getResults(int, int)}.
	 */
	@Test
	public void testGetResults() {
		final int startIndex = 0;
		final int maxResults = 10;
		context.checking(new Expectations() {
			{
				oneOf(mockSearcher).search(startIndex, maxResults);
			}
		});
		solrIndexSearchResult.getResults(startIndex, maxResults);
	}

	/**
	 * Test method for {@link SolrIndexSearchResult#getResults(int, int)} where we fall back to
	 * due to an empty page. This specifically tests where one of the fallback pages will have a
	 * start index that is negative (searches should not have negative start indexes).
	 */
	@Test
	public void testGetResultsFallback() {
		final int startIndex = 6; // needs to be above 5 for testing, but lower than 10
		final int maxResults = 10;
		context.checking(new Expectations() {
			{
				oneOf(mockSearcher).search(startIndex, maxResults);
			}
		});
		solrIndexSearchResult.getResults(startIndex, maxResults);
	}

	/**
	 * Test method for {@link SolrIndexSearchResult#getNumFound()}.
	 */
	@Test
	public void testNumFound() {
		final int startIndex = 0;
		final int maxResults = 0;
		context.checking(new Expectations() {
			{
				oneOf(mockSearcher).search(startIndex, maxResults);
			}
		});
		solrIndexSearchResult.getNumFound();
	}

	/**
	 * Test method for {@link SolrIndexSearchResult#getLastNumFound()}.
	 */
	@Test
	public void testGetLastNumFound() {
		final int startIndex = 0;
		final int maxResults = 0;
		context.checking(new Expectations() {
			{
				oneOf(mockSearcher).search(startIndex, maxResults);
			}
		});
		solrIndexSearchResult.getLastNumFound();

		// a second (or more attempts) should not invoke search (this result is cache as long as
		// at least 1 attempt has been made)
		final int maxTests = 10;
		for (int i = 0; i < maxTests; ++i) {
			solrIndexSearchResult.getLastNumFound();
		}
	}

	/**
	 * Test method for {@link SolrIndexSearchResult#getAllResults()}.
	 */
	@Test
	public void testGetAllResults() {
		final int startIndex = 0;
		final int maxResults = 0;
		final int numFound = 30;
		context.checking(new Expectations() {
			{
				oneOf(mockSearcher).search(startIndex, maxResults);
				oneOf(mockSearcher).search(startIndex, numFound);
			}
		});
		testSolrSearcher.setNumFoundOnSearch(numFound);
		solrIndexSearchResult.getAllResults();
	}

	/**
	 * Test method for {@link SolrIndexSearchResult#getAttributeRangeFilterOptions()}. This
	 * should never be null, even after a query that returns nothing.
	 */
	@Test
	public void testGetAttributeRangeFilterOptions() {
		assertNotNull(solrIndexSearchResult.getAttributeRangeFilterOptions());

		solrIndexSearchResult.setAttributeRangeFilterOptions(null);
		mockAllowAllSearcher();
		solrIndexSearchResult.getResults(0, 0);
	}

	private void mockAllowAllSearcher() {
		context.checking(new Expectations() {
			{
				allowing(mockSearcher).search(0, 0);
			}
		});
	}

	/**
	 * Test method for {@link SolrIndexSearchResult#getAttributeValueFilterOptions()}. This
	 * should never be null, even after a query that returns nothing.
	 */
	@Test
	public void testGetAttributeValueFilterOptions() {
		assertNotNull(solrIndexSearchResult.getAttributeValueFilterOptions());

		solrIndexSearchResult.setAttributeValueFilterOptions(null);
		mockAllowAllSearcher();
		solrIndexSearchResult.getResults(0, 0);
	}

	/**
	 * Test method for {@link SolrIndexSearchResult#getPriceFilterOptions()}. This should never
	 * be null, even after a query that returns nothing.
	 */
	@Test
	public void testGetPriceFilterOptions() {
		assertNotNull(solrIndexSearchResult.getPriceFilterOptions());

		solrIndexSearchResult.setPriceFilterOptions(null);
		mockAllowAllSearcher();
		solrIndexSearchResult.getResults(0, 0);
	}

	/**
	 * Test method for {@link SolrIndexSearchResult#getBrandFilterOptions()}. This should never
	 * be null, even after a query that returns nothing.
	 */
	@Test
	public void testGetBrandFilterOptions() {
		assertNotNull(solrIndexSearchResult.getBrandFilterOptions());

		solrIndexSearchResult.setBrandFilterOptions(null);
		mockAllowAllSearcher();
		solrIndexSearchResult.getResults(0, 0);
	}
	
	/**
	 * Test method for {@link SolrIndexSearchResult#getCategoryFilterOptions()}. This should never
	 * be null, even after a query that returns nothing.
	 */
	@Test
	public void testGetCategoryFilterOptions() {
		assertNotNull(solrIndexSearchResult.getCategoryFilterOptions());

		solrIndexSearchResult.setCategoryFilterOptions(null);
		mockAllowAllSearcher();
		solrIndexSearchResult.getResults(0, 0);
	}
	
	/**
	 * Test the {@link SolrIndexSearchResult#getStoreCatalog()} and  cache store to catalog cache.  
	 */
	@Test
	public void testGetStoreCatalog() {
		mockStoreService = context.mock(StoreService.class);
		context.checking(new Expectations() {
			{
				allowing(mockStoreService).getCatalogCodeForStore(with(any(String.class)));
				will(returnValue(TEST_MASTER_CATALOG_CODE));

				allowing(elasticPath).getBean(ContextIdNames.STORE_SERVICE);
				will(returnValue(mockStoreService));
			}
		});
		
		testSolrSearcher.getStoreCodeToCatalogCodeMap().clear();
		final Catalog catalog = testSolrSearcher.getStoreCatalog(TEST_STORE_CODE);

		assertEquals(TEST_MASTER_CATALOG_CODE, catalog.getCode());
		//be sure, that catalog in cache
		assertEquals(1, testSolrSearcher.getStoreCodeToCatalogCodeMap().size());
		assertEquals(TEST_MASTER_CATALOG_CODE, testSolrSearcher.getStoreCodeToCatalogCodeMap().get(TEST_STORE_CODE).getCode());
	}
	
	/**
	 * Test method for {@link SolrIndexSearchResult#getResults(int, int)} with negative numbers.
	 * No search should occur.
	 */
	@Test
	public void testWithNegativeNumbers() {
		final int negNum1 = -4;
		final int negNum2 = -535;
		final int negNum3 = -345345;
		final int negNum4 = -3445;
		
		assertNull(solrIndexSearchResult.getResultUids());
		solrIndexSearchResult.getResults(negNum1, negNum1);
		assertNotNull(solrIndexSearchResult.getResultUids());
		solrIndexSearchResult.getResults(negNum4, negNum2);
		assertNotNull(solrIndexSearchResult.getResultUids());
		solrIndexSearchResult.getResults(negNum4, negNum3);
		assertNotNull(solrIndexSearchResult.getResultUids());
	}
	
	/**
	 * Test method for {@link SolrIndexSearchResult#getResults(int, int)} with rememberOptions = false.
	 * Ensures that filter options are cleared.
	 */
	@Test
	public void testGetResultsWithoutRememberOptions() {
		testGetResults(false);
	}
	
	/**
	 * Test method for {@link SolrIndexSearchResult#getResults(int, int)} with rememberOptions = true.
	 * Ensures that filter options are NOT cleared.
	 */
	@Test
	public void testGetResultsWithRememberOptions() {
		testGetResults(true);
	}

	private void testGetResults(final boolean rememberOptions) {
		populateSearchResult();
		
		solrIndexSearchResult.setRememberOptions(rememberOptions);
		
		final int startIndex = 0;
		final int maxResults = 1;
		context.checking(new Expectations() {
			{
				oneOf(mockSearcher).search(startIndex, maxResults);
			}
		});
		
		solrIndexSearchResult.getResults(startIndex, maxResults); // calls clearAllFilters()
		
		assertTrue(solrIndexSearchResult.getResultUids().isEmpty());
		assertEquals(solrIndexSearchResult.getCategoryFilterOptions().isEmpty(), !rememberOptions);
		assertEquals(solrIndexSearchResult.getBrandFilterOptions().isEmpty(), !rememberOptions);
		assertEquals(solrIndexSearchResult.getPriceFilterOptions().isEmpty(), !rememberOptions);
		assertEquals(solrIndexSearchResult.getAttributeValueFilterOptions().isEmpty(), !rememberOptions);
		assertEquals(solrIndexSearchResult.getAttributeRangeFilterOptions().isEmpty(), !rememberOptions);
	}

	private void populateSearchResult() {
		final List<Long> results = new ArrayList<Long>();
		results.add(new Long(1L));
		final Map<Attribute, List<FilterOption<AttributeRangeFilter>>> attributeRangeFilterOptions = 
			new HashMap<Attribute, List<FilterOption<AttributeRangeFilter>>>();
		attributeRangeFilterOptions.put(new AttributeImpl(), null);
		final List<FilterOption<CategoryFilter>> categoryFilterOptions = new ArrayList<FilterOption<CategoryFilter>>();
		categoryFilterOptions.add(new FilterOptionImpl<CategoryFilter>());
		final List<FilterOption<BrandFilter>> brandFilterOptions = new ArrayList<FilterOption<BrandFilter>>();
		brandFilterOptions.add(new FilterOptionImpl<BrandFilter>());
		final List<FilterOption<PriceFilter>> priceFilterOptions = new ArrayList<FilterOption<PriceFilter>>();
		priceFilterOptions.add(new FilterOptionImpl<PriceFilter>());
		final Map<Attribute, List<FilterOption<AttributeValueFilter>>> attributeFilterOptions = 
			new HashMap<Attribute, List<FilterOption<AttributeValueFilter>>>();
		attributeFilterOptions.put(new AttributeImpl(), null);
		
		solrIndexSearchResult.setResultUids(results);
		solrIndexSearchResult.setCategoryFilterOptions(categoryFilterOptions);
		solrIndexSearchResult.setBrandFilterOptions(brandFilterOptions);
		solrIndexSearchResult.setPriceFilterOptions(priceFilterOptions);
		solrIndexSearchResult.setAttributeValueFilterOptions(attributeFilterOptions);
		solrIndexSearchResult.setAttributeRangeFilterOptions(attributeRangeFilterOptions);
	}
	
	/**
	 * Test getter and setter for rememberOptions flag.
	 */
	@Test
	public void testGetSetRememberOptions() {
		assertFalse("rememberOptions is not set yet, should be false", solrIndexSearchResult.isRememberOptions());
		solrIndexSearchResult.setRememberOptions(true);
		assertTrue("rememberOptions should be true", solrIndexSearchResult.isRememberOptions());
	}

	/**
	 * Stub class for testing purposes.
	 */
	private class TestSolrSearcher extends SolrIndexSearcherImpl {

		private Searcher searcher;

		private int numFound = 0;
		
		public TestSolrSearcher(final ElasticPath elasticPath) {
			super();
			setElasticPath(elasticPath);
		}

		@Override
		public void search(final SearchCriteria searchCriteria, final int startIndex, final int maxResults,
				final SolrIndexSearchResult searchResult) {
			final int five = 5;
			if (startIndex <= five) {
				searchResult.setNumFound(-1);
			}
			searcher.search(startIndex, maxResults);
			searchResult.setResultUids(new ArrayList<Long>());
			searchResult.setNumFound(numFound);
		}

		public void setNumFoundOnSearch(final int numFound) {
			this.numFound = numFound;
		}
		
		@Override
		public Catalog getStoreCatalog(final String storeCode) { //NOPMD
			return super.getStoreCatalog(storeCode);
		}
		
		@Override
		public Map<String, Catalog> getStoreCodeToCatalogCodeMap() { //NOPMD
			return super.getStoreCodeToCatalogCodeMap();			
		}
		
		@Override
		public StoreService getStoreService() { //NOPMD
			return super.getStoreService();
		}

		@Override
		public CatalogService getCatalogService() { //NOPMD
			return super.getCatalogService();
		}		
		
	}

	/**
	 * Stub interface for testing purposes.
	 */
	private interface Searcher {
		void search(int startIndex, int maxResults);
	}

	/**
	 * @return a mock catalog.
	 */
	private Catalog getCatalog() {
		Catalog catalog = new CatalogImpl();
		catalog.setCode(TEST_MASTER_CATALOG_CODE);
		return catalog;
	}
	
	private void createMockCatalogService() {
		mockCatalogService = context.mock(CatalogService.class);
		context.checking(new Expectations() {
			{
		
				allowing(mockCatalogService).findByCode(with(any(String.class)));
				will(returnValue(getCatalog()));

				allowing(elasticPath).getBean(ContextIdNames.CATALOG_SERVICE);
				will(returnValue(mockCatalogService));
			}
		});
	}

}
