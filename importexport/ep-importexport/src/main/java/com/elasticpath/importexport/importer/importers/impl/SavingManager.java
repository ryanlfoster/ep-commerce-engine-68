package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.persistence.api.Persistable;

/**
 * The saving manager interface. It provides methods for saving object to database, update and so on.
 *
 * @param <T> the Java type of instance to persist
 */
public interface SavingManager<T extends Persistable> {
	
	/**
	 * Saves the given object.
	 *
	 * @param persistable the object
	 */
	void save(final T persistable);
	
	/**
	 * Update the given object.
	 *
	 * @param persistable the object
	 * @return the updated persistable object.
	 */
	T update(final T persistable);

}
