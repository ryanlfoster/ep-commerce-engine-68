/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport.dao.impl;

import java.util.List;

import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.dataimport.dao.ImportJobStatusDao;

/**
 * The default implementation of the {@link ImportJobStatusDao} based of the usage of {@link PersistenceEngine}.
 */
public class ImportJobStatusDaoImpl implements ImportJobStatusDao {

	private PersistenceEngine persistenceEngine;
	
	@Override
	public List<ImportJobStatus> findByImportJobGuid(final String importJobId) {
		return getPersistenceEngine().retrieveByNamedQuery("FIND_BY_JOB_GUID", importJobId);
	}

	@Override
	public long countByState(final ImportJobState running) {
		List<Long> result = getPersistenceEngine().retrieveByNamedQuery("COUNT_BY_JOB_STATE", running);
		return result.iterator().next();
	}

	@Override
	public List<ImportJobStatus> findByState(final ImportJobState state) {
		return getPersistenceEngine().retrieveByNamedQuery("FIND_BY_JOB_STATE", state);
	}

	@Override
	public ImportJobStatus saveOrUpdate(final ImportJobStatus importJobStatus) {
		return getPersistenceEngine().saveOrUpdate(importJobStatus);
	}

	/**
	 *
	 * @return the persistenceEngine
	 */
	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 *
	 * @param persistenceEngine the persistenceEngine to set
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	@Override
	public void remove(final ImportJobStatus status) {
		getPersistenceEngine().delete(status);
	}

	@Override
	public ImportJobStatus findByProcessId(final String importJobProcessId) {
		List<ImportJobStatus> result = getPersistenceEngine().retrieveByNamedQuery("FIND_BY_PROCESS_ID", importJobProcessId);
		if (result.size() > 1) {
			throw new IllegalStateException("Found more than one statuses with the same process ID");
		} else if (result.isEmpty()) {
			return null;
		}
		return result.iterator().next();
	}
	
	@Override
	public boolean doesImportJobExist(final String importJobProcessId) {
		List<Long> result = getPersistenceEngine().retrieveByNamedQuery("IMPORT_JOB_COUNT_BY_ID", importJobProcessId);
		if (!result.isEmpty() && result.get(0) > 0) {
			return true;
		}
		return false;
	}

}
