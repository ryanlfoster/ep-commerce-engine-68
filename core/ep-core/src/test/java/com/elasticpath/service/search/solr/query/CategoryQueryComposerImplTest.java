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
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.CategorySearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test case for {@link CategoryQueryComposerImpl}.
 */
public class CategoryQueryComposerImplTest extends QueryComposerTestCase {

	private static final String SPACE_REGEX = "\\s";

	private CategoryQueryComposerImpl categoryQueryComposerImpl;

	private CategorySearchCriteria searchCriteria;


	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		categoryQueryComposerImpl = new CategoryQueryComposerImpl();
		categoryQueryComposerImpl.setAnalyzer(getAnalyzer());
		categoryQueryComposerImpl.setIndexUtility(getIndexUtility());

		searchCriteria = new CategorySearchCriteria();
		searchCriteria.setLocale(Locale.US);
	}

	/**
	 * Test method for {@link CategorySearchCriteria#setActiveOnly(boolean)} and
	 * {@link CategorySearchCriteria#setInActiveOnly(boolean)}.
	 */
	@Test
	public void testActiveInActive() {
		searchCriteria.setActiveOnly(true);

		Query query = categoryQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.START_DATE);
		assertQueryContainsDateRange(query, SolrIndexConstants.END_DATE);
		query = categoryQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.START_DATE);
		assertQueryContainsDateRange(query, SolrIndexConstants.END_DATE);

		searchCriteria.setActiveOnly(false);
		searchCriteria.setInActiveOnly(true);

		query = categoryQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.START_DATE);
		assertQueryContainsDateRange(query, SolrIndexConstants.END_DATE);
		query = categoryQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.START_DATE);
		assertQueryContainsDateRange(query, SolrIndexConstants.END_DATE);
	}

	/**
	 * Test method for {@link CategorySearchCriteria#setCategoryCode(String)}.
	 */
	@Test
	public void testCategoryCode() {
		final String catCode = "some code";
		searchCriteria.setCategoryCode(catCode);

		Query query = categoryQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATEGORY_CODE, catCode);
		query = categoryQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.CATEGORY_CODE, catCode);
	}

	/**
	 * Test method for {@link CategorySearchCriteria#setCategoryName(String)} and
	 * {@link CategorySearchCriteria#setCategoryNameExact(boolean).
	 */
	@Test
	public void testCategoryName() {
		final String catName = "some name";
		searchCriteria.setCategoryName(catName);

		Query query = categoryQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATEGORY_NAME, catName.split(SPACE_REGEX), searchCriteria.getLocale());
		query = categoryQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.CATEGORY_NAME, catName.split(SPACE_REGEX), searchCriteria.getLocale());

		searchCriteria.setCategoryNameExact(true);
		query = categoryQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATEGORY_NAME_EXACT, catName, searchCriteria.getLocale());
		query = categoryQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.CATEGORY_NAME_EXACT, catName, searchCriteria.getLocale());
	}

	/**
	 * Testing the query builder with multiple locales to be handled.
	 */
	@Test
	public void testCategoryNameWithMultipleLocales() {
		final String categoryName = "category name";
		searchCriteria.setCategoryName(categoryName);
		
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
		
		Query query = categoryQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATEGORY_NAME, categoryName.split(SPACE_REGEX), locales);
		
		query = categoryQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.CATEGORY_NAME, categoryName.split(SPACE_REGEX), locales);
	}	
	
	/**
	 * Test method for {@link CategorySearchCriteria#setFilterUids(Set)}.
	 */
	@Test
	public void testFilterUids() {
		final Set<Long> emptySet = Collections.emptySet();
		final Set<Long> someSet = new HashSet<Long>(Arrays.asList(123L, 34325L, 123124124L));
		Query query;

		searchCriteria.setFilterUids(emptySet);
		try {
			query = categoryQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for empty filter UID set in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
		try {
			query = categoryQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for empty filter UID set in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		searchCriteria.setFilterUids(someSet);
		query = categoryQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContainsSet(query, SolrIndexConstants.OBJECT_UID, someSet);
		query = categoryQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsSet(query, SolrIndexConstants.OBJECT_UID, someSet);
	}
	
	/**
	 * Test method for {@link CategorySearchCriteria#setDisplayableOnly(boolean)}.
	 */
	@Test
	public void testDisplayable() {
		final boolean displayable = true;
		searchCriteria.setDisplayableOnly(displayable);

		Query query = categoryQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.DISPLAYABLE, String.valueOf(displayable));
		query = categoryQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.DISPLAYABLE, String.valueOf(displayable));
	}
	
	/**
	 * Tests loading of the catalog code into a query.
	 */
	@Test
	public void testCatalogCode() {
		final String catalogCode = "SOMETHINSNAPPY";
		searchCriteria.setCatalogCodes(new HashSet<String>(Arrays.asList(catalogCode)));
		
		Query query = categoryQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATALOG_CODE, String.valueOf(catalogCode));
		query = categoryQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATALOG_CODE, String.valueOf(catalogCode));
	}
	
	/**
	 * Test method for {@link CategorySearchCriteria#setLinked(Boolean)}.
	 */
	@Test
	public void testLinkedOnly() {
		final Boolean linkedFlagFalse = false;
		searchCriteria.setLinked(linkedFlagFalse);
		Query query1 = categoryQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query1, SolrIndexConstants.CATEGORY_LINKED, String.valueOf(linkedFlagFalse));
		query1 = categoryQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query1, SolrIndexConstants.CATEGORY_LINKED, String.valueOf(linkedFlagFalse));
		
		final Boolean linkedFlagTrue = true;
		searchCriteria.setLinked(linkedFlagTrue);
		Query query2 = categoryQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query2, SolrIndexConstants.CATEGORY_LINKED, String.valueOf(linkedFlagTrue));
		query2 = categoryQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query2, SolrIndexConstants.CATEGORY_LINKED, String.valueOf(linkedFlagTrue));
	}
	
	/**
	 * Tests building a query with an ancestorCode in it.
	 */
	@Test
	public void testCategoryAncestorCode() {
		final String ancestorCode = "90000011";
		searchCriteria.setAncestorCode(ancestorCode);
		
		Query query = categoryQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.PARENT_CATEGORY_CODES, String.valueOf(ancestorCode));
		query = categoryQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.PARENT_CATEGORY_CODES, String.valueOf(ancestorCode));
	}
	
	@Override
	protected QueryComposer getComposerUnderTest() {
		return categoryQueryComposerImpl;
	}

	@Override
	protected SearchCriteria getCriteriaUnderTest() {
		return searchCriteria;
	}
}
