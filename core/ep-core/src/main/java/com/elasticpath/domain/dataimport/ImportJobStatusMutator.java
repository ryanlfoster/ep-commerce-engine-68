/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport;

import java.util.Date;


/**
 * A mutator interface for the {@link ImportJobStatus}.
 */
public interface ImportJobStatusMutator extends ImportJobStatus {

	/**
	 * Sets the import job.
	 * 
	 * @param importJob the import job to set
	 */
	void setImportJob(ImportJob importJob);

	/**
	 * Sets the new state.
	 * 
	 * @param importJobState the import job state
	 */
	void setState(ImportJobState importJobState);

	/**
	 * Sets the current row.
	 * 
	 * @param rowNumber the row number
	 */
	void setCurrentRow(int rowNumber);

	/**
	 * Sets the failed rows number.
	 * 
	 * @param failedRows the number of failed rows
	 */
	void setFailedRows(int failedRows);

	/**
	 * Sets the total rows of the import job.
	 * 
	 * @param totalRows the total rows
	 */
	void setTotalRows(int totalRows);

	/**
	 * Add an <code>ImportBadRow</code>.
	 * 
	 * @param importBadRow the import bad row to add.
	 */
	void addBadRow(ImportBadRow importBadRow);
	
	/**
	 * Sets the start date.
	 * 
	 * @param startTime the start date
	 */
	void setStartTime(Date startTime);

	/**
	 * Sets the end date.
	 * 
	 * @param endTime the end date
	 */
	void setEndTime(Date endTime);

}
