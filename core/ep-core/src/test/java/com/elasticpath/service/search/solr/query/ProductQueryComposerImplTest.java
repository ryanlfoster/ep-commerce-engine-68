package com.elasticpath.service.search.solr.query;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import org.apache.lucene.search.Query;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.EpEmptySearchCriteriaException;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.IndexUtilityImpl;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test case for {@link ProductQueryComposerImpl}.
 */
public class ProductQueryComposerImplTest extends QueryComposerTestCase {

	private static final String S_SYMBOL = "\\s";

	private ProductQueryComposerImpl productQueryComposerImpl;

	private ProductSearchCriteria searchCriteria;


	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		productQueryComposerImpl = new ProductQueryComposerImpl();
		productQueryComposerImpl.setAnalyzer(getAnalyzer());
		productQueryComposerImpl.setIndexUtility(getIndexUtility());
		productQueryComposerImpl.setSolrQueryFactory(getSolrQueryFactory());

		searchCriteria = new ProductSearchCriteria();
		searchCriteria.setLocale(Locale.US);
	}

	/**
	 * Test method for {@link ProductSearchCriteria#setProductName(String)}.
	 */
	@Test
	public void testProductName() {
		final String productName = "product name";
		searchCriteria.setProductName(productName);

		Query query = productQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.PRODUCT_NAME, productName.split(S_SYMBOL), searchCriteria.getLocale());
		query = productQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.PRODUCT_NAME, productName.split(S_SYMBOL), searchCriteria.getLocale());
	}

	/**
	 * Testing the query builder with multiple locales to be handled.
	 */
	@Test
	public void testProductNameWithMultipleLocales() {
		final String productName = "product name";
		searchCriteria.setProductName(productName);
		
		Locale frCA = Locale.CANADA_FRENCH;
		Locale enCA = Locale.CANADA;
		Locale frFR = Locale.FRANCE;
		Locale french = Locale.FRENCH;
		
		Set<Locale> locales = new HashSet<Locale>();
		locales.add(french);
		locales.add(frFR);
		locales.add(enCA);
		locales.add(frCA);
		
		searchCriteria.setCatalogSearchableLocales(locales);
		
		Query query = productQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.PRODUCT_NAME, productName.split(S_SYMBOL), locales);
		
		query = productQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.PRODUCT_NAME, productName.split(S_SYMBOL), locales);
	}
	
	/**
	 * Test method for {@link ProductSearchCriteria#setProductSku(String)}.
	 */
	@Test
	public void testProductSku() {
		final String productSku = "product sku";
		searchCriteria.setProductSku(productSku);

		Query query = productQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.PRODUCT_SKU_CODE, productSku);
		query = productQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.PRODUCT_SKU_CODE, productSku);
	}

	/**
	 * Test method for {@link ProductSearchCriteria#setProductCode(String)}.
	 */
	@Test
	public void testProductCode() {
		final String productCode = "product code";
		searchCriteria.setProductCode(productCode);

		Query query = productQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.PRODUCT_CODE, productCode);
		query = productQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.PRODUCT_CODE, productCode);
	}

	/**
	 * Test that when the Category UID is set in the search criteria, the 
	 * Category Code will be added to the index.
	 */
	@Test
	public void testCategoryUid() {
		final long directCategoryUid = 123;
		final String directCategoryCode = "categoryCode";
		final String catalogCode = "catalogCode";
		searchCriteria.setDirectCategoryUid(directCategoryUid);
		searchCriteria.setCatalogCode(catalogCode);

		ProductQueryComposerImpl composer = new ProductQueryComposerImpl() {
			@Override
			protected String getCategoryCodeFromProductSearchCriteria(final ProductSearchCriteria productSearchCriteria) {
				return directCategoryCode;
			}
		};
		composer.setAnalyzer(getAnalyzer());
		composer.setIndexUtility(getIndexUtility());
		
		Query query = composer.composeQuery(searchCriteria, getSearchConfig());
		String productCategoryFieldName = new IndexUtilityImpl().createProductCategoryFieldName(SolrIndexConstants.PRODUCT_CATEGORY, catalogCode);
		assertQueryContains(query, productCategoryFieldName, directCategoryCode);
		query = composer.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.PRODUCT_CATEGORY + "_" + catalogCode, directCategoryCode);
	}

	/**
	 * Test method for {@link ProductSearchCriteria#setAncestorCategoryUids(Set)}.
	 */
	@Test
	public void testAncestorCategoryUids() {
		final Set<Long> emptySet = Collections.emptySet();
		final Set<Long> ancestorUids = new HashSet<Long>(Arrays.asList(1357L, 16198L, 777L));
		final Set<String> ancestorCodes = new HashSet<String>(Arrays.asList("Code1", "Code2", "Code3"));
		Query query;

		ProductQueryComposerImpl composer = new ProductQueryComposerImpl() {
			@Override
			protected Set<String> getAncestorCategoryCodesFromProductSearchCritiera(final ProductSearchCriteria productSearchCriteria) {
				if (productSearchCriteria.getAncestorCategoryUids() != null && !productSearchCriteria.getAncestorCategoryUids().isEmpty()) {
					return ancestorCodes;
				}
				return null;
			}
		};
		composer.setAnalyzer(getAnalyzer());
		composer.setIndexUtility(getIndexUtility());
		
		searchCriteria.setAncestorCategoryUids(emptySet);
		try {
			query = composer.composeQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for empty ancestor UID set in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
		try {
			query = composer.composeFuzzyQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for empty ancestor UID set in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		searchCriteria.setAncestorCategoryUids(ancestorUids);
		query = composer.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContainsSet(query, SolrIndexConstants.PARENT_CATEGORY_CODES, ancestorCodes);
		query = composer.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsSet(query, SolrIndexConstants.PARENT_CATEGORY_CODES, ancestorCodes);
	}

	/**
	 * Test method for {@link ProductSearchCriteria#setBrandCode(String)}.
	 */
	@Test
	public void testBrandCode() {
		final String brandCode = "brand code";
		searchCriteria.setBrandCode(brandCode);

		Query query = productQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.BRAND_CODE, brandCode);
		query = productQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.BRAND_CODE, brandCode);
	}

	/**
	 * Test method for {@link ProductSearchCriteria#setActiveOnly(boolean)} and
	 * {@link ProductSearchCriteria#setInActiveOnly(boolean)}.
	 */
	@Test
	public void testActiveInActive() {
		searchCriteria.setActiveOnly(true);

		Query query = productQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.START_DATE);
		assertQueryContainsDateRange(query, SolrIndexConstants.END_DATE);
		query = productQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.START_DATE);
		assertQueryContainsDateRange(query, SolrIndexConstants.END_DATE);

		searchCriteria.setActiveOnly(false);
		searchCriteria.setInActiveOnly(true);

		query = productQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.START_DATE);
		assertQueryContainsDateRange(query, SolrIndexConstants.END_DATE);
		query = productQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.START_DATE);
		assertQueryContainsDateRange(query, SolrIndexConstants.END_DATE);
	}

	/**
	 * Test method for {@link ProductSearchCriteria#setFilterUids(Set)}.
	 */
	@Test
	public void testFilterUids() {
		final Set<Long> emptySet = Collections.emptySet();
		final Set<Long> someSet = new HashSet<Long>(Arrays.asList(123L, 34325L, 123124124L));
		Query query;

		searchCriteria.setFilterUids(emptySet);
		try {
			query = productQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for empty filter UID set in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
		try {
			query = productQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for empty filter UID set in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		searchCriteria.setFilterUids(someSet);
		query = productQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContainsSet(query, SolrIndexConstants.OBJECT_UID, someSet);
		query = productQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsSet(query, SolrIndexConstants.OBJECT_UID, someSet);
	}

	/**
	 * Test method for {@link ProductSearchCriteria#setOnlyFeaturedProducts(boolean)}.
	 */
	@Test
	public void testFeaturedProductsOnly() {
		searchCriteria.setOnlyFeaturedProducts(true);

		Query query = productQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.FEATURED, String.valueOf(true));
		query = productQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.FEATURED, String.valueOf(true));
	}
	
	/**
	 * Test that that if {@link ProductSearchCriteria#setFeaturedOnlyInCategory(boolean)} is
	 * true without setting a category UID then an exception will be thrown.
	 */
	@Test
	public void testFeaturedOnlyInCategoryRequiresCategoryUid() {
		searchCriteria.setFeaturedOnlyInCategory(true);
		
		try {
			productQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
			fail("Expected EpEmptySearchCriteriaException. FeaturedOnlyInCategory not valid alone.");
		} catch (EpEmptySearchCriteriaException e) {
			assertNotNull(e);
		}
		try {
			productQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
			fail("Expected EpEmptySearchCriteriaException. FeaturedOnlyInCategory not valid alone.");
		} catch (EpEmptySearchCriteriaException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 * Test that when the {@link ProductSearchCriteria#setFeaturedOnlyInCategory(boolean)} 
	 * is true then the generated query and the generated fuzzy query contain the appropriate
	 * key/value pair.
	 */
	@Test
	public void testFeaturedOnlyInCategory() {
		final ProductSearchCriteria pSearchCriteria = new ProductSearchCriteria();
		pSearchCriteria.setLocale(Locale.US);
		pSearchCriteria.setFeaturedOnlyInCategory(true);
		pSearchCriteria.setOnlyFeaturedProducts(true);
		final long categoryUid = 235235;
		final String directCategoryCode = "categoryCode";
		final String catalogCode = "catalogCode";
		pSearchCriteria.setDirectCategoryUid(categoryUid);		
		pSearchCriteria.setCatalogCode(catalogCode);
		
		ProductQueryComposerImpl composer = new ProductQueryComposerImpl() {
			@Override
			protected String getCategoryCodeFromProductSearchCriteria(final ProductSearchCriteria productSearchCriteria) {
				return directCategoryCode;
			}
		};
		composer.setAnalyzer(getAnalyzer());
		composer.setIndexUtility(getIndexUtility());
		
		Query query = composer.composeQueryInternal(pSearchCriteria, getSearchConfig());
		assertQueryContains(query, String.valueOf(pSearchCriteria.getCategoryUid()), String.valueOf(0));
		query = composer.composeFuzzyQueryInternal(pSearchCriteria, getSearchConfig());
		assertQueryContains(query, String.valueOf(pSearchCriteria.getCategoryUid()), String.valueOf(0));
	}
	
	/**
	 * Test method for {@link ProductSearchCriteria#setDisplayableOnly(boolean)}.
	 */
	@Test
	public void testDisplayable() {
		final boolean displayable = true;
		searchCriteria.setDisplayableOnly(displayable);
		
		try {
			productQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
			fail("EpUnsupportedOperationException expected");
		} catch (EpUnsupportedOperationException e) {
			assertNotNull(e);
		}
		
		try {
			productQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
			fail("EpUnsupportedOperationException expected");
		} catch (EpUnsupportedOperationException e) {
			assertNotNull(e);
		}
		
		final String storeCode = "MYSTORE";
		searchCriteria.setStoreCode(storeCode);

		Query query = productQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, getIndexUtility().createDisplayableFieldName(SolrIndexConstants.DISPLAYABLE, storeCode), String
				.valueOf(displayable));
		query = productQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, getIndexUtility().createDisplayableFieldName(SolrIndexConstants.DISPLAYABLE, storeCode), String
				.valueOf(displayable));
	}
	
	/**
	 * Test method for {@link ProductSearchCriteria#setCatalogUid(Long)}.
	 */
	@Test
	public void testCatalogUid() {
		final String catalogCode = "catalogCode";

		final ProductSearchCriteria pSearchCriteria = new ProductSearchCriteria();
		pSearchCriteria.setLocale(Locale.US);
		pSearchCriteria.setCatalogCode(catalogCode);
		
		ProductQueryComposerImpl composer = new ProductQueryComposerImpl();
		composer.setAnalyzer(getAnalyzer());
		composer.setIndexUtility(getIndexUtility());
		
		Query query = composer.composeQuery(pSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATALOG_CODE, String.valueOf(catalogCode));
		query = composer.composeFuzzyQuery(pSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATALOG_CODE, String.valueOf(catalogCode));
	}

	@Override
	protected QueryComposer getComposerUnderTest() {
		return productQueryComposerImpl;
	}

	@Override
	protected SearchCriteria getCriteriaUnderTest() {
		return searchCriteria;
	}
}
