package com.elasticpath.search.searchengine;

import java.util.List;
import java.util.Map;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Query;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.SearchExecutionException;

/**
 * Represents sql searcher.
 */
public class SQLIndexSearcherImpl implements IndexSearcher {

	private PersistenceEngine persistenceEngine;

	private Map<EPQueryType, SearchResultConverter<?, ?>> converterMap;

	@Override
	public <T> EpQlSearchResult<T> search(final EpQuery epQuery) throws SearchExecutionException {
		return search(epQuery, 0, Integer.MAX_VALUE);
	}

	@Override
	public <T> EpQlSearchResult<T> search(final EpQuery epQuery, final int startIndex, final int maxResults) throws SearchExecutionException {
		return searchInternal(epQuery, startIndex, maxResults);
	}

	/**
	 * Searches the uids or guids based on jpql query.
	 *
	 * @param <T> the expected type of element returned by the search
	 * @param epQuery the query
	 * @param startIndex the start index
	 * @param maxResults the max result
	 * @return the search result
	 */
	<T> EpQlSearchResult<T> searchInternal(final EpQuery epQuery, final int startIndex, final int maxResults) {
		final int startFromIndex = startIndex + epQuery.getStartIndex();
		final PersistenceSession session = persistenceEngine.getPersistenceSession();
		final Query query = session.createSQLQuery(epQuery.toString());
		query.setFirstResult(startFromIndex);
		query.setMaxResults(getMaxReturnNum(epQuery, startIndex, maxResults));
		setQueryParameters(query, epQuery.getParams().toArray());

		final List<T> queryResults = query.list();

		return createBuildResult(queryResults, startFromIndex, epQuery.getQueryType());
	}

	private <T> EpQlSearchResult<T> createBuildResult(final List<T> objects, final int startFromIndex, final EPQueryType queryType) {
		final SolrIndexSearchResult<T> epQlSearchResult = new SolrIndexSearchResult<T>();
		epQlSearchResult.setStartIndex(startFromIndex);

		// FIXME: THIS FIELD SHOULD CONTAIN NUMBER OF ALL OBJECTS WHICH SATISFY QUERY BUT NOW IT IS SET TO NUMBER OF FOUNDED OBJECTS BECAUSE IN JPA
		// THERE IS NO SIMPLE WAY TO RETRIEVE NUMBER OF ALL OBJECTS WHICH SATISFY THE QUERY AS IT CAN BE DONE IN SOLR
		epQlSearchResult.setNumFound(objects.size());

		List<T> conversion = attemptToConvertSearchResults(objects, queryType);
		if (conversion == null) {
			epQlSearchResult.setResultUids(objects);
		} else {
			epQlSearchResult.setResultUids(conversion);
		}
		return epQlSearchResult;
	}

	private <T> List<T> attemptToConvertSearchResults(final List<T> objects, final EPQueryType queryType) {
		if (converterMap == null) {
			return null;
		}

		@SuppressWarnings("unchecked")
		SearchResultConverter<T, T> searchResultConverter = (SearchResultConverter<T, T>) converterMap.get(queryType);

		if (searchResultConverter == null) {
			return null;
		}

		return searchResultConverter.convert(objects);
	}

	private void setQueryParameters(final Query query, final Object[] parameters) {
		for (int i = 0; i < parameters.length; i++) {
			query.setParameter(i + 1, parameters[i]);
		}
	}

	private int getMaxReturnNum(final EpQuery epQuery, final int startIndex, final int maxResults) {
		final int maxReturnNum;
		if (startIndex < epQuery.getLimit()) {
			maxReturnNum = Math.min(epQuery.getLimit() - startIndex, maxResults);
		} else {
			maxReturnNum = 0;
		}
		return maxReturnNum;
	}

	/**
	 * Sets the persistence engine.
	 * 
	 * @param persistenceEngine the persistence engine
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	@Override
	public void setSearchResultConverterMap(final Map<EPQueryType, SearchResultConverter<?, ?>> converterMap) {
		this.converterMap = converterMap;
	}

}