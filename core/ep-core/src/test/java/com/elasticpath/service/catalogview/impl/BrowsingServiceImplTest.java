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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalog.TopSeller;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.TopSellerImpl;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.browsing.BrowsingRequest;
import com.elasticpath.domain.catalogview.browsing.BrowsingResult;
import com.elasticpath.domain.catalogview.browsing.impl.BrowsingResultImpl;
import com.elasticpath.domain.catalogview.impl.FeaturedProductFilterImpl;
import com.elasticpath.domain.catalogview.impl.FilterOptionCompareToComparatorImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.impl.FilterBucketComparatorImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.attribute.impl.AttributeServiceImpl;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.TopSellerService;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.index.IndexSearchService;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.search.solr.IndexUtilityImpl;
import com.elasticpath.settings.SettingValueFactory;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingDefinitionImpl;
import com.elasticpath.settings.impl.SettingValueFactoryWithDefinitionImpl;
import com.elasticpath.test.factory.TestShopperFactory;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/** Test cases for <code>CategoryServiceImpl</code>. */
@SuppressWarnings({ "PMD.ExcessiveImports" })
public class BrowsingServiceImplTest extends AbstractEPServiceTestCase {

	private BrowsingServiceImpl browsingServiceImpl;

	private CategoryService categoryService;

	private CategoryService mockCategoryService;

	private StoreProductService mockStoreProductService;

	private TopSellerService mockTopSellerService;

	private IndexSearchService mockIndexSearchService;

	private IndexSearchResult mockIndexSearchResult;

	private ShoppingCart mockShoppingCart;

	private SettingsService mockSettingsService;

	private TopSellerService topSellerService;

	private StoreProductService storeProductService;

	private CustomerSession mockCustomerSession;

	private PriceListStack mockPriceListStack;

	private Store store;

