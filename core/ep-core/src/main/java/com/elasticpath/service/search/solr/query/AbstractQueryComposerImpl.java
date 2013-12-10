package com.elasticpath.service.search.solr.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.util.impl.LocaleUtils;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.search.index.Analyzer;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.solr.IndexUtility;

/**
 * This is an abstract implementation of <code>QueryComposer</code>.
 */
public abstract class AbstractQueryComposerImpl implements QueryComposer {

	private static final String S_SYMBOL = "\\s";

	private static final SortOrder DEFAULT_BUSINESSCODE_SORTORDER = SortOrder.ASCENDING;

	private static final int POSSIBLE_LOCALE_FALLBACK = 3;

	private Analyzer analyzer;

	private IndexUtility indexUtility;
	
	/**
	 * The frequently used "*:*" query.
	 * <p>
	 * NOTE: Can't use the org.apache.lucene.search.MatchAllDocsQuery here because its' toString()
	 * method doesn't return something useful. We use the toString() method to build the query.
	 */
	protected static final Query MATCH_ALL_QUERY = new TermQuery(new Term("*", "*"));
	
	/**
	 * Returns the analyzer.
	 * 
	 * @return the analyzer
	 */
	public Analyzer getAnalyzer() {
		return analyzer;
	}

	/**
	 * Sets the analyzer.
	 * 
	 * @param analyzer the analyzer
	 */
	public void setAnalyzer(final Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	/**
	 * Sets the index utility instance.
	 * 
	 * @param indexUtility the index utility instance
	 */
	public void setIndexUtility(final IndexUtility indexUtility) {
		this.indexUtility = indexUtility;
	}
	
	/**
	 * Gets the {@link IndexUtility} instance.
	 * 
	 * @return the {@link IndexUtility} instance
	 */
	protected IndexUtility getIndexUtility() {
		return indexUtility;
	}

	/**
	 * Gets the query to match all results.
	 * 
	 * @return the query to match all results
	 */
	protected Query getMatchAllQuery() {
		return MATCH_ALL_QUERY;
	}
	
	/**
	 * Attempts to add a term query to the given boolean query with the given field and value. The
	 * value is split on whitespace so that words can be searched/added separately. Returns a
	 * value to indicate whether the addition was successful or a non-fatal error occurred. If
	 * given a locale, the field is assumed to be a locale field. If no locale is given, it's
	 * assumed not to be a locale field.
	 * 
	 * @param field the field to add a term for
	 * @param text the actual text to search for
	 * @param locale the locale to add the fuzzy query for
	 * @param searchConfig the search configuration to use
	 * @param booleanQuery the query to add a term query to
	 * @param occur the occurrence value of the added query
	 * @param applyBoost whether to apply the boost to the query
	 * @return whether the addition was successful or a non-fatal error occurred
	 */
	protected boolean addSplitFieldToQuery(final String field, final String text, final Locale locale,
			final SearchConfig searchConfig, final BooleanQuery booleanQuery, final Occur occur, final boolean applyBoost) {
		if (!isValidText(text)) {
			return false;
		}
		
		// split should always produce valid text
		for (String str : text.split(S_SYMBOL)) {
			addWholeFieldToQuery(field, str, locale, searchConfig, booleanQuery, occur, applyBoost);
		}
		return true;
	}

	/**
	 * Attempts to add a term query to the given boolean query with the given field and value. The
	 * value is split on whitespace so that words can be searched/added separately. Returns a
	 * value to indicate whether the addition was successful or a non-fatal error occurred. It handles 
	 * the case, when multiple locales should be used to search the given field. Example: you perform 
	 * search across multiple catalogs that have en_GB, en, fr_CA locales set up by default. So you need
	 * to add all of them into the query in order to get relevent search results.
	 * 
	 * @param field the field to add a term for
	 * @param text the actual text to search for
	 * @param locales the locales to add to the fuzzy query
	 * @param searchConfig the search configuration to use
	 * @param booleanQuery the query to add a term query to
	 * @param occur the occurrence value of the added query
	 * @param applyBoost whether to apply the boost to the query
	 * @return whether the addition was successful or a non-fatal error occurred
	 */
	protected boolean addSplitFieldToQueryWithMultipleLocales(final String field, final String text, final Set<Locale> locales,
			final SearchConfig searchConfig, final BooleanQuery booleanQuery, final Occur occur, final boolean applyBoost) {
		if (!isValidText(text)) {
			return false;
		}
		
		// split should always produce valid text
		for (String str : text.split(S_SYMBOL)) {
			addWholeFieldToQueryWithMultipleLocales(field, str, locales, searchConfig, booleanQuery, occur, applyBoost);
		}
		return true;
	}

	
	/**
	 * Attempts to add a fuzzy query to the given boolean query with the given field and value.
	 * The value is split on whitespace so that words can be searched/added separately. Returns a
	 * value to indicate whether the addition was successful or a non-fatal error occurred. If
	 * given a locale, the field is assumed to be a locale field. If no locale is given, it's
	 * assumed not to be a locale field.
	 * 
	 * @param field the field to add a term for
	 * @param text the actual text to search for
	 * @param locale the locale to add the fuzzy query for
	 * @param searchConfig the search configuration to use
	 * @param booleanQuery the query to add the constructed query to
	 * @param occur the occurrence value of the added query
	 * @param applyBoost whether to apply the boost to the query
	 * @return whether the addition was successful or a non-fatal error occurred
	 */
	protected boolean addSplitFuzzyFieldToQuery(final String field, final String text, final Locale locale,
			final SearchConfig searchConfig, final BooleanQuery booleanQuery, final Occur occur, final boolean applyBoost) {
		if (!isValidText(text)) {
			return false;
		}
		
		// split should always produce valid text
		for (String str : text.split(S_SYMBOL)) {
			addWholeFuzzyFieldToQuery(field, str, locale, searchConfig, booleanQuery, occur, applyBoost);
		}
		return true;
	}

	/**
	 * Attempts to add a fuzzy query to the given boolean query with the given field and value.
	 * The value is split on whitespace so that words can be searched/added separately. Returns a
	 * value to indicate whether the addition was successful or a non-fatal error occurred. It handles 
	 * the case, when multiple locales should be used to search the given field. Example: you perform 
	 * search across multiple catalogs that have en_GB, en, fr_CA locales set up by default. So you need
	 * to add all of them into the query in order to get relevent search results.
	 * 
	 * @param field the field to add a term for
	 * @param text the actual text to search for
	 * @param locales the locales to add to the fuzzy query
	 * @param searchConfig the search configuration to use
	 * @param booleanQuery the query to add the constructed query to
	 * @param occur the occurrence value of the added query
	 * @param applyBoost whether to apply the boost to the query
	 * @return whether the addition was successful or a non-fatal error occurred
	 */
	protected boolean addSplitFuzzyFieldToQueryWithMultipleLocales(final String field, final String text, final Set<Locale> locales,
			final SearchConfig searchConfig, final BooleanQuery booleanQuery, final Occur occur, final boolean applyBoost) {
		if (!isValidText(text)) {
			return false;
		}
		
		// split should always produce valid text
		for (String str : text.split(S_SYMBOL)) {
			addWholeFuzzyFieldToQueryWithMultipleLocales(field, str, locales, searchConfig, booleanQuery, occur, applyBoost);
		}
		return true;
	}
	
	/**
	 * Attempts to add a term query to the given boolean query with the given field and value. The
	 * whole field is added to the query. Returns a value to indicate whether the addition was
	 * successful or a non-fatal error occurred. If given a locale, the field is assumed to be a
	 * locale field. If no locale is given, it's assumed not to be a locale field.
	 * 
	 * @param field the field to add a term for
	 * @param text the actual text to search for
	 * @param locale the locale to add the fuzzy query for
	 * @param searchConfig the search configuration to use
	 * @param booleanQuery the query to add a term query to
	 * @param occur the occurrence value of the added query
	 * @param applyBoost whether to apply the boost to the query
	 * @return whether the addition was successful or a non-fatal error occurred
	 */
	protected boolean addWholeFieldToQuery(final String field, final String text, final Locale locale,
			final SearchConfig searchConfig, final BooleanQuery booleanQuery, final Occur occur, final boolean applyBoost) {
		if (!isValidText(text)) {
			return false;
		}

		final BooleanQuery innerQuery = new BooleanQuery();
		Locale fieldLocale = locale;
		
		// we could potentially have a locale fall back 3 times
		for (int i = 0; i < POSSIBLE_LOCALE_FALLBACK; ++i) {
			addFieldLocalePart(field, text, searchConfig, applyBoost,
					innerQuery, fieldLocale);

			// it's possible that we didn't pass in a locale
			if (fieldLocale == null) {
				break;
			}
			// otherwise, let's try and broaden the locale for the search
			final Locale newLocale = LocaleUtils.broadenLocale(fieldLocale);
			if (newLocale.equals(fieldLocale)) {
				// we weren't able to broaden the locale any further
				break;
			}
			fieldLocale = newLocale;
		}
		booleanQuery.add(innerQuery, occur);
		return true;
	}

	/**
	 * Attempts to add a term query to the given boolean query with the given field and value. The
	 * whole field is added to the query. Returns a value to indicate whether the addition was
	 * successful or a non-fatal error occurred. Handles the case, when multiple locales should be used 
	 * to search the given field. Example: you perform search across multiple catalogs that have
	 * en_GB, en, fr_CA locales set up by default. So you need to add all of them into the query in 
	 * order to get relevent search results.
	 * 
	 * @param field the field to add a term for
	 * @param text the actual text to search for
	 * @param locales the locales to add into the query
	 * @param searchConfig the search configuration to use
	 * @param booleanQuery the query to add a term query to
	 * @param occur the occurrence value of the added query
	 * @param applyBoost whether to apply the boost to the query
	 * @return whether the addition was successful or a non-fatal error occurred
	 */
	protected boolean addWholeFieldToQueryWithMultipleLocales(final String field, final String text, final Set<Locale> locales,
			final SearchConfig searchConfig, final BooleanQuery booleanQuery, final Occur occur, final boolean applyBoost) {
		if (!isValidText(text)) {
			return false;
		}
		final BooleanQuery innerQuery = new BooleanQuery();
		
		final Set<Locale> broadenedLocales = new HashSet<Locale>();
		
		for (Locale locale : locales) {
			addFieldLocalePart(field, text, searchConfig, applyBoost,
					innerQuery, locale);
			Locale broadenedLocale = LocaleUtils.broadenLocale(locale);
			if (broadenedLocale != null && !broadenedLocales.contains(broadenedLocale) && !locales.contains(broadenedLocale)) {
				broadenedLocales.add(broadenedLocale);
				addFieldLocalePart(field, text, searchConfig, applyBoost, innerQuery, broadenedLocale);
			}
		}

		booleanQuery.add(innerQuery, occur);
		return true;
	}

	private void addFieldLocalePart(final String field, final String text,
			final SearchConfig searchConfig, final boolean applyBoost,
			final BooleanQuery innerQuery, final Locale locale) {
		final String fieldName = constructFieldName(field, locale);
		final Query query = createQuery(text, fieldName);
		if (applyBoost) {
			query.setBoost(getBoostValue(fieldName, locale, searchConfig));
		}
		innerQuery.add(query, Occur.SHOULD);
	}

	private void addFuzzyFieldLocalePart(final String field, final String text,
			final SearchConfig searchConfig, final boolean applyBoost,
			final BooleanQuery innerQuery, final Locale locale) {
		final String fieldName = constructFieldName(field, locale);
		final Query query = new FuzzyQuery(new Term(fieldName, getAnalyzer().analyze(text)), searchConfig
				.getMinimumSimilarity(), searchConfig.getPrefixLength());
		if (applyBoost) {
			if (locale == null) {
				query.setBoost(searchConfig.getBoostValue(fieldName));
			} else {
				query.setBoost(getIndexUtility().getLocaleBoostWithFallback(searchConfig, fieldName, locale));
			}
		}
		innerQuery.add(query, Occur.SHOULD);
	}

	
	/**
	 * Create Query object. By default it's a TermQuery.
	 * @param text search text
	 * @param fieldName field name
	 * @return Query object
	 */
	protected Query createQuery(final String text, final String fieldName) {
		return new TermQuery(new Term(fieldName, getAnalyzer().analyze(text)));
	}
	
	/**
	 * Attempts to add a fuzzy query to the given boolean query with the given field and value.
	 * The whole value is added to the query. Returns a value to indicate whether the addition was
	 * successful or a non-fatal error occurred. If given a locale, the field is assumed to be a
	 * locale field. If no locale is given, it's assumed not to be a locale field.
	 * 
	 * @param field the field to add a term for
	 * @param text the actual text to search for
	 * @param locale the locale to add the fuzzy query for
	 * @param searchConfig the search configuration to use
	 * @param booleanQuery the query to add the constructed query to
	 * @param occur the occurrence value of the added query
	 * @param applyBoost whether to apply the boost to the query
	 * @return whether the addition was successful or a non-fatal error occurred
	 */
	protected boolean addWholeFuzzyFieldToQuery(final String field, final String text, final Locale locale,
			final SearchConfig searchConfig, final BooleanQuery booleanQuery, final Occur occur, final boolean applyBoost) {
		if (!isValidText(text)) {
			return false;
		}
		
		final BooleanQuery innerQuery = new BooleanQuery();
		Locale fieldLocale = locale;
		
		// we could potentially have a locale fall back 3 times
		for (int i = 0; i < POSSIBLE_LOCALE_FALLBACK; ++i) {
			addFuzzyFieldLocalePart(field, text, searchConfig, applyBoost, innerQuery, fieldLocale);

			// it's possible that we didn't pass in a locale
			if (fieldLocale == null) {
				break;
			}
			// otherwise, let's try and broaden the locale for the search
			final Locale newLocale = LocaleUtils.broadenLocale(fieldLocale);
			if (newLocale.equals(fieldLocale)) {
				// we weren't able to broaden the locale any further
				break;
			}
			fieldLocale = newLocale;
		}
		booleanQuery.add(innerQuery, occur);
		return true;
	}
	
	/**
	 * Attempts to add a fuzzy query to the given boolean query with the given field and value.
	 * The whole value is added to the query. Returns a value to indicate whether the addition was
	 * successful or a non-fatal error occurred. IHandles the case, when multiple locales should be used 
	 * to search the given field. Example: you perform search across multiple catalogs that have
	 * en_GB, en, fr_CA locales set up by default. So you need to add all of them into the query in 
	 * order to get relevent search results.
	 * 
	 * @param field the field to add a term for
	 * @param text the actual text to search for
	 * @param locales the locales to add to the fuzzy query
	 * @param searchConfig the search configuration to use
	 * @param booleanQuery the query to add the constructed query to
	 * @param occur the occurrence value of the added query
	 * @param applyBoost whether to apply the boost to the query
	 * @return whether the addition was successful or a non-fatal error occurred
	 */
	protected boolean addWholeFuzzyFieldToQueryWithMultipleLocales(final String field, final String text, final Set<Locale> locales,
			final SearchConfig searchConfig, final BooleanQuery booleanQuery, final Occur occur, final boolean applyBoost) {
		if (!isValidText(text)) {
			return false;
		}
		final BooleanQuery innerQuery = new BooleanQuery();		
		final Set<Locale> broadenedLocales = new HashSet<Locale>();
		
		for (Locale locale : locales) {
			
			addFuzzyFieldLocalePart(field, text, searchConfig, applyBoost,
					innerQuery, locale);
			Locale broadenedLocale = LocaleUtils.broadenLocale(locale);
			if (broadenedLocale != null && !broadenedLocales.contains(broadenedLocale) && !locales.contains(broadenedLocale)) {
				broadenedLocales.add(broadenedLocale);
				addFuzzyFieldLocalePart(field, text, searchConfig, applyBoost, innerQuery, broadenedLocale);
			}
		}

		booleanQuery.add(innerQuery, occur);
		return true;
	}
	
	/**
	 * Attempts to add a collection of term queries to the given boolean query with the given
	 * field and collection of values. Returns a value to indicate whether the addition was
	 * successful or a non-fatal error occurred. If given a locale, the field is assumed to be a
	 * locale field. If no locale is given, it's assumed not to be a locale field. Each value in
	 * the collection is parse to a string via their {@link #toString()} method and ORed together.
	 * If any of the items in the collection fails, the constructed query will <i>not</i> be
	 * added.
	 * 
	 * @param field the field to add a term for
	 * @param collection the collection of text to search for
	 * @param locale the locale to add the fuzzy query for
	 * @param searchConfig the search configuration to use
	 * @param booleanQuery the query to add a term query to
	 * @param occur the occurrence value of the added query
	 * @param applyBoost whether to apply the boost to the query
	 * @return whether the addition was successful or a non-fatal error occurred
	 */
	protected boolean addWholeFieldToQuery(final String field, final Collection< ? > collection, final Locale locale,
			final SearchConfig searchConfig, final BooleanQuery booleanQuery, final Occur occur, final boolean applyBoost) {
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		final BooleanQuery innerQuery = new BooleanQuery();
		for (Object obj : collection) {
			if (!addWholeFieldToQuery(field, obj.toString(), locale, searchConfig, innerQuery, Occur.SHOULD, false)) {
				return false;
			}
		}
		if (applyBoost) {
			innerQuery.setBoost(getBoostValue(constructFieldName(field, locale), locale, searchConfig));
		}
		booleanQuery.add(innerQuery, occur);
		return true;
	}

	/**
	 * Check if text is valid.
	 * @param text test text string
	 * @return true if OK
	 */
	protected boolean isValidText(final String text) {
		return text != null && text.trim().length() > 0;
	}
	
	private String constructFieldName(final String field, final Locale locale) {
		if (locale == null) {
			return field;
		}
		return getIndexUtility().createLocaleFieldName(field, locale);
	}
	
	private float getBoostValue(final String fieldName, final Locale locale, final SearchConfig searchConfig) {
		if (locale == null) {
			return searchConfig.getBoostValue(fieldName);
		}
		return getIndexUtility().getLocaleBoostWithFallback(searchConfig, fieldName, locale);
	}
	
	/**
	 * Returns whether the given search criteria is valid or not.
	 *
	 * @param searchCriteria the search criteria
	 * @return whether the given search criteria is valid or not
	 */
	protected abstract boolean isValidSearchCriteria(final SearchCriteria searchCriteria);
	
	/**
	 * Compose a query based on the given search criteria. Criteria's given are guaranteed to be
	 * validated by {@link #isValidSearchCriteria(SearchCriteria)}.
	 * 
	 * @param searchCriteria the given search criteria
	 * @param searchConfig the configuration to use
	 * @return a query
	 */
	protected abstract Query composeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig);
	
