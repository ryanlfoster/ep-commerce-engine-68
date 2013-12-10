/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.changeset.dao.impl;

import java.util.List;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.changeset.dao.ChangeSetUserFinder;

/**
 * The implementation class of Change Set CmUser Finder implementation.
 */
public class ChangeSetCmUserFinderImpl implements ChangeSetUserFinder {
	
	private PersistenceEngine persistenceEngine;

	/**
	 * Get the persistence engine.
	 * 
	 * @return the instance of persistence engine
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 * Set persistence engine.
	 *
	 * @param persistenceEngine the instance of persistence engine
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * Find CmUser guid by cm user name.
	 * 
	 * @param userName cm user name
	 * @return CmUser guid
	 */
	public String findUserGuidByUserName(final String userName) {
		final List<CmUser> results = getPersistenceEngine().retrieveByNamedQuery("CMUSER_FIND_BY_USERNAME", userName);
		if (results.size() == 1) {
			return results.get(0).getGuid();
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate user name -- " + userName);
		}
		return null;
	}

}
