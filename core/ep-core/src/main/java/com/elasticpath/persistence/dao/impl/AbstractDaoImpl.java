/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.persistence.dao.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * Defines methods useful for all persistable DAO classes.
 */
public class AbstractDaoImpl {

	private PersistenceEngine persistenceEngine;
	private BeanFactory beanFactory;
	private final PersistentBeanFinder persistentBeanFinder = new PersistentBeanFinder();
	
	/**
	 * Helper for finding persistent beans which determines the bean implementation
	 * class from a bean factory and then delegates the loading to a 
	 * {@link PersistenceEngine}.
	 */
	public final class PersistentBeanFinder {
		
		/**
		 * Private constructor to prevent external instantiation.
		 */
		PersistentBeanFinder() {
			// Prevent external instantiation.
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
			return getPersistenceEngine().get(beanFactory.<T>getBeanImplClass(beanName), uidPk);
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
			return getPersistenceEngine().load(beanFactory.<T>getBeanImplClass(beanName), uidPk);
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
			return getPersistenceEngine().loadWithNewSession(beanFactory.<T>getBeanImplClass(beanName), uidPk);		
		}	
	}

	/**
	 * Convenience method for getting a bean instance.
	 * @param <T> the type of bean to return
	 * @param beanName the name of the bean to get and instance of.
	 * @return an instance of the requested bean.
	 */
	public <T> T getBean(final String beanName) {
		return beanFactory.<T>getBean(beanName);
	}
	
	/**
	 * Convenience method for getting a bean implementation class.
	 * 
	 * @param <T> the type of bean to return 
	 * @param beanName the name of the bean
	 * @return the implementation class of the bean
	 */
	public <T> Class<T> getBeanImplClass(final String beanName) {
		return beanFactory.<T>getBeanImplClass(beanName);
	}

	/**
	 * Gets the persistence engine.
	 * 
	 * @return The persistence engine.
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 * Return a convenience class for retrieving persistent bean instances from 
	 * their bean name, not their implementation class.
	 * 
	 * @return a finder for persistent beans.
	 */
	public PersistentBeanFinder getPersistentBeanFinder() {
		return persistentBeanFinder;
	}

	/**
	 * Sets the bean factory object.
	 * 
	 * @param beanFactory the bean factory instance.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Sets the persistence engine to use.
	 *  
	 * @param persistenceEngine The persistence engine.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

}
