/**
 * 
 */
package com.elasticpath.persistence.openjpa;

import com.elasticpath.persistence.api.PersistenceSession;

import javax.persistence.EntityManager;

/**
 * Persistence session with an entity manager.
 */
public interface JpaPersistenceSession extends PersistenceSession {

	/**
	 * Get the Entity Manager.
	 *
	 * @return the EntityManager
	 */
	EntityManager getEntityManager();
	
}
