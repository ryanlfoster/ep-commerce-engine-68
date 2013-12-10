/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.tools.sync.beanfactory.local;

import com.elasticpath.persistence.api.PersistenceSessionFactory;

/**
 * Initialize local JPA configuration. This will be called by spring as a dependency of the persistence engine to ensure the datasource is changed
 * before anything can use the persistence engine.
 */
public class LocalPersistenceEngineInitialization {
	
	private PersistenceSessionFactory sessionFactory;
	
	private DataSourceProperties dataSourceProperties;

	/**
	 * Initialize the datasource from the set properties.
	 */
	public void init() {
		// set the factory name to be null in order to avoid its creation
		sessionFactory.setConnectionFactoryName(null);
		
		// update the properties onto the data source class. 
		// They will be taken in consideration when the data source is created.
		JPADataSource.setDataSourceProperties(dataSourceProperties);
		
		sessionFactory.setConnectionDriverName(JPADataSource.class.getName());
	}

	/**
	 * 
	 * @param sessionFactory persistence factory to initialize
	 */
	public void setSessionFactory(final PersistenceSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 *
	 * @param dataSourceProperties the dataSourceProperties to set
	 */
	public void setDataSourceProperties(final DataSourceProperties dataSourceProperties) {
		this.dataSourceProperties = dataSourceProperties;
	}

}