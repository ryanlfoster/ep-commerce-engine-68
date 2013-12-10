package com.elasticpath.service.search.solr.query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import org.apache.lucene.search.Query;

import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SkuSearchCriteria;


/**
 * The unit test class for {@link SkuQueryComposerImpl}.
 */
public class SkuQueryComposerImplTest extends QueryComposerTestCase {
	
	private SkuQueryComposerImpl skuQueryComposerImpl;
	private SkuSearchCriteria searchCriteria;

	@Override
	protected QueryComposer getComposerUnderTest() {
		return skuQueryComposerImpl;
	}

	@Override
	protected SearchCriteria getCriteriaUnderTest() {
		return searchCriteria;
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();

		skuQueryComposerImpl = new SkuQueryComposerImpl();
		skuQueryComposerImpl.setAnalyzer(getAnalyzer());
		skuQueryComposerImpl.setIndexUtility(getIndexUtility());

		searchCriteria = new SkuSearchCriteria();
		searchCriteria.setLocale(Locale.US);
		
		Locale[] localeArray = new Locale[]{Locale.US, Locale.FRENCH};
		Set<Locale> locales = new HashSet<Locale>(Arrays.asList(localeArray));
		searchCriteria.setCatalogSearchableLocales(locales);
	}
	
	/**
	 * Test query for sku options.
	 */
	@Test
	public void testSkuOptions() {
		String skuOptionKey = "TEST";
		String[] skuOptionValueArray = new String[]{"OPTION_VALUE_1", "OPTION_VALUE_2"};
		HashSet<String> skuOptionValues = new HashSet<String>(Arrays.asList(skuOptionValueArray));
		
		searchCriteria.addSkuOptionAndValues(skuOptionKey, skuOptionValues);

		Query query = skuQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, skuOptionKey, skuOptionValueArray, searchCriteria.getCatalogSearchableLocales());
		
		query = skuQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, skuOptionKey, skuOptionValueArray, searchCriteria.getCatalogSearchableLocales());
	}
	
	/**
	 * test the query for sku options with default local from search criteria.
	 */
	@Test
	public void testSkuOptionsWithDefaultLocaleFromSearchCriteria() {
		String skuOptionKey = "TEST";
		String[] skuOptionValueArray = new String[]{"OPTION_VALUE_1", "OPTION_VALUE_2"};
		HashSet<String> skuOptionValues = new HashSet<String>(Arrays.asList(skuOptionValueArray));
		
		searchCriteria.addSkuOptionAndValues(skuOptionKey, skuOptionValues);
		
		searchCriteria.setCatalogSearchableLocales(new HashSet<Locale>());

		Query query = skuQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, skuOptionKey, skuOptionValueArray, searchCriteria.getLocale());
		
		query = skuQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, skuOptionKey, skuOptionValueArray, searchCriteria.getCatalogSearchableLocales());
	}

}
