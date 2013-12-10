package com.elasticpath.importexport.exporter.search.impl;

import java.util.Collections;
import java.util.List;

import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.search.searchengine.EpQLSearchEngine;
import com.elasticpath.search.searchengine.EpQlSearchResult;

/**
 * Default implementation of <code>ImportExportSearcher</code>.
 */
public class ImportExportSearcherImpl implements ImportExportSearcher {

	private EpQLSearchEngine epQLSearchEngine;

	@Override
	public List<Long> searchUids(final SearchConfiguration config, final EPQueryType epQueryType) {
		return search(config, epQueryType);
	}

	@Override
	public List<String> searchGuids(final SearchConfiguration config, final EPQueryType epQueryType) {
		return search(config, epQueryType);
	}

	@Override
	public List<Object> searchCompoundGuids(final SearchConfiguration config, final EPQueryType epQueryType) {
		return search(config, epQueryType);
	}

	private <T> List<T> search(final SearchConfiguration config, final EPQueryType epQueryType) {
		final String epQlQuery = config.getQuery(epQueryType);
		if (epQlQuery == null || epQlQuery.length() == 0) {
			return Collections.emptyList();
		}

		final EpQlSearchResult<T> searchResult = epQLSearchEngine.search(epQlQuery);
		if (!searchResult.getEpQueryType().equals(epQueryType)) {
			return Collections.emptyList();
		}
		return searchResult.getSearchResults();
	}

	/**
	 * Gets the epQLSearchEngine.
	 * 
	 * @return the epQLSearchEngine
	 */
	public EpQLSearchEngine getEpQLSearchEngine() {
		return epQLSearchEngine;
	}

	/**
	 * Sets the epQLSearchEngine.
	 * 
	 * @param epQLSearchEngine the epQLSearchEngine to set
	 */
	public void setEpQLSearchEngine(final EpQLSearchEngine epQLSearchEngine) {
		this.epQLSearchEngine = epQLSearchEngine;
	}

}
