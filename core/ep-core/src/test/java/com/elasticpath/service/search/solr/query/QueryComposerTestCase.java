package com.elasticpath.service.search.solr.query;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.ConstantScoreRangeQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.domain.misc.impl.SearchConfigImpl;
import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.index.Analyzer;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.IndexUtility;
import com.elasticpath.service.search.solr.IndexUtilityImpl;
import com.elasticpath.service.search.solr.SolrQueryFactory;
import com.elasticpath.service.search.solr.SolrQueryFactoryImpl;

/**
 * This is an abstract Test case. It contains some simple checks for <code>QueryComposer</code>s.
 */
@SuppressWarnings("PMD.AbstractNaming")
public abstract class QueryComposerTestCase {

	private static final char FUZZY_CHAR = '~';

	private static IndexUtility indexUtility;
	
	private static Analyzer analyzer;
	
	private static SolrQueryFactoryImpl solrQueryFactory;

	private static final String EXPECTED_WITHIN = "Expected <%1$s> within <%2$s>.";
	
	private static final String NOT_EXPECTED_WITHIN = "Did not expected <%1$s> within <%2$s>.";
	
	private SearchConfig searchConfig;
	
	static {
		//For test purposes we'll override the methods in IndexUtility that would normally make service calls,
		//and simply return a sensible string.
		indexUtility = new IndexUtilityImpl() {
			@Override
			public String createFeaturedField(final long categoryUid) {
				return String.valueOf(categoryUid);
			}
			
			@Override
			public String createDisplayableFieldName(final String name, final String storeCode) {
				return name + storeCode;
			}
		};
		
		analyzer = new AnalyzerImpl();
		
		solrQueryFactory = new SolrQueryFactoryImpl();
		solrQueryFactory.setAnalyzer(analyzer);
	}
	
	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception in case of any errors
	 */
	@Before
	public void setUp() throws Exception {
		searchConfig = new SearchConfigImpl();
	}
	
	/**
	 * Asserts that a query does not contain the specified key and value.
	 *
	 * @param query the query
	 * @param key the key a <code>query</code> must not contain
	 * @param value the value a <code>query</code> must not contain
	 */
	public static void assertQueryNotContains(final Query query, final String key, final String value) {
		assertQueryNotContains(query, key, new String[] { value });
	}
	
	/**
	 * Asserts that a query does not contain the specified key and array of values. Each value in
	 * the array must not occur at all within the given query.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must not contain
	 * @param values the values <code>query</code> must not contain
	 */
	public static void assertQueryNotContains(final Query query, final String key, final String[] values) {
		if (values == null) {
			return;
		}
		for (String value : values) {
			final TermQuery testQuery = new TermQuery(new Term(key, value));
			if (query.toString().indexOf(testQuery.toString()) >= 0) {
				fail(String.format(NOT_EXPECTED_WITHIN, testQuery.toString(), query.toString()));
			}
		}
	}

	/**
	 * Asserts that a query contains the specified key and value.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param value the value <code>query</code> must contain
	 */
	public static void assertQueryContains(final Query query, final String key, final String value) {
		assertQueryContains(query, key, new String[] { value });
	}

	/**
	 * Asserts that a query contains the specified key and array of values. Each value in the
	 * array must occur a separate time within the given query.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param values the values <code>query</code> must contain
	 */
	public static void assertQueryContains(final Query query, final String key, final String[] values) {
		if (values == null) {
			return;
		}
		for (String value : values) {
			final TermQuery testQuery = new TermQuery(new Term(key, value));
			final boolean assertion = query.toString().indexOf(testQuery.toString()) >= 0;
			if (!assertion) {
				fail(String.format(EXPECTED_WITHIN, testQuery.toString(), query.toString()));
			}
		}
	}

	/**
	 * Asserts that a query contains the specified key and value. The key is modified such that it
	 * conforms to the given locale.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param value the value <code>query</code> must contain
	 * @param locale the locale to test for
	 */
	public static void assertQueryContains(final Query query, final String key, final String value, final Locale locale) {
		assertQueryContains(query, key, new String[] { value }, locale);
	}

