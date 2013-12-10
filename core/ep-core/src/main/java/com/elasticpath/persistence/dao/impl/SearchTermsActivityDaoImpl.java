package com.elasticpath.persistence.dao.impl;

import com.elasticpath.domain.search.query.SearchTermsActivity;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.SearchTermsActivityDao;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * Implementation of {@link SearchTermsActivityDao}.
 */
public class SearchTermsActivityDaoImpl extends AbstractEpServiceImpl implements SearchTermsActivityDao {

	private PersistenceEngine persistenceEngine;

	/**
	 * Saves the given {@link SearchTermsActivity}.
	 * 
	 * @param activity a {@link SearchTermsActivity}
	 */
	public void save(final SearchTermsActivity activity) {
		persistenceEngine.save(activity);
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}
}
