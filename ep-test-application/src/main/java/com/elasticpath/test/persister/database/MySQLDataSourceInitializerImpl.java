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
 * MySql implementation of AbstractDataSourceInitializer.
 */
public class MySQLDataSourceInitializerImpl extends AbstractDataSourceInitializer {
	private static final Logger LOG = Logger.getLogger(MySQLDataSourceInitializerImpl.class);
	
	/**
	 * Create an instance of that initializes a MySQL db.
	 * 
	 * @param properties the properties to initialize this instance with.
	 */
	public MySQLDataSourceInitializerImpl(final Properties properties) {
		super(properties);
	}

	@Override
	public void dropAndCreateDatabase() {
		LOG.info("Using MySQL database.");
		LOG.info("Resetting the database: " + getDatabaseName());
		
		Connection conn = null;
		Statement stmnt = null;
		
		try {
			conn = DriverManager.getConnection(getConnectionUrl(), username, password);
			stmnt = conn.createStatement();
			stmnt.execute("drop database if exists " + getDatabaseName());
			stmnt.execute("create database " + getDatabaseName() + " character set utf8");
			stmnt.execute("grant all on " + getDatabaseName() + ".* to '" + username + "'@'" + host + "' identified by '" + password + "'");
		} catch (Exception exception) {
			LOG.fatal("Failed to create test database", exception);
			
			throw new DataSourceInitializerException("Failed to create test database", exception);
		} finally {
			close(stmnt);
			close(conn);
		}
	}

	@Override
	public String getConnectionUrl() {
		return "jdbc:" + rdbms + "://" + host + ":" + port + "/" + getDatabaseName();
	}
}
