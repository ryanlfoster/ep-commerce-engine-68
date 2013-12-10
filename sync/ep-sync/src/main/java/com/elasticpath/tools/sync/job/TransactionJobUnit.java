package com.elasticpath.tools.sync.job;

import java.util.List;

import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.processing.SerializableObject;


/**
 * Provides a list of job entries which should be processed in a single transaction on a server.
 */
public interface TransactionJobUnit extends SerializableObject, Iterable<SerializableObject>  {

	/**
	 * Gets name of this transaction job unit if any.
	 * 
	 * @return name of this transaction job unit.
	 */
	String getName();
	
	/**
	 * Sets name of this transaction job unit if any.
	 * 
	 * @param name name of this transaction job unit.
	 */
	void setName(final String name);
	
	/**
	 * Creates a list of {@link JobEntry}s. The list is created from {@link #getJobDescriptorEntries()}.
	 * 
	 * @return list of {@link JobEntry}s
	 */
	List<JobEntry> createJobEntries();

	/**
	 * Gets the list of {@link TransactionJobDescriptorEntry}.
	 * 
	 * @return list of {@link TransactionJobDescriptorEntry}
	 */
	List<TransactionJobDescriptorEntry> getJobDescriptorEntries();
	
	/**
	 * Adds a given job entry.
	 * 
	 * @param jobEntry the job entry to add
	 */
	void addJobEntry(final TransactionJobDescriptorEntry jobEntry);

}
