package com.elasticpath.service.impl;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * <code>AbstractEpPersistenceServiceImpl</code> is abstract implementation of the base interface for
 * other services of the persistable domain models.
 */
public abstract class AbstractEpPersistenceServiceImpl extends AbstractEpServiceImpl implements EpPersistenceService {
	private static final Logger LOG = Logger.getLogger(AbstractEpPersistenceServiceImpl.class);
	private PersistenceEngine persistenceEngine;
	private final PersistentBeanFinder persistentBeanFinder = new PersistentBeanFinder();

	/**
	 * Sets the persistence engine.
	 *
	 * @param persistenceEngine the persistence engine to set.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Persistence engine initialized ... " + persistenceEngine);
		}
	}

	/**
	 * Returns the persistence engine.
	 *
	 * @return the persistence engine.
	 */
	public PersistenceEngine getPersistenceEngine() {
		return this.persistenceEngine;
	}

	/**
	 * Sanity check of this service instance.
	 * @throws EpServiceException - if something goes wrong.
	 */
	protected void sanityCheck() throws EpServiceException {
		if (getPersistenceEngine() == null) {
			throw new EpServiceException("The persistence engine is not correctly initialized.");
		}
	}

	/**
	 * Load method for all persistable domain models specifying fields to be loaded.
	 * By default, just calls the generic load method.
	 *
	 * @param uid the persisted instance uid
	 * @param fieldsToLoad the fields of this object that need to be loaded
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Object getObject(final long uid, final Collection<String> fieldsToLoad) throws EpServiceException {
		return getObject(uid);
	}
	
	/**
	 * Return a convience class for retrieving persistent bean instances from 
	 * their bean name, not their implementation class.
	 * 
	 * @return a finder for persistent beans.
	 */
	protected PersistentBeanFinder getPersistentBeanFinder() {
		return persistentBeanFinder;
	}
	
	/**
	 * Helper for finding persistent beans which determines the bean implementation
	 * class from a bean factory and then delegates the loading to a 
	 * {@link PersistenceEngine}.
	 */
	protected final class PersistentBeanFinder {
		
		/**
		 * Private constructor to prevent external instantiation.
		 */
		PersistentBeanFinder() {
			// Prevent external instantiation.
		}
		
		/**
		 * Load a persistent instance with a given id.  The persistent class to 
		 * load will be determined from the beanName
		 * Throw an unrecoverable exception if there is no matching database row.
		 *
		 * @param <T> the type of the object
		 * @param beanName the name of the bean to find the implementation class for.
		 * @param uidPk the persistent instance id.
		 * @return the persistent instance
		 * @throws com.elasticpath.persistence.api.EpPersistenceException - in case of persistence errors
		 */
		public <T extends Persistable> T load(final String beanName, final long uidPk) {
			return getPersistenceEngine().load(getElasticPath().<T>getBeanImplClass(beanName), uidPk);
		}
		
		/**
		 * Load a persistent instance with the given id. Throw an unrecoverable exception if there is
		 * no matching database row. This method will create a new session (EntityManager) to execute
		 * the query, and close the new session when completed.
		 * 
		 * @param <T> the type of the object
		 * @param beanName the name of the bean to find the implementation class for.
		 * @param uidPk the persistent instance id.
		 * @return the persistent instance
		 * @throws com.elasticpath.persistence.api.EpPersistenceException in case of persistence errors
		 */
		public <T extends Persistable> T loadWithNewSession(final String beanName, final long uidPk) {
			return getPersistenceEngine().loadWithNewSession(getElasticPath().<T>getBeanImplClass(beanName), uidPk);		
		}
		
		/**
		 * Get a persistent instance with the given id. Return null if no matching record exists.
		 *
		 * @param <T> the type of the object
		 * @param beanName the name of the bean to find the implementation class for.
		 * @param uidPk the persistent instance id.
		 * @return the persistent instance
		 * @throws com.elasticpath.persistence.api.EpPersistenceException - in case of persistence errors
		 */
		public <T extends Persistable> T get(final String beanName, final long uidPk) {
			return getPersistenceEngine().get(getElasticPath().<T>getBeanImplClass(beanName), uidPk);
		}

	}
	
	
}