	/**
	 * Compose a fuzzy query based on the given search criteria. Criteria's given are guaranteed
	 * to be validated by {@link #isValidSearchCriteria(SearchCriteria)}.
	 * 
	 * @param searchCriteria the given search criteria
	 * @param searchConfig the configuration to use
	 * @return a query
	 */
	protected abstract Query composeFuzzyQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig);
	
	/**
	 * Compose a query based on the given search criteria.
	 * 
	 * @param searchCriteria the given search criteria
	 * @param searchConfig the configuration to use
	 * @return a query
	 */
	@Override
	public Query composeQuery(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		if (!isValidSearchCriteria(searchCriteria)) {
			throw new EpServiceException("Unknown search criteria : " + searchCriteria.getClass().getName());
		}
		if (searchCriteria.isMatchAll()) {
			return MATCH_ALL_QUERY;
		}
		return composeQueryInternal(searchCriteria, searchConfig);
	}
	
	/**
	 * Compose a fuzzy query based on the given search criteria.
	 * 
	 * @param searchCriteria the given search criteria
	 * @param searchConfig the configuration to use
	 * @return a fuzzy query
	 */
	@Override
	public Query composeFuzzyQuery(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		if (!isValidSearchCriteria(searchCriteria)) {
			throw new EpServiceException("Unknown search criteria : " + searchCriteria.getClass().getName());
		}
		if (searchCriteria.isMatchAll()) {
			return MATCH_ALL_QUERY;
		}
		return composeFuzzyQueryInternal(searchCriteria, searchConfig);
	}
	
	/**
	 * Abstract query composer returns <code>Collections.EMPTY_MAP</code> as sorting fields map.
	 * 
	 * @param searchCriteria the given search criteria
	 * @param searchConfig the configuration to use
	 * @return <code>Collections.EMPTY_MAP</code>
	 */
	@Override
	public Map<String, SortOrder> resolveSortField(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final Map<String, SortOrder> resultMap = new LinkedHashMap<String, SortOrder>();
		fillSortFieldMap(resultMap, searchCriteria, searchConfig);
		
		checkResultMap(searchCriteria, resultMap);		
		
		if (isBusinessCodeFieldAcceptable(getBusinessCodeField(), resultMap)) {
			resultMap.put(getBusinessCodeField(), DEFAULT_BUSINESSCODE_SORTORDER);
		}
		return resultMap;
	}

	private void checkResultMap(final SearchCriteria searchCriteria, final Map<String, SortOrder> resultMap) {
		if (MapUtils.isEmpty(resultMap)) {
			throw new EpSystemException(String.format("Sort field %1$S unimplemented.", searchCriteria.getSortingType()));
		}
	}

	private boolean isBusinessCodeFieldAcceptable(final String businessCode, final Map<String, SortOrder> resultMap) {
		return StringUtils.isNotEmpty(businessCode) && !resultMap.containsKey(businessCode);
	}

	/**
	 * Fills the sort field map with appropriate fields for sorting, default implementation does nothing.
	 * Note: <code>sortFieldMap</code> keeps the order in which keys were inserted into the map
	 * 
	 * @param sortFieldMap the sort field map to fill
	 * @param searchCriteria the given search criteria
	 * @param searchConfig the configuration to use
	 */
	protected void fillSortFieldMap(final Map<String, SortOrder> sortFieldMap, final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		//do nothing
	}

	/**
	 * Returns business code field to be used by <code>sortingFieldAndCode()</code> as second sorting field.
	 * Must be overridden by a subclass if <code>sortingFieldAndCode()</code> is in service of that subclass.
	 * 
	 * @return business code Solr field. Must be unique across all documents.
	 */
	protected String getBusinessCodeField() {		
		return "";
	}
}
