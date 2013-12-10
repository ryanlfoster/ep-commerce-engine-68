package com.elasticpath.search.searchengine;

import com.elasticpath.ql.parser.EpQLParseException;
import com.elasticpath.ql.parser.SearchExecutionException;

/**
 * This interface represents methods for searching object uids/guids by given epql query.
 */
public interface EpQLSearchEngine {

	/**
	 * Searches an index with the given EpQueryLanguage (EPQL) search string starting from 0 index and the number of results is not limited.
	 *
	 * @param <T> the expected type of element returned by the search
	 * @param searchString the search query string
	 * @return a SolrIndexSearchResult object retailing search results
	 * @throws SearchExecutionException if the search string cannot be parsed
	 */
	<T> EpQlSearchResult<T> search(final String searchString) throws SearchExecutionException;

	/**
	 * Searches an index (determined by the type of the search criteria) starting from the specified index, the number of results is limited by the
	 * specified <code>maxResults</code>. The results are only those on the first page.
	 *
	 * @param <T> the expected type of element returned by the search
	 * @param searchString the search query string
	 * @param startIndex the initial index to display results for
	 * @param maxResults the maximum results to return from given start index (maximum per page)
	 * @return a SolrIndexSearchResult object retailing search results
	 * @throws SearchExecutionException if the search string cannot be parsed
	 */
	<T> EpQlSearchResult<T> search(final String searchString, final int startIndex, final int maxResults) throws SearchExecutionException;

    /**
	 * Verify a query string, returning the string representation of the lucene query.
	 * 
	 * @param query the query string to be parsed.
	 * @return string representation of lucene query
	 * @throws EpQLParseException if the parsing fails
	 */
	String verify(String query) throws EpQLParseException;

}