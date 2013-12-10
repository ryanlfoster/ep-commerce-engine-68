package com.elasticpath.persistence.api;


/**
 * The perisistence session interface. It provides more control than <code>PersistenceEngine</code>. For example, you can control the transaction
 * programmatically.
 */
public interface PersistenceSession {

	/**
	 * Begins a transaction.
	 *
	 * @return returns a transaction
	 * @throws EpPersistenceException in case of any error
	 */
	Transaction beginTransaction() throws EpPersistenceException;

	/**
	 * Close the session.
	 *
	 * @throws EpPersistenceException in case of any error
	 */
	void close() throws EpPersistenceException;

	/**
	 * Update the given object.
	 *
	 * @param <T> the type of the object to return.
	 * @param object the object
	 * @return the updated persistable object.
	 * @throws EpPersistenceException in case of any error
	 */
	<T extends Persistable> T update(Persistable object) throws EpPersistenceException;

	/**
	 * Save the given object.
	 *
	 * @param object the object
	 * @throws EpPersistenceException in case of any error
	 */
	void save(Persistable object) throws EpPersistenceException;

	/**
	 * Creates and returns a query based on the given query string.
	 *
	 * @param queryString the query string
	 * @return a query
	 * @throws EpPersistenceException in case of any error
	 */
	Query createQuery(String queryString) throws EpPersistenceException;

	/**
	 * Creates and returns a query based on the given named query.
	 *
	 * @param queryName the named query
	 * @return a query
	 * @throws EpPersistenceException in case of any error
	 */
	Query createNamedQuery(String queryName) throws EpPersistenceException;

	/**
	 * Creates and returns a sql query based on the given query string.
	 *
	 * @param queryString the query string
	 * @return a query
	 * @throws EpPersistenceException in case of any error
	 */
	Query createSQLQuery(String queryString) throws EpPersistenceException;
}
