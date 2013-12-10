package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Currency;
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
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.domain.catalogview.search.impl.SearchRequestImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.search.query.CategorySearchCriteria;
import com.elasticpath.service.search.query.KeywordSearchCriteria;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for {@link SearchCriteriaFactoryImpl}.
 */
public class SearchCriteriaFactoryImplTest {

	private static final String STORE_CODE = "store_code";

	private static final String ANCESTOR_CATEGORY_CODE = "1234";

	private static final String KEYWORDS = "sony";

	private static final String CATALOG_CODE = "CATALOG_CODE";

	private SearchCriteriaFactoryImpl searchCriteriaFactory;

	private CategoryService mockCategoryService;

	private StoreConfig mockStoreConfig;

	private CatalogService mockCatalogService;

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final Locale LOCALE = Locale.CANADA;

	private static final Currency CURRENCY = Currency.getInstance(LOCALE);

	private static final long CATEGORY_UID = 123L;

	/**
	 * The setup steps.
	 */
	@Before
	public void setUp() {
		searchCriteriaFactory = new SearchCriteriaFactoryImpl();
		mockStoreConfig = context.mock(StoreConfig.class);
		mockCategoryService = context.mock(CategoryService.class);
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		searchCriteriaFactory.setCatalogService(mockCatalogService);
		searchCriteriaFactory.setCategoryService(mockCategoryService);
		searchCriteriaFactory.setStoreConfig(mockStoreConfig);
		final Store store = new StoreImpl();
		Catalog catalog = new CatalogImpl();
		catalog.setCode(CATALOG_CODE);
		store.setCatalog(catalog);
		context.checking(new Expectations() {
			{
				allowing(mockStoreConfig).getStore();
				will(returnValue(store));
				
				allowing(mockStoreConfig).getStoreCode();
				will(returnValue(STORE_CODE));
			}
		});

	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests population of search criteria from a search request with np category uid by <code>createCategorySearchCriteria()</code>.
	 */
	@Test
	public void testCreateCategorySearchCriteriaNoCategoryUid() {

		SearchRequest searchRequest = setUpSearchRequest();

		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.CATEGORY_SEARCH_CRITERIA);
				will(returnValue(new CategorySearchCriteria()));
			}
		});

		CategorySearchCriteria categorySearchCriteria = searchCriteriaFactory.createCategorySearchCriteria(searchRequest);

		examineSearchCriteria(categorySearchCriteria);

		assertNull("The ancestor category code in the search criteria should be null", categorySearchCriteria.getAncestorCode());
	}

	/**
	 * Tests population of search criteria from a search request with category uid by <code>createCategorySearchCriteria()</code>.
	 */
	@Test
	public void testCreateCategorySearchCriteriaWithCategoryUid() {

		SearchRequest searchRequest = setUpSearchRequest();
		searchRequest.setCategoryUid(CATEGORY_UID);

		context.checking(new Expectations() {
			{
				oneOf(mockCategoryService).findCodeByUid(CATEGORY_UID);
				will(returnValue(ANCESTOR_CATEGORY_CODE));

				oneOf(beanFactory).getBean(ContextIdNames.CATEGORY_SEARCH_CRITERIA);
				will(returnValue(new CategorySearchCriteria()));

			}
		});

		CategorySearchCriteria categorySearchCriteria = searchCriteriaFactory.createCategorySearchCriteria(searchRequest);

		examineSearchCriteria(categorySearchCriteria);
		assertEquals("The ancestor category code in the search criteria should be equal to the category code ", ANCESTOR_CATEGORY_CODE,
				categorySearchCriteria.getAncestorCode());
	}

	/**
	 * Test for <code>createProductSearchCriteria</code> to make sure the object is created.
	 */
	@Test
	public void testCreateProductSearchCriteria() {

		SearchRequest searchRequest = setUpSearchRequest();
		context.checking(new Expectations() {
			{

				oneOf(beanFactory).getBean(ContextIdNames.PRODUCT_SEARCH_CRITERIA);
				will(returnValue(new ProductSearchCriteria()));

			}
		});
		
		
		assertNotNull("The product search criteria object should not be null", searchCriteriaFactory.createProductSearchCriteria(searchRequest));
	}
	
	/**
	 * Test for <code>createKeywordProductCategorySearchCriteria</code> to make sure the object is created.
	 */
	@Test
	public void testCreateKeywordProductCategorySearchCriteria() {
		SearchRequest searchRequest = setUpSearchRequest();
		
		context.checking(new Expectations() {
			{

				oneOf(beanFactory).getBean(ContextIdNames.KEYWORD_SEARCH_CRITERIA);
				will(returnValue(new KeywordSearchCriteria()));

			}
		});
		
		KeywordSearchCriteria keywordSearchCriteria = searchCriteriaFactory.createKeywordProductCategorySearchCriteria(searchRequest);
		assertEquals("The catalog code for search criteria should be the same as the store catalog code.", CATALOG_CODE, keywordSearchCriteria
				.getCatalogCode());
		assertEquals("The search criteria should contain the store code from the search request.", STORE_CODE, keywordSearchCriteria.getStoreCode());
		assertEquals("The search criteria should have the same keywords as the search request.", KEYWORDS, keywordSearchCriteria.getKeyword());
		assertEquals("The search criteria should have isFuzzySearchEnabled set to false.", false, keywordSearchCriteria.isFuzzySearchDisabled());
		
	}

	private void examineSearchCriteria(final CategorySearchCriteria categorySearchCriteria) {
		assertEquals("The category name in the search criteria should contain the key words from the search request.", KEYWORDS,
				categorySearchCriteria.getCategoryName());
		assertEquals("The search criteria should contain the locale from the search request.", LOCALE, categorySearchCriteria.getLocale());
		assertEquals("The search criteria should have categoryNameExact set to true.", true, categorySearchCriteria.isCategoryNameExact());
		assertEquals("The search criteria should have displayableOnly set to true.", true, categorySearchCriteria.isDisplayableOnly());
		assertNotNull("The collection of catalog codes in the search criteria should not be null.", categorySearchCriteria.getCatalogCodes());
		assertTrue("The collection of catalog codes in the search criteria should contain the catalogCode.", categorySearchCriteria
				.getCatalogCodes().contains(CATALOG_CODE));
	}

	private SearchRequest setUpSearchRequest() {
		SearchRequest searchRequest = new SearchRequestImpl() {
			private static final long serialVersionUID = -5966026798431773853L;

			/**
			 * overrides the setFilterIdStr to remove the extra logic not required for the purpose of this tests.
			 * 
			 * @param filtersIdStr the filter id str
			 * @param store the store
			 * @throws EpCatalogViewRequestBindException not being used
			 */
			@Override
			public void setFiltersIdStr(final String filtersIdStr, final Store store) throws EpCatalogViewRequestBindException {
				// does nothing
			};
		};
		searchRequest.setCurrency(CURRENCY);
		searchRequest.setFiltersIdStr("filter", null);
		searchRequest.setLocale(LOCALE);
		searchRequest.setKeyWords(KEYWORDS);

		return searchRequest;
	}

}
