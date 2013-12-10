package com.elasticpath.tools.sync.configuration.dao;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import com.elasticpath.persistence.openjpa.JpaPersistenceEngine;

/**
 * Initializes the {@link JpaPersistenceEngine}.
 */
public class PersistenceEngineInitialization {
	
	private JpaPersistenceEngine persistenceEngine;
	private FlushModeType flushModeType;

	/**
	 * Initializes the {@link JpaPersistenceEngine}.  Called on initialization of the spring context.
	 */
	public void init() {
		final EntityManager entityManager = persistenceEngine.getEntityManager();
		entityManager.setFlushMode(flushModeType);
	}
	
	/**
	 * Sets the {@link JpaPersistenceEngine}.
	 *
	 * @param persistenceEngine the {@link JpaPersistenceEngine}.
	 */
	public void setPersistenceEngine(final JpaPersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * Sets the {@link FlushModeType}.
	 *
	 * @param flushModeType the {@link FlushModeType}
	 */
	public void setFlushModeType(final FlushModeType flushModeType) {
		this.flushModeType = flushModeType;
	}
}
