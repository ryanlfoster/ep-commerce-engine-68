package com.elasticpath.tools.sync.job.transaction.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * Transaction per given quantity job entry list iterator implementation, return each given qty of job entries in separate lists. 
 */
public class TransactionPerGivenQtyJobEntryIterator extends AbstractTransactionListIterator {
	
	private static final Logger LOG = Logger.getLogger(TransactionPerGivenQtyJobEntryIterator.class);

	private int index = 0;
	
	private int offset = 1;
	
	@Override
	public void initialize(final List<TransactionJobDescriptorEntry> jobDescriptorEntriesList, final List<String> params) {
		super.initialize(jobDescriptorEntriesList, params);
		if (!params.isEmpty()) {
			offset = Integer.parseInt(params.get(0));
			LOG.debug("Initialize iterator with offset = " + offset);
		}
	}

	@Override
	protected boolean hasNextElement() {
		return index < getJobDescriptorEntriesList().size();
	}

	@Override
	protected List<TransactionJobDescriptorEntry> nextElement() {
		final int endIndex = getEndIndex(index + offset);
		final List<TransactionJobDescriptorEntry> subList = getJobDescriptorEntriesList().subList(index, endIndex);
		index = endIndex;
		return subList;
	}

	private int getEndIndex(final int endIndex) {
		if (endIndex > getJobDescriptorEntriesList().size()) {
			return getJobDescriptorEntriesList().size();
		}
		return endIndex;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(final int offset) {
		int offsetResult = offset;
		if (offsetResult == -1) {
			offsetResult = Integer.MAX_VALUE;
		}
		this.offset = offsetResult;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

}
