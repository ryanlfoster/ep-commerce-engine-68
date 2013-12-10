/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.search.searchengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.SolrParams;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrIndexConstants;
import com.elasticpath.service.search.solr.SolrProvider;

/**
 * Data access for the SOLR index.
 */
public class SolrIndexSearcherImpl implements IndexSearcher {

	private static final Logger LOG = Logger.getLogger(SolrIndexSearcherImpl.class);

	private static final String STANDARD_REQUEST_HANDLER = "standard";

	private SolrProvider solrProvider;

	private Map<EPQueryType, IndexType> typeMap;

	@Override
	public <T> EpQlSearchResult<T> search(final EpQuery epQuery) {
		final EpQlSearchResult<T> searchResult = search(epQuery, 0, 0);
		return search(epQuery, 0, searchResult.getNumFound());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> EpQlSearchResult<T> search(final EpQuery epQuery, final int startIndex, final int maxResults) {
		final IndexType indexType = typeMap.get(epQuery.getQueryType());
		final SearchConfig searchConfig = solrProvider.getSearchConfig(indexType);
		final SolrIndexSearchResult<Long> searchResult = new SolrIndexSearchResult<Long>();

		SolrQuery solrQuery = createSolrQuery(epQuery, startIndex, maxResults, searchConfig, searchResult);
		// get the SOLR server for the appropriate index and do the search
		QueryResponse response = performSearch(solrProvider.getServer(indexType), solrQuery);

		parseResponseDocument(response, searchResult, epQuery);
		return (EpQlSearchResult<T>) searchResult;
	}

	/**
	 * Constructs a SolrQuery from an EpQuery.
	 *
	 * @param epQuery the EP query from which to construct a Solr query.
	 * @param startIndex the index from which the result set should begin
	 * @param maxResults the maximum results to return from given start index (maximum per page).
	 * @param searchConfig the SearchConfig
	 * @param searchResult SolrIndexSearchResult object to collect actual search parameters
	 * @return the Solr query that is equivalent to the Ep query
	 */
	SolrQuery createSolrQuery(final EpQuery epQuery, final int startIndex, final int maxResults, final SearchConfig searchConfig,
			final SolrIndexSearchResult<Long> searchResult) {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQueryType(STANDARD_REQUEST_HANDLER);

		solrQuery.setQuery(epQuery.getNativeQuery().getNativeQuery());

		if (LOG.isDebugEnabled()) {
			LOG.debug("Generated query: " + solrQuery.toString());
		}

		final int startFromIndex = startIndex + epQuery.getStartIndex();
		final int maxReturnNum = getMaxReturnNum(epQuery, startIndex, maxResults);

		solrQuery.setStart(startFromIndex);
		solrQuery.setRows(maxReturnNum);
		solrQuery.setFields(SolrIndexConstants.OBJECT_UID);

		searchResult.setStartIndex(startFromIndex);
		return solrQuery;
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
	 * Searches the given SOLR server with the given query.
	 *
	 * @param server the SOLR server to search
	 * @param query the query to search with
	 * @return list of UIDs representing matches of the given query
	 */
	QueryResponse performSearch(final SolrServer server, final SolrParams query) {
		try {
			QueryRequest queryRequest = new QueryRequest(query);
			queryRequest.setMethod(METHOD.POST);
			return queryRequest.process(server);
		} catch (SolrServerException e) {
			throw new EpPersistenceException("Solr exception executing search", e);
		}
	}

	/**
	 * Parses a SOLR query response document and returns a list of all the object UIDs contained in the response.
	 *
	 * @param response the response document
	 * @param searchResult SolrIndexSearchResult object to be populated with result information
	 * @param epQuery epQuery
	 */
	void parseResponseDocument(final QueryResponse response, final SolrIndexSearchResult<Long> searchResult, final EpQuery epQuery) {
		List<Long> objectUidList = Collections.emptyList();

		searchResult.setNumFound(Math.min(epQuery.getLimit(), (int) response.getResults().getNumFound() - epQuery.getStartIndex()));

		objectUidList = new ArrayList<Long>(response.getResults().size());
		for (SolrDocument document : response.getResults()) {
			objectUidList.add((Long) document.getFieldValue(SolrIndexConstants.OBJECT_UID));
		}
		searchResult.setResultUids(objectUidList);
	}

	/**
	 * Gets the {@link SolrProvider} instance.
	 *
	 * @return the {@link SolrProvider} instance
	 */
	public SolrProvider getSolrProvider() {
		return solrProvider;
	}

	/**
	 * Sets the {@link SolrProvider} instance.
	 *
	 * @param solrProvider the {@link SolrProvider} instance
	 */
	public void setSolrProvider(final SolrProvider solrProvider) {
		this.solrProvider = solrProvider;
	}

	/**
	 * Sets the map between <code>EpQueryType</code> and <code>IndexType</code>.
	 *
	 * @param typeMap the typeMap to set
	 */
	public void setTypeMap(final Map<EPQueryType, String> typeMap) {
		this.typeMap = new HashMap<EPQueryType, IndexType>();
		for (Entry<EPQueryType, String> entry : typeMap.entrySet()) {
			this.typeMap.put(entry.getKey(), IndexType.findFromName(entry.getValue()));
		}
	}

	@Override
	public void setSearchResultConverterMap(final Map<EPQueryType, SearchResultConverter<?, ?>> converterMap) {
		//isn't used by Solr as results returned are always SolrIndexConstants.OBJECT_UID
	}

}
