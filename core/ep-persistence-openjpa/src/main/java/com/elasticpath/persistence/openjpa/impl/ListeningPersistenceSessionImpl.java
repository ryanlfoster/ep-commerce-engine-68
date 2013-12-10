package com.elasticpath.persistence.openjpa.impl;

import javax.persistence.EntityManager;

import org.springframework.transaction.PlatformTransactionManager;

import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.openjpa.ChangeType;
import com.elasticpath.persistence.openjpa.JpaPersistenceEngineInternal;

/**
 * Extends {@code JpaSessionIMpl} in order to ensure that the the listening functionality of
 * {@code JpaPersistenceEngine} is used.<br/>
 * Note that {@code createNamedQuery}, {@code createQuery} and {@code createSQLQuery} are not listened to as it is 
 * currently not required and it is unclear when to call {@code endBulkOperation}.
 */
public class ListeningPersistenceSessionImpl extends JpaSessionImpl {
	
	private final JpaPersistenceEngineInternal jpaPersistenceEngine;
	
	/**
	 * Normal constructor. Default constructor is not useful.
	 * @param session the JPA session (<code>EntityManager</code>) to wrap
	 * @param txManager the transaction manager to be used to manage custom created transactions
	 * @param sharedSession identifies if the session is shared 
	 * @param jpaPersistenceEngine The persistenceEngine to use to signal that listening should begin or end.
	 */
	public ListeningPersistenceSessionImpl(final EntityManager session,
			final PlatformTransactionManager txManager,
			final boolean sharedSession,
			final JpaPersistenceEngineInternal jpaPersistenceEngine) {
		super(session, txManager, sharedSession);
		this.jpaPersistenceEngine = jpaPersistenceEngine;
	}

	@Override
	public void save(final Persistable object) throws EpPersistenceException {
		jpaPersistenceEngine.beginSingleOperation(object, ChangeType.CREATE);
		super.save(object);
		jpaPersistenceEngine.endSingleOperation(object, ChangeType.CREATE);
	}

	@Override
	public <T extends Persistable> T update(final Persistable object)
			throws EpPersistenceException {
		jpaPersistenceEngine.beginSingleOperation(object, ChangeType.UPDATE);
		T returnValue = super.update(object);
		jpaPersistenceEngine.endSingleOperation(object, ChangeType.UPDATE);
		return returnValue;
	}	
}
