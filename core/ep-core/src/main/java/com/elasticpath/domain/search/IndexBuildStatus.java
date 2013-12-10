package com.elasticpath.domain.search;

import java.util.Date;

import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.IndexType;

/**
 * Represents a Status of Build Index.
 */
public interface IndexBuildStatus extends Persistable, DatabaseLastModifiedDate {

	/**
	 * Returns the index type.
	 *
	 * @return the index type
	 */
	IndexType getIndexType();

	/**
	 * Sets the index type.
	 *
	 * @param type the index type
	 */
	void setIndexType(IndexType type);

	/**
	 * Sets the last build date for a Index Build Status.
	 *
	 * @param date the release date
	 */
	void setLastBuildDate(Date date);

	/**
	 * Returns the last build date.
	 *
	 * @return <code>Date</code>
	 */
	Date getLastBuildDate();

	/**
	 * Returns the index status.
	 *
	 * @return the index status
	 */
	IndexStatus getIndexStatus();

	/**
	 * Sets the index status.
	 *
	 * @param status the index status
	 */
	void setIndexStatus(IndexStatus status);

	/**
	 * Gets the total number of records.
	 *
	 * @return the total number of records or {@code -1} if not identified yet
	 */
	int getTotalRecords();

	/**
	 * Sets the total number of records for this operation.
	 *
	 * @param totalRecords the number of records
	 */
	void setTotalRecords(int totalRecords);

	/**
	 * Gets the records processed up until now.
	 *
	 * @return the number of processed records in the current operation
	 */
	int getProcessedRecords();

	/**
	 * Sets the number of processed records.
	 * @param processedRecords the number of processed records.
	 */
	void setProcessedRecords(int processedRecords);

	/**
	 * The operation's start date.
	 *
	 * @return the date when the operation was triggered. Could be null in case the operation has not started yet.
	 */
	Date getOperationStartDate();

	/**
	 * Sets the operation start date.
	 *
	 * @param operationStartDate the new date to set
	 */
	void setOperationStartDate(Date operationStartDate);
}
