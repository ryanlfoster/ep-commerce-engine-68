package com.elasticpath.persistence.dao.impl;

import java.util.List;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.IndexBuildStatusDao;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.IndexType;

/**
 * Provides <code>IndexBuildStatusDao</code> data access methods.
 */
public class IndexBuildStatusDaoImpl implements IndexBuildStatusDao {
	
	private PersistenceEngine persistenceEngine;

	@Override
	public IndexBuildStatus get(final IndexType indexType) {
		sanityCheck();
		
		if (indexType == null) {
			throw new EpServiceException("Cannot retrieve null indexType.");
		}

		List<IndexBuildStatus> list = getPersistenceEngine().retrieveByNamedQuery("INDEXBUILDSTATUS_FIND_BY_TYPE", indexType.getIndexName());
		
        if (list.size() == 1) {
            return list.get(0);            
        } else if (list.size() > 1) {
            throw new EpServiceException("Inconsistent data -- duplicate index type exist -- " + indexType);
        }		
		return null;
	}

	@Override
	public List<IndexBuildStatus> list() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("INDEXBUILDSTATUS_FIND_ALL");

	}

	@Override
	public IndexBuildStatus saveOrUpdate(final IndexBuildStatus indexBuildStatus) {
		sanityCheck();
		return getPersistenceEngine().saveOrUpdate(indexBuildStatus);
	}

	/**
	 * Sanity check of this service instance.
	 * 
	 * @throws EpServiceException - if something goes wrong.
	 */
	protected void sanityCheck() throws EpServiceException {
		if (getPersistenceEngine() == null) {
			throw new EpServiceException("The persistence engine is not correctly initialized.");
		}
	}

	/**
	 * Gets the persistence engine.
	 * 
	 * @return The persistence engine.
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 * Sets the persistence engine to use.
	 *  
	 * @param persistenceEngine The persistence engine.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}
}
