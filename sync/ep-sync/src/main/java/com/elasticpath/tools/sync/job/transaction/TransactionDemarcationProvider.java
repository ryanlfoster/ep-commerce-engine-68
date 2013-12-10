package com.elasticpath.tools.sync.job.transaction;

import java.util.List;

import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * Class responsible for transaction demarcation.
 */
public interface TransactionDemarcationProvider {

	/**
	 * Gets grouped transaction entries based on transaction.
	 * 
	 * @return transaction entries
	 */
	Iterable<List<TransactionJobDescriptorEntry>> getTransactionEntries();
	
	/**
	 * Initializes transaction demarcation provider.
	 * 
	 * @param jobDescriptorEntries the job descriptor entries
	 * @param transactionSettings the transaction settings
	 */
	void initialize(final List<TransactionJobDescriptorEntry> jobDescriptorEntries, final TransactionSettings transactionSettings);
	
}