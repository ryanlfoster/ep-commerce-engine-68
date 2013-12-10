package com.elasticpath.search.searchengine;

import java.util.Map;

import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.SearchExecutionException;

/**
 * This interface represents methods for searching indexes.
 */
public interface IndexSearcher {

	/**
	 * Searches an index with the given EpQueryLanguage (EPQL) search string starting from 0 index and the number of results is not limited.
	 *
	 * @param <T> the expected type of element returned by the search
	 * @param epQuery the epQuery object
	 * @return a SolrIndexSearchResult object retailing search results
	 * @throws SearchExecutionException if the search string cannot be parsed
	 */
	<T> EpQlSearchResult<T> search(final EpQuery epQuery) throws SearchExecutionException;

	/**
	 * Searches an index (determined by the type of the search criteria) starting from the specified index, the number of results is limited by the
	 * specified <code>maxResults</code>. The results are only those on the first page.
	 *
	 * @param <T> the expected type of element returned by the search
	 * @param epQuery the epQuery object
	 * @param startIndex the initial index to display results for
	 * @param maxResults the maximum results to return from given start index (maximum per page)
	 * @return a SolrIndexSearchResult object retailing search results
	 * @throws SearchExecutionException if the search string cannot be parsed
	 */
	<T> EpQlSearchResult<T> search(final EpQuery epQuery, final int startIndex, final int maxResults) throws SearchExecutionException;
	
	/**
	 * Sets optional conversion map which is a map between Ep query type and <code>SearchResultConverter</code> object. 
	 *
	 * @param converterMap conversion map
	 */
	void setSearchResultConverterMap(final Map<EPQueryType, SearchResultConverter<?, ?>> converterMap);
}
