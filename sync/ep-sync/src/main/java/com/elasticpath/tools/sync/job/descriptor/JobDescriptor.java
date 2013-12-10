package com.elasticpath.tools.sync.job.descriptor;

import java.io.Serializable;
import java.util.List;


/**
 * Lists <code>JobDecriptorEntry</code> objects describing add or delete operation on one single object.
 */
public interface JobDescriptor extends Serializable {

	/**
	 * @return entries forming synchronization job
	 */
	List<TransactionJobDescriptor> getTransactionJobDescriptors();

	/**
	 * Adds a transaction job descriptor to this job descriptor.
	 * 
	 * @param descriptor the descriptor to add
	 */
	void addTransactionJobDescriptor(TransactionJobDescriptor descriptor);
}