	/**
	 * Asserts that a query contains the specified key and value. The key is modified such that it
	 * conforms to each of given locales.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param values the values <code>query</code> must contain
	 * @param locales the locales to test for
	 */
	public static void assertQueryContains(final Query query, final String key, final String[] values, final Set<Locale> locales) {
		for (Locale locale : locales) {
			assertQueryContains(query, key, values, locale);		
		}
	}
	
	/**
	 * Asserts that a query contains the specified key and value. The key is modified such that it
	 * conforms to the given locale. Each value in the array must occur a separate time within the
	 * given query.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param values the values <code>query</code> must contain
	 * @param locale the locale to test for
	 */
	public static void assertQueryContains(final Query query, final String key, final String[] values, final Locale locale) {
		if (values == null) {
			return;
		}
		for (String value : values) {
			TermQuery testQuery = new TermQuery(new Term(indexUtility.createLocaleFieldName(key, locale), value));
			boolean assertion = query.toString().indexOf(testQuery.toString()) >= 0;
			if (!assertion) {
				fail(String.format(EXPECTED_WITHIN, testQuery.toString(), query.toString()));
			}
		}
	}

	/**
	 * Asserts that a query contains the specified key and value that are not specified for a
	 * fuzzy search. Does not depend on the exact fuzziness of the query.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param value the value <code>query</code> must contain
	 */
	public static void assertQueryContainsNotFuzzy(final Query query, final String key, final String value) {
		assertQueryContainsNotFuzzy(query, key, new String[] { value });
	}

	/**
	 * Asserts that a query contains the specified key and value that are not specified for a
	 * fuzzy search. Does not depend on the exact fuzziness of the query. Each value in the array
	 * must occur a separate time within the given query.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param values the values <code>query</code> must contain
	 */
	public static void assertQueryContainsNotFuzzy(final Query query, final String key, final String[] values) {
		if (values == null) {
			return;
		}
		for (String value : values) {
			TermQuery testQuery = new TermQuery(new Term(key, value));
			int index = query.toString().indexOf(testQuery.toString());
			if (index < 0) {
				fail(String.format(EXPECTED_WITHIN, testQuery.toString(), query.toString()));
			}
			if (testNextChar(query.toString(), index + testQuery.toString().length(), FUZZY_CHAR)) {
				fail(String.format("Expected non-fuzzy search for <%1$s> within <%2$s>.", testQuery.toString(), query.toString()));
			}
		}
	}

	/**
	 * Asserts that a query contains the specified key and value that are not specified for a
	 * fuzzy search. Does not depend on the exact fuzziness of the query. The key is modified such
	 * that it conforms to the given locale.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param value the value <code>query</code> must contain
	 * @param locale the locale to test for
	 */
	public static void assertQueryContainsNotFuzzy(final Query query, final String key, final String value, final Locale locale) {
		assertQueryContainsNotFuzzy(query, key, new String[] { value }, locale);
	}

	/**
	 * Asserts that a query contains the specified key and value that are not specified for a
	 * fuzzy search. Does not depend on the exact fuzziness of the query. The key is modified such
	 * that it conforms to the given locale. Each value in the array must occur a separate time
	 * within the given query.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param values the values <code>query</code> must contain
	 * @param locale the locale to test for
	 */
	public static void assertQueryContainsNotFuzzy(final Query query, final String key, final String[] values, final Locale locale) {
		if (values == null) {
			return;
		}
		for (String value : values) {
			TermQuery testQuery = new TermQuery(new Term(indexUtility.createLocaleFieldName(key, locale), value));
			int index = query.toString().indexOf(testQuery.toString());
			if (index < 0) {
				fail(String.format(EXPECTED_WITHIN, testQuery.toString(), query.toString()));
			}
			if (testNextChar(query.toString(), index + testQuery.toString().length(), FUZZY_CHAR)) {
				fail(String.format("Expected non-fuzzy search for <%1$s> within <%2$s>.", testQuery.toString(), query.toString()));
			}
		}
	}

	/**
	 * Asserts that a query contains the given key and value as a fuzzy search. Does not depend on
	 * the exact fuzziness of the query.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param value the value <code>query</code> must contain
	 */
	public static void assertQueryContainsFuzzy(final Query query, final String key, final String value) {
		assertQueryContainsFuzzy(query, key, new String[] { value });
	}

