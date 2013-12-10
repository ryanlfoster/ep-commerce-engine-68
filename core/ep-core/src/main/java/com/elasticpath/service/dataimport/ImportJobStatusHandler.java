/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;

/**
 * A handler that uses that is used to report status and retrieve status from a single source.
 */
public interface ImportJobStatusHandler {

	/**
	 * Reports the current row.
	 * 
	 * @param importJobProcessId the import job process ID
	 * @param rowNumber the row number
	 */
	void reportCurrentRow(String importJobProcessId, int rowNumber);

	/**
	 * Checks out if an import job has been cancelled.
	 * 
	 * @param importJobProcessId the import job process ID
	 * @return true if the job has been cancelled
	 */
	boolean isImportJobCancelled(String importJobProcessId);

	/**
	 * Reports the failed rows since the last report.
	 * 
	 * @param importJobProcessId the import job process ID
	 * @param failedRows the number of failed rows
	 */
	void reportFailedRows(String importJobProcessId, int failedRows);

	/**
	 * Verifies whether an import job has exceeded the allowed failed rows number.
	 * 
	 * @param importJobProcessId the import job process ID
	 * @param maxAllowedFailedRows the max allowed failed rows
	 * @return true of the failed rows have exceeded the allowed number
	 */
	boolean verifyImportJobFailedRows(String importJobProcessId, int maxAllowedFailedRows);

	/**
	 * Reports bad rows.
	 * 
	 * @param importJobProcessId the import job process ID
	 * @param importBadRow the import bad rows (one or many)
	 */
	void reportBadRows(String importJobProcessId, ImportBadRow... importBadRow);

	/**
	 * Reports the total rows count for an import job.
	 * 
	 * @param importJobProcessId the import job process ID
	 * @param totalRows the total rows
	 */
	void reportTotalRows(String importJobProcessId, int totalRows);

	/**
	 * Gets the import job status.
	 * 
	 * @param importJobProcessId the import job process ID
	 * @return the current import job status 
	 */
	ImportJobStatus getImportJobStatus(String importJobProcessId);

	/**
	 * Reports a state to all interested parties.
	 * 
	 * @param importJobProcessId the import job process ID
	 * @param state the state reported
	 */
	void reportImportJobState(String importJobProcessId, ImportJobState state);

	/**
	 * Creates a new job status representing an import job's initial state and deletes any other
	 * job status with the same parameters from the database.
	 * 
	 * @param processId the import job process ID 
	 * @param importJob the import job
	 * @param initiator the initiator
	 * @return the import job status
	 */
	ImportJobStatus initialiseJobStatus(String processId, ImportJob importJob, CmUser initiator);
	
	/**
	 * Determines whether an import job exists for the given process ID.
	 * 
	 * @param importJobProcessId the ID to check
	 * @return true if the import job has a status
	 */
	boolean doesImportJobExist(final String importJobProcessId);

}
