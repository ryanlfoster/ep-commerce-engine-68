package com.elasticpath.service.search.query;

import com.elasticpath.service.EpService;
import com.elasticpath.domain.search.query.SearchTerms;
import com.elasticpath.domain.search.query.SearchTermsMemento;

/**
 * Provides operations on {@link SearchTerms}s.
 */
public interface SearchTermsService extends EpService {

	/**
	 * Save a SearchTerms if it is not already in the database.
	 * 
	 * @param searchTerms The SearchTerms.
	 * @return The ID of the SearchTerms object. Since the ID is derived from the object, it doesn't matter 
	 * if the SearchTerms object is already in the database, or is just persisted.
	 */
	SearchTermsMemento.SearchTermsId saveIfNotExists(SearchTerms searchTerms);

	/**
	 * Load an {@link SearchTerms}.
	 * 
	 * @param searchTermsId The ID of the saved SearchTerms to load.
	 * @return The SearchTerms if is exists, <code>null</code> otherwise.
	 */
	SearchTerms load(SearchTermsMemento.SearchTermsId searchTermsId);

}
