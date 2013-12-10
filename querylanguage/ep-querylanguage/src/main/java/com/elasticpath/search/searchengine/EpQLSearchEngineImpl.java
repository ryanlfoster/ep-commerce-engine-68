package com.elasticpath.search.searchengine;

import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.ql.parser.EpQLParseException;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.EpQueryParser;
import com.elasticpath.ql.parser.SearchExecutionException;
import com.elasticpath.ql.parser.TargetLanguage;

/**
 * This class represents methods for searching object uids by given epql query.
 */
public class EpQLSearchEngineImpl implements EpQLSearchEngine {

	private static final Logger LOG = Logger.getLogger(EpQLSearchEngineImpl.class);

	private EpQueryParser epQueryParser;

	private Map<TargetLanguage, IndexSearcher> indexSearcherMap;

	@Override
	public <T> EpQlSearchResult<T> search(final String searchString) {
		return search(new QueryLauncher() {
			@Override
			public <R> EpQlSearchResult<R> executeQuery(final EpQuery epQuery) {
				return getIndexSearcher(epQuery).search(epQuery);
			}
		}, searchString);
	}

	@Override
	public <T> EpQlSearchResult<T> search(final String searchString, final int startIndex, final int maxResults) {
		return search(new QueryLauncher() {
			@Override
			public <R> EpQlSearchResult<R> executeQuery(final EpQuery epQuery) {
				return getIndexSearcher(epQuery).search(epQuery, startIndex, maxResults);
			}
		}, searchString);
	}

	private <T> EpQlSearchResult<T> search(final QueryLauncher provider, final String searchString) {
		final EpQuery epQuery = getEpQueryFromParseString(searchString);

		logQuery(epQuery);

		final EpQlSearchResult<T> result = provider.executeQuery(epQuery);

		if (result != null) {
			result.setEpQueryType(epQuery.getQueryType());
		}
		return result;
	}

	private void logQuery(final EpQuery epQuery) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("generated query : " + epQuery.toString());
		}
	}

	/**
	 * Gets an EPQuery object given a search string.
	 * 
	 * @param searchString the EPQL search string
	 * @return an EpQuery
	 * @throws SearchExecutionException if the search string cannot be parsed
	 */
	EpQuery getEpQueryFromParseString(final String searchString) {
		EpQuery epQuery = null;
		try {
			epQuery = getEpQueryParser().parse(searchString);
		} catch (EpQLParseException exception) {
			throw new SearchExecutionException("Unable to parse query string", exception);
		}
		return epQuery;
	}

	private IndexSearcher getIndexSearcher(final EpQuery epQuery) {
		final TargetLanguage language = epQuery.getQueryType().getTargetLanguage();
		final IndexSearcher indexSearcher = indexSearcherMap.get(language);
		if (indexSearcher == null) {
			throw new EpPersistenceException("Can not find index searcher for given target language : " + language);
		}
		return indexSearcher;
	}

	/**
	 * Gets EpQueryParser object.
	 * 
	 * @return the EpQueryParser object
	 */
	protected EpQueryParser getEpQueryParser() {
		return epQueryParser;
	}

	/**
	 * Sets the EpQueryParser object.
	 * 
	 * @param epQueryParser EpQueryParser
	 */
	public void setEpQueryParser(final EpQueryParser epQueryParser) {
		this.epQueryParser = epQueryParser;
	}

	/**
	 * Sets index searcher map.
	 * 
	 * @param indexSearcherMap the indexSearcherMap
	 */
	public void setIndexSearcherMap(final Map<TargetLanguage, IndexSearcher> indexSearcherMap) {
		this.indexSearcherMap = indexSearcherMap;
	}

	/**
	 * Sets the specific index searcher for given language.
	 * 
	 * @param language the target language
	 * @param indexSearcher the index searcher
	 */
	public void putIndexSearcher(final TargetLanguage language, final IndexSearcher indexSearcher) {
		indexSearcherMap.put(language, indexSearcher);
	}

	/**
	 * Represents method for execute the search query.
	 */
	private interface QueryLauncher {

		/**
		 * Executes the search query.
		 * 
		 * @param epQuery the query to execute
		 * @return the search result
		 */
		<T> EpQlSearchResult<T> executeQuery(final EpQuery epQuery);
	}

	@Override
	public String verify(final String query) throws EpQLParseException {
		return getEpQueryParser().verify(query);
	}
}
