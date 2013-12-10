package com.elasticpath.importexport.client;

import java.util.Properties;

/**
 * Singleton representing the datasource properties.
 */
public final class DataSourceProperties {

	private static final String DB_CLASS_NAME = "db.connection.driver_class";

	private static final String DB_URL = "db.connection.url";

	private static final String DB_USERNAME = "db.connection.username";

	private static final String DB_PASSWORD = "db.connection.password";

	private static final int MAX_ACTIVE = 10;

	private static final long MAX_WAIT = 10000;

	private static final boolean TEST_ON_BORROW = true;

	private static DataSourceProperties instance;

	private Properties properties;

	private DataSourceProperties() {
	}

	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static DataSourceProperties getInstance() {
		synchronized (DataSourceProperties.class) {
			if (instance == null) {
				instance = new DataSourceProperties();
			}
			return instance;
		}
	}

	/**
	 * Sets the properties that contains datasource properties.
	 *
	 * @param properties datasource properties
	 */
	public void setProperties(final Properties properties) {
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
	 * Gets test on borrow property.
	 *
	 * @return test on borrow
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getTestOnBorrow() {
		return TEST_ON_BORROW;
	}

}
