package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.persistence.api.Persistable;

/**
 * Interface for listener that can be added to saving strategy to inspect saving processing.
 */
public interface LifecycleListener {
	/**
	 * Calls before populate action, when object has already been created.
	 * 
	 * @param persistable the persistable object that will be saved to data base
	 */
	void beforePopulate(final Persistable persistable);
	
	/**
	 * Calls before save action, when object has already populated.
	 * 
	 * @param persistable the persistable object that will be saved to data base
	 */
	void beforeSave(final Persistable persistable);
	
	/**
	 * Calls after save action, when object has alredy saved to data base.
	 * 
	 * @param persistable the persistent object
	 */
	void afterSave(final Persistable persistable);

}
