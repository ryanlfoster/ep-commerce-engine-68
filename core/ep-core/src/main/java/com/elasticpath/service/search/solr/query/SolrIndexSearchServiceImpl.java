package com.elasticpath.service.search.solr.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.search.SpellIndexSearcher;
import com.elasticpath.service.search.SpellSuggestionSearchCriteria;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.index.IndexSearchService;
import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Provides an implentation of the IndexSearchService that searches Solr
 * indexes.
 */
public class SolrIndexSearchServiceImpl extends AbstractEpServiceImpl implements IndexSearchService {
	
	private static final Logger LOG = Logger.getLogger(SolrIndexSearchServiceImpl.class);

	private SpellIndexSearcher spellIndexSearcher;

	/**
	 * Returns an immutable search result object that is pageable.
	 * 
	 * @param searchCriteria the search criteria
	 * @return search result
	 */
	public IndexSearchResult search(final SearchCriteria searchCriteria) {

		// Create and populate a Solr search result object that will
		// actually search when the user request result pages.

		SolrIndexSearchResult result = getBean(ContextIdNames.SOLR_SEARCH_RESULT);
		
		// Copy the search criteria so they cannot be modified externally.
		SearchCriteria clonedSearchCriteria = searchCriteria;
		try {
			clonedSearchCriteria = searchCriteria.clone();
		} catch (CloneNotSupportedException e) {
			LOG.warn("Unable to clone search criteria, using given instance, continuing with original object", e);
		}
		result.setSearchCriteria(clonedSearchCriteria);
		
		return result;
	}

	/**
	 * Suggests alternate query strings based on the given search criteria.
	 * 
	 * @param searchCriteria the search criteria to base the suggestions on
	 * @return alternate query strings
	 */
	public List<String> suggest(final SpellSuggestionSearchCriteria searchCriteria) {
		final Map<String, List<String>> suggestionMap = spellIndexSearcher.suggest(searchCriteria);
		final List<String> suggestionsList = new ArrayList<String>();
		for (List<String> sug : suggestionMap.values()) {
			suggestionsList.addAll(sug);
		}
		return suggestionsList;
	}

	
	/**
	 * Sets the spell index searcher.
	 * 
	 * @param spellIndexSearcher the spell index searcher
	 */
	public void setSpellIndexSearcher(final SpellIndexSearcher spellIndexSearcher) {
		this.spellIndexSearcher = spellIndexSearcher;
	}
	
	/**
	 * Searches the index with the given search criteria.
	 * 
	 * @param searchCriteria the search criteria
	 * @param startIndex start index
	 * @param pageSize page size
	 * @return a search result
	 */
	public IndexSearchResult search(final SearchCriteria searchCriteria, final int startIndex, final int pageSize) {
		// Create and populate a Solr search result object that will
		// actually search when the user request result pages.
		SolrIndexSearchResult result = getBean(ContextIdNames.SOLR_SEARCH_RESULT);

		// Copy the search criteria so they cannot be modified externally.
		SearchCriteria clonedSearchCriteria = searchCriteria;
		try {
			clonedSearchCriteria = searchCriteria.clone();
		} catch (CloneNotSupportedException e) {
			LOG.warn("Unable to clone search criteria, using given instance, continuing with original object", e);
		}
		result.setSearchCriteria(clonedSearchCriteria);
		result.filterUids(startIndex, pageSize);
		return result;
	}

}
