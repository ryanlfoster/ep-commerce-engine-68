package com.elasticpath.service.search.index;

import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Factory class which distributes {@link QueryComposer}s.
 */
public interface QueryComposerFactory {

	/**
	 * Retrieves the {@link QueryComposer} that should be used for the given
	 * {@link SearchCriteria}. The search criteria is <i>not</i> modified.
	 * 
	 * @param searchCriteria the search criteria
	 * @return a {@link QueryComposer} for the given {@link SearchCriteria}
	 */
	QueryComposer getComposerForCriteria(SearchCriteria searchCriteria);

}
