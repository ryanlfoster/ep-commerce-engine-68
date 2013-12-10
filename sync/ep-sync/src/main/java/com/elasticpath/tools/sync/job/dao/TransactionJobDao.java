/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.job.dao;

import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;

/**
 * A DAO for loading/saving a {@link TransactionJob}.
 */
public interface TransactionJobDao {

	/**
	 * Saves a transaction job.
	 * 
	 * @param transactionJob the transaction job to save
	 */
	void save(TransactionJob transactionJob);

	/**
	 * Loads a transaction job by loading its elements.
	 * This depends on the order the job gets saved.
	 * 
	 * @param listener an object listener to be notified of the transaction job elements
	 */
	void load(SerializableObjectListener listener);

}
