package com.elasticpath.test.persister;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.elasticpath.domain.ElasticPath;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.test.common.exception.TestApplicationException;
import com.elasticpath.test.persister.database.DataSourceConfiguration;
import com.elasticpath.test.persister.database.DataSourceInitializer;

/**
 * Provides support for managing multiple database connection and configuration.
 */
public class MultiDatabaseManager {

	private static final Logger LOG = Logger.getLogger(MultiDatabaseManager.class);

	private final Object lock = new Object();
	private Map<String, DBConfig> databases = new HashMap<String, DBConfig>();
	private DataSourceInitializer currentInitializer;
	private TransactionTemplate txTemplate;

	@Autowired
	protected TestConfig testConfig;
	
	@Autowired
	protected PersistenceEngine persistenceEngine;

	@Autowired
	protected JpaTransactionManager transactionManager;

	@Autowired
	protected ElasticPath elasticPath;

	@Autowired
	protected ConfigurableBeanFactory beanFactory;
	
	@Autowired
	protected DynamicDataSourceFactory dataSourceFactory;
	
	@Autowired
	private DatasourceInitializerFactory dataSourceInitializerFactory;
	
	static class DBConfig {
		private DataSourceInitializer initializer;
		private boolean shouldReinitialize = true;

		public DataSourceInitializer getInitializer() {
			return initializer;
		}

		public void setInitializer(DataSourceInitializer initializer) {
			this.initializer = initializer;
		}

		public boolean shouldReinitialize() {
			return shouldReinitialize;
		}

		public void setShouldReinitialize(boolean shouldReinitialize) {
			this.shouldReinitialize = shouldReinitialize;
		}

	}

	/* package */MultiDatabaseManager() {
		// static class - package for tests
	}

	/**
	 * Prepares a database for use by dropping (if necessary) and recreating the
	 * base schema/data. On multiple calls, preparation will happen once. Use
	 * {@link #resetDatabase(String)} to cause multiple preparations.
	 * 
	 * @param name
	 *            database name to use
	 * @param reinitializeDb 
	 * @return the created {@link DataSourceInitializer}
	 * @throws IllegalArgumentException
	 *             if {@code name} is {@code null}
	 * @see #resetDatabase(String)
	 */
	public DataSourceInitializer prepareDatabase(final String name, Boolean reinitializeDb) {
		Properties dbProperties = getPropertiesForName(name);
		return prepareDatabase(dbProperties, reinitializeDb);
		}

	public Properties getPropertiesForName(final String name) {
		TestConfig config = getTestConfig();
		Properties dbProperties = config.getDatabaseProperties();
		dbProperties.setProperty(TestConfig.PROPERTY_DB_NAME, name);
		return dbProperties;
	}

	/**
	 * Prepares a database for use by dropping (if necessary) and recreating the
	 * base schema/data. On multiple calls, preparation will happen once. Use
	 * {@link #resetDatabase(String)} to cause multiple preparations. THe
	 * properties that should be defined are all useful database properties in
	 * {@link TestConfig}.
	 * 
	 * @param properties
	 *            properties to use
	 * @return the created {@link DataSourceInitializer}
	 * @throws IllegalArgumentException
	 *             if {@code properties} or the database name (property defined
	 *             by {@link TestConfig#PROPERTY_DB_NAME}) is {@code null}
	 * @see #resetDatabase(String)
	 */
	public DataSourceInitializer prepareDatabase(final Properties properties, Boolean reinitializeDb) {
		synchronized (lock) {
			DataSourceInitializer initializer = prepareEmptyDatabaseConfiguration(properties);
			String name = properties.getProperty(TestConfig.PROPERTY_DB_NAME);
			DBConfig dbConfig = databases.get(name);
			reloadDatabase(name, dbConfig, reinitializeDb);
			return initializer;
		}
	}
	public DataSourceInitializer prepareEmptyDatabaseConfiguration(final String dbname) {
		Properties dbProperties = getPropertiesForName(dbname);
		return prepareEmptyDatabaseConfiguration(dbProperties);
	}
	public DataSourceInitializer prepareEmptyDatabaseConfiguration(final Properties properties) {
		if (properties == null) {
			throw new IllegalArgumentException("properties is null");
		}

		String name = properties.getProperty(TestConfig.PROPERTY_DB_NAME);
		if (name == null) {
			throw new IllegalArgumentException("no database name");
		}

		synchronized (lock) {
			DBConfig dbConfig = databases.get(name);
			if (dbConfig == null) { // don't prepare twice
				dbConfig = createDBConfig();
				dbConfig.setInitializer(createInitializer(properties));
				databases.put(name, dbConfig);
				// HACK: Really should just call setDatabaseTo.
				setCurrentDatabase(dbConfig.getInitializer());
			}

			return dbConfig.getInitializer();
			}
			}

