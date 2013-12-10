package com.elasticpath.tools.sync.job;

import java.util.Comparator;

import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;


/**
 * Represents custom sorting policy, basically for sorting objects in hierarchies. E.g. nested categories can not be sorted merely by type. More deep
 * analysis considering parent-relation is required.
 */
public interface SortingPolicy extends Comparator<TransactionJobDescriptorEntry> {

	/**
	 * Compares two entries which must be of the same type.
	 * 
	 * @param leftEntry a transaction job descriptor entry
	 * @param rightEntry a transaction job descriptor entry
	 * @return 0 if entries are equal, -1 if left < right, 1 if left > right
	 */
	int compare(TransactionJobDescriptorEntry leftEntry, TransactionJobDescriptorEntry rightEntry);
}
