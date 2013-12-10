/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.persistence.openjpa.support.impl;

import javax.persistence.EntityManagerFactory;

/**
 * Initialize OpenJPA so that it is ready to accept requests.
 * 
 * Normally OpenJPA doesn't get initialized until an Entity Manager call is made. This means
 * that when Spring creates the beans upon Spring initialization, OpenJPA is still not itself
 * initialized (unless coincidentally one of the Spring beans initialization makes a JPA call).
 * 
 * There are times we will want OpenJPA to be initialized when the spring context is started as
 * waiting until later may be too late. This class can be added as a spring bean to initialize
 * OpenJPA during spring initialization, as follows:
 * <pre>
 * 	<bean id="openjpaInitializer" class="com.elasticpath.persistence.openjpa.support.impl.OpenJPAInitializer" init-method="init">
 * 		<property name="entityManagerFactory" ref="entityManagerFactory"/>
 * 	</bean>
 * </pre>
 */
public class OpenJPAInitializer {

	private EntityManagerFactory entityManagerFactory;

	/**
	 * Initialize OpenJPA by forcing the EntityManagerFactory to create an entity manager.
	 * As we don't actually use that Entity Manager it can be closed immediately.
	 */
	void init() {
		getEntityManagerFactory().createEntityManager().close();
	}
	
	/**
	 * Set the EntityManagerFactory to initialize.
	 * 
	 * @param entityManagerFactory the entityManagerFactory to set
	 */
	public void setEntityManagerFactory(final EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	/**
	 * Get the entity manager factory.
	 * 
	 * @return the entityManagerFactory
	 */
	protected EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}
}