	private void reloadDatabase(final String name, DBConfig dbConfig, Boolean reinitializeDb) {
		if (dbConfig.shouldReinitialize() && BooleanUtils.isTrue(reinitializeDb)) {
			LOG.debug(String.format("Reloading database [%s]...", name));
			initialize(name, dbConfig.getInitializer(), dbConfig);
			LOG.debug(String.format("Completed reloading database [%s]", name));
			dbConfig.setShouldReinitialize(false);
		}
	}

	private void initialize(final String name,
			DataSourceInitializer initializer, DBConfig dst) {
		initializer.dropAndCreateDatabase();

		// some databases require a new connection when the database is
		// recreated
		dataSourceFactory.reset(initializer);

		initializeCoreSchema();
		initializeExtensionSchema();
	}

	/**
	 * Sets up the database context to the given database name which may involve
	 * preparing the database (automatically) if it hasn't been used before.
	 * 
	 * @param name
	 *            database name to set context to
	 * @param reinitializeDb whether or not to reinitialize the database
	 * @throws IllegalArgumentException
	 *             if {@code name} is {@code null}
	 */
	public void setDatabaseTo(final String name, Boolean reinitializeDb) {
		if (name == null) {
			throw new IllegalArgumentException("name is null");
		}

		LOG.trace(String.format("Configuring database [%s]...", name));
		
		DBConfig dbConfig;
		synchronized (lock) {
			dbConfig = databases.get(name);
			if (dbConfig == null) {
				prepareDatabase(name, reinitializeDb);
				dbConfig = databases.get(name);
		}
		}

		setCurrentDatabase(dbConfig.getInitializer());
		clear();

		LOG.trace(String.format("Completed configuring database [%s]", name));
	}

	/**
	 * Resets the given database context name allowing the database to be
	 * prepared again.
	 * 
	 * @param name
	 *            database name
	 */
	public void resetDatabase(final String name) {
		DBConfig initailizer = databases.get(name);
		if (initailizer != null) {
			initailizer.setShouldReinitialize(true);
		}
	}

	private void setCurrentDatabase(final DataSourceInitializer initializer) {
		// needs to happen all the time to handle the first currentInitializer
		// set (when object first created)
		currentInitializer = initializer;
		dataSourceFactory.reset(initializer);
	}

	public DataSourceInitializer getInitializer() {
		return currentInitializer;
	}

	private void clear() {
		if (persistenceEngine.isCacheEnabled()) {
			persistenceEngine.clearCache();
		}
	}

	/**
	 * @return last initialized transaction template
	 */
	protected TransactionTemplate getTransactionTemplate() {
		if (txTemplate == null) {
			txTemplate = new TransactionTemplate(transactionManager);
		}
		return txTemplate;
	}

	private void initializeCoreSchema() {
		if (beanFactory != null) {
		try {
			getBean("liquibaseCoreSchemaInitializerForTestApplication");
		} catch (final NoSuchBeanDefinitionException e) {
			throw new TestApplicationException(
					"core-schema's liquibase-for-testapplication.xml may not be in the classpath",
					e);
		}
	}
	}

	private void initializeExtensionSchema() {
		if (beanFactory != null) {
		try {
			getBean("schemaExtension");
		} catch (NoSuchBeanDefinitionException e) {
			LOG.debug("No extension schema found, ignoring");
		}
	}
	}

	/**
	 * @deprecated Wire dependencies on to this class properly please.
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	private <T> T getBean(final String name) {
		return (T) beanFactory.getBean(name);
	}

	private DataSourceInitializer createInitializer(
			final Properties dbProperties) {
		return dataSourceInitializerFactory.getInstance(dbProperties);
	}

	DBConfig createDBConfig() {
		return new DBConfig();
	}

	public TestConfig getTestConfig() {
		return testConfig;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final ConfigurableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	public DataSourceConfiguration prepareDatabase(Properties props) {
		return prepareDatabase(props, true);
	}
}
