package com.elasticpath.tools.sync.job;

import java.util.List;

import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * Sort out a list of <code>JobEntry</code>s considering ep domain type as a key.
 */
public interface DomainSorter {

	/**
	 * Accepting unsorted list of <code>JobEntry</code> provides sorting.
	 * 
	 * @param list unsorted list of <code>JobEntry</code> containing ep domain object guids.
	 */
	void sort(List< ? extends TransactionJobDescriptorEntry> list);

}
