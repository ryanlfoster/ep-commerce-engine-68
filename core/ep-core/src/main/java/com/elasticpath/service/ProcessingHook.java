package com.elasticpath.service;

import com.elasticpath.persistence.api.Persistable;

/**
 * A processing hook provides operations for specific operations within a service.
 */
public interface ProcessingHook {

	/**
	 * Perform operations with the given {@link Persistable object} <i>before</i> persisting. This should
	 * only be called for operations where the object has not been previously persisted.
	 * 
	 * @param domain the object that is to be persisted
	 */
	void preAdd(Persistable domain);
	
	/**
	 * Perform operations with the given {@link Persistable object} <i>after</i> persisting. This should
	 * only be called for operations where the object has not been previously persisted.
	 * 
	 * @param domain the object that is to be persisted
	 */
	void postAdd(Persistable domain);
	
	/**
	 * Perform operations with the given {@link Persistable object}s <i>before</i> updating.
	 * This should only be done for objects that have been previously persisted. It is up to the
	 * service to provide a valid {@code oldObject} or {@code null} if the old object
	 * cannot/should not be obtained.
	 * 
	 * @param oldObject the old object
	 * @param newObject the new object
	 */
	void preUpdate(Persistable oldObject, Persistable newObject);
	
	/**
	 * Perform operations with the given {@link Persistable object}s <i>after</i> updating. This should
	 * only be done for objects that have been previously persisted. It is up to the
	 * service to provide a valid {@code oldObject} or {@code null} if the old object
	 * cannot/should not be obtained.
	 * 
	 * @param oldObject the old object
	 * @param newObject the new object
	 */
	void postUpdate(Persistable oldObject, Persistable newObject);
	
	/**
	 * Perform operations with the given {@link Persistable object} <i>before</i> deletion.
	 *
	 * @param object the object that is to be deleted
	 */
	void preDelete(Persistable object);
	
	/**
	 * Perform operations with the given {@link Persistable object} <i>after</i> deletion.
	 *
	 * @param object the object that is to be deleted
	 */
	void postDelete(Persistable object);
}
