package com.elasticpath.tools.sync.job.transaction.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * Transaction group by command and type iterator implementation, return job descriptor entries.
 */
public class TrasnactionGroupByCommandAndTypeIterator extends AbstractTransactionListIterator {

	private int offset = 0;

	@Override
	protected boolean hasNextElement() {
		return offset < getJobDescriptorEntriesList().size();
	}

	@Override
	protected List<TransactionJobDescriptorEntry> nextElement() {
		final List<TransactionJobDescriptorEntry> resultList = new ArrayList<TransactionJobDescriptorEntry>();

		final List<TransactionJobDescriptorEntry> jobDescriptorEntriesList = getJobDescriptorEntriesList();

		final TransactionJobDescriptorEntry firstDescriptorEntry = jobDescriptorEntriesList.get(offset);
		resultList.add(firstDescriptorEntry);
		for (offset = offset + 1; offset < jobDescriptorEntriesList.size(); offset++) {
			final TransactionJobDescriptorEntry descriptorEntry = jobDescriptorEntriesList.get(offset);
			if (!addToResult(firstDescriptorEntry, descriptorEntry)) {
				break;
			}
			resultList.add(descriptorEntry);
		}

		return resultList;
	}

	/**
	 * Checks should the descriptor entry be added to result list.
	 * 
	 * @param firstDescriptorEntry the first descriptor entry that was added to result list
	 * @param descriptorEntry the descriptor entry to check
	 * @return true if descriptor entry should be added to result list and false otherwise
	 */
	protected boolean addToResult(final TransactionJobDescriptorEntry firstDescriptorEntry, final TransactionJobDescriptorEntry descriptorEntry) {
		return firstDescriptorEntry.getCommand().equals(descriptorEntry.getCommand())
				&& firstDescriptorEntry.getType().equals(descriptorEntry.getType());
	}

}
