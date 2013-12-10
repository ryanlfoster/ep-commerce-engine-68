package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.StoreProductLoadTunerImpl;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.impl.FeaturedProductFilterImpl;
import com.elasticpath.domain.catalogview.search.AdvancedSearchRequest;
import com.elasticpath.domain.catalogview.search.SearchResult;
import com.elasticpath.domain.catalogview.search.impl.AdvancedSearchRequestImpl;
import com.elasticpath.domain.catalogview.search.impl.SearchResultImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.catalogview.PaginationService;
import com.elasticpath.service.catalogview.SearchCriteriaFactory;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.index.IndexSearchService;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.solr.IndexUtility;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.test.factory.TestCustomerSessionFactory;

/**
 * Collection of unit tests for {@link AdvancedSearchServiceImpl}.
 */
public class AdvancedSearchServiceImplTest {

	private static final int NUMBER_OF_PRODUCTS_FOUND = 1;

	private static final int FEATURED_PRODUCTS_COUNT = 2;

	private static final long PRODUCT_UID = 123L;

	private static final String PRODUCT_CODE = "PRODUCT_CODE";

	private static final int PAGE_NUMBER = 1;

	private static final int NUMBER_OF_ITEMS_PER_PAGE = 50;

	private static final String CATALOG_CODE = "CATALOG_CODE";

	private static final String STORE_CODE = "store_code";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private StoreProductService mockStoreProductService;

	private AdvancedSearchServiceImpl advancedSearchService;

	private IndexSearchService mockIndexSearchService;

	private SearchCriteriaFactory mockSearchCriteriaFactory;

	private StoreConfig mockStoreConfig;

	private IndexUtility mockIndexUtility;

	private IndexSearchResult mockIndexSearchResult;

	private PaginationService mockPaginationService;

	private Store store;

	private ShoppingCart mockShoppingCart;

    private BeanFactoryExpectationsFactory expectationsFactory;

    private BeanFactory beanFactory;

