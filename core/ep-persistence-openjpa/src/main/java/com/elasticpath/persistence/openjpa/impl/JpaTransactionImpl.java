package com.elasticpath.persistence.openjpa.impl;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.Transaction;

/**
 * The JPA implementation of a transaction in ElasticPath. It is a wrap of <code>javax.persistence.EntityTransaction</code>.
 */
public class JpaTransactionImpl implements Transaction {
	
	private final PlatformTransactionManager txManager;
	private final TransactionStatus txStatus;

	/**
	 * Commit changes.
	 *
	 * @throws EpPersistenceException in case of any error
	 */
	public void commit() throws EpPersistenceException {
		this.txManager.commit(txStatus);
	}

	/**
	 * Rollback changes.
	 *
	 * @throws EpPersistenceException in case of any error
	 */
	public void rollback() throws EpPersistenceException {
		this.txManager.rollback(txStatus);
	}

	/**
	 * Create an EP transaction based on the given <code>EntityTransaction</code>.
	 *
	 * @param txManager the <code>PlatformTransactionManager</code>
	 * @param txStatus the status of the transaction
	 */
	public JpaTransactionImpl(final PlatformTransactionManager txManager, final TransactionStatus txStatus) {
		this.txManager = txManager;
		this.txStatus = txStatus;
	}

	@Override
	public boolean isRollbackOnly() {
		return txStatus.isRollbackOnly();
	}
}
