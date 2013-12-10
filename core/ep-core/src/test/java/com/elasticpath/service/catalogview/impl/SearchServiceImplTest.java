/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.FeaturedProductFilter;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.domain.catalogview.search.SearchResult;
import com.elasticpath.domain.catalogview.search.impl.SearchResultImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.search.SfSearchLog;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalogview.PaginationService;
import com.elasticpath.service.catalogview.SearchCriteriaFactory;
import com.elasticpath.service.catalogview.SfSearchLogService;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.search.ProductCategorySearchCriteria;
import com.elasticpath.service.search.SearchConfigFactory;
import com.elasticpath.service.search.SpellSuggestionSearchCriteria;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.index.IndexSearchService;
import com.elasticpath.service.search.query.CategorySearchCriteria;
import com.elasticpath.service.search.query.KeywordSearchCriteria;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.search.solr.IndexUtilityImpl;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.test.factory.TestShopperFactory;

/** Test cases for <code>SearchServiceImpl</code>. */
@SuppressWarnings({ "PMD.ExcessiveImports", "PMD.CouplingBetweenObjects", "PMD.TooManyFields" })
public class SearchServiceImplTest {

	private static final int NUM_OF_ITEMS_PER_PAGE = 50;

	private static final String CATALOG_CODE = "catalog_code";

	private static final String STORE_CODE = "storecode";

	private static final long DIGITAL_CAMERA_CID = 688129L;

	private static final String KEY_WORDS = "KEY WORDS";

	private static final Locale LOCALE = Locale.ENGLISH;

	private SearchServiceImpl searchServiceImpl;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private StoreProductService mockStoreProductService;

	private IndexSearchService mockIndexSearchService;

	private CategoryService mockCategoryService;

	private SfSearchLogService mockSfSearchLogService;

	private TimeService mockTimeService;

	private IndexSearchResult mockIndexSearchResult;

	private PaginationService mockPaginationService;

	private SettingsService mockSettingsService;

	private ShoppingCart mockShoppingCart;

	private IndexSearchResult indexSearchResult;

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private Store mockStore;

	private StoreConfig mockStoreConfig;

	private SearchConfig mockSearchConfig;

	private Catalog mockCatalog;

	private CustomerSession mockCustomerSession;

	private PriceListStack mockPriceListStack;

	private SearchCriteriaFactory mockSearchCriteriaFactory;


	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		// Create basic mock objects
		mockSearchConfig = context.mock(SearchConfig.class);
		context.checking(new Expectations() {
			{
				allowing(mockSearchConfig).getMinimumResultsThreshold();
				will(returnValue(SearchConfig.MINIMUM_RESULTS_THRESHOLD_DEFAULT));
			}
		});
		final SearchConfigFactory mockSearchConfigFactory = context.mock(SearchConfigFactory.class);
		context.checking(new Expectations() {
			{
				allowing(mockSearchConfigFactory).getSearchConfig(with(any(String.class)));
				will(returnValue(mockSearchConfig));
			}
		});
		mockIndexSearchService = context.mock(IndexSearchService.class);
		mockStoreProductService = context.mock(StoreProductService.class);
		mockCategoryService = context.mock(CategoryService.class);
		mockSfSearchLogService = context.mock(SfSearchLogService.class);
		mockTimeService = context.mock(TimeService.class);
		mockPaginationService = context.mock(PaginationService.class);
		mockCustomerSession = context.mock(CustomerSession.class);
		mockPriceListStack = context.mock(PriceListStack.class);
		mockSearchCriteriaFactory = context.mock(SearchCriteriaFactory.class);

		// setup other mock dependencies
		setupMockIndexSearchResult();
		setupMockSettingsService();
		setupMockElasticPath();
		setupMockStoreObjects();

