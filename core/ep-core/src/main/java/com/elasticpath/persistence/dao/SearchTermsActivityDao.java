package com.elasticpath.persistence.dao;

import com.elasticpath.domain.search.query.SearchTermsActivity;
import com.elasticpath.service.EpService;

/**
 * CRUD for {@link SearchTermsActivity}.
 */
public interface SearchTermsActivityDao extends EpService {

	/**
	 * Saves the given {@link SearchTermsActivity}.
	 * 
	 * @param activity a {@link SearchTermsActivity}
	 */
	void save(SearchTermsActivity activity);
}
