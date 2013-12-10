package com.elasticpath.domain.catalogview.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Currency;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewRequestUnmatchException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.SortUtility;
import com.elasticpath.domain.catalogview.impl.BrandFilterImpl;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * Test <code>SearchRequestImpl</code>.
 */
public class SearchRequestImplTest {
	private static final long DIGITAL_CAMERA_CID = 688129L;

	private static final String CAT_3_PRICE_5_BRAND_8 = "cat-3  price-5  brand-8";

	private static final String CAT_4 = "cat-4";

	private static final String CAT_3 = "cat-3";

	private static final String DIGITAL_AND_CAMERA = "digital+camera";

	private static final String DIGITAL_CAMERA = "digital camera";

	private static final String PRICE_ASC = SortUtility.constructSortTypeOrderString(StandardSortBy.PRICE, SortOrder.ASCENDING);
	
	private static final String FEATURED_DESC = SortUtility.constructSortTypeOrderString(StandardSortBy.FEATURED_CATEGORY,
			SortOrder.DESCENDING);

	private SearchRequest searchRequest;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private FilterFactory mockFilterFactory;

	private FilterFactory filterFactory;

	private Filter<?> mockFilterOfCategory;

	private Filter<?> mockFilterOfPrice;

	private Filter<?> mockFilterOfBrand;

	private BrandService mockBrandService;

	private BrandService brandService;

	private ElasticPath mockElasticPath;

	private Store mockStore;
	
	private Store store;

	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		mockElasticPath = context.mock(ElasticPath.class);
		mockStore = context.mock(Store.class);
		store = mockStore;

