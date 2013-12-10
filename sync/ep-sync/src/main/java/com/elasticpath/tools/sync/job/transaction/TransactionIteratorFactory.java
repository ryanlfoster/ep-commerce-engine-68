package com.elasticpath.tools.sync.job.transaction;

import java.util.Iterator;
import java.util.List;

import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * This factory uses to create iterators for different transaction types.
 */
public interface TransactionIteratorFactory {

	/**
	 * Creates iterator based on transaction settings.
	 * 
	 * @param transactionSettings the transaction settings
	 * @param jobDescriptorEntries the job descriptor entries
	 * @return iterator for given transaction attribute
	 */
	Iterator<List<TransactionJobDescriptorEntry>> createIterator(final TransactionSettings transactionSettings,
			final List<TransactionJobDescriptorEntry> jobDescriptorEntries);

}