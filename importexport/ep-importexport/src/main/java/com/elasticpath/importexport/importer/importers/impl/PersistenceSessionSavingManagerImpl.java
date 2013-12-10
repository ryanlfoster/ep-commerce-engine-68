package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * Implementation of <code>SavingManager</code> interface with <code>PersistenceEngine</code>.
 */
public class PersistenceSessionSavingManagerImpl implements SavingManager<Persistable> {
	
	private PersistenceEngine persistenceEngine;

	@Override
	public void save(final Persistable persistable) {
		persistenceEngine.save(persistable);		
	}

	@Override
	public Persistable update(final Persistable persistable) {
		return persistenceEngine.update(persistable);
	}

	/**
	 * Gets the persistenceEngine.
	 * 
	 * @return the persistenceEngine
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 * Sets the persistenceEngine.
	 * 
	 * @param persistenceEngine the persistenceEngine to set
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

}
