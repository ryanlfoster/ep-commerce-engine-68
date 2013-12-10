package com.elasticpath.service.search.index;

import java.util.List;

import com.elasticpath.service.EpService;
import com.elasticpath.service.search.SpellSuggestionSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Provides services to search index.
 */
public interface IndexSearchService  extends EpService {
	/**
	 * Searches the index with the given search criteria.
	 * 
	 * @param searchCriteria the search criteria
	 * @return a search result
	 */
	IndexSearchResult search(SearchCriteria searchCriteria);
	
	/**
	 * Suggests new keywords based on the given search criteria.
	 *
	 * @param searchCriteria the keyword search criteria
	 * @return a list of alternate search queries that are similar to the specified criteria
	 */
	List<String> suggest(SpellSuggestionSearchCriteria searchCriteria);

	/**
	 * Searches the index with the given search criteria.
	 * 
	 * @param searchCriteria the search criteria
	 * @param startIndex start index
	 * @param pageSize page size
	 * @return a search result
	 */
	IndexSearchResult search(SearchCriteria searchCriteria, int startIndex, int pageSize);
}
