/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.persistence.openjpa.impl;

import javax.persistence.EntityManager;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.Query;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.persistence.openjpa.JpaPersistenceSession;

/**
 * The JPA implementation of a <code>Session</code> in ElasticPath. It is a wrap of <code>javax.persistence.EntityManager</code>.
 */
public class JpaSessionImpl implements JpaPersistenceSession {

	private final EntityManager session;
	private final PlatformTransactionManager txManager;
	private final boolean sharedSession;
	
	/**
	 * Begins a transaction.
	 *
	 * @return returns a transaction
	 * @throws EpPersistenceException in case of any error
	 */
	public Transaction beginTransaction() throws EpPersistenceException {
		// Use the Spring defined transaction manager to ensure services retrieved from Spring
		// are work within the same transaction used by the importer.
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		// explicitly setting the transaction name is something that can only be done programmatically
		def.setName("ExplicitlyCreatedTransaction");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		
		TransactionStatus txStatus = txManager.getTransaction(def);

		// join is not allowed on shared sessions
		if (!sharedSession) {
			// Tell the EntityManager to join the newly created transaction. 
			// Otherwise it will think it is outside of any existing transaction 
			// and will not be able to work properly with the data source. 
			this.session.joinTransaction();
		}
		
		return new JpaTransactionImpl(txManager, txStatus);
	}

	/**
	 * Close the session.
	 *
	 * @throws EpPersistenceException in case of any error
	 */
	public void close() throws EpPersistenceException {
		session.close();
	}

	/**
	 * Update the given object.
	 *
	 * @param <T> the type of the object being saved/returned.
	 * @param object the object
	 * @return the updated persistent object.
	 * @throws EpPersistenceException in case of any error
	 */
	@SuppressWarnings("unchecked")
	public <T extends Persistable> T update(final Persistable object) throws EpPersistenceException {
		return (T) session.merge(object);
	}

	/**
	 * Save the given object.
	 *
	 * @param object the object
	 * @throws EpPersistenceException in case of any error
	 */
	public void save(final Persistable object) throws EpPersistenceException {
		session.persist(object);
	}

	/**
	 * The default constructor.
	 *
	 * @param session the JPA session (<code>EntityManager</code>) to wrap
	 * @param txManager the transaction manager to be used to manage custom created transactions
	 * @param sharedSession identifies if the session is shared 
	 */
	public JpaSessionImpl(final EntityManager session, final PlatformTransactionManager txManager, final boolean sharedSession) {
		super();
		if (session == null) {
			throw new EpPersistenceException("Session cannot be set to null!");
		}
		if (txManager == null) {
			throw new EpPersistenceException("Transaction manager cannot be null.");
		}
		this.session = session;
		this.txManager = txManager;
		this.sharedSession = sharedSession;
	}

	/**
	 * Creates and returns a query based on the given query string.
	 *
	 * @param queryString the query string
	 * @return a query
	 * @throws EpPersistenceException in case of any error
	 */
	public Query createQuery(final String queryString) throws EpPersistenceException {
		final javax.persistence.Query query = this.session.createQuery(queryString);
		return new JpaQueryImpl(query);
	}

	/**
	 * Creates and returns a query based on the given query string.
	 *
	 * @param queryString the query string
	 * @return a query
	 * @throws EpPersistenceException in case of any error
	 */
	public Query createSQLQuery(final String queryString) throws EpPersistenceException {
		final javax.persistence.Query query = this.session.createNativeQuery(queryString);
		return new JpaQueryImpl(query);
	}

	/**
	 * Creates and returns a query based on the given named query.
	 *
	 * @param queryName the named query
	 * @return a query
	 * @throws EpPersistenceException in case of any error
	 */
	public Query createNamedQuery(final String queryName) throws EpPersistenceException {
		final javax.persistence.Query query = this.session.createNamedQuery(queryName);
		return new JpaQueryImpl(query);
	}

	/**
	 * Get the underlying EntityManager session object.
	 * @return the EntityManager
	 */
	public EntityManager getEntityManager() {
		return this.session;
	}
}