	/**
	 * The setup steps.
	 */
	@Before
	public void setUp() {
        beanFactory = context.mock(BeanFactory.class);
        expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		advancedSearchService = new AdvancedSearchServiceImpl() {
			@Override
			protected void setFilterOptions(final CatalogViewRequest request, final CatalogViewResult result,
					final IndexSearchResult indexSearchResult) {
				// does not do anything here as we are not testing this logic
			}
		};
		mockStoreProductService = context.mock(StoreProductService.class);
		advancedSearchService.setStoreProductService(mockStoreProductService);

		mockIndexSearchService = context.mock(IndexSearchService.class);
		advancedSearchService.setIndexSearchService(mockIndexSearchService);

		mockSearchCriteriaFactory = context.mock(SearchCriteriaFactory.class);
		advancedSearchService.setSearchCriteriaFactory(mockSearchCriteriaFactory);

		mockStoreConfig = context.mock(StoreConfig.class);
		advancedSearchService.setStoreConfig(mockStoreConfig);

		mockShoppingCart = context.mock(ShoppingCart.class);
		final Shopper shopper = createShopper();

		mockIndexUtility = context.mock(IndexUtility.class);
		advancedSearchService.setIndexUtility(mockIndexUtility);

		mockPaginationService = context.mock(PaginationService.class);
		advancedSearchService.setPaginationService(mockPaginationService);

		store = new StoreImpl();
		Catalog catalog = new CatalogImpl();
		catalog.setCode(CATALOG_CODE);
		store.setCatalog(catalog);

		final SettingValue value = new SettingValueImpl();
		value.setIntegerValue(FEATURED_PRODUCTS_COUNT);

		mockIndexSearchResult = context.mock(IndexSearchResult.class);

		context.checking(new Expectations() {
			{

				allowing(mockShoppingCart).getShopper();
				will(returnValue(shopper));

				allowing(mockStoreConfig).getStore();
				will(returnValue(store));

				allowing(mockStoreConfig).getStoreCode();
				will(returnValue(STORE_CODE));

				allowing(mockStoreConfig).getSetting("COMMERCE/STORE/CATALOG/featuredProductCountToDisplay");
				will(returnValue(value));

				allowing(mockShoppingCart).getStore();
				will(returnValue(store));

				allowing(mockPaginationService).getNumberOfItemsPerPage(STORE_CODE);
				will(returnValue(NUMBER_OF_ITEMS_PER_PAGE));
			}
		});

		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.SEARCH_RESULT, SearchResultImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.FEATURED_PRODUCT_FILTER, FeaturedProductFilterImpl.class);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests the steps for performing an advanced <code>search</code> on {@link AdvancedSearchServiceImpl}.
	 */
	@Test
	public void testSearch() {
		final AdvancedSearchRequest searchRequest = setUpSearchRequest();

		final List<Product> featuredProducts = new ArrayList<Product>();
		final List<Long> featuredProductsUids = new ArrayList<Long>();
		final List<Product> products = new ArrayList<Product>();
		final Product product = new ProductImpl();
		product.setGuid(PRODUCT_CODE);
		product.setUidPk(PRODUCT_UID);
		products.add(product);
		final List<Long> productsUids = new ArrayList<Long>();
		productsUids.add(PRODUCT_UID);
		final StoreProductLoadTunerImpl productLoadTuner = new StoreProductLoadTunerImpl();

		context.checking(new Expectations() {
			{
				exactly(2).of(mockSearchCriteriaFactory).createProductSearchCriteria(searchRequest);
				will(returnValue(new ProductSearchCriteria()));

				allowing(mockIndexSearchService).search(with(any(ProductSearchCriteria.class)));
				will(returnValue(mockIndexSearchResult));

				oneOf(mockIndexSearchResult).getResults(0, FEATURED_PRODUCTS_COUNT);
				will(returnValue(featuredProductsUids));

				oneOf(mockStoreProductService).getProductsForStore(featuredProductsUids, store, productLoadTuner);
				will(returnValue(featuredProducts));

				oneOf(mockIndexUtility).sortDomainList(featuredProductsUids, featuredProducts);
				will(returnValue(featuredProducts));

				oneOf(mockIndexSearchResult).getResults(0, NUMBER_OF_ITEMS_PER_PAGE);
				will(returnValue(productsUids));

				oneOf(mockStoreProductService).getProductsForStore(productsUids, store, productLoadTuner);
				will(returnValue(products));

				oneOf(mockIndexUtility).sortDomainList(productsUids, products);
				will(returnValue(products));

				oneOf(mockIndexSearchResult).getLastNumFound();
				will(returnValue(NUMBER_OF_PRODUCTS_FOUND));

			}
		});
		SearchResult result = advancedSearchService.search(searchRequest, mockShoppingCart, productLoadTuner, PAGE_NUMBER);
		assertNotNull("The search result is not supposed to be null.", result);
		assertEquals("The result count does not match the expected value", NUMBER_OF_PRODUCTS_FOUND, result.getResultsCount());
		assertEquals("The products returned in the result should match the expected products", products, result.getProducts());
		assertEquals("The featured products returned in the result should match the expected featured products", featuredProducts, result
				.getFeaturedProducts());
		assertEquals("The search request returned in the result should match the sent request", searchRequest, result.getCatalogViewRequest());
	}

	private AdvancedSearchRequest setUpSearchRequest() {
		return new AdvancedSearchRequestImplTesting();
	}

	/**
	 * A fake implementation of {@link AdvancedSearchRequest} which is used for testing purposes.
	 */
	class AdvancedSearchRequestImplTesting extends AdvancedSearchRequestImpl {
		private static final long serialVersionUID = 4995690557742818385L;

		@Override
		public void setLocale(final Locale locale) {
			// does nothing. added for testing purposes
		}

		@Override
		public void setFiltersIdStr(final String filtersIdStr, final Store store) throws EpCatalogViewRequestBindException {
			// does nothing. added for testing purposes
		}

		@Override
		public void setCurrency(final Currency currency) {
			// does nothing. added for testing purposes
		}

		@Override
		public List<Filter< ? >> getAdvancedSearchFilters() {
			return null;
		}
	}

	private Shopper createShopper() {
		CustomerSession session = TestCustomerSessionFactory.getInstance().createNewCustomerSession();
		return session.getShopper();
	}

}
