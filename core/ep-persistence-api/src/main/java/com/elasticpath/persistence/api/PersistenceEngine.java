/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.api;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The persistence engine interface.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface PersistenceEngine {
	/**
	 * Persist the given instance.
	 *
	 * @param object the instance to save.
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	void save(final Persistable object) throws EpPersistenceException;

	/**
	 * Retrieve a list of persistent instances with the specified query and bounds, namely,
	 * the maximum number of rows to retrieve and / or the first row to retrieve.
	 * This help to support pagination.
	 *
	 * @param <T> the object's type to retrieve
	 * @param query the query
	 * @param firstResult the first row to retrieve
	 * @param maxResults the maximum number of rows to retrieve
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	<T> List<T> retrieve(final String query, final int firstResult, final int maxResults) throws EpPersistenceException;
	
	/**
	 * Retrieve a list of persistent instances with the specified query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param <E> the type of values used in the query 
	 * @param query the JPQL query
	 * @param values the collection of values
	 * @param listParameterName the name of the parameter for the list values
	 * @param parameters the parameters to be used with the criteria
	 * @param firstResult the first row to retrieve
	 * @param maxResults the maximum number of rows to retrieve
	 * @return a list of persistent instances.
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	<T, E> List<T> retrieveWithList(final String query, final String listParameterName, 
			final Collection<E> values, final Object[] parameters, final int firstResult, final int maxResults) throws EpPersistenceException;

	/**
	 * Retrieve a list of persistent instances with the specified query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param query the query
	 * @param parameters the parameters to be used with the given query
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	<T> List<T> retrieve(String query, Object... parameters) throws EpPersistenceException;

	/**
	 * Retrieve a list of persistent instances with the specified query. This method will create a new
	 * session (EntityManager) to excute the query, and close the new session when completed.
	 *
	 * @param <T> the object's type to retrieve
	 * @param query the HQL query
	 * @param parameters the prameters to be used with the criteria
	 * @return a list of persistent instances.
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	<T> List<T> retrieveWithNewSession(String query, Object... parameters) throws EpPersistenceException;
	
	/**
	 * Retrieve a list of persistent instances with the specified query. This method will create a new
	 * session (EntityManager) to excute the query, and close the new session when completed.
	 *
	 * @param <T> the object's type to retrieve
	 * @param <E> the type of values used in the query 
	 * @param query the HQL query
	 * @param values the collection of values
	 * @param listParameterName the name of the parameter for the list values
	 * @param parameters the parameters to be used with the criteria
	 * @return a list of persistent instances.
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	<T, E> List<T> retrieveWithListWithNewSession(String query, String listParameterName, Collection<E> values, Object... parameters)
			throws EpPersistenceException;

	/**
	 * Retrieve a list of persistent instances with the specified query and bounds, namely,
	 * the maximum number of rows to retrieve and / or the first row to retrieve.
	 * This help to support pagination.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queryStr the HQL query string to be executed
	 * @param firstResult the first row to retrieve
	 * @param maxRestuls the maximum number of rows to retrieve
	 * @param parameters the parameters to be used with the criteria
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	<T> List<T> retrieve(final String queryStr, final Object[] parameters, final int firstResult, final int maxRestuls)
			throws EpPersistenceException;

	/**
	 * Retrieve a list of persistent instances with the specified query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param query the query
	 * @param parameters the prameters to be used with the given query
	 * @param cacheQuery set it to <code>true</code> to cache the query result
	 * @return a list of persistent instances.
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	<T> List<T> retrieve(final String query, final Object[] parameters, boolean cacheQuery) throws EpPersistenceException;

	/**
	 * Update the given persistent instance.
	 *
	 * @param <T> the type of the object
	 * @param object the instance to update
	 * @return the instance that the state was merged to
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	<T extends Persistable> T update(final T object) throws EpPersistenceException;

	/**
	 * Save the persistable instance if it's new or update the persistent instance if it exists.
	 *
	 * @param <T> the type of the object
	 * @param object the instance to save or update
	 * @return the merged object if it is merged, or the persisted object for save action
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	<T extends Persistable> T saveOrUpdate(final T object) throws EpPersistenceException;

	/**
	 * Merge the given persistent instance.
	 *
	 * @param <T> the type of the object
	 * @param object the instance to merge
	 * @return the merged object
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	<T extends Persistable> T merge(final T object) throws EpPersistenceException;

	/**
	 * Save the persistable instance if it's new or merge the persistent instance if it exists.
	 *
	 * @param <T> the type of the object
	 * @param object the instance to save or merge
	 * @return the merged object if it is merged, or the persisted object for save action
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	<T extends Persistable> T saveOrMerge(final T object) throws EpPersistenceException;

	/**
	 * Delete the given persistent instance.
	 *
	 * @param object The instance to delete
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	void delete(final Persistable object) throws EpPersistenceException;

	/**
	 * Load a persistent instance with the given id. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param <T> the type of the object
	 * @param persistenceClass the persistent class of the given id.
	 * @param uidPk the persistent instance id.
	 * @return the persistent instance
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	<T extends Persistable> T load(final Class<T> persistenceClass, final long uidPk) throws EpPersistenceException;
	
	/**
	 * Load a persistent instance with the given id. Throw an unrecoverable exception if there is
	 * no matching database row. This method will create a new session (EntityManager) to execute
	 * the query, and close the new session when completed.
	 * 
	 * @param <T> the type of the object
	 * @param persistenceClass the persistent class of the given id.
	 * @param uidPk the persistent instance id.
	 * @return the persistent instance
	 * @throws EpPersistenceException in case of persistence errors
	 */
	<T extends Persistable> T loadWithNewSession(final Class<T> persistenceClass, final long uidPk);

	/**
	 * Get a persistent instance with the given id. Return null if no matching record exists.
	 *
	 * @param <T> the type of the object
	 * @param persistenceClass the persistent class of the given id.
	 * @param uidPk the persistent instance id.
	 * @return the persistent instance
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	<T extends Persistable> T get(final Class<T> persistenceClass, final long uidPk) throws EpPersistenceException;

	/**
	 * Flush the cache.
	 */
	void flush();

	/**
	 * Clear the cache.
	 */
	void clear();

	/**
	 * Initialize the given object.
	 *
	 * @param object the object to initialize.
	 */
	void initialize(Object object);

	/**
	 * Returns a session.
	 *
	 * @return a session
	 */
	PersistenceSession getPersistenceSession();

	/**
	 * Returns a shared session.
	 *
	 * @return a session
	 */
	PersistenceSession getSharedPersistenceSession();

	/**
	 * Retrieve a list of persistent instances with the given list of queries.
	 * <p>
	 * Note: all queries must return the same type of data. Their results will be merged into the list returned by this method.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queries the list of queries
	 * @param parameters the parameters to be used with the given query
	 * @return a list of persistent instances.
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	<T> List<T> retrieve(List<String> queries, Object... parameters) throws EpPersistenceException;


	/**
	 * Update/delete all objects according to the given query.
	 *
	 * @param query the HQL query
	 * @param parameters the parameters to be used with the criteria
	 * @return the number of instances updated/deleted
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	int bulkUpdate(String query, Object... parameters) throws EpPersistenceException;

	/**
	 * Retrieve a list of persistent instances with the specified named query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queryName the named query
	 * @param parameters the parameters to be used with the given query
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	<T> List<T> retrieveByNamedQuery(String queryName, Object... parameters) throws EpPersistenceException;

	/**
	 * Retrieve a list of persistent instances with the specified named query, using the given FlushMode.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queryName the named query
	 * @param flushMode the flush mode to use when executing the query
	 * @param parameters the parameters to be used with the given query
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	<T> List<T> retrieveByNamedQuery(String queryName, FlushMode flushMode, Object... parameters) throws EpPersistenceException;

	/**
	 * Retrieves a collection of persistent instances with the specified named query. Parameter
	 * names should <i>not</i> be numerical.
	 * 
	 * @param <T> the object's type to retrieve
	 * @param queryName the named query
	 * @param parameters the named list of parameters to be used with the given query
	 * @return a collection of persistable instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	<T> List<T> retrieveByNamedQuery(String queryName, Map<String, Object> parameters) throws EpPersistenceException;

	/**
	 * Retrieve a list of persistent instances with the specified named query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queryName the named query
	 * @param firstResult the first row to retrieve
	 * @param maxResults the maximum number of rows to retrieve
	 * @return a list of persistent instances.
	 * @throws EpPersistenceException in case of persistence errors
	 */
	<T> List<T> retrieveByNamedQuery(final String queryName, final int firstResult, final int maxResults) throws EpPersistenceException;

	/**
	 * Retrieve a list of persistent instances with the specified named query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queryName the named query
	 * @param parameters the parameters to be used with the given query
	 * @param firstResult the first row to retrieve
	 * @param maxResults the maximum number of rows to retrieve
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	<T> List<T> retrieveByNamedQuery(final String queryName, final Object[] parameters, final int firstResult,
			final int maxResults) throws EpPersistenceException;
	
	/**
	 * Retrieve a list of persistent instances with the specified named query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param <E> the type of values used in the query
	 * @param queryName the named query
	 * @param parameterValuesMap the map of name - list values
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	<T, E> List<T> retrieveByNamedQueryWithList(final String queryName,
			final Map<String, Collection<E>> parameterValuesMap) throws EpPersistenceException;	

	/**
	 * Retrieve a list of persistent instances with the specified named query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param <E> the type of values used in the query
	 * @param queryName the named query
	 * @param listParameterName the name of the parameter for the list values
	 * @param values the collection of values
	 * @param parameters the parameters to be used with the given query
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	<T, E> List<T> retrieveByNamedQueryWithList(String queryName, String listParameterName, Collection<E> values, Object... parameters)
			throws EpPersistenceException;
	
	/**
	 * Retrieve a list of object array with the specified named query.
	 * Array of object represent single row from JPA reporting query.
	 * Object types in array corresponds to persistent instance fileds, that 
	 * retrieved by named query.  
	 *
	 * @param queryName the named query
	 * @param listParameterName the name of the parameter for the list values
	 * @param values the collection of values
	 * @param parameters the parameters to be used with the given query
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	List<Object[]> retrievePartByNamedQueryWithList(String queryName, String listParameterName, Collection<?> values, Object... parameters)
			throws EpPersistenceException;

	/**
	 * Retrieve a list of persistent instances with the specified named query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queryName the named query
	 * @param listParameterName name of the list parameter
	 * @param values collection of values for the list parameter
	 * @param parameters the parameters to be used with the given query
	 * @param firstResult the first row to retrieve
	 * @param maxResults the maximum number of rows to retrieve
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	<T> List<T> retrieveByNamedQueryWithList(String queryName, String listParameterName, Collection<?> values, Object[] parameters,
			int firstResult, int maxResults) throws EpPersistenceException;

	/**
	 * Execute an update or delete named query.
	 *
	 * @param queryName the named query
	 * @param parameters the parameters to be used with the given query
	 * @return the number of entities updated or deleted
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	int executeNamedQuery(String queryName, Object... parameters) throws EpPersistenceException;

	/**
	 * Execute an update or delete named query.
	 *
	 * @param <E> the type of values used in the query
	 * @param queryName the named query
	 * @param listParameterName the name of the parameter for the list values
	 * @param values the collection of values
	 * @param parameters the parameters to be used with the given query
	 * @return the number of entities updated or deleted
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	<E> int executeNamedQueryWithList(String queryName, String listParameterName, Collection<E> values, Object... parameters)
			throws EpPersistenceException;

	/**
	 * Get the persistence session factory.
	 * @return an instance of <code>PersistenceSessionFactory</code>
	 */
	PersistenceSessionFactory getSessionFactory();

	/**
	 * Get a direct connection.
	 * @return the connection
	 */
	Connection getConnection();

	/**
	 * Output some debug information on a persistable object.
	 * @param name - the name of the persistable object
	 * @param object - the persistable object to debug
	 */
	void debugObject(final String name, final Persistable object);

	/**
	 * Remove data for the given object from the cache.
	 * 
	 * @param object the Persistence object to evict.
	 */
	void evictObjectFromCache(final Persistable object);

	/**
	 * Clear the cache completely.
	 */
	void clearCache();

	/**
	 * Indicate whether caching is enabled or not.
	 * 
	 * @return true if the data cache is enabled
	 */
	boolean isCacheEnabled();

	/**
	 * Indicate to the persistence engine that the current transaction
	 * is large so it will treat caching appropriately.
	 * 
	 * @param largeTransaction set the large transaction mode
	 */
	void setLargeTransaction(final boolean largeTransaction);

	/**
	 * Detach the specified object from the entity manager.
	 * 
	 * @param <T> the type of the object
	 * @param object the object to detach
	 * @return the detached instance
	 */
	<T> T detach(T object);


}