	/**
	 * Asserts that a query contains the given key and value as a fuzzy search. Does not depend on
	 * the exact fuzziness of the query. Each value in the array must occur a separate time within
	 * the given query.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param values the values <code>query</code> must contain
	 */
	public static void assertQueryContainsFuzzy(final Query query, final String key, final String[] values) {
		if (values == null) {
			return;
		}
		for (String value : values) {
			FuzzyQuery testQuery = new FuzzyQuery(new Term(key, value));

			// remove actual fuzziness just so that we aren't dependent on that
			String fuzzyQuery = testQuery.toString().substring(0, testQuery.toString().indexOf(FUZZY_CHAR) + 1);
			if (query.toString().indexOf(fuzzyQuery) < 0) {
				fail(String.format("Expected fuzzy search for <%1$s> within <%2$s>.", fuzzyQuery, query.toString()));
			}
		}

	}

	/**
	 * Asserts that a query contains the given key and value as a fuzzy search. Does not depend on
	 * the exact fuzziness of the query. The key is modified such that it conforms to the given
	 * locale.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param value the value <code>query</code> must contain
	 * @param locale the locale to test for
	 */
	public static void assertQueryContainsFuzzy(final Query query, final String key, final String value, final Locale locale) {
		assertQueryContainsFuzzy(query, key, new String[] { value }, locale);
	}

	/**
	 * Asserts that a query contains the given key and value as a fuzzy search. Does not depend on
	 * the exact fuzziness of the query. The key is modified such that it conforms to the given
	 * locale. Each value in the array must occur a separate time within the given query.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param values the values <code>query</code> must contain
	 * @param locale the locale to test for
	 */
	public static void assertQueryContainsFuzzy(final Query query, final String key, final String[] values, final Locale locale) {
		if (values == null) {
			return;
		}
		for (String value : values) {
			FuzzyQuery testQuery = new FuzzyQuery(new Term(indexUtility.createLocaleFieldName(key, locale), value));

			// remove actual fuzziness just so that we aren't dependent on that
			String fuzzyQuery = testQuery.toString().substring(0, testQuery.toString().indexOf(FUZZY_CHAR) + 1);
			if (query.toString().indexOf(fuzzyQuery) < 0) {
				fail(String.format("Expected fuzzy search for <%1$s> within <%2$s>.", fuzzyQuery, query.toString()));
			}
		}
	}

	/**
	 * Asserts that a query contains the given key and value as a fuzzy search. Does not depend on
	 * the exact fuzziness of the query. The key is modified such that it conforms to each of given
	 * locales. Each value in the array must occur a separate time within the given query.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param values the values <code>query</code> must contain
	 * @param locales the locales to test for
	 */
	public static void assertQueryContainsFuzzy(final Query query, final String key, final String[] values, final Set<Locale> locales) {
		for (Locale locale : locales) {
			assertQueryContainsFuzzy(query, key, values, locale);
		}
	}
	
	/**
	 * Asserts that a query contains a search for the given collection under the given key.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param collection the locale to test for
	 */
	public static void assertQueryContainsSet(final Query query, final String key, final Collection< ? > collection) {
		if (collection == null || collection.isEmpty()) {
			return;
		}

		TermQuery testQuery = new TermQuery(new Term(key, ""));

		int index = -1;
		boolean assertion = false;

		// ensure that our key occurs collection.size() many times
		for (int i = 0; i < collection.size(); ++i) {
			// ensure we don't catch the same string again ( + 1 )
			index = query.toString().indexOf(testQuery.toString(), index + 1);
			if (index < 0) {
				assertion = false;
				break;
			}
			assertion = true;
		}

		if (!assertion) {
			fail(String.format("Expected <%1$s> %2$s many times within <%3$s>", testQuery.toString(), collection.size(), query
					.toString()));
		}
	}

