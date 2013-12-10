/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.test.persister.MultiDatabaseManager;
import com.elasticpath.test.persister.TestConfig;
import com.elasticpath.test.persister.database.DataSourceInitializer;

/**
 * Utility for reseting database to support testing. {@link MultiDatabaseManager}.
 */
public class DatabaseResetUtil {
	
	private static final Logger LOG = Logger.getLogger(DatabaseResetUtil.class);
	
	/**
	 * Reset database. 
	 * @param applicationContext
	 */
	public void resetDatabase(final BeanFactory beanFactory, final String name) {
		LOG.info(String.format("Resetting database '%s'", name));
		MultiDatabaseManager dbManager = beanFactory.getBean(MultiDatabaseManager.class);
		dbManager.resetDatabase(name);
		dbManager.prepareDatabase(name, true);
		LOG.info("Resetting database done.");

		clearCaches(beanFactory);
		
		dbManager.setDatabaseTo(name, true);
	}
	
	/**
	 * Recreate database.
	 *
	 * @param beanFactory the bean factory
	 */
	public void recreateDatabase(final BeanFactory beanFactory) {
		LOG.info("Dropping database...");
		MultiDatabaseManager dbManager = beanFactory.getBean(MultiDatabaseManager.class);
		TestConfig config = beanFactory.getBean(TestConfig.class);
		
		DataSourceInitializer initializer = dbManager.getInitializer();
		if (initializer == null) {
			initializer = dbManager.prepareEmptyDatabaseConfiguration(config.getDbName());
		}
		initializer.dropAndCreateDatabase();
		
		clearCaches(beanFactory);
	}

	private void clearCaches(final BeanFactory beanFactory) {
		PersistenceEngine persistenceEngine = beanFactory.getBean("persistenceEngine", PersistenceEngine.class);
		LOG.trace("Clearing entity manager...");
		persistenceEngine.clear();
		LOG.trace("Clearing L2 cache...");
		persistenceEngine.clearCache();
	}


}
