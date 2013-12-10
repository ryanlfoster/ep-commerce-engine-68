package com.elasticpath.service.search.solr;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.search.SpellIndexSearcher;
import com.elasticpath.service.search.SpellSuggestionSearchCriteria;

/**
 * Default implementation of <code>SpellIndexSearcher</code>.
 */
public class SolrSpellIndexSearcherImpl implements SpellIndexSearcher {
	
	private SolrProvider solrProvider;
	
	private SolrQueryFactory solrQueryFactory;
	
	/**
	 * Generates a map of potential fixes to strings that were misspelled. The key is the string
	 * that could be misspelled where the list is the suggestions for that word only.
	 * 
	 * @param searchCriteria the keyword search criteria
	 * @return map of potential fixes to string that were misspelled
	 * @throws EpPersistenceException in case of any errors
	 */
	@Override
	public Map<String, List<String>> suggest(final SpellSuggestionSearchCriteria searchCriteria) throws EpPersistenceException {
		SolrServer server = solrProvider.getServer(searchCriteria.getIndexType());
		SearchConfig config = solrProvider.getSearchConfig(searchCriteria.getIndexType());
		
		SolrQuery query = solrQueryFactory.composeSpellingQuery(searchCriteria, config);
		QueryResponse queryResponse = null;
		try {
			queryResponse = server.query(query);
		} catch (SolrServerException e) {
			throw new EpPersistenceException("SOLR Error -- spelling suggestion", e);
		}
		
		NamedList<Object> responseDoc = queryResponse.getResponse();
		if (responseDoc == null) {
			return Collections.emptyMap();
		}
		@SuppressWarnings("unchecked")
		final NamedList<List<String>> suggestions = (NamedList<List<String>>) responseDoc.get("suggestions");
		if (suggestions == null) {
			return Collections.emptyMap();
		}
		final Map<String, List<String>> result = new LinkedHashMap<String, List<String>>(suggestions.size());
		for (Entry<String, List<String>> sug : suggestions) {
			result.put(sug.getKey(), sug.getValue());
		}
		return result;
	}
	
	/**
	 * Sets the SOLR provider instance.
	 *
	 * @param solrProvider the SOLR provider instance to use
	 */
	public void setSolrProvider(final SolrProvider solrProvider) {
		this.solrProvider = solrProvider;
	}
	
	/**
	 * Sets the {@link SolrQueryFactory} instance to use.
	 *
	 * @param solrQueryFactory the {@link SolrQueryFactory} instance to use
	 */
	public void setSolrQueryFactory(final SolrQueryFactory solrQueryFactory) {
		this.solrQueryFactory = solrQueryFactory;
	}
}
