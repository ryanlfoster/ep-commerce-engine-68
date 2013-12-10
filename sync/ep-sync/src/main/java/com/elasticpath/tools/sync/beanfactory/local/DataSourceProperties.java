package com.elasticpath.tools.sync.beanfactory.local;

import java.util.Properties;

/**
 * Represents the datasource properties.
 */
public final class DataSourceProperties {

	/** db.connection.driver_class.*/
	public static final String DB_CLASS_NAME = "db.connection.driver_class";

	/** db.connection.url.*/
	public static final String DB_URL = "db.connection.url";

	/** db.connection.username.*/
	public static final String DB_USERNAME = "db.connection.username";

	/** db.connection.password.*/
	public static final String DB_PASSWORD = "db.connection.password";

	private static final int MAX_ACTIVE = 50;

	private static final long MAX_WAIT = 100000;

	private static final boolean TEST_ON_BORROW = true;
	
	private static final int INITIAL_SIZE = 10;
	
	private final Properties properties;

	/**
	 * Wrapper around java.uril.properties containing some defaults.
	 * @param properties connection properties
	 */
	public DataSourceProperties(final Properties properties) {
		this.properties = properties;
	}


	/**
	 * Gets the driver classname for database connection.
	 *
	 * @return classname
	 */
	public String getDriverClassName() {
		return properties.getProperty(DB_CLASS_NAME);
	}

	/**
	 * Gets the url to necessary database.
	 *
	 * @return url
	 */
	public String getUrl() {
		return properties.getProperty(DB_URL);
	}
	
	/**
	 * Gets the datasource username.
	 *
	 * @return username
	 */
	public String getUsername() {
		return properties.getProperty(DB_USERNAME);
	}

	/**
	 * Gets the datasource password.
	 *
	 * @return password
	 */
	public String getPassword() {
		return properties.getProperty(DB_PASSWORD);
	}

	/**
	 * Gets max active threads property.
	 *
	 * @return max active
	 */
	public int getMaxActive() {
		return MAX_ACTIVE;
	}

	/**
	 * Gets max wait property.
	 *
	 * @return max wait
	 */
	public long getMaxWait() {
		return MAX_WAIT;
	}

	/**
	 * How many data sources are initially allocated in the pool.
	 * 
	 * @return a number usually given to {@code BasicDataSource#setInitialSize(int)}.
	 */
	public int getInitialSize() {
		return INITIAL_SIZE;
	}
	/**
	 * Gets test on borrow property.
	 *
	 * @return test on borrow
	 */
	public boolean isTestOnBorrow() {
		return TEST_ON_BORROW;
	}

}
