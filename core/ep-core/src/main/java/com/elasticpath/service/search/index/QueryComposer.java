package com.elasticpath.service.search.index;

import java.util.Map;

import org.apache.lucene.search.Query;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SortOrder;

/**
 * Implementations provide search technology specific conversion between
 * <code>SearchCriteria</code> and the query representation required by
 * the specific technology. 
 */
public interface QueryComposer {

	/**
	 * Compose a query based on the given search criteria.
	 * 
	 * @param searchCriteria the given search criteria
	 * @param searchConfig the configuration to use
	 * @return a query
	 */
	Query composeQuery(final SearchCriteria searchCriteria, final SearchConfig searchConfig);

	/**
	 * Compose a fuzzy query based on the given search criteria.
	 * 
	 * @param searchCriteria the given search criteria
	 * @param searchConfig the configuration to use
	 * @return a fuzzy query
	 */
	Query composeFuzzyQuery(final SearchCriteria searchCriteria, final SearchConfig searchConfig);

	/**
	 * Resolves Solr fields which corresponds to sorting values in search criteria. The resolved fields 
	 * will be used to sort out search results.
	 * 
	 * @param searchCriteria the given search criteria
	 * @param searchConfig the configuration to use
	 * @return a sorted map between solr field and sort order or empty map indicating that sorting field resolving failed
	 */
	Map<String, SortOrder> resolveSortField(final SearchCriteria searchCriteria, final SearchConfig searchConfig);
}
