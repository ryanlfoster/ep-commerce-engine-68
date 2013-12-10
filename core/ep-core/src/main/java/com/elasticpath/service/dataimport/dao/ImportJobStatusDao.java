/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport.dao;

import java.util.List;

import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;

/**
 * Import job status DAO for handling data source requests.
 */
public interface ImportJobStatusDao {

	/**
	 * Finds a list of statuses by the import job GUID.
	 * 
	 * @param importJobGuid the import job GUID
	 * @return a list of statuses
	 */
	List<ImportJobStatus> findByImportJobGuid(String importJobGuid);

	/**
	 * Counts the total number of {@link ImportJobStatus} by their {@link ImportJobState}.
	 * 
	 * @param state the state
	 * @return the count of existing statuses
	 */
	long countByState(ImportJobState state);

	/**
	 * Finds statuses by their state field.
	 * 
	 * @param state the state
	 * @return the list of statuses found
	 */
	List<ImportJobStatus> findByState(ImportJobState state);

	/**
	 * Saves or updates a status to the data source.
	 * 
	 * @param importJobStatus the import job status
	 * @return the updated status
	 */
	ImportJobStatus saveOrUpdate(ImportJobStatus importJobStatus);

	/**
	 * Deletes a status from the data source.
	 * 
	 * @param status the status
	 */
	void remove(ImportJobStatus status);

	/**
	 * Finds a status by its process ID.
	 * 
	 * @param importJobProcessId the ID
	 * @return the import job status
	 */
	ImportJobStatus findByProcessId(String importJobProcessId);

	/**
	 * Determine whether the job with the given ID exists.
	 * 
	 * @param importJobProcessId the ID
	 * @return true if the job exists
	 */
	boolean doesImportJobExist(final String importJobProcessId);
}
