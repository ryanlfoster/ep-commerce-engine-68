/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.persistence.api.PersistenceEngine;

import java.util.Collection;

/**
 * <code>EpPersistenceService</code> serves as the base interface for all services manipulating persistable domain models.
 */
public interface EpPersistenceService extends EpService {

	/**
	 * Sets the persistence engine.
	 *
	 * @param persistenceEngine the persistence engine to set.
	 */
	void setPersistenceEngine(PersistenceEngine persistenceEngine);

	/**
	 * Returns the persistence engine.
	 *
	 * @return the persistence engine.
	 */
	PersistenceEngine getPersistenceEngine();

	/**
	 * Get a persistent instance with the given id.
	 *
	 * @param uid the persistent instance uid
	 * @return the persistent instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Object getObject(long uid) throws EpServiceException;

	/**
	 * Get a persistent instance with the given id.
	 *
	 * @param uid the persistent instance uid
	 * @param fieldsToLoad the fields of this object that need to be loaded
	 * @return the persistent instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Object getObject(long uid, Collection<String> fieldsToLoad) throws EpServiceException;
}
