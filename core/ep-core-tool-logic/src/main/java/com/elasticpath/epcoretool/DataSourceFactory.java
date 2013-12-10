package com.elasticpath.epcoretool;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * This is semi-copied from {@code DBTestsDataSource}.
 */
public class DataSourceFactory extends BasicDataSource {

	/**
	 * Settings passed in from the environment.
	 */
	private static String mavenDbURL = "";

	private static String mavenDbUsername = "";

	private static String mavenDbPassword = "";

	private static String mavenDbDriverClassName = "";

	private static Integer mavenDbConnectionPoolMinIdle;

	private static Integer mavenDbConnectionPoolMaxIdle;

	/** Maximum number of connections and. */
	public static final int MAVENDB_MAX_ACTIVE = 5;

	/** how long to wait on the pool for a free one. */
	public static final int MAVENDB_MAX_WAIT = 30000;

	@Override
	public DataSource createDataSource() throws SQLException {
		setUrl(mavenDbURL);
		setUsername(mavenDbUsername);
		setPassword(mavenDbPassword);
		setDriverClassName(mavenDbDriverClassName);

		setMaxActive(MAVENDB_MAX_ACTIVE);
		setMaxWait(MAVENDB_MAX_WAIT);

		if (null != mavenDbConnectionPoolMinIdle) {
			setMinIdle(mavenDbConnectionPoolMinIdle);
		}
		if (null != mavenDbConnectionPoolMaxIdle) {
			setMaxIdle(mavenDbConnectionPoolMaxIdle);
		}
		setTestOnBorrow(true);
		return super.createDataSource();
	}

	/**
	 * Statically configure the JDBC settings for the factory.
	 *
	 * @param url JDBC-style URL (required)
	 * @param username passed to the driver (so it may be optional).
	 * @param password passed to the driver (so it may be optional).
	 * @param driverClass The JDBC driver to use (required).
	 * @param minIdle the min idle
	 * @param maxIdle the max idle
	 */
	public static void configure(final String url, final String username, final String password, final String driverClass, final Integer minIdle,
			final Integer maxIdle) {
		mavenDbURL = url;
		mavenDbUsername = username;
		mavenDbPassword = password;
		mavenDbDriverClassName = driverClass;
		mavenDbConnectionPoolMinIdle = minIdle;
		mavenDbConnectionPoolMaxIdle = maxIdle;
	}
}
