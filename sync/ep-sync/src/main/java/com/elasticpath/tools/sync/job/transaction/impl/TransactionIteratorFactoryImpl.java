package com.elasticpath.tools.sync.job.transaction.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.job.transaction.TransactionAttribute;
import com.elasticpath.tools.sync.job.transaction.TransactionIteratorFactory;
import com.elasticpath.tools.sync.job.transaction.TransactionSettings;

/**
 * This factory uses to create iterators for different transaction types.
 */
public class TransactionIteratorFactoryImpl implements TransactionIteratorFactory {
	
	private static final Logger LOG = Logger.getLogger(TransactionIteratorFactoryImpl.class);

	private Map<TransactionAttribute, TransactionListIterator> iteratorMap;

	@Override
	public Iterator<List<TransactionJobDescriptorEntry>> createIterator(final TransactionSettings transactionSettings,
			final List<TransactionJobDescriptorEntry> jobDescriptorEntries) {
		LOG.debug("Create trasaction iterator and initialize it");
		final TransactionListIterator iterator = iteratorMap.get(transactionSettings.getTransactionAttribute());
		if (iterator == null) {
			throw new SyncToolRuntimeException("There is no iterator for transaction type " + transactionSettings.getTransactionAttribute());
		}
		
		iterator.initialize(jobDescriptorEntries, transactionSettings.getParameters());
		return iterator;
	}

	/**
	 * @param iteratorMap the iteratorMap to set
	 */
	public void setIteratorMap(final Map<TransactionAttribute, TransactionListIterator> iteratorMap) {
		this.iteratorMap = iteratorMap;
	}

}
