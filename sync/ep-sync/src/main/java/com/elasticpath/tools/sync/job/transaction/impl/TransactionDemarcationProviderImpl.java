package com.elasticpath.tools.sync.job.transaction.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.job.transaction.TransactionDemarcationProvider;
import com.elasticpath.tools.sync.job.transaction.TransactionIteratorFactory;
import com.elasticpath.tools.sync.job.transaction.TransactionSettings;

/**
 * Class responsible for transaction demarcation.
 */
public class TransactionDemarcationProviderImpl implements TransactionDemarcationProvider {
	
	private static final Logger LOG = Logger.getLogger(TransactionDemarcationProviderImpl.class);
	
	private TransactionIteratorFactory transactionIteratorFactory;
	
	private List<TransactionJobDescriptorEntry> descriptorList;
	
	private TransactionSettings transactionSettings;
		
	@Override
	public void initialize(final List<TransactionJobDescriptorEntry> jobDescriptorEntries, final TransactionSettings transactionSettings) {
		LOG.debug("Initializes demarcation provider with transaction settings");
		descriptorList = jobDescriptorEntries;
		this.transactionSettings = transactionSettings;
	}
		
	/**
	 * Gets grouped transaction entries based on transaction.
	 * 
	 * @return transaction entries
	 */	
	public Iterable<List<TransactionJobDescriptorEntry>> getTransactionEntries() {
		return new Iterable<List<TransactionJobDescriptorEntry>>() {

			public Iterator<List<TransactionJobDescriptorEntry>> iterator() {
				return transactionIteratorFactory.createIterator(transactionSettings, descriptorList);
			}
			
		};
	}

	/**
	 * @param transactionIteratorFactory the transactionIteratorFactory to set
	 */
	public void setTransactionIteratorFactory(final TransactionIteratorFactory transactionIteratorFactory) {
		this.transactionIteratorFactory = transactionIteratorFactory;
	}
}