	private Catalog masterCatalog;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	@Override
	public void setUp() throws Exception {
		super.setUp();
		stubGetBean("filterFactory", FilterFactory.class);
		stubGetBean(ContextIdNames.BROWSING_RESULT, BrowsingResultImpl.class);
		browsingServiceImpl = new BrowsingServiceImpl() {
			@Override
			boolean isAttributeFilterEnabled() {
				return true;
			}
		};

		mockCategoryService = context.mock(CategoryService.class);
		categoryService = mockCategoryService;
		browsingServiceImpl.setCategoryService(categoryService);

		mockTopSellerService = context.mock(TopSellerService.class);
		topSellerService = mockTopSellerService;
		browsingServiceImpl.setTopSellerService(topSellerService);

		mockStoreProductService = context.mock(StoreProductService.class);
		storeProductService = mockStoreProductService;
		browsingServiceImpl.setStoreProductService(storeProductService);

		mockIndexSearchService = context.mock(IndexSearchService.class);
		browsingServiceImpl.setIndexSearchService(mockIndexSearchService);

		mockIndexSearchResult = context.mock(IndexSearchResult.class);
		context.checking(new Expectations() {
			{
				allowing(mockIndexSearchResult).getNumFound();
				will(returnValue(0));

				allowing(mockIndexSearchResult).getLastNumFound();
				will(returnValue(0));

				allowing(mockIndexSearchResult).getCategoryFilterOptions();
				will(returnValue(new ArrayList<FilterOption<CategoryFilter>>()));

				allowing(mockIndexSearchResult).getBrandFilterOptions();
				will(returnValue(new ArrayList<FilterOption<BrandFilter>>()));

				allowing(mockIndexSearchResult).getPriceFilterOptions();
				will(returnValue(new ArrayList<FilterOption<PriceFilter>>()));

				allowing(mockIndexSearchResult).getAttributeValueFilterOptions();
				will(returnValue(new HashMap<Attribute, List<FilterOption<AttributeValueFilter>>>()));

				allowing(mockIndexSearchResult).getAttributeRangeFilterOptions();
				will(returnValue(new HashMap<Attribute, List<FilterOption<AttributeRangeFilter>>>()));

				allowing(mockIndexSearchResult).setRememberOptions(with(any(boolean.class)));
			}
		});

		browsingServiceImpl.setIndexUtility(new IndexUtilityImpl());

		final SettingValue settingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				allowing(settingValue).getValue();
				will(returnValue("50"));
			}
		});

		mockCustomerSession = context.mock(CustomerSession.class);
		mockShoppingCart = context.mock(ShoppingCart.class);
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockCustomerSession).setShopper(with(any(Shopper.class)));
			}
		});

		final Shopper shopper = TestShopperFactory.getInstance().createNewShopperWithMementoAndCustomerAndCustomerSessionAndShoppingCart(null,
				mockCustomerSession, mockShoppingCart);

		mockSettingsService = context.mock(SettingsService.class);
		context.checking(new Expectations() {
			{
				allowing(mockSettingsService).getSettingValue("COMMERCE/STORE/CATALOG/catalogViewPagination", getMockedStore().getCode());
				will(returnValue(settingValue));

				allowing(mockShoppingCart).getStore();
				will(returnValue(getMockedStore()));

				allowing(mockShoppingCart).getShopper();
				will(returnValue(shopper));
			}
		});
		//Create a setting value that will be returned when the mockStoreConfig's
		// "getSetting" method is called
		final StoreConfig mockStoreConfig = context.mock(StoreConfig.class);
		final SettingValueFactory svf = new SettingValueFactoryWithDefinitionImpl();
		final SettingValue value = svf.createSettingValue(new SettingDefinitionImpl());
		value.setValue("1");
		context.checking(new Expectations() {
			{
				allowing(mockStoreConfig).getSetting(with(any(String.class)));
				will(returnValue(value));

				allowing(mockStoreConfig).getStore();
				will(returnValue(getMockedStore()));

				allowing(mockStoreConfig).getStoreCode();
				will(returnValue(getMockedStore().getCode()));
			}
		});
		browsingServiceImpl.setStoreConfig(mockStoreConfig);
		browsingServiceImpl.setSettingsService(mockSettingsService);
		browsingServiceImpl.setPaginationService(new PaginationServiceImpl() {
			@Override
			public int getNumberOfItemsPerPage(final String storeCode) {
				return 2;
			}
		});


		mockPriceListStack = context.mock(PriceListStack.class);
	}

	/**
	 * Test browsing(BrowsingRequest, null).
	 */
	@Test
	public void testBrowsing() {
		stubGetBean(ContextIdNames.FEATURED_PRODUCT_FILTER, FeaturedProductFilterImpl.class);
		stubGetBean(ContextIdNames.FILTER_BUCKET_COMPARATOR, FilterBucketComparatorImpl.class);
		stubGetBean(ContextIdNames.FILTER_OPTION_COMPARETO_COMPARATOR, FilterOptionCompareToComparatorImpl.class);
		stubGetBean(ContextIdNames.PRODUCT_SEARCH_CRITERIA, ProductSearchCriteria.class);

		final BrowsingRequest browsingRequest = getMockedBrowsingRequest();

		final Category category = getCategory();
		context.checking(new Expectations() {
			{
				allowing(mockCategoryService).load(with(any(long.class)));
				will(returnValue(category));
			}
		});

		final TopSeller topSeller = new TopSellerImpl();
		context.checking(new Expectations() {
			{
				allowing(mockTopSellerService).findTopSellerByCategoryUid(with(any(long.class)));
				will(returnValue(topSeller));
			}
		});

		final List<Product> products = new ArrayList<Product>();
		context.checking(new Expectations() {
			{
				allowing(mockStoreProductService).getProductsForStore(
						with(Collections.<Long>emptyList()), with(any(Store.class)), with(aNull(StoreProductLoadTuner.class)));
				will(returnValue(products));

				allowing(mockCategoryService).findDescendantCategoryUids(with(any(long.class)));
				will(returnValue(new ArrayList<Long>()));
			}
		});

		final IndexSearchResult searchResult = mockIndexSearchResult;
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockIndexSearchService).search(with(any(SearchCriteria.class)));
				will(returnValue(searchResult));

				atLeast(1).of(mockIndexSearchResult).getResults(with(any(int.class)), with(any(int.class)));
				will(returnValue(new ArrayList<Long>()));

				atLeast(1).of(mockIndexSearchResult).getAllResults();
				will(returnValue(new ArrayList<Long>()));

				atLeast(1).of(mockCustomerSession).getPriceListStack();
				will(returnValue(mockPriceListStack));
			}
		});

		final BrowsingResult browsingResult = browsingServiceImpl.browsing(browsingRequest, null, mockShoppingCart, null, 0);
		assertNotNull(browsingResult);
		assertEquals(category, browsingResult.getCategory());
		assertEquals(products, browsingResult.getProducts());
	}

	private BrowsingRequest getMockedBrowsingRequest() {
		final BrowsingRequest mockBrowsingRequest = context.mock(BrowsingRequest.class);
		context.checking(new Expectations() {
			{
				allowing(mockBrowsingRequest).getCategoryUid();
				will(returnValue((long) 0));

				allowing(mockBrowsingRequest).getLocale();
				will(returnValue(Locale.US));

				allowing(mockBrowsingRequest).getCurrency();
				will(returnValue(Currency.getInstance(Locale.US)));

				allowing(mockBrowsingRequest).getFilters();
				will(returnValue(new ArrayList<Filter< ? >>()));

				allowing(mockBrowsingRequest).getSortOrder();
				will(returnValue(SortOrder.DESCENDING));

				allowing(mockBrowsingRequest).getSortType();
				will(returnValue(StandardSortBy.RELEVANCE));
			}
		});
		return mockBrowsingRequest;
	}

	/**
	 * Test browsing(BrowsingRequest, BrowsingResultHistory).
	 */
	@Test
	public void testBrowsingWithBrowsingResultHistory() {
		stubGetBean(ContextIdNames.FEATURED_PRODUCT_FILTER, FeaturedProductFilterImpl.class);
		stubGetBean(ContextIdNames.FILTER_BUCKET_COMPARATOR, FilterBucketComparatorImpl.class);
		stubGetBean(ContextIdNames.FILTER_OPTION_COMPARETO_COMPARATOR, FilterOptionCompareToComparatorImpl.class);
		stubGetBean(ContextIdNames.PRODUCT_SEARCH_CRITERIA, ProductSearchCriteria.class);

		final Category category = getCategory();
		context.checking(new Expectations() {
			{
				allowing(mockCategoryService).load(with(any(long.class)));
				will(returnValue(category));
			}
		});

		final BrowsingRequest browsingRequest = getMockedBrowsingRequest();
		final CatalogViewResultHistory mockBrowsingResultHistory = context.mock(CatalogViewResultHistory.class);
		final CatalogViewResultHistory browsingResultHistory = mockBrowsingResultHistory;
		final BrowsingResult dummyBrowsingResult = new BrowsingResultImpl();
		context.checking(new Expectations() {
			{
				allowing(mockBrowsingResultHistory).addRequest(with(same(browsingRequest)));
				will(returnValue(dummyBrowsingResult));
			}
		});

		final TopSeller topSeller = new TopSellerImpl();
		context.checking(new Expectations() {
			{
				allowing(mockTopSellerService).findTopSellerByCategoryUid(with(any(long.class)));
				will(returnValue(topSeller));

				allowing(mockStoreProductService).getProductsForStore(
						with(Collections.<Long>emptyList()), with(any(Store.class)), with(aNull(StoreProductLoadTuner.class)));
				will(returnValue(new ArrayList<Product>()));
			}
		});

		final IndexSearchResult searchResult = mockIndexSearchResult;
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockIndexSearchService).search(with(any(SearchCriteria.class)));
				will(returnValue(searchResult));

				atLeast(1).of(mockIndexSearchResult).getResults(with(any(int.class)), with(any(int.class)));
				will(returnValue(new ArrayList<Long>()));

				atLeast(1).of(mockIndexSearchResult).getAllResults();
				will(returnValue(new ArrayList<Long>()));

				atLeast(1).of(mockCustomerSession).getPriceListStack();
				will(returnValue(mockPriceListStack));
			}
		});

		final BrowsingResult browsingResult =
			browsingServiceImpl.browsing(browsingRequest, browsingResultHistory, mockShoppingCart, null, 0);
		assertNotNull(browsingResult);
		assertNotNull(browsingResult.getProducts());
		assertSame(dummyBrowsingResult, browsingResult);
	}

	/**
	 * Mock getBean("attributeService").
	 */
	protected void mockGetBeanAttributeService() {
		final AttributeService attributeService = new AttributeServiceImpl();
		final PersistenceEngine mockPersistenceEngine = context.mock(PersistenceEngine.class);
		final List<Attribute> attributeList = new ArrayList<Attribute>();
		final Attribute attribute = new AttributeImpl();
		attribute.setAttributeType(AttributeType.SHORT_TEXT);
		attribute.setAttributeUsage(AttributeUsageImpl.PRODUCT_USAGE);
		attribute.setMultiValueEnabled(false);
		attributeList.add(attribute);
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieve(with(any(String.class)));
				will(returnValue(attributeList));

				allowing(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)));
				will(returnValue(attributeList));
			}
		});
		attributeService.setPersistenceEngine(mockPersistenceEngine);

		stubGetBean("attributeService", attributeService);
	}

	/**
	 * @return the default mocked store.
	 */
	protected Store getMockedStore() {
		if (store == null) {
			store = new StoreImpl();
			store.setCatalog(getCatalog());
			store.setCode("irrelevant store code");
		}
		return store;
	}

	/**
	 * @return the master catalog singleton
	 */
	protected Catalog getCatalog() {
		if (masterCatalog == null) {
			masterCatalog = new CatalogImpl();
			masterCatalog.setMaster(true);
			masterCatalog.setCode("irrelevant catalog code");
		}
		return masterCatalog;
	}

	/**
	 * @return a new <code>Category</code> instance.
	 */
	protected Category getCategory() {
		final Category category = new CategoryImpl();
		category.initialize();
		category.setCode((new RandomGuidImpl()).toString());
		return category;
	}
}
