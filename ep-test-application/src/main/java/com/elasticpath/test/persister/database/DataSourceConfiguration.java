package com.elasticpath.test.persister.database;

import java.sql.Driver;

/**
 * Provides configuration information for a particular database.
 */
public interface DataSourceConfiguration {

	/**
	 * @return connection url for the current database
	 */
	String getConnectionUrl();

	/**
	 * @return database user name
	 */
	String getUsername();

	/**
	 * @return database password
	 */
	String getPassword();

	/**
	 * @return JDBC driver class
	 */
	Class<? extends Driver> getDriverClass();
}
