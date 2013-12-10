package com.elasticpath.tools.sync.job.descriptor;

import java.io.Serializable;
import java.util.List;

/**
 * Lists <code>JobDecriptorEntry</code> objects describing add or delete operation on one single object.
 */
public interface TransactionJobDescriptor extends Serializable {

	/**
	 * Gets name of this transaction job descriptor if any.
	 * 
	 * @return name of this transaction job descriptor.
	 */
	String getName();
	
	/**
	 * Sets name of this transaction job descriptor if any.
	 * 
	 * @param name name of this transaction job descriptor.
	 */
	void setName(final String name);
	
	/**
	 * @return entries forming synchronization job
	 */
	List<TransactionJobDescriptorEntry> getJobDescriptorEntries();

	/**
	 * @param jobDescriptorEntries job entry descriptor
	 */
	void setJobDescriptorEntries(final List<TransactionJobDescriptorEntry> jobDescriptorEntries);
}