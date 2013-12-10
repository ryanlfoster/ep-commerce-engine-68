package com.elasticpath.domain.catalogview.browsing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewRequest.Breadcrumb;
import com.elasticpath.domain.catalogview.CatalogViewRequestUnmatchException;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.SortUtility;
import com.elasticpath.domain.catalogview.StoreSeoUrlBuilderFactory;
import com.elasticpath.domain.catalogview.browsing.BrowsingRequest;
import com.elasticpath.domain.catalogview.impl.BrandFilterImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>BrowsingRequestImpl</code>.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class BrowsingRequestImplTest {

	private static final String TEST_TITLE = "testTitle";

	private static final String CATEGORY_PRICE_BRAND = "category/price/brand";

	private static final String CATEGORY_BRAND = "category/brand";

	private static final String CATEGORY_PRICE = "category/price";

	private static final String CATEGORY = "category";

	private static final String EXPECTED = "expected";

	private static final String CAT_3_PRICE_5_BRAND_8 = "cat-3  price-5  brand-8";

	private static final String CAT_4 = "cat-4";

	private static final String CAT_3 = "cat-3";

	private static final String PRICE_ASC = SortUtility.constructSortTypeOrderString(StandardSortBy.PRICE, SortOrder.ASCENDING);
	
	private static final String FEATUREDPRODUCT_DESC = SortUtility.constructSortTypeOrderString(StandardSortBy.FEATURED_CATEGORY,
			SortOrder.DESCENDING);

	private static final long CATEGORY_UID = 1;

	private static final long ANOTHER_CATEGORY_UID = 2;

	private static final String PRICE_5_BRAND_8 = "price-5  brand-8";

	private static final String BRAND_8 = "brand-8";

	private BrowsingRequest browsingRequest;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private FilterFactory mockFilterFactory;

	private FilterFactory filterFactory;

	private Filter<?> mockFilterOfCategory;

	private Filter<?> mockFilterOfPrice;

	private Filter<?> mockFilterOfBrand;

	private BrandService mockBrandService;

	private BeanFactory mockBeanFactory;

	private StoreSeoUrlBuilderFactory mockSeoUrlBuilderFactory;
	
	private SeoUrlBuilder mockSeoUrlBuilder;

	private Store mockStore;

	private BeanFactoryExpectationsFactory expectationsFactory;
	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		mockBeanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, mockBeanFactory);
		mockStore = context.mock(Store.class);

		setupFilterFactory();
		setupBrandService();
		setupSeoUrlBuiler();
		
		browsingRequest = getBrowsingRequest();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	private BrowsingRequestImpl getBrowsingRequest() {
		BrowsingRequestImpl browsingRequestImpl = new BrowsingRequestImpl();
		browsingRequestImpl.setFilterFactory(this.filterFactory);
		return browsingRequestImpl;
	}

	private void setupBrandService() {
		this.mockBrandService = context.mock(BrandService.class);
		context.checking(new Expectations() {
			{
				allowing(mockBeanFactory).getBean(ContextIdNames.BRAND_SERVICE);
				will(returnValue(mockBrandService));
			}
		});
	}

	private void setupSeoUrlBuiler() {
		
		// Mock the factory that returns seoUrlBuilders
		mockSeoUrlBuilderFactory = context.mock(StoreSeoUrlBuilderFactory.class);
		context.checking(new Expectations() {
			{

				allowing(mockBeanFactory).getBean(ContextIdNames.STORE_SEO_URL_BUILDER_FACTORY);
				will(returnValue(mockSeoUrlBuilderFactory));
			}
		});
		
		mockSeoUrlBuilder = context.mock(SeoUrlBuilder.class);
		context.checking(new Expectations() {
			{
		
				allowing(mockSeoUrlBuilderFactory).getStoreSeoUrlBuilder();
	 	will(returnValue(mockSeoUrlBuilder));
			}
		});
	}
	
	private void setupFilterFactory() {
		// Mock FilterFactory
		this.mockFilterFactory = context.mock(FilterFactory.class);
		this.filterFactory = this.mockFilterFactory;
		context.checking(new Expectations() {
			{
				allowing(mockBeanFactory).getBean("filterFactory");
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

		mockFilterOfPrice = context.mock(PriceFilter.class);
		context.checking(new Expectations() {
			{
				allowing(mockFilterOfPrice).getId();
				will(returnValue("price-5"));

				allowing(mockFilterOfPrice).getDisplayName(with(any(Locale.class)));
				will(returnValue("price-name-5"));

				allowing(mockFilterOfPrice).getSeoName(with(any(Locale.class)));
				will(returnValue("price-url-5"));

				allowing(mockFilterOfPrice).getSeoId();
				will(returnValue("pr100_200"));

				allowing(mockFilterFactory).getFilter(with("price-5"), with(any(Store.class)));
				will(returnValue(mockFilterOfPrice));
			}
		});

		mockFilterOfBrand = context.mock(BrandFilter.class);
		context.checking(new Expectations() {
			{
				allowing(mockFilterOfBrand).getId();
				will(returnValue("brand-8"));

				allowing(mockFilterOfBrand).getDisplayName(with(any(Locale.class)));
				will(returnValue("brand-name-8"));

				allowing(mockFilterOfBrand).getSeoName(with(any(Locale.class)));
				will(returnValue("brand-url-8"));

				allowing(mockFilterOfBrand).getSeoId();
				will(returnValue("b8"));

				allowing(mockFilterFactory).getFilter(with("brand-8"), with(any(Store.class)));
				will(returnValue(mockFilterOfBrand));
			}
		});
	}

	/**
	 * Test that setCategoryUid can only be called once.
	 */
	@Test
	public void testSetCategoryUid() {
		BrowsingRequestImpl request = new BrowsingRequestImpl();
		request.setCategoryUid(1);
		assertEquals("category uid should be what we set", 1, request.getCategoryUid());

		try {
			request.setCategoryUid(2);
			fail("category uid can only be set once, an EpDomainException should have been thrown");
		} catch (EpDomainException e) {
			// succeed
			assertNotNull(e);
		}
	}

	/**
	 * Test that getQueryString() will return all the parts of a query string
	 * (the categoryID, the filter IDs, and the Sort order/type, separated
	 * by ampersands).
	 */
	@Test
	public void testGetQueryStringFull() {
		final String categoryString = "CATEGORY_PORTION";
		final String filterString = "FILTER_PORTION";
		final String sortString = "SORT_PORTION";
		BrowsingRequestImpl requestImpl = new BrowsingRequestImpl() {
			private static final long serialVersionUID = 4115897285604188438L;

			@Override
			public String createCategoryQueryString() {
				return categoryString;
			}
			
			@Override
			String createFiltersQueryString() {
				return filterString;
			}
			
			@Override
			protected String createSortingQueryString() {
				return sortString;
			}
		};
		StringBuffer expected = new StringBuffer()
			.append(categoryString)
			.append(WebConstants.SYMBOL_AND).append(filterString)
			.append(WebConstants.SYMBOL_AND).append(sortString);
		
		assertEquals(expected.toString(), requestImpl.getQueryString());
	}

	/**
	 * Test that getQueryString() will not insert an extra ampersand
	 * if the filter string is empty.
	 */
	@Test
	public void testGetQueryStringEmptyFilters() {
		final String categoryString = "CATEGORY_PORTION";
		final String filterString = "";
		final String sortString = "SORT_PORTION";
		BrowsingRequestImpl requestImpl = new BrowsingRequestImpl() {
			private static final long serialVersionUID = 2663085577004503998L;

			@Override
			public String createCategoryQueryString() {
				return categoryString;
			}
			
			@Override
			String createFiltersQueryString() {
				return filterString;
			}
			
			@Override
			protected String createSortingQueryString() {
				return sortString;
			}
		};
		StringBuffer expected = new StringBuffer()
			.append(categoryString)
			.append(WebConstants.SYMBOL_AND).append(sortString);
		
		assertEquals(expected.toString(), requestImpl.getQueryString());
	}
	
	/**
	 * Test that getCategoryQueryString() creates a string like:
	 * "categoryUid=1".
	 */
	@Test
	public void testGetCategoryQueryString() {
		BrowsingRequestImpl requestImpl = new BrowsingRequestImpl() {
			private static final long serialVersionUID = 2963187096556728656L;

			@Override
			public long getCategoryUid() {
				return CATEGORY_UID;
			}
		};
		StringBuffer expected = new StringBuffer().append(WebConstants.REQUEST_CID).append(WebConstants.SYMBOL_EQUAL).append(CATEGORY_UID);
		assertEquals(expected.toString(), requestImpl.createCategoryQueryString());
	}

	/**
	 * Test that createFiltersQueryString() creates a string like:
	 * "filters=filter1+filter2+..." where all filters returned by
	 * getFilters() are represented. 
	 */
	@Test
	public void testGetFiltersQueryString() {
		final List<String> filterIds = new ArrayList<String>();
		filterIds.add("Filter1");
		filterIds.add("Filter2");
		
		BrowsingRequestImpl requestImpl = new BrowsingRequestImpl() {
			private static final long serialVersionUID = -3932077945426840817L;

			@Override
			public List<String> getFilterIdList() {
				return filterIds;
			}
		};
		
		StringBuffer expected = new StringBuffer().append(WebConstants.REQUEST_FILTERS).append(WebConstants.SYMBOL_EQUAL)
		.append("Filter1").append(WebConstants.SYMBOL_PLUS).append("Filter2");
		
		assertEquals(expected.toString(), requestImpl.createFiltersQueryString());
	}
	
	/**
	 * Test that createFiltersQueryString() returns an empty string if
	 * there are no filters returned by getFilters().
	 */
	@Test
	public void testGetFiltersQueryStringNull() {
		BrowsingRequestImpl requestImpl = new BrowsingRequestImpl() {
			private static final long serialVersionUID = -3333397345466266073L;

			@Override
			public List<String> getFilterIdList() {
				return Collections.emptyList();
			}
		};
		assertEquals(StringUtils.EMPTY, requestImpl.createFiltersQueryString());
	}
	
	/**
	 * Testing class for exposing {@link #createCategoryQueryString()}.
	 */
	private class DefaultBrowsingResultTest extends BrowsingRequestImpl {
		private static final long serialVersionUID = 5427562909589436190L;

		@Override
		public SortBy getSortType() {
			return null;
		}

		@Override
		public String createSortingQueryString() { // NOPMD
			return super.createSortingQueryString();
		}
	}

	/**
	 * Test that if not SortType has been specified, createSortingQueryString returns 
	 * a query string with a sortOrder of DESCENDING and a sortBy of FEATURED_CATEGORY,
	 * but does not permanently alter whatever the current SortOrder happens to be.
	 */
	@Test
	public void testCreateSortingQueryStringNoSortType() {
		DefaultBrowsingResultTest request = new DefaultBrowsingResultTest();
		
		//Unfortunately, this test crosses a few other classes and methods, but without refactoring it's hard to avoid.
		StringBuffer expected = new StringBuffer().append(WebConstants.REQUEST_SORTER).append(WebConstants.SYMBOL_EQUAL)
			.append(StandardSortBy.FEATURED_CATEGORY).append('-').append(SortOrder.DESCENDING);
		
		assertEquals(expected.toString(), request.createSortingQueryString());
		assertNull(request.getSortOrder()); //It was never set, so should remain null after the call
	}
	
	
	/**
	 * Test method getQueryString of <code>BrowsingRequest</code>.
	 */
	@Test
	public void testGetQueryStringWithCategoryUidAndFilters() {
		BrowsingRequestImpl request = getBrowsingRequest();
		request.setCategoryUid(CATEGORY_UID);
		final String filtersStr = CAT_3_PRICE_5_BRAND_8;
		request.setFiltersIdStr(filtersStr, new StoreImpl());
//		request.setMaxReturnNumber(2);

		StringBuffer sbf = new StringBuffer();
		sbf.append(WebConstants.REQUEST_CID).append(WebConstants.SYMBOL_EQUAL).append(CATEGORY_UID).append(
				WebConstants.SYMBOL_AND).append(WebConstants.REQUEST_FILTERS).append(WebConstants.SYMBOL_EQUAL).append(
				"cat-3+price-5+brand-8").append(WebConstants.SYMBOL_AND).append(WebConstants.REQUEST_SORTER).append(
				WebConstants.SYMBOL_EQUAL).append(FEATUREDPRODUCT_DESC);
		assertEquals(sbf.toString(), request.getQueryString());
	}

	/**
	 * Test method getQueryString() of <code>BrowsingRequest</code>.
	 */
	@Test
	public void testGetQueryStringWithCategoryUidAndSorter() {
		BrowsingRequestImpl request = getBrowsingRequest();
		request.setCategoryUid(CATEGORY_UID);
		request.parseSorterIdStr(PRICE_ASC);
//		request.setMaxReturnNumber(2);

		StringBuffer sbf = new StringBuffer();
		sbf.append(WebConstants.REQUEST_CID).append(WebConstants.SYMBOL_EQUAL).append(CATEGORY_UID).append(WebConstants.SYMBOL_AND).append(
				WebConstants.REQUEST_SORTER).append(WebConstants.SYMBOL_EQUAL).append(PRICE_ASC);
		assertEquals(sbf.toString(), request.getQueryString());
	}

	/**
	 * Test method addFilter(Filter) of <code>BrowsingRequest</code>.
	 */
	@Test
	public void testAddFilter() {
		browsingRequest.setCategoryUid(CATEGORY_UID);
		final String filtersStr = "cat-3  price-5";
		browsingRequest.setFiltersIdStr(filtersStr, new StoreImpl());

		final BrowsingRequest newBrowsingRequest = (BrowsingRequest) browsingRequest.createRefinedRequest(new BrandFilterImpl());
		assertFalse(browsingRequest.equals(newBrowsingRequest));
		assertEquals(browsingRequest.getFilters().size() + 1, newBrowsingRequest.getFilters().size());
	}

	/**
	 * Test method compare of <code>BrowsingRequest</code>.
	 */
	@Test
	public void testCompare() {
		Store store = new StoreImpl();
		BrowsingRequest browsingRequest1 = getBrowsingRequest();
		browsingRequest1.setCategoryUid(CATEGORY_UID);

		BrowsingRequest browsingRequest2 = getBrowsingRequest();
		browsingRequest2.setCategoryUid(CATEGORY_UID);
		String filtersStr = CAT_3;
		browsingRequest2.setFiltersIdStr(filtersStr, store);
//		browsingRequest2.setMaxReturnNumber(2);

		BrowsingRequest browsingRequest3 = getBrowsingRequest();
		browsingRequest3.setCategoryUid(CATEGORY_UID);
		filtersStr = "cat-3  price-5";
		browsingRequest3.setFiltersIdStr(filtersStr, store);

		BrowsingRequest browsingRequest4 = getBrowsingRequest();
		browsingRequest4.setCategoryUid(CATEGORY_UID);
		browsingRequest4.parseSorterIdStr(PRICE_ASC);
		browsingRequest4.setFiltersIdStr(filtersStr, store);
//		browsingRequest4.setMaxReturnNumber(2);

		BrowsingRequest browsingRequest5 = getBrowsingRequest();
		browsingRequest5.setCategoryUid(ANOTHER_CATEGORY_UID);

		BrowsingRequest browsingRequest6 = getBrowsingRequest();
		browsingRequest6.setCategoryUid(CATEGORY_UID);
		filtersStr = CAT_4;
		browsingRequest6.setFiltersIdStr(filtersStr, store);

		assertTrue(browsingRequest1.compare(browsingRequest2) < 0);
		assertTrue(browsingRequest2.compare(browsingRequest1) > 0);

		assertTrue(browsingRequest2.compare(browsingRequest3) < 0);
		assertTrue(browsingRequest3.compare(browsingRequest2) > 0);

		assertTrue(browsingRequest1.compare(browsingRequest3) < 0);
		assertTrue(browsingRequest3.compare(browsingRequest1) > 0);

		assertEquals(0, browsingRequest3.compare(browsingRequest4));
		assertEquals(0, browsingRequest4.compare(browsingRequest3));

		assertTrue(browsingRequest3.compare(browsingRequest6) < 0);
		assertTrue(browsingRequest6.compare(browsingRequest3) < 0);

		try {
			browsingRequest1.compare(browsingRequest5);
			fail("EpSearchRequestUnmatchException expected.");
		} catch (CatalogViewRequestUnmatchException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method getSeoUrl of <code>BrowsingRequest</code>.
	 */
	@Test
	public void testGetSeoUrl() {
		final long uidPk = 1;
		final Locale locale = Locale.US;

		browsingRequest.setCategoryUid(uidPk);
		browsingRequest.parseSorterIdStr(PRICE_ASC);
		final String filtersStr = PRICE_5_BRAND_8;
		browsingRequest.setFiltersIdStr(filtersStr, mockStore);
		browsingRequest.setLocale(locale);

		CategoryFilter mockCategoryFilter = context.mock(CategoryFilter.class);
		browsingRequest.getFilters().add(0, mockCategoryFilter);
		context.checking(new Expectations() {
			{
				oneOf(mockSeoUrlBuilder).filterSeoUrl(
						locale, browsingRequest.getFilters(), browsingRequest.getSortType(), browsingRequest.getSortOrder(), -1);
				will(returnValue(EXPECTED));
			}
		});

		assertEquals("the returned Seo Url should be our expected value", EXPECTED, browsingRequest.getSeoUrl());
	}
	
	/**
	 * Test method getFilterSeoUrls() of <code>browsingRequest</code>.
	 */
	@Test
	public void testGetFilterSeoUrlsForMultipleFilters() {
		final long uidPk = 1;
		final int expectedFilters = 3;
		final Locale locale = Locale.US;

		browsingRequest.setCategoryUid(uidPk);
		browsingRequest.parseSorterIdStr(PRICE_ASC);
		final String filtersStr = PRICE_5_BRAND_8;
		browsingRequest.setFiltersIdStr(filtersStr, new StoreImpl());
		browsingRequest.setLocale(locale);

		final Category mockCategory = context.mock(Category.class);
		final CategoryFilter mockCategoryFilter = context.mock(CategoryFilter.class);
		browsingRequest.getFilters().add(0, mockCategoryFilter);
		
		assertEquals("There should be 3 filters", expectedFilters, browsingRequest.getFilters().size());

		final List<Filter< ? >> filters1 = new ArrayList<Filter< ? >>();
		filters1.add(browsingRequest.getFilters().get(0));

		final List<Filter< ? >> filters2 = new ArrayList<Filter< ? >>();
		filters2.add(browsingRequest.getFilters().get(0));
		filters2.add(browsingRequest.getFilters().get(1));

		final List<Filter< ? >> filters3 = new ArrayList<Filter< ? >>();
		filters3.add(browsingRequest.getFilters().get(0));
		filters3.add(browsingRequest.getFilters().get(2));
		context.checking(new Expectations() {
			{

				oneOf(mockCategoryFilter).getDisplayName(locale);
				will(returnValue("displayName"));

				oneOf(mockSeoUrlBuilder).filterSeoUrl(locale, filters1, browsingRequest.getSortType(), browsingRequest.getSortOrder(), 1);
				will(returnValue(CATEGORY));

				oneOf(mockSeoUrlBuilder).filterSeoUrl(locale, filters2, browsingRequest.getSortType(), browsingRequest.getSortOrder(), 1);
				will(returnValue(CATEGORY_PRICE));

				oneOf(mockSeoUrlBuilder).filterSeoUrl(locale, filters2, browsingRequest.getSortType(), browsingRequest.getSortOrder(), -1);
				will(returnValue(CATEGORY_PRICE));

				oneOf(mockSeoUrlBuilder).filterSeoUrl(locale, filters3, browsingRequest.getSortType(), browsingRequest.getSortOrder(), -1);
				will(returnValue(CATEGORY_BRAND));

				oneOf(mockSeoUrlBuilder).filterSeoUrl(
						locale, browsingRequest.getFilters(), browsingRequest.getSortType(), browsingRequest.getSortOrder(), 1);
				will(returnValue(CATEGORY_PRICE_BRAND));
			}
		});
		
		final List<Breadcrumb> filterSeoUrls = browsingRequest.getFilterSeoUrls();
		assertEquals(2 + 1, filterSeoUrls.size());
		final CatalogViewRequest.Breadcrumb filterSeoUrl1 = filterSeoUrls.get(1);
		
		assertNotNull(filterSeoUrl1.getDisplayName());
		assertEquals(CATEGORY_PRICE, filterSeoUrl1.getUrlFragment());
		assertEquals(CATEGORY_BRAND, filterSeoUrl1.getUrlFragmentWithoutThisCrumb());

		final CatalogViewRequest.Breadcrumb filterSeoUrl2 = filterSeoUrls.get(2);
		assertNotNull(filterSeoUrl2.getDisplayName());
		assertEquals(CATEGORY_PRICE_BRAND, filterSeoUrl2
				.getUrlFragment());
		assertEquals(CATEGORY_PRICE, filterSeoUrl2.getUrlFragmentWithoutThisCrumb());

		final LocaleDependantFields mockLocaleDependantFields = context.mock(LocaleDependantFields.class);
		context.checking(new Expectations() {
			{
				exactly(2).of(mockLocaleDependantFields).getTitle();
				will(returnValue(TEST_TITLE));

				oneOf(mockCategory).getLocaleDependantFields(with(any(Locale.class)));
				will(returnValue(mockLocaleDependantFields));
			}
		});
		
		// Test getTitle()
		assertEquals("brand-name-8 - testTitle - price-name-5", browsingRequest.getTitle(mockCategory));

	}
	
	/**
	 * Test method getFilterSeoUrls() of <code>browsingRequest</code>.
	 */
	@Test
	public void testGetFilterSeoUrlsForOneFilter() {
		final long uidPk = 1;
		final int expectedFilters = 2;
		final Locale locale = Locale.US;

		browsingRequest.setCategoryUid(uidPk);
		browsingRequest.parseSorterIdStr(PRICE_ASC);
		final String filtersStr = BRAND_8;
		browsingRequest.setFiltersIdStr(filtersStr, new StoreImpl());
		browsingRequest.setLocale(locale);

		final Category mockCategory = context.mock(Category.class);
		final CategoryFilter mockCategoryFilter = context.mock(CategoryFilter.class);
		browsingRequest.getFilters().add(0, mockCategoryFilter);
		assertEquals("There should be 2 filters", expectedFilters, browsingRequest.getFilters().size());

		final List<Filter< ? >> filters1 = new ArrayList<Filter< ? >>();
		filters1.add(browsingRequest.getFilters().get(0));

		final List<Filter< ? >> filters2 = new ArrayList<Filter< ? >>();
		filters2.add(browsingRequest.getFilters().get(0));
		filters2.add(browsingRequest.getFilters().get(1));
		context.checking(new Expectations() {
			{
		
				oneOf(mockCategoryFilter).getDisplayName(locale);
				will(returnValue("categoryName"));

				oneOf(mockSeoUrlBuilder).filterSeoUrl(locale, filters1, browsingRequest.getSortType(), browsingRequest.getSortOrder(), 1);
				will(returnValue(CATEGORY));

				oneOf(mockSeoUrlBuilder).filterSeoUrl(locale, filters1, browsingRequest.getSortType(), browsingRequest.getSortOrder(), -1);
				will(returnValue(CATEGORY));

				oneOf(mockSeoUrlBuilder).filterSeoUrl(locale, filters2, browsingRequest.getSortType(), browsingRequest.getSortOrder(), 1);
				will(returnValue(CATEGORY_BRAND));
			}
		});

		final List<Breadcrumb> filterSeoUrls = browsingRequest.getFilterSeoUrls();
		assertEquals(2, filterSeoUrls.size());
		final CatalogViewRequest.Breadcrumb filterSeoUrl1 = filterSeoUrls.get(1);
		assertNotNull(filterSeoUrl1.getDisplayName());
		assertEquals(CATEGORY_BRAND, filterSeoUrl1.getUrlFragment());
		assertEquals(CATEGORY, filterSeoUrl1.getUrlFragmentWithoutThisCrumb());

		final LocaleDependantFields mockLocaleDependantFields = context.mock(LocaleDependantFields.class);
		context.checking(new Expectations() {
			{
				exactly(2).of(mockLocaleDependantFields).getTitle();
				will(returnValue(TEST_TITLE));

				oneOf(mockCategory).getLocaleDependantFields(with(any(Locale.class)));
				will(returnValue(mockLocaleDependantFields));
			}
		});

		// Test getTitle()
		assertEquals("brand-name-8 - testTitle", browsingRequest.getTitle(mockCategory));
	}

	/**
	 * Test method getFilterSeoUrls() of <code>browsingRequest</code>.
	 */
	@Test
	public void testGetFilterSeoUrlsWithNoFilter() {
		final long uidPk = 1;
		final Locale locale = Locale.US;

		browsingRequest.setCategoryUid(uidPk);
		browsingRequest.parseSorterIdStr(PRICE_ASC);
		browsingRequest.setLocale(locale);

		final Category mockCategory = context.mock(Category.class);
		final LocaleDependantFields mockLocaleDependantFields = context.mock(LocaleDependantFields.class);
		context.checking(new Expectations() {
			{
				exactly(2).of(mockLocaleDependantFields).getTitle();
				will(returnValue(TEST_TITLE));

				oneOf(mockCategory).getLocaleDependantFields(with(any(Locale.class)));
				will(returnValue(mockLocaleDependantFields));
			}
		});

		final List<Breadcrumb> filterSeoUrls = browsingRequest.getFilterSeoUrls();
		assertEquals(0, filterSeoUrls.size());

		// Test getTitle()
		assertEquals(TEST_TITLE, browsingRequest.getTitle(mockCategory));
	}

	/**
	 * Test method getSeoUrl of <code>BrowsingRequest</code>.
	 */
	@Test
	public void testGetSeoUrlWithPageNumber() {
		final long uidPk = 1;
		final Locale locale = Locale.US;

		browsingRequest.setCategoryUid(uidPk);
		browsingRequest.parseSorterIdStr(PRICE_ASC);
		final String filtersStr = PRICE_5_BRAND_8;
		browsingRequest.setFiltersIdStr(filtersStr, new StoreImpl());
		browsingRequest.setLocale(locale);
		context.checking(new Expectations() {
			{
				oneOf(mockSeoUrlBuilder).filterSeoUrl(
						locale, browsingRequest.getFilters(), browsingRequest.getSortType(), browsingRequest.getSortOrder(), 2);
				will(returnValue(EXPECTED));
			}
		});

		CategoryFilter mockCategoryFilter = context.mock(CategoryFilter.class);
		browsingRequest.getFilters().add(0, mockCategoryFilter);
		assertEquals(EXPECTED, browsingRequest.getSeoUrl(2));
	}

	/**
	 * Test method getSeoUrl of <code>BrowsingRequest</code>.
	 */
	@Test
	public void testGetSeoUrlWithoutSorter() {
		final long uidPk = 1;
		final Locale locale = Locale.US;

		browsingRequest.setCategoryUid(uidPk);
		final String filtersStr = PRICE_5_BRAND_8;
		browsingRequest.setFiltersIdStr(filtersStr, new StoreImpl());
		browsingRequest.setLocale(locale);
		context.checking(new Expectations() {
			{

				oneOf(mockSeoUrlBuilder).filterSeoUrl(locale, browsingRequest.getFilters(), null, null, 2);
				will(returnValue(EXPECTED));
			}
		});

		CategoryFilter mockCategoryFilter = context.mock(CategoryFilter.class);
		browsingRequest.getFilters().add(0, mockCategoryFilter);
		assertEquals(EXPECTED, browsingRequest.getSeoUrl(2));
	}

	/**
	 * Test method getFilterSeoUrls() of <code>browsingRequest</code>.
	 */
	@Test
	public void testGetFilterSeoUrlsWithoutSorter() {
		final long uidPk = 1;
		final int expectedFilters = 3;
		final Locale locale = Locale.US;

		browsingRequest.setCategoryUid(uidPk);
		final String filtersStr = PRICE_5_BRAND_8;
		browsingRequest.setFiltersIdStr(filtersStr, new StoreImpl());
		browsingRequest.setLocale(locale);

		final CategoryFilter mockCategoryFilter = context.mock(CategoryFilter.class);
		browsingRequest.getFilters().add(0, mockCategoryFilter);

		assertEquals("There should be 3 filters", expectedFilters, browsingRequest.getFilters().size());

		final List<Filter< ? >> filters1 = new ArrayList<Filter< ? >>();
		filters1.add(browsingRequest.getFilters().get(0));

		final List<Filter< ? >> filters2 = new ArrayList<Filter< ? >>();
		filters2.add(browsingRequest.getFilters().get(0));
		filters2.add(browsingRequest.getFilters().get(1));

		final List<Filter< ? >> filters3 = new ArrayList<Filter< ? >>();
		filters3.add(browsingRequest.getFilters().get(0));
		filters3.add(browsingRequest.getFilters().get(2));
		context.checking(new Expectations() {
			{

				oneOf(mockCategoryFilter).getDisplayName(locale);
				will(returnValue("categoryName"));

				oneOf(mockSeoUrlBuilder).filterSeoUrl(locale, filters1, browsingRequest.getSortType(), browsingRequest.getSortOrder(), 1);
				will(returnValue(CATEGORY));

				oneOf(mockSeoUrlBuilder).filterSeoUrl(locale, filters2, browsingRequest.getSortType(), browsingRequest.getSortOrder(), 1);
				will(returnValue(CATEGORY_PRICE));

				oneOf(mockSeoUrlBuilder).filterSeoUrl(locale, filters2, browsingRequest.getSortType(), browsingRequest.getSortOrder(), -1);
				will(returnValue(CATEGORY_PRICE));

				oneOf(mockSeoUrlBuilder).filterSeoUrl(locale, filters3, browsingRequest.getSortType(), browsingRequest.getSortOrder(), -1);
				will(returnValue(CATEGORY_BRAND));

				oneOf(mockSeoUrlBuilder).filterSeoUrl(
						locale, browsingRequest.getFilters(), browsingRequest.getSortType(), browsingRequest.getSortOrder(), 1);
				will(returnValue(CATEGORY_PRICE_BRAND));
			}
		});

		final List<Breadcrumb> filterSeoUrls = browsingRequest.getFilterSeoUrls();
		assertEquals(2 + 1, filterSeoUrls.size());
		final CatalogViewRequest.Breadcrumb filterSeoUrl1 = filterSeoUrls.get(1);
		assertNotNull(filterSeoUrl1.getDisplayName());
		assertEquals(CATEGORY_PRICE, filterSeoUrl1.getUrlFragment());
		assertEquals(CATEGORY_BRAND, filterSeoUrl1.getUrlFragmentWithoutThisCrumb());

		final CatalogViewRequest.Breadcrumb filterSeoUrl2 = filterSeoUrls.get(2);
		assertNotNull(filterSeoUrl2.getDisplayName());
		assertEquals(CATEGORY_PRICE_BRAND, filterSeoUrl2.getUrlFragment());
		assertEquals(CATEGORY_PRICE, filterSeoUrl2.getUrlFragmentWithoutThisCrumb());
	}

}
