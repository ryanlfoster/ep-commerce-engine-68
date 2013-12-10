/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.processing.impl;

import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.processing.SyncJobObjectProcessor;
import com.elasticpath.tools.sync.target.result.Summary;

/**
 * A processor that does not perform any processing of the received events.
 * It only makes sure that the entries are logged as successfully processed ones.
 */
public class NoProcessingJobObjectsProcessorImpl implements SyncJobObjectProcessor {

	@Override
	public void finished(final Summary summary) {
		// nothing to do
	}

	@Override
	public void transactionJob(final TransactionJob job) {
		// nothing to do
	}

	@Override
	public void transactionJobEntry(final JobEntry entry, final Summary summary) {
		// only report the job entry
		summary.addSuccessJobEntry(entry);
	}

	@Override
	public void transactionJobUnitEnd(final TransactionJobUnit unit, final Summary summary) {
		// nothing to do
	}

	@Override
	public void transactionJobUnitStart(final TransactionJobUnit unit) {
		// nothing to do
	}

}
