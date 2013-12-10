/**
 * Copyright (c) Elastic Path Software Inc., 2008, 2011
 */
package com.elasticpath.test.persister.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.elasticpath.test.common.exception.DataSourceInitializerException;

/**
 * H2 implementation of AbstractDataSourceInitializer.<br>
 * Runs with the H2 server mode.
 * 
 * # database configuration settings for H2:<br>
 * db.rdbms=h2<br>
 * db.connection.driver_class=org.h2.Driver<br>
 * 
 */
public class H2DataSourceInitializerImpl extends AbstractDataSourceInitializer {
	private static final Logger LOG = Logger
			.getLogger(H2DataSourceInitializerImpl.class);

	public H2DataSourceInitializerImpl(final Properties properties) {
		super(properties);
	}

	@Override
	public String getConnectionUrl() {
		return "jdbc:h2:tcp://" + host + ":" + port + "/" + getDatabaseName();
	}

	@Override
	public void dropAndCreateDatabase() {
		LOG.info("Using H2 database.");
		LOG.info("Erasing the entire database: " + getDatabaseName());

		Connection conn = null;
		Statement stmnt = null;
		try {
			conn = DriverManager.getConnection(
					getConnectionUrl(), username, password);
			stmnt = conn.createStatement();

			stmnt.executeQuery("DROP ALL OBJECTS");

		} catch (SQLException exception) {
			// no need to do anything
		} finally {
			close(stmnt);
			close(conn);
		}

		try {
			// H2 autocreates database upon first connection to them
			conn = DriverManager.getConnection(
					getConnectionUrl(), username, password);
		} catch (Exception exception) {
			LOG.fatal("Failed to create test database", exception);
			throw new DataSourceInitializerException(
					"Failed to create test database", exception);
		} finally {
			close(conn);
		}

	}

}
