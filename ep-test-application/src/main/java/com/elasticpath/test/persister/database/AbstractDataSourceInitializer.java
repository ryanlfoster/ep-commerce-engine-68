package com.elasticpath.test.persister.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.elasticpath.test.common.exception.DataSourceInitializerException;
import com.elasticpath.test.persister.TestConfig;

/**
 * Abstract class that provides common methods for DataSourceInitializers.
 * <h2>Database</h2>
 * For running tests on different RDBMS you must create appropriate implementation of this abstract class. Each class that extends this must
 * implement {@link #dropAndCreateDatabase(String)} and {@link #getConnectionUrl(String)} methods. Method initialize() creates database(schema) in appropriate RDBMS with name that is
 * obtained as parameter or recreates existent one. Method getConnectionUrl() must return an url specific for appropriate RDBMS and database in it.
 * Parameters for the database, such as database name, user, password and so on, are received from properties in constructor. Any user specified will
 * need privileges to:
 * <ul>
 * <li>Create databases</li>
 * <li>Drop databases</li>
 * </ul>
 */
public abstract class AbstractDataSourceInitializer implements DataSourceInitializer {

	private static final Logger LOG = Logger.getLogger(AbstractDataSourceInitializer.class);
	
	protected final String rdbms;

	private final String databaseName;

	protected final String host;

	protected final String port;

	protected final String username;

	protected final String password;

	protected final String dbaUsername;

	protected final String dbaPassword;

	private final Class<? extends Driver> driverClass;
	
	/** The directory where the database initialization scripts are to be found. */
	protected final String scriptDir;

	protected Map<String, List<String>> parsedFileCache = new HashMap<String, List<String>>();

	/**
	 * Set initial parameters.
	 * 
	 * @param properties the properties.
	 */
    public AbstractDataSourceInitializer(final Properties properties) {
		rdbms = properties.getProperty(TestConfig.PROPERTY_RDBMS);
		databaseName = properties.getProperty(TestConfig.PROPERTY_DB_NAME);
		host = properties.getProperty(TestConfig.PROPERTY_DB_CONNECTION_HOST);
		port = properties.getProperty(TestConfig.PROPERTY_DB_CONNECTION_PORT);
		username = properties.getProperty(TestConfig.PROPERTY_DB_CONNECTION_USERNAME);
		password = properties.getProperty(TestConfig.PROPERTY_DB_CONNECTION_PASSWORD);
		scriptDir = properties.getProperty(TestConfig.PROPERTY_DB_SCRIPT_DIR);
		//dba username and password
		dbaUsername = properties.getProperty(TestConfig.PROPERTY_DB_DBA_USERNAME);
		dbaPassword = properties.getProperty(TestConfig.PROPERTY_DB_DBA_PASSWORD);
		
		String driverClassName = properties.getProperty(TestConfig.PROPERTY_DB_CONNECTION_DRIVER_CLASS);
		try {
			driverClass = loadDriverClass(driverClassName);
		} catch (ClassNotFoundException e) {
			throw new DataSourceInitializerException("Failed to load database driver class: " + driverClassName, e);
		}
	}

    @SuppressWarnings("unchecked")
    private Class<? extends Driver> loadDriverClass(final String driverClassName) throws ClassNotFoundException {
        return (Class<? extends Driver>) Class.forName(driverClassName);
    }

	/**
	 * @return database name
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * Get user name.
	 * 
	 * @return the user name
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Get DBA user name.
	 * 
	 * @return the user name
	 */
	public String getDbaUsername() {
		return dbaUsername;
	}

	/**
	 * Get user password.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Get DBA password.
	 * 
	 * @return the password
	 */
	public String getDbaPassword() {
		return dbaPassword;
	}

	@Override
	public Class<? extends Driver> getDriverClass() {
		return driverClass;
	}

	/**
	 * Subclasses can call this to hide some boilerplate code.
	 * @param connection the connection to close.
	 * @throws DataSourceInitializerException if there was a problem closing the connection.
	 */
	protected void close(final Connection connection) {
		if (connection == null) {
			return;
		}
		
		try {
			connection.close();
		} catch (SQLException sqle) {
			LOG.warn("Failed to close connection");
			throw new DataSourceInitializerException("Failed to close connection", sqle);
		}
	}
	
	/**
	 * Subclasses can call this to hide some boilerplate code.
	 * @param statement the statement to close.
	 * @throws DataSourceInitializerException if there was a problem closing the statement.
	 */
	protected void close(final Statement statement) {
		if (statement == null) {
			return;
		}
		
		try {
			statement.close();
		} catch (SQLException sqle) {
			LOG.warn("Failed to close statement");
			throw new DataSourceInitializerException("Failed to close statement", sqle);
		}
	}
  	
}
