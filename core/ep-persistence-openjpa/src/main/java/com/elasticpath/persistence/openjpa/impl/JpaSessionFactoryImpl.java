/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.persistence.openjpa.impl;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.apache.openjpa.persistence.OpenJPAEntityManagerFactorySPI;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.springframework.transaction.PlatformTransactionManager;

import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.PersistenceSessionFactory;

/**
 * The JPA implementation of the persistence session factory interface. This is a wrapper around the JPA EntityManagerFactory.
 */
public class JpaSessionFactoryImpl implements PersistenceSessionFactory {

	private EntityManagerFactory entityManagerFactory;
	private Map<String, Object> propertiesMap;
	private PlatformTransactionManager txManager;


	/**
	 * Close the factory, releasing any resources that it holds.
	 */
	public void close() {
		getEntityManagerFactory().close();
	}

	/**
	 * Create a new <code>PersistenceSession</code>. This method returns a new <code>PersistenceSession</code> instance.
	 * @return a new <code>PersistenceSession</code> instance.
	 */
	public PersistenceSession createPersistenceSession() {
		return new JpaSessionImpl(getEntityManagerFactory().createEntityManager(propertiesMap), txManager, false);
	}

	/**
	 * Return the map of properties used for sessions created by this factory.
	 * @return the properties
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getProperties() {
		if (this.propertiesMap == null) {
			OpenJPAEntityManagerFactorySPI emf = (OpenJPAEntityManagerFactorySPI) OpenJPAPersistence.cast(getEntityManagerFactory());
			return emf.getConfiguration().toProperties(true);
		}
		return propertiesMap;
	}

	/**
	 * Set the map of properties to be used for sessions created by this factory.
	 * @param properties the map of properties
	 */
	public void setProperties(final Map<String, Object> properties) {
		this.propertiesMap = properties;
	}

	/**
	 * Indicates whether the factory is open.
	 * @return true until a call to close has been made
	 */
	public boolean isOpen() {
		return getEntityManagerFactory().isOpen();
	}

	/**
	 * Get the underlying EntityManagerFactory object.
	 * @return the EntityManagerFactory
	 */
	public EntityManagerFactory getSessionFactory() {
		return getEntityManagerFactory();
	}

	/**
	 * Get the connection driver name.
	 * @return the connectionDriverName
	 */
	public String getConnectionDriverName() {
		OpenJPAEntityManagerFactorySPI emf = (OpenJPAEntityManagerFactorySPI) OpenJPAPersistence.cast(getEntityManagerFactory());
		return emf.getConfiguration().getConnectionDriverName();
	}

	/**
	 * Set the connection driver name.
	 * @param connectionDriverName the connectionDriverName to set
	 */
	public void setConnectionDriverName(final String connectionDriverName) {
		OpenJPAEntityManagerFactorySPI emf = (OpenJPAEntityManagerFactorySPI) OpenJPAPersistence.cast(getEntityManagerFactory());
		emf.getConfiguration().setConnectionDriverName(connectionDriverName);
	}

	/**
	 * Get the connection factory name.
	 * @return the connectionFactoryNameSPI
	 */
	public String getConnectionFactoryName() {
		OpenJPAEntityManagerFactorySPI emf = (OpenJPAEntityManagerFactorySPI) OpenJPAPersistence.cast(getEntityManagerFactory());
		return emf.getConfiguration().getConnectionFactoryName();
	}

	/**
	 * Set the connection factory name.
	 * @param connectionFactoryName the connectionFactoryName to set
	 */
	public void setConnectionFactoryName(final String connectionFactoryName) {
		OpenJPAEntityManagerFactorySPI emf = (OpenJPAEntityManagerFactorySPI) OpenJPAPersistence.cast(getEntityManagerFactory());
		emf.getConfiguration().setConnectionFactoryName(connectionFactoryName);
	}

	/**
	 * Get the connection password.
	 * @return the connectionPassword
	 */
	public String getConnectionPassword() {
		OpenJPAEntityManagerFactorySPI emf = (OpenJPAEntityManagerFactorySPI) OpenJPAPersistence.cast(getEntityManagerFactory());
		return emf.getConfiguration().getConnectionPassword();
	}

	/**
	 * Set the connection password.
	 * @param connectionPassword the connectionPassword to set
	 */
	public void setConnectionPassword(final String connectionPassword) {
		OpenJPAEntityManagerFactorySPI emf = (OpenJPAEntityManagerFactorySPI) OpenJPAPersistence.cast(getEntityManagerFactory());
		emf.getConfiguration().setConnectionPassword(connectionPassword);
	}

	/**
	 * Get the connection URL.
	 * @return the connectionURL
	 */
	public String getConnectionURL() {
		OpenJPAEntityManagerFactorySPI emf = (OpenJPAEntityManagerFactorySPI) OpenJPAPersistence.cast(getEntityManagerFactory());
		return emf.getConfiguration().getConnectionURL();
	}

	/**
	 * Set the connection URL.
	 * @param connectionURL the connectionURL to set
	 */
	public void setConnectionURL(final String connectionURL) {
		OpenJPAEntityManagerFactorySPI emf = (OpenJPAEntityManagerFactorySPI) OpenJPAPersistence.cast(getEntityManagerFactory());
		emf.getConfiguration().setConnectionURL(connectionURL);
	}

	/**
	 * Get the connection user name.
	 * @return the connectionUserName
	 */
	public String getConnectionUserName() {
		OpenJPAEntityManagerFactorySPI emf = (OpenJPAEntityManagerFactorySPI) OpenJPAPersistence.cast(getEntityManagerFactory());
		return emf.getConfiguration().getConnectionUserName();
	}

	/**
	 * Set the connection user name.
	 * @param connectionUserName the connectionUserName to set
	 */
	public void setConnectionUserName(final String connectionUserName) {
		OpenJPAEntityManagerFactorySPI emf = (OpenJPAEntityManagerFactorySPI) OpenJPAPersistence.cast(getEntityManagerFactory());
		emf.getConfiguration().setConnectionUserName(connectionUserName);
	}

	/**
	 * Get the entity manager factory.
	 * @return the entityManagerFactory
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	/**
	 * Set the entity manager factory.
	 * @param entityManagerFactory the entityManagerFactory to set
	 */
	public void setEntityManagerFactory(final EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	/**
	 * Sets the transaction manager.
	 * 
	 * @param txManager the transaction manager
	 */
	public void setTransactionManager(final PlatformTransactionManager txManager) {
		this.txManager = txManager;
	}

}
