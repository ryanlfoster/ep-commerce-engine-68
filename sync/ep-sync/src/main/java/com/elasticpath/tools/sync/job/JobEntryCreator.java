/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.job;

import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * An instantiator of a JobEntry.
 */
public interface JobEntryCreator {

	/**
	 * Creates a new job entry object using the provided {@code transactionJobUnit} and {@code entryDesc}.
	 * 
	 * @param transactionJobUnit the job unit
	 * @param entryDesc the entry descriptor
	 * @return a new job entry
	 */
	JobEntry createJobEntry(TransactionJobUnit transactionJobUnit, TransactionJobDescriptorEntry entryDesc);

}
