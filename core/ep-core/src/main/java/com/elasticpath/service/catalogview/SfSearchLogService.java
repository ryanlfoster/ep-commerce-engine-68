package com.elasticpath.service.catalogview;

import com.elasticpath.domain.search.SfSearchLog;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * A description of an interface that persists and retrieves SfSearchLog objects.
 */
public interface SfSearchLogService extends EpPersistenceService {
	/**
	 * Adds the given SfSearhLog.
	 * 
	 * @param log the SfSearchLog to save
	 * @return the persisted instance of SfSearchLog
	 * @throws EpServiceException if there are any errors
	 */
	SfSearchLog add(final SfSearchLog log) throws EpServiceException;

	/**
	 * Updates the given SfSearhLog.
	 * 
	 * @param log the SfSearhLog to update
	 * @return the updated SfSearchLog instance
	 * @throws EpServiceException if there are any errors
	 */
	SfSearchLog update(final SfSearchLog log) throws EpServiceException;

	/**
	 * Loads the SfSearhLog indicated by the given Uid.
	 * 
	 * @param sfSearchLogUid the uid of the SfSearhLog to load
	 * @return the SfSearhLog with the specified uid if it exists
	 * @throws EpServiceException if there is an error or the uid does not exist
	 */
	SfSearchLog load(final long sfSearchLogUid) throws EpServiceException;

	/**
	 * Gets the SfSearhLog indicated by the given Uid.
	 * 
	 * @param sfSearchLogUid the uid of the SfSearhLog to load
	 * @return the SfSearhLog with the specified uid if it exists
	 * @throws EpServiceException if there is an error or the uid does not exist
	 */
	SfSearchLog get(final long sfSearchLogUid) throws EpServiceException;
}
