/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.importexport.client;

import java.util.Properties;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.persistence.api.PersistenceSessionFactory;

/**
 * Initialize the import/export client. This will be called by spring as a dependency 
 * of the persistence engine to ensure the datasource is changed before anything
 * can use the persistence engine.
 */
public class ImportExportInitialization {

	private BeanFactory beanFactory;
	
	private Properties appProperties;
	
	/**
	 * Initialize the datasource from the set properties.
	 */
	public void init() {
		DataSourceProperties dataSourceProperties = DataSourceProperties.getInstance();
		dataSourceProperties.setProperties(appProperties);
		PersistenceSessionFactory sessionFactory = getBeanFactory().getBean("sessionFactory");

		sessionFactory.setConnectionFactoryName(null);
		sessionFactory.setConnectionDriverName(JPADataSource.class.getName());

	}

	/**
	 * Get the Bean Factory to use for gettings beans.
	 * 
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * Set the bean factory to be used for getting beans. This is normally injected by Spring.
	 * 
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Get the application properties collection.
	 * 
	 * @return the appProperties
	 */
	public Properties getAppProperties() {
		return appProperties;
	}

	/**
	 * Set the application properties, this can be injected by Spring using the PropertiesFactoryBean class
	 * to read from a file.
	 * 
	 * @param appProperties the appProperties to set
	 */
	public void setAppProperties(final Properties appProperties) {
		this.appProperties = appProperties;
	}
	
	

}
