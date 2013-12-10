/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.processing;

import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.target.result.Summary;

/**
 * A notification listener for new available sync job objects.
 */
public interface SyncJobObjectProcessor {
	
	/**
	 * Notifies of a new transaction job.
	 * 
	 * @param job the transaction job
	 */
	void transactionJob(TransactionJob job);
	
	/**
	 * Notifies of a new job unit.
	 * 
	 * @param unit the job unit
	 */
	void transactionJobUnitStart(TransactionJobUnit unit);

	/**
	 * Notifies of the transaction job unit end.
	 * 
	 * @param unit the unit that was finished 
	 * @param summary the summary object to report results
	 */
	void transactionJobUnitEnd(TransactionJobUnit unit, Summary summary);

	/**
	 * Notifies of a new transaction job entry.
	 * 
	 * @param entry the job  entry
	 * @param summary the summary to be used for reporting results
	 */
	void transactionJobEntry(JobEntry entry, Summary summary);

	/**
	 * Invoked when no other object notifications are available.
	 * 
	 * @param summary the summary object to report results
	 */
	void finished(Summary summary);

}
