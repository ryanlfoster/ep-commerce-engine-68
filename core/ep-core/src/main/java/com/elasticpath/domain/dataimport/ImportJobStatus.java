/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport;

import java.util.Date;
import java.util.List;

import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.persistence.api.Persistable;

/**
 * Import Job status to notify clients of the current status of a scheduled/running import job.
 */
public interface ImportJobStatus extends Persistable, DatabaseLastModifiedDate {

	/**
	 * Gets the import job process ID.
	 * 
	 * @return the ID of the import job process
	 */
	String getProcessId();
	
	/**
	 * Sets the import job process ID.
	 * 
	 * @param processId the new ID
	 */
	void setProcessId(String processId);
	
	/**
	 * Returns the import job that is running.
	 * 
	 * @return the import job that is running
	 */
	ImportJob getImportJob();

	/**
	 * Returns the total number of rows to import.
	 * 
	 * @return the total number of rows to import
	 */
	int getTotalRows();

	/**
	 * Returns the current row number is in importing.
	 * 
	 * @return the current row number is in importing
	 */
	int getCurrentRow();

	/**
	 * Returns the row number that has been imported successfully.
	 * 
	 * @return the row number that has been imported successfully
	 */
	int getSucceededRows();

	/**
	 * Returns the row number that failed the imported.
	 * 
	 * @return the row number that failed the imported
	 */
	int getFailedRows();

	/**
	 * Returns a list of <code>ImportBadRow</code>.
	 * 
	 * @return a list of <code>ImportBadRow</code>
	 */
	List<ImportBadRow> getBadRows();

	/**
	 * Returns the import job start time.
	 * 
	 * @return the import job start time
	 */
	Date getStartTime();

	/**
	 * Returns the import job end time.
	 * 
	 * @return the import job end time
	 */
	Date getEndTime();

	/**
	 * Returns <code>true</code> if the job is finished, otherwise, <code>false</code>.
	 * 
	 * @return <code>true</code> if the job is finished, otherwise, <code>false</code>
	 */
	boolean isFinished();

	/**
	 * Returns the left rows to import.
	 * 
	 * @return the left rows to import
	 */
	int getLeftRows();

	/**
	 * Returns <code>true</code> if the job is cancelled, otherwise, <code>false</code>.
	 * 
	 * @return <code>true</code> if the job is cancelled, otherwise, <code>false</code>
	 */
	boolean isCanceled();
	
	/**
	 * Get the user who started the import job.
	 * 
	 * @return the user the job was started by
	 */
	CmUser getStartedBy();
	
	/**
	 * Set the user who started the import job.
	 *
	 * @param cmUser the user who started the job
	 */
	void setStartedBy(CmUser cmUser);

	/**
	 * Gets the current state.
	 * 
	 * @return the current state of the import job
	 */
	ImportJobState getState();

}
