package com.elasticpath.tools.sync.target.result;

import java.util.List;

import com.elasticpath.tools.sync.job.JobEntry;


/**
 * Collects synchronization errors.
 */
public interface Summary {

	/**
	 * Returns a list of errors occurred during synchronization.
	 * 
	 * @return a list of errors occurred during synchronization.
	 */
	List<SyncErrorResultItem> getSyncErrors();

	/**
	 * Adds the error to this collector.
	 * 
	 * @param syncError SyncError
	 */
	void addSyncError(SyncErrorResultItem syncError);

	/**
	 * Returns true is the collector has any errors.
	 * @return true if there error collector is not empty.
	 */
	boolean hasErrors();

	/**
	 * Number of errors.
	 * @return number of errors
	 */
	int getNumberOfErrors();
	
	/**
	 * Adds a successful job entry to this collector.
	 * 
	 * @param jobEntry the successful job entry
	 */
	void addSuccessJobEntry(JobEntry jobEntry);
	
	/**
	 * Returns list of success results.
	 * 
	 * @return list of SyncResultItem instances.
	 */
	List<SyncResultItem> getSuccessResults();
	
	/**
	 * Returns list of success results merged with error results.
	 * 
	 * @return list of SyncResultItem instances.
	 */
	List<SyncResultItem> getAllResults();
}
