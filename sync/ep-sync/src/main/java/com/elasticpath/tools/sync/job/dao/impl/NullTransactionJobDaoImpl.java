/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.job.dao.impl;

import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.dao.TransactionJobDao;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;

/**
 * An empty implementation of the {@link TransactionJobDao}
 * used when no save or load are required.
 */
public class NullTransactionJobDaoImpl implements TransactionJobDao {

	/**
	 * This implementation does not load.
	 * 
	 * @param listener the listener
	 */
	public void load(final SerializableObjectListener listener) {
		// this implementation is supposed to do nothing
	}

	/**
	 * This implementation does not save.
	 * 
	 * @param transactionJob the job
	 */
	public void save(final TransactionJob transactionJob) {
		// this implementation is supposed to do nothing
	}

}