	/**
	 * Asserts that a query contains the specified key for a date query.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 * @param startDate the start date that should be used
	 * @param endDate the end date that should be used
	 * @param startInclusive whether the start date is inclusive
	 * @param endInclusive whether the end date is inclusive
	 */
	public static void assertQueryContainsDateRange(final Query query, final String key, final Date startDate, final Date endDate,
			final boolean startInclusive, final boolean endInclusive) {
		String startDateStr = null;
		String endDateStr = null;
		if (startDate != null) {
			startDateStr = analyzer.analyze(startDate);
		}
		if (endDate != null) {
			endDateStr = analyzer.analyze(endDate);
		}
		
		ConstantScoreRangeQuery testQuery = new ConstantScoreRangeQuery(key, startDateStr, endDateStr, startInclusive,
				endInclusive);
		int index = query.toString().indexOf(testQuery.toString());
		if (index < 0) {
			fail(String.format(EXPECTED_WITHIN, testQuery.toString(), query.toString()));
		}
	}
	
	/**
	 * Asserts that a query contains the specified key for a date query. Does <i>not</i> depend
	 * on the date.
	 * 
	 * @param query the query
	 * @param key the key a <code>query</code> must contain
	 */
	public static void assertQueryContainsDateRange(final Query query, final String key) {
		TermQuery testQuery = new TermQuery(new Term(key, ""));
		Pattern dateRangePattern = Pattern.compile(testQuery.toString() + "[\\[\\{]\\S+? TO \\S+?[\\}\\]]");
		if (!dateRangePattern.matcher(query.toString()).find()) {
			fail(String.format("Expected date range search <%1$s> within <%2$s>", dateRangePattern.pattern(), query.toString()));
		}
	}

	/**
	 * Tests the next character of a string at the given index for the given character.
	 * 
	 * @param string the string
	 * @param index the index to look at
	 * @param character the character that should be there
	 * @return a boolean whether the character is at index
	 */
	private static boolean testNextChar(final String string, final int index, final char character) {
		if (string.length() <= index) {
			return false;
		}
		if (string.charAt(index) == character) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the {@link IndexUtility} instance.
	 *
	 * @return the {@link IndexUtility} instance
	 */
	protected IndexUtility getIndexUtility() {
		return indexUtility;
	}
	
	/**
	 * Returns the {@link Analyzer} instance.
	 *
	 * @return the {@link Analyzer} instance
	 */
	protected Analyzer getAnalyzer() {
		return analyzer;
	}
	
	/**
	 * Returns the SolrQueryFactory instance.
	 *
	 * @return the SolrQueryFactory instance
	 */
	protected SolrQueryFactory getSolrQueryFactory() {
		return solrQueryFactory;
	}
	
	/**
	 * Returns a default search configuration to use.
	 * 
	 * @return a default search configuration to use
	 */
	protected SearchConfig getSearchConfig() {
		return searchConfig;
	}
	
	/**
	 * Returns the proper {@link SearchCriteria} that should be used to test the
	 * {@link QueryComposer}.
	 * 
	 * @return the proper {@link SearchCriteria} that should be used for the test
	 */
	protected abstract SearchCriteria getCriteriaUnderTest();
	
	/**
	 * Returns the proper {@link QueryComposer} that should be used to test the
	 * {@link QueryComposer}.
	 * 
	 * @return the proper {@link SearchCriteria} that should be used for the test
	 */
	protected abstract QueryComposer getComposerUnderTest();
	
	/**
	 * Test method for wrong search criteria.
	 */
	@Test
	public void testWrongSearchCriteria() {
		final SearchCriteria wrongSearchCriteria = new AbstractSearchCriteriaImpl() {
			private static final long serialVersionUID = -9034421640079035307L;

			public void optimize() {
				// do nothing
			}

			public IndexType getIndexType() {
				return null;
			}
		};
		
		SearchConfig searchConfig = new SearchConfigImpl();

		try {
			getComposerUnderTest().composeQuery(wrongSearchCriteria, searchConfig);
			fail("EpServiceException expected for wrong search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		try {
			getComposerUnderTest().composeFuzzyQuery(wrongSearchCriteria, searchConfig);
			fail("EpServiceException expected for wrong search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 * Test method for empty search criteria.
	 */
	@Test
	public void testEmptyCriteria() {
		SearchConfig searchConfig = new SearchConfigImpl();
		try {
			getComposerUnderTest().composeQuery(getCriteriaUnderTest(), searchConfig);
			fail("EpServiceException expected for no parameters in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		try {
			getComposerUnderTest().composeFuzzyQuery(getCriteriaUnderTest(), searchConfig);
			fail("EpServiceException expected for no parameters in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}
}
