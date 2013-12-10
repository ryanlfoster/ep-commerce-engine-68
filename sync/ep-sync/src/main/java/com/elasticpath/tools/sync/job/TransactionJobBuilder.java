package com.elasticpath.tools.sync.job;

import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * Having provided <code>JobDescriptor</code> information, retrieves appropriate source objects from the source environment and populates
 * transaction list of <code>JobEntry</code>. Provides sorting of <code>JobDescriptorEntry</code> objects if required.
 */
public interface TransactionJobBuilder {

	/**
	 * Builds <code>TransactionJob</code> object which represents a list of transaction job units.
	 * 
	 * @param jobDescriptor the job descriptor
	 * @param populate tells the builder whether to populate the objects with the data or do that when the object is accessed
	 * @return list of transaction job units
	 * @throws SyncToolConfigurationException in case configuration has not been initialized
	 */
	TransactionJob build(JobDescriptor jobDescriptor, boolean populate) throws SyncToolConfigurationException;

	/**
	 * Creates job entry based in given descriptor entry.
	 * 
	 * @param transactionJobUnit the parent transaction job unit
	 * @param descriptorEntry the job descriptor entry
	 * @return job entry
	 */
	JobEntry createJobEntry(TransactionJobUnit transactionJobUnit, TransactionJobDescriptorEntry descriptorEntry);

}
