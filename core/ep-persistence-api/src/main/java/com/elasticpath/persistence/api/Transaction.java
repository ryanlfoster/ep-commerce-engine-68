package com.elasticpath.persistence.api;


/**
 * Represents a transaction in ElasticPath.
 */
public interface Transaction {

	/**
	 * Commit changes.
	 * 
	 * @throws EpPersistenceException in case of any error
	 */
	void commit() throws EpPersistenceException;

	/**
	 * Rollback changes.
	 * 
	 * @throws EpPersistenceException in case of any error
	 */
	void rollback() throws EpPersistenceException;
	
	/**
	 * Determines whether the transaction is marked as roll-back only.
	 *
	 * @return <code>true</code> iff the transaction is marked as roll-back only
	 */
	boolean isRollbackOnly();
}
