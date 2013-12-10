package com.elasticpath.test.persister;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.elasticpath.test.common.exception.TestApplicationException;
import com.elasticpath.test.persister.TestConfigurationFactory.ResourceProvider;

/**
 * Handles loading/reading of test configuration. Configuration is held within {@code test_application.config} assumed
 * to be on the classpath. The file can be overridden by using the system property {@code config}.
 */
public class TestConfig {
	private static final Logger LOG = Logger.getLogger(TestConfig.class);

	/** Relative directory to the project for static runtime data. */
	public static final String PROPERTY_RUNNING_DIRECTORY = "runtime.directory";
	/** Assets runtime directory. */
	public static final String PROPERTY_ASSETS_DIR = "runtime.assets.dir";
	/** Boolean whether search is available. **/
	public static final String PROPERTY_SEARCH_AVAILABLE = "context.use_search";
	/** Database reset mode, one of {@link DbResetMode}. **/
	public static final String PROPERTY_DB_RESET_MODE = "context.db_reset_mode";
	/** Boolean whether a default database is used for tests. **/
	public static final String PROPERTY_USE_DEFAULT_DB = "context.use_default_db";
	/** Database default name. **/
	public static final String PROPERTY_DEFAULT_DB_NAME = "context.default_db_name";
	/** Database search home directory. **/
	public static final String PROPERTY_SEARCH_HOME_DIR = "context.search_home_dir";
	/** Database rdbms variable. **/
	public static final String PROPERTY_RDBMS = "db.rdbms";
	/** Database name. **/
	public static final String PROPERTY_DB_NAME = "db.name";
	/** Directory with the schema initialization scripts. */
	public static final String PROPERTY_DB_SCRIPT_DIR = "db.script.dir";
	/** Database connection host. **/
	public static final String PROPERTY_DB_CONNECTION_HOST = "db.connection.host";
	/** Database connection port. **/
	public static final String PROPERTY_DB_CONNECTION_PORT = "db.connection.port";
	/** Database connection username. **/
	public static final String PROPERTY_DB_CONNECTION_USERNAME = "db.connection.username";
	/** Database connection password. **/
	public static final String PROPERTY_DB_CONNECTION_PASSWORD = "db.connection.password";
	/** Database dba username. **/
	public static final String PROPERTY_DB_DBA_USERNAME = "db.connection.dbausername";
	/** Database dba password. **/
	public static final String PROPERTY_DB_DBA_PASSWORD = "db.connection.dbapassword";
	/** Database connection driver. **/
	public static final String PROPERTY_DB_CONNECTION_DRIVER_CLASS = "db.connection.driver_class";

	private Properties testConfig;
	private ResourceProvider resourceProvider;

	/** Database usage mode */
	public static enum DbResetMode {
		/** Reinstantiate on each db name. **/
		EACH_DB_NAME,
		/** Reinstantiate on each db use call. **/
		EACH_USE,
		/** Never. */
		NONE;
	}

	/**
	 * New {@link TestConfig} with the given {@link ResourceProvider}.
	 * 
	 * @param resourceProvider {@link ResourceProvider} for resources
	 */
	public TestConfig(final ResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
		testConfig = loadConfiguration(resourceProvider);
	}

	/** @deprecated Just create a new instance...
	 * TODO: Everyone and their dog currently holds this object.  Reduce coupling. */
	@Deprecated
	public void reloadConfiguration() {
		testConfig = loadConfiguration(resourceProvider);
	}

	@SuppressWarnings("PMD.AvoidReassigningParameters")
	private Properties loadConfiguration(ResourceProvider resourceProvider) {
		final Properties testConfigProperties = new Properties(System.getProperties());
		String testApplicationConfigFile = System.getProperty("config");
			if (testApplicationConfigFile == null) {
			testApplicationConfigFile = "test_application.config";
			resourceProvider = new TestConfigurationFactory.ClassPathResourceProvider();
			}
		try {
			LOG.debug(String.format("Loading EP integration test configuration [%s] using resource provider [%s]...",
					testApplicationConfigFile, resourceProvider));
			testConfigProperties.load(resourceProvider.getResource(testApplicationConfigFile).getInputStream());
			LOG.debug(String.format("Completed loading EP integration test configuration [%s] from the classpath",
					testApplicationConfigFile));
		} catch (final IOException e) {
			throw new TestApplicationException("Failed to load properties from file:" + testApplicationConfigFile, e);
		}
		
		return testConfigProperties;
	}

	public String getProperty(final String key) {
		return testConfig.getProperty(key);
	}

	/**
	 * Gets a property with a default value.
	 * 
	 * @param key property to get
	 * @param defaultValue default value if property is not set
	 * @return the property value for {@code key} or {@code defaultValue} if not set
	 */
	public String getProperty(final String key, final String defaultValue) {
		return testConfig.getProperty(key, defaultValue);
	}

	public String getDbName() {
		return testConfig.getProperty(PROPERTY_DB_NAME);
	}

	public DbResetMode getDbResetMode() {
		return DbResetMode.valueOf(testConfig.getProperty(PROPERTY_DB_RESET_MODE));
	}

	public String getRuntimeDirectory() {
		return testConfig.getProperty(PROPERTY_RUNNING_DIRECTORY, "target" + File.separator + "runtime");
	}

	public boolean isUsingDefaultDb() {
		return Boolean.parseBoolean(testConfig.getProperty(PROPERTY_USE_DEFAULT_DB));
	}

	public String getDefaultDbName() {
		return testConfig.getProperty(PROPERTY_DEFAULT_DB_NAME);
	}

	public boolean isSearchAvailable() {
		return Boolean.parseBoolean(testConfig.getProperty(PROPERTY_SEARCH_AVAILABLE));
	}

	public String getSearchHomeDir() {
		return testConfig.getProperty(PROPERTY_SEARCH_HOME_DIR, "../../com.elasticpath.search/WEB-INF/solrHome");
	}

	public String getRdbms() {
		return testConfig.getProperty(PROPERTY_RDBMS);
	}

	public String getDbScriptDir() {
		return testConfig.getProperty(PROPERTY_DB_SCRIPT_DIR);
	}

	public Properties getDatabaseProperties() {
		Properties properties = new Properties();
		for (Entry<Object, Object> entry : testConfig.entrySet()) {
			String key = (String) entry.getKey();
			if (key.startsWith("db.")) {
				properties.setProperty(key, (String) entry.getValue());
			}
		}
		properties.setProperty(TestConfig.PROPERTY_DB_NAME, getDefaultDbName());
		properties.setProperty(TestConfig.PROPERTY_RUNNING_DIRECTORY, getRuntimeDirectory());
		return properties;
	}

	public String getAssetsDir() {
		return testConfig.getProperty(PROPERTY_ASSETS_DIR);
	}
}
