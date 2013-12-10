package com.elasticpath.service.catalogview.impl;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.search.SfSearchLog;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalogview.SfSearchLogService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * A default implementation of SfSearchLogService.
 *
 */
public class SfSearchLogServiceImpl extends AbstractEpPersistenceServiceImpl implements SfSearchLogService {

	/**
	 * Adds the given SfSearhLog.
	 * 
	 * @param log the SfSearchLog to save
	 * @return the persisted instance of SfSearchLog
	 * @throws EpServiceException if there are any errors
	 */
	public SfSearchLog add(final SfSearchLog log) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().save(log);
		return log;
	}

	/**
	 * Updates the given SfSearhLog.
	 * 
	 * @param log the SfSearhLog to update
	 * @return the updated SfSearchLog instance
	 * @throws EpServiceException if there are any errors
	 */
	public SfSearchLog update(final SfSearchLog log) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().merge(log);
	}

	/**
	 * Loads the SfSearhLog indicated by the given Uid.
	 * 
	 * @param sfSearchLogUid the uid of the SfSearhLog to load
	 * @return the SfSearhLog with the specified uid if it exists
	 * @throws EpServiceException if there is an error or the uid does not exist
	 */
	public SfSearchLog load(final long sfSearchLogUid) throws EpServiceException {
		sanityCheck();
		SfSearchLog log = null;
		if (sfSearchLogUid <= 0) {
			log = getBean(ContextIdNames.SF_SEARCH_LOG);
		} else {
			log = getPersistenceEngine().load(SfSearchLog.class, sfSearchLogUid);
		}

		return log;
	}

	/**
	 * Gets the SfSearhLog indicated by the given Uid.
	 * 
	 * @param sfSearchLogUid the uid of the SfSearhLog to load
	 * @return the SfSearhLog with the specified uid if it exists
	 * @throws EpServiceException if there is an error or the uid does not exist
	 */
	public SfSearchLog get(final long sfSearchLogUid) throws EpServiceException {
		sanityCheck();
		SfSearchLog log = null;
		if (sfSearchLogUid <= 0) {
			log = getBean(ContextIdNames.SF_SEARCH_LOG);
		} else {
			log = getPersistenceEngine().get(SfSearchLog.class, sfSearchLogUid);
		}

		return log;
	}

	/**
	 * Generic load method for all persistable domain models.
	 *
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

}
