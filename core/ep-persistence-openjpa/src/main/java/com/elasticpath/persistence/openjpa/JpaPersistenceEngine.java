/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.persistence.openjpa;

import com.elasticpath.persistence.api.PersistenceEngine;
import org.apache.openjpa.kernel.Broker;

import javax.persistence.EntityManager;


/**
 * JPA Specific persistence engine methods.
 */
public interface JpaPersistenceEngine extends PersistenceEngine {

	/**
	 * Get the Entity Manager.
	 *
	 * @return the EntityManager
	 */
	EntityManager getEntityManager();
	
	/**
	 * Get the OpenJPA Broker.
	 *
	 * @return the broker
	 */
	Broker getBroker();
}