		// Create search service for testing
		searchServiceImpl = new SearchServiceImpl();
		searchServiceImpl.setSearchConfigFactory(mockSearchConfigFactory);
		searchServiceImpl.setIndexSearchService(mockIndexSearchService);
		searchServiceImpl.setStoreProductService(mockStoreProductService);
		searchServiceImpl.setCategoryService(mockCategoryService);
		searchServiceImpl.setSfSearchLogService(mockSfSearchLogService);
		searchServiceImpl.setTimeService(mockTimeService);
		searchServiceImpl.setPaginationService(mockPaginationService);
		searchServiceImpl.setIndexUtility(new IndexUtilityImpl());
		searchServiceImpl.setStoreConfig(mockStoreConfig);
		searchServiceImpl.setSettingsService(mockSettingsService);
		searchServiceImpl.setSearchCriteriaFactory(mockSearchCriteriaFactory);
		context.checking(new Expectations() {
			{

				allowing(mockPaginationService).getNumberOfItemsPerPage(STORE_CODE);
				will(returnValue(NUM_OF_ITEMS_PER_PAGE));
			}
		});
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	private void setupMockStoreObjects() {
		mockCatalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				allowing(mockCatalog).getUidPk();
				will(returnValue(1L));

				allowing(mockCatalog).getCode();
				will(returnValue(CATALOG_CODE));
			}
		});

		mockStore = context.mock(Store.class);
		context.checking(new Expectations() {
			{
				allowing(mockStore).getCode();
				will(returnValue(STORE_CODE));

				allowing(mockStore).getCatalog();
				will(returnValue(mockCatalog));

				atLeast(1).of(mockCustomerSession).setShopper(with(any(Shopper.class)));
			}
		});
		final Store store = mockStore;

		mockShoppingCart = context.mock(ShoppingCart.class);

		final Shopper shopper = TestShopperFactory.getInstance().createNewShopperWithMementoAndCustomerAndCustomerSessionAndShoppingCart(
				null, mockCustomerSession, mockShoppingCart);
		context.checking(new Expectations() {
			{
				allowing(mockShoppingCart).getStore();
				will(returnValue(store));

				allowing(mockShoppingCart).getShopper();
				will(returnValue(shopper));

				atLeast(1).of(mockCustomerSession).getPriceListStack();
				will(returnValue(mockPriceListStack));
			}
		});

		mockStoreConfig = context.mock(StoreConfig.class);
		final SettingValue mockSettingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				allowing(mockSettingValue).getValue();
				will(returnValue("1"));


				allowing(mockStoreConfig).getSetting(with(any(String.class)));
				will(returnValue(mockSettingValue));

				allowing(mockStoreConfig).getStore();
				will(returnValue(store));

				allowing(mockStoreConfig).getStoreCode();
				will(returnValue(STORE_CODE));
			}
		});
	}

	private void setupMockElasticPath() {
		final FeaturedProductFilter mockFeaturedProductFilter = context.mock(FeaturedProductFilter.class);
		final SfSearchLog mockSfSearchLog = context.mock(SfSearchLog.class);
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		context.checking(new Expectations() {
			{
				allowing(mockFeaturedProductFilter).setCategoryUid(with(any(Long.class)));
				allowing(mockSfSearchLog).setCategoryRestriction(with(any(long.class)));
				allowing(mockSfSearchLog).setKeywords(with(any(String.class)));
				allowing(mockSfSearchLog).setResultCount(with(any(int.class)));
				allowing(mockSfSearchLog).setSearchTime(with(any(Date.class)));
				allowing(mockSfSearchLog).setSuggestionsGenerated(with(any(boolean.class)));

				allowing(beanFactory).getBean(ContextIdNames.KEYWORD_SEARCH_CRITERIA);
				will(returnValue(new KeywordSearchCriteria()));

				allowing(beanFactory).getBean(ContextIdNames.PRODUCT_SEARCH_CRITERIA);
				will(returnValue(new ProductSearchCriteria()));

				allowing(beanFactory).getBean(ContextIdNames.CATEGORY_SEARCH_CRITERIA);
				will(returnValue(new CategorySearchCriteria()));

				allowing(beanFactory).getBean(ContextIdNames.FEATURED_PRODUCT_FILTER);
				will(returnValue(mockFeaturedProductFilter));

				allowing(beanFactory).getBean(ContextIdNames.SF_SEARCH_LOG);
				will(returnValue(mockSfSearchLog));
			}
		});
		mockGetBeanSearchResult();
	}

	private void setupMockSettingsService() {
		final SettingValue settingValue = context.mock(SettingValue.class, "searchCategoriesFirst setting");
		final SettingValue catalogSettingValue = context.mock(SettingValue.class, "catalog setting value");
		mockSettingsService = context.mock(SettingsService.class);
		context.checking(new Expectations() {
			{
				allowing(settingValue).getValue();
				will(returnValue("false"));

				allowing(catalogSettingValue).getValue();
				will(returnValue("50"));

				allowing(mockSettingsService).getSettingValue("COMMERCE/STORE/SEARCH/searchCategoriesFirst", STORE_CODE);
				will(returnValue(settingValue));
			}
		});
	}

	private void setupMockIndexSearchResult() {
		mockIndexSearchResult = context.mock(IndexSearchResult.class);
		context.checking(new Expectations() {
			{
				allowing(mockIndexSearchResult).getResults(with(any(int.class)), with(any(int.class)));
				will(returnValue(new ArrayList<Long>()));

				allowing(mockIndexSearchResult).getAllResults();
				will(returnValue(new ArrayList<Long>()));

				allowing(mockIndexSearchResult).getLastPage(with(any(int.class)));
				will(returnValue(new ArrayList<Long>()));

				allowing(mockIndexSearchResult).getNumFound();
				will(returnValue(0));

				allowing(mockIndexSearchResult).getLastNumFound();
				will(returnValue(0));

				allowing(mockIndexSearchResult).getCategoryFilterOptions();
				will(returnValue(new ArrayList<FilterOption<CategoryFilter>>()));

				allowing(mockIndexSearchResult).getPriceFilterOptions();
				will(returnValue(new ArrayList<FilterOption<PriceFilter>>()));

				allowing(mockIndexSearchResult).getBrandFilterOptions();
				will(returnValue(new ArrayList<FilterOption<BrandFilter>>()));

				allowing(mockIndexSearchResult).getAttributeValueFilterOptions();
				will(returnValue(new HashMap<Attribute, List<FilterOption<AttributeValueFilter>>>()));

				allowing(mockIndexSearchResult).getAttributeRangeFilterOptions();
				will(returnValue(new HashMap<Attribute, List<FilterOption<AttributeRangeFilter>>>()));
			}
		});
		indexSearchResult = mockIndexSearchResult;
	}

	private void mockGetBeanSearchResult() {
		// Mock WebApplicationContext.getBean("SearchResult")
		context.checking(new Expectations() {
			{
				final int size = 9;
				final Action[] stubs = new Action[size];
				for (int i = 0; i < size; i++) {
					SearchResult searchResult = new SearchResultImpl();
					stubs[i] = returnValue(searchResult);
				}

				allowing(beanFactory).getBean(ContextIdNames.SEARCH_RESULT);
				will(onConsecutiveCalls(stubs));
			}
		});
	}

	/**
	 * Test search(SearchRequest, null).
	 */
	@Test
	public void testSearch() {
		SearchRequest searchRequest = getMockedSearchRequest();

		final List<Product> products = new ArrayList<Product>();
		context.checking(new Expectations() {
			{
				allowing(mockStoreProductService).getProductsForStore(
						with(Collections.<Long>emptyList()), with(any(Store.class)), with(aNull(StoreProductLoadTuner.class)));
				will(returnValue(products));

				// once for featured products, once for main search
				exactly(2).of(mockIndexSearchService).search(with(any(ProductCategorySearchCriteria.class)));
				will(returnValue(indexSearchResult));


				oneOf(mockIndexSearchService).suggest(with(any(SpellSuggestionSearchCriteria.class)));
				will(returnValue(Collections.emptyList()));

				oneOf(mockCategoryService).get(with(any(long.class)));
				will(returnValue(null));


				oneOf(mockSfSearchLogService).add(with(any(SfSearchLog.class)));

				will(returnValue(null));

				oneOf(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));


				atLeast(1).of(mockSearchCriteriaFactory).createKeywordProductCategorySearchCriteria(with(any(CatalogViewRequest.class)));
				will(returnValue(new KeywordSearchCriteria()));
			}
		});
		// Do the first search.
		final SearchResult searchResult = this.searchServiceImpl.search(searchRequest, null,
				mockShoppingCart, null, 0);
		assertNotNull(searchResult);
		assertEquals(products, searchResult.getProducts());
	}

	private SearchRequest getMockedSearchRequest() {
		final SearchRequest mockSearchRequest = context.mock(SearchRequest.class);
		context.checking(new Expectations() {
			{
				allowing(mockSearchRequest).getKeyWords();
				will(returnValue(KEY_WORDS));

				allowing(mockSearchRequest).getLocale();
				will(returnValue(LOCALE));

				allowing(mockSearchRequest).getCurrency();
				will(returnValue(Currency.getInstance(Locale.US)));

				allowing(mockSearchRequest).getCategoryUid();
				will(returnValue(DIGITAL_CAMERA_CID));

				allowing(mockSearchRequest).isFuzzySearchDisabled();
				will(returnValue(true));

				allowing(mockSearchRequest).getFilters();
				will(returnValue(new ArrayList<Filter<?>>()));

				allowing(mockSearchRequest).getSortOrder();
				will(returnValue(SortOrder.ASCENDING));

				allowing(mockSearchRequest).getSortType();
				will(returnValue(StandardSortBy.RELEVANCE));
			}
		});
		return mockSearchRequest;
	}

	/**
	 * Test search(SearchRequest, SearchResultHistory).
	 */
	@Test
	public void testSearchWithSearchResultHistory() {
		context.checking(new Expectations() {
			{
				allowing(mockStoreProductService).getProductsForStore(
						with(Collections.<Long>emptyList()), with(any(Store.class)), with(aNull(StoreProductLoadTuner.class)));
				will(returnValue(Collections.emptyList()));
			}
		});

		// once for featured products, once for main search
		context.checking(new Expectations() {
			{
				exactly(2).of(mockIndexSearchService).search(with(any(ProductCategorySearchCriteria.class)));
				will(returnValue(indexSearchResult));


				oneOf(mockIndexSearchService).suggest(with(any(SpellSuggestionSearchCriteria.class)));
				will(returnValue(Collections.emptyList()));

				oneOf(mockCategoryService).get(with(any(long.class)));
				will(returnValue(null));


				oneOf(mockSfSearchLogService).add(with(any(SfSearchLog.class)));
				will(returnValue(null));

				oneOf(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});

		final SearchRequest searchRequest = getMockedSearchRequest();
		final CatalogViewResultHistory mockSearchResultHistory = context.mock(CatalogViewResultHistory.class);
		final SearchResult dummySearchResult = new SearchResultImpl();
		context.checking(new Expectations() {
			{
				allowing(mockSearchResultHistory).addRequest(with(same(searchRequest)));
				will(returnValue(dummySearchResult));

				atLeast(1).of(mockSearchCriteriaFactory).createKeywordProductCategorySearchCriteria(with(any(CatalogViewRequest.class)));
				will(returnValue(new KeywordSearchCriteria()));
			}
		});

		SearchResult searchResult = searchServiceImpl.search(searchRequest, mockSearchResultHistory, mockShoppingCart, null, 0);
		assertNotNull(searchResult);
		assertNotNull(searchResult.getProducts());
		assertSame(dummySearchResult, searchResult);
	}

}