		setupFilterFactory();
		setupBrandService();
		this.searchRequest = getSearchRequest();
	}

	private SearchRequestImpl getSearchRequest() {
		SearchRequestImpl searchRequestImpl = new SearchRequestImpl();
		searchRequestImpl.setFilterFactory(this.filterFactory);
		return searchRequestImpl;
	}

	private void setupBrandService() {
		this.mockBrandService = context.mock(BrandService.class);
		this.brandService = this.mockBrandService;
		context.checking(new Expectations() {
			{
				allowing(mockElasticPath).getBean("brandService");
				will(returnValue(brandService));
			}
		});
	}

	private void setupFilterFactory() {
		// Mock FilterFactory
		this.mockFilterFactory = context.mock(FilterFactory.class);
		this.filterFactory = this.mockFilterFactory;
		context.checking(new Expectations() {
			{
				allowing(mockElasticPath).getBean("filterFactory");
				will(returnValue(filterFactory));
			}
		});

		mockFilterOfCategory = context.mock(Filter.class);
		context.checking(new Expectations() {
			{
				allowing(mockFilterOfCategory).getId();
				will(returnValue(CAT_3));

				allowing(mockFilterOfCategory).getDisplayName(with(any(Locale.class)));
				will(returnValue(CAT_3));

				allowing(mockFilterFactory).getFilter(with(CAT_3), with(any(Store.class)));
				will(returnValue(mockFilterOfCategory));
			}
		});

		mockFilterOfCategory = context.mock(Filter.class, "another filter");
		context.checking(new Expectations() {
			{
				allowing(mockFilterOfCategory).getId();
				will(returnValue(CAT_4));

				allowing(mockFilterOfCategory).getDisplayName(with(any(Locale.class)));
				will(returnValue(CAT_4));

				allowing(mockFilterFactory).getFilter(with(CAT_4), with(any(Store.class)));
				will(returnValue(mockFilterOfCategory));
			}
		});

		mockFilterOfPrice = context.mock(Filter.class, "price filter");
		context.checking(new Expectations() {
			{
				allowing(mockFilterOfPrice).getId();
				will(returnValue("price-5"));

				allowing(mockFilterOfPrice).getDisplayName(with(any(Locale.class)));
				will(returnValue("price-5"));

				allowing(mockFilterFactory).getFilter(with("price-5"), with(any(Store.class)));
				will(returnValue(mockFilterOfPrice));
			}
		});

		mockFilterOfBrand = context.mock(Filter.class, "brand filter");
		context.checking(new Expectations() {
			{
				allowing(mockFilterOfBrand).getId();
				will(returnValue("brand-8"));

				allowing(mockFilterOfBrand).getDisplayName(with(any(Locale.class)));
				will(returnValue("brand-8"));

				allowing(mockFilterFactory).getFilter(with("brand-8"), with(any(Store.class)));
				will(returnValue(mockFilterOfBrand));
			}
		});
	}

	/**
	 * Test <code>SearchRequest</code> setKeyWords(String) method.
	 */
	@Test
	public void testSetKeyWords() {
		searchRequest.setKeyWords(DIGITAL_CAMERA);

		assertNotNull(searchRequest.getKeyWords());
		assertNull(searchRequest.getSortType());
		assertEquals(0, searchRequest.getFilters().size());
	}

	/**
	 * Test <code>SearchRequest</code> with a sorter.
	 */
	@Test
	public void testSetSorter() {
		searchRequest.setKeyWords(DIGITAL_CAMERA);
		searchRequest.parseSorterIdStr(PRICE_ASC);
		searchRequest.setFiltersIdStr(null, store);

		assertNotNull(searchRequest.getKeyWords());
		assertNotNull(searchRequest.getSortType());
		assertEquals(0, searchRequest.getFilters().size());
	}

	/**
	 * Test <code>SearchRequest</code> with filters.
	 */
	@Test
	public void testSetFilters() {

		searchRequest.setKeyWords(DIGITAL_CAMERA);
		searchRequest.parseSorterIdStr(PRICE_ASC);
		final String filtersStr = CAT_3_PRICE_5_BRAND_8;
		final int filterNumber = filtersStr.split("\\s+").length;
		searchRequest.setFiltersIdStr(filtersStr, store);

		assertNotNull(searchRequest.getKeyWords());
		assertEquals(filterNumber, searchRequest.getFilters().size());

		// Sorter number can noly be set once.
		try {
			searchRequest.setFiltersIdStr(filtersStr, null);
			fail("EpDomainException expected!");
		} catch (EpDomainException e) {
			// succeed
			assertNotNull(e);
		}
	}

	/**
	 * Test method getQueryString of <code>SearchRequest</code>.
	 */
	@Test
	public void testGetQueryStringFull() {
		searchRequest.setCategoryUid(DIGITAL_CAMERA_CID);
		searchRequest.setKeyWords(DIGITAL_CAMERA);
		searchRequest.parseSorterIdStr(PRICE_ASC);
		final String filtersStr = CAT_3_PRICE_5_BRAND_8;
		searchRequest.setFiltersIdStr(filtersStr, store);

		StringBuffer sbf = new StringBuffer();
		sbf.append(WebConstants.REQUEST_CATEGORY_ID).append(WebConstants.SYMBOL_EQUAL).append(DIGITAL_CAMERA_CID).append(
				WebConstants.SYMBOL_AND);
		sbf.append(WebConstants.REQUEST_KEYWORDS).append(WebConstants.SYMBOL_EQUAL).append(DIGITAL_AND_CAMERA).append(
				WebConstants.SYMBOL_AND).append(WebConstants.REQUEST_FILTERS).append(WebConstants.SYMBOL_EQUAL).append(
				"cat-3+price-5+brand-8").append(WebConstants.SYMBOL_AND).append(WebConstants.REQUEST_SORTER).append(
				WebConstants.SYMBOL_EQUAL).append(PRICE_ASC);
		assertEquals(sbf.toString(), searchRequest.getQueryString());
	}

	/**
	 * Test method getQueryString of <code>SearchRequest</code>.
	 */
	@Test
	public void testGetQueryStringWithOnlyKeywords() {
		searchRequest.setKeyWords(DIGITAL_CAMERA);

		StringBuffer sbf = new StringBuffer();
		sbf.append(WebConstants.REQUEST_KEYWORDS).append(WebConstants.SYMBOL_EQUAL).append(DIGITAL_AND_CAMERA).append(
				WebConstants.SYMBOL_AND).append(WebConstants.REQUEST_SORTER).append(WebConstants.SYMBOL_EQUAL).append(
				FEATURED_DESC);
		assertEquals(sbf.toString(), searchRequest.getQueryString());
	}

	/**
	 * Test method getQueryString of <code>SearchRequest</code>.
	 */
	@Test
	public void testGetQueryStringWithKeywordsAndCategory() {
		searchRequest.setCategoryUid(DIGITAL_CAMERA_CID);
		searchRequest.setKeyWords(DIGITAL_CAMERA);

		StringBuffer sbf = new StringBuffer();
		sbf.append(WebConstants.REQUEST_CATEGORY_ID).append(WebConstants.SYMBOL_EQUAL).append(DIGITAL_CAMERA_CID).append(
				WebConstants.SYMBOL_AND);
		sbf.append(WebConstants.REQUEST_KEYWORDS).append(WebConstants.SYMBOL_EQUAL).append(DIGITAL_AND_CAMERA).append(
				WebConstants.SYMBOL_AND).append(WebConstants.REQUEST_SORTER).append(WebConstants.SYMBOL_EQUAL).append(
				FEATURED_DESC);
		assertEquals(sbf.toString(), searchRequest.getQueryString());
	}

	/**
	 * Test method getQueryString of <code>SearchRequest</code>.
	 */
	@Test
	public void testGetQueryStringWithKeyWordsAndFilters() {
		searchRequest.setKeyWords(DIGITAL_CAMERA);
		final String filtersStr = CAT_3_PRICE_5_BRAND_8;
		searchRequest.setFiltersIdStr(filtersStr, store);

		StringBuffer sbf = new StringBuffer();
		sbf.append(WebConstants.REQUEST_KEYWORDS).append(WebConstants.SYMBOL_EQUAL).append(DIGITAL_AND_CAMERA).append(
				WebConstants.SYMBOL_AND).append(WebConstants.REQUEST_FILTERS).append(WebConstants.SYMBOL_EQUAL).append(
				"cat-3+price-5+brand-8").append(WebConstants.SYMBOL_AND).append(WebConstants.REQUEST_SORTER).append(
				WebConstants.SYMBOL_EQUAL).append(FEATURED_DESC);
		assertEquals(sbf.toString(), searchRequest.getQueryString());
	}

	/**
	 * Test method getQueryString() of <code>SearchRequest</code>.
	 */
	@Test
	public void testGetQueryStringWithKeyWordsAndSorter() {
		searchRequest.setKeyWords(DIGITAL_CAMERA);
		searchRequest.parseSorterIdStr(PRICE_ASC);

		StringBuffer sbf = new StringBuffer();
		sbf.append(WebConstants.REQUEST_KEYWORDS).append(WebConstants.SYMBOL_EQUAL).append(DIGITAL_AND_CAMERA).append(
				WebConstants.SYMBOL_AND).append(WebConstants.REQUEST_SORTER).append(WebConstants.SYMBOL_EQUAL).append(PRICE_ASC);
		assertEquals(sbf.toString(), searchRequest.getQueryString());
	}

	/**
	 * Test method getQueryString of <code>SearchRequest</code>.
	 */
	@Test
	public void testGetQueryStringWithoutInitialization() {
		try {
			searchRequest.getQueryString();
			fail("EpDomainException expected");
		} catch (EpDomainException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method addFilter(Filter) of <code>SearchRequest</code>.
	 */
	@Test
	public void testAddFilter() {
		searchRequest.setKeyWords(DIGITAL_CAMERA);
		final String filtersStr = "cat-3  price-5";
		searchRequest.setFiltersIdStr(filtersStr, store);

		final SearchRequest newSearchRequest = (SearchRequest) searchRequest.createRefinedRequest(new BrandFilterImpl());
		assertFalse(searchRequest.equals(newSearchRequest));
		assertEquals(searchRequest.getFilters().size() + 1, newSearchRequest.getFilters().size());
	}

	/**
	 * Test <code>SearchRequest</code> setCurrency(Currency) method.
	 */
	@Test
	public void testSetCurrency() {
		Currency currency = Currency.getInstance("CAD");
		searchRequest.setCurrency(currency);
		assertEquals(currency, searchRequest.getCurrency());
	}

	/**
	 * Test <code>SearchRequest</code> setLocale(Locale) method.
	 */
	@Test
	public void testSetLocale() {
		Locale locale = Locale.CANADA_FRENCH;
		searchRequest.setLocale(locale);
		assertEquals(locale, searchRequest.getLocale());
	}

	/**
	 * Test method getFilterIds() of <code>SearchRequest</code>.
	 */
	@Test
	public void testGetFilterIds() {
		searchRequest.setKeyWords(DIGITAL_CAMERA);
		searchRequest.parseSorterIdStr(PRICE_ASC);
		final String filtersStr = "cat-3 price-5 brand-8";
		searchRequest.setFiltersIdStr(filtersStr, store);
		assertEquals(filtersStr, searchRequest.getFilterIds());
	}
	
	/**
	 * Compares that the two search requests are the same.
	 */
	@Test
	public void testCompareSame() {
		String keyWords = "test";
		
		searchRequest.setKeyWords(keyWords);
		assertEquals(0, searchRequest.compare(searchRequest));
		
		SearchRequestImpl anotherRequest = new SearchRequestImpl();
		anotherRequest.setKeyWords(keyWords);
		assertEquals(0, searchRequest.compare(anotherRequest));
	}
	
	/**
	 * Compares two different search requests will throw exception.
	 */
	@Test
	public void testCompareThrowException() {		
		CatalogViewRequest anotherRequest = new AdvancedSearchRequestImpl();
		
		boolean exception = false;
		try {
			searchRequest.compare(anotherRequest);
		} catch (EpDomainException e) {
			exception = true;
		}
		
		assertEquals(true, exception);	
	}
	
	/**
	 * Test passing in null will return exception.
	 */
	@Test
	public void testCompareNullRequestThrowsException() {
		boolean exception = false;
		try {
			searchRequest.compare(null);
		} catch (EpDomainException e) {
			exception = true;
		}
		
		assertEquals(true, exception);	
	}
	/**
	 * Compares two search requests that have different keywords.
	 */
	@Test
	public void testCompareDifferentKeywords() {
		SearchRequestImpl anotherRequest = new SearchRequestImpl();
		anotherRequest.setKeyWords("test");
		
		searchRequest.setKeyWords("test2");
				
		boolean exception = false;
		try {
			searchRequest.compare(anotherRequest);
		} catch (CatalogViewRequestUnmatchException e) {
			exception = true;
		}
		
		assertEquals(true, exception);
	}
	
	/**
	 * Test method compare of <code>SearchRequest</code>.
	 */
	@Test
	public void testCompareFilters() {
		SearchRequest searchRequest1 = getSearchRequest();
		searchRequest1.setKeyWords(DIGITAL_CAMERA);
		searchRequest1.setCategoryUid(DIGITAL_CAMERA_CID);

		SearchRequest searchRequest2 = getSearchRequest();
		searchRequest2.setKeyWords(DIGITAL_CAMERA);
		searchRequest2.setCategoryUid(DIGITAL_CAMERA_CID);
		String filtersStr = CAT_3;
		searchRequest2.setFiltersIdStr(filtersStr, store);

		SearchRequest searchRequest3 = getSearchRequest();
		searchRequest3.setKeyWords(DIGITAL_CAMERA);
		filtersStr = "cat-3  price-5";
		searchRequest3.setFiltersIdStr(filtersStr, store);
		searchRequest3.setCategoryUid(DIGITAL_CAMERA_CID);

		SearchRequest searchRequest4 = getSearchRequest();
		searchRequest4.setKeyWords(DIGITAL_CAMERA);
		searchRequest4.parseSorterIdStr(PRICE_ASC);
		searchRequest4.setFiltersIdStr(filtersStr, store);
		searchRequest4.setCategoryUid(DIGITAL_CAMERA_CID);

		SearchRequest searchRequest5 = getSearchRequest();
		searchRequest5.setKeyWords("OTHER KEYWORDS");

		SearchRequest searchRequest6 = getSearchRequest();
		searchRequest6.setKeyWords(DIGITAL_CAMERA);
		searchRequest6.setCategoryUid(DIGITAL_CAMERA_CID);
		filtersStr = CAT_4;
		searchRequest6.setFiltersIdStr(filtersStr, store);

		SearchRequest searchRequest7 = getSearchRequest();
		searchRequest7.setKeyWords("OTHER KEYWORDS");
		searchRequest7.setCategoryUid(DIGITAL_CAMERA_CID);

		assertTrue(searchRequest1.compare(searchRequest2) < 0);
		assertTrue(searchRequest2.compare(searchRequest1) > 0);

		assertTrue(searchRequest2.compare(searchRequest3) < 0);
		assertTrue(searchRequest3.compare(searchRequest2) > 0);

		assertTrue(searchRequest1.compare(searchRequest3) < 0);
		assertTrue(searchRequest3.compare(searchRequest1) > 0);

		assertEquals(0, searchRequest3.compare(searchRequest4));
		assertEquals(0, searchRequest4.compare(searchRequest3));

		assertTrue(searchRequest3.compare(searchRequest6) < 0);
		assertTrue(searchRequest6.compare(searchRequest3) < 0);

		try {
			searchRequest1.compare(searchRequest5);
			fail("EpSearchRequestUnmatchException expected.");
		} catch (CatalogViewRequestUnmatchException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			searchRequest1.compare(searchRequest7);
			fail("EpSearchRequestUnmatchException expected.");
		} catch (CatalogViewRequestUnmatchException e) {
			// succeed!
			assertNotNull(e);
		}
	}
}
