package com.elasticpath.tools.sync.job.transaction.impl;

import java.util.Collections;
import java.util.List;

import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * Abstract transaction list iterator implementation.
 */
public abstract class AbstractTransactionListIterator implements TransactionListIterator {
	
	private List<TransactionJobDescriptorEntry> jobDescriptorEntriesList;
	
	private List<String> params;
	
	/**
	 * Initializes transaction list iterator.
	 * 
	 * @param jobDescriptorEntriesList the job descriptor entries list
	 * @param params params for iterator
	 */
	public void initialize(final List<TransactionJobDescriptorEntry> jobDescriptorEntriesList, final List<String> params) {
		this.jobDescriptorEntriesList = jobDescriptorEntriesList;
		this.params = params;
	}
	
	/**
	 * @return the jobDescriptorEntriesList
	 */
	protected List<TransactionJobDescriptorEntry> getJobDescriptorEntriesList() {
		return jobDescriptorEntriesList;
	}

	/**
	 * @return the params
	 */
	protected List<String> getParams() {
		if (params == null) {
			return Collections.emptyList();
		}
		return params;
	}
	
	private void sanityCheck() {
		if (jobDescriptorEntriesList == null) {
			throw new SyncToolRuntimeException("jobDescriptorEntriesList has not been properly initialized");
		}
	}

	@Override
	public boolean hasNext() {
		sanityCheck();
		return hasNextElement();
	}
	
	@Override
	public List<TransactionJobDescriptorEntry> next() {
		sanityCheck();
		return nextElement();
	}
	
	@Override
	public void remove() {
		sanityCheck();
		removeElement();
	}

	/**
	 * Checks if next element exists.
	 * 
	 * @return true if element exists and false otherwise
	 */
	protected abstract boolean hasNextElement(); 
	
	/**
	 * Gets next element.
	 * 
	 * @return next element if it exists.
	 */
	protected abstract List<TransactionJobDescriptorEntry> nextElement();
	
	/**
	 * Removes current element.
	 */
	protected void removeElement() {
		throw new UnsupportedOperationException("Remove operation is not supported");
	}
}
