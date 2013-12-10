/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.persister.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.elasticpath.test.common.exception.DataSourceInitializerException;

/**
 * MSSQL implementation of AbstractDataSourceInitializer.
 */
public class SqlServerDataSourceInitializerImpl extends AbstractDataSourceInitializer {
	private static final Logger LOG = Logger.getLogger(SqlServerDataSourceInitializerImpl.class);

	/**
	 * Constructor.
	 * @param properties the properties
	 */
	public SqlServerDataSourceInitializerImpl(final Properties properties) {
		super(properties);
	}

	@Override
	public void dropAndCreateDatabase() {
		LOG.info("Using MS SQL Server database.");
		LOG.info("Resetting the database: " + getDatabaseName());

		Connection conn = null;
		Statement stmnt = null;
		try {
			conn = DriverManager.getConnection(getConnectionUrl(), username, password);
			stmnt = conn.createStatement();
			stmnt.execute("IF EXISTS(SELECT * FROM sys.databases WHERE name='" + getDatabaseName() + "') DROP DATABASE " + getDatabaseName());
			stmnt.execute("create database " + getDatabaseName());
		} catch (Exception e) {
			LOG.fatal("Failed to create test database", e);

			throw new DataSourceInitializerException("Failed to create test database", e);
		} finally {
			close(stmnt);
			close(conn);
		}

	}

	@Override
	public String getConnectionUrl() {
		return "jdbc:" + rdbms + "://" + host + ":" + port + ";DatabaseName=" + getDatabaseName() + ";selectMethod=cursor";
	}
}
