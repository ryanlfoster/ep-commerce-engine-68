/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.persister.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.elasticpath.test.common.exception.DataSourceInitializerException;
import com.elasticpath.test.persister.TestConfig;

/**
 * HSQLDB implementation of AbstractDataSourceInitializer.<br>
 * Provides in-memory table storage facilities which improve performance during development
 * drastically. Be careful with running HSQL data source in product since there are some limitations
 * for the database.<br>
 * Derby's database SQL scripts are most similar to HSQLDB. The scripts should be generated
 * using <code>ant create-derby</code> command under codebase/database folder.<br>
 *
 * # database configuration settings for HSQLDB:<br>
 * db.rdbms=hsqldb<br>
 * db.name=<br>
 * db.connection.host=localhost<br>
 * db.connection.port=3306<br>
 * db.connection.username=sa<br>
 * db.connection.password=<br>
 * db.connection.driver_class=org.hsqldb.jdbcDriver<br>
 * db.script.dir=../database/target/derby
 * 
 */
public class HSQLDbDataSourceInitializerImpl extends AbstractDataSourceInitializer {
	
	private static final Logger LOG = Logger.getLogger(HSQLDbDataSourceInitializerImpl.class);
	
	private enum RunMode {IN_MEMORY, FILE_BASED, SERVER_MODE};
	
	private RunMode runMode = RunMode.IN_MEMORY;
	
	private Properties properties;
	private String databaseLocation;

	/**
	 * Create a datasource initlializer for HSQLDB - will create a disk or file
	 * based db depending on the RDBMS property in the passed in properties.
	 * 
	 * @param properties the properties to use for initialization.
	 */
	public HSQLDbDataSourceInitializerImpl(final Properties properties) {
		super(properties);
		this.properties = properties;
		if ("hsqldbmem".equals(properties.get(TestConfig.PROPERTY_RDBMS))) {
			runMode = RunMode.IN_MEMORY;
		} else if ("hsqldbtcp".equals(properties.get(TestConfig.PROPERTY_RDBMS))) {
			runMode = RunMode.SERVER_MODE;
		} else {
			runMode = RunMode.FILE_BASED;
		}
	}

	@Override
	public void dropAndCreateDatabase() {
		LOG.debug(String.format("Using HsqlDB database [%s]", getConnectionUrl()));
		LOG.debug(String.format("Resetting the database: [%s]", getDatabaseName()));
		
		Connection conn = null;
		Statement stmnt = null;
		try {
			conn = DriverManager.getConnection(getConnectionUrl(), username, password);
			stmnt = conn.createStatement();

			/** clear public schema if one exists. New blank PUBLIC schema will be automatically created. */
			stmnt.executeQuery("DROP SCHEMA PUBLIC CASCADE");			

		} catch (SQLException exception) {			
			throw new DataSourceInitializerException("Failed to drop schema on the test database", exception);
		} finally {
			close(stmnt);
			close(conn);		
		}
		
		try {
			conn = DriverManager.getConnection(getConnectionUrl(), username, password);
		} catch (Exception exception) {
			LOG.fatal("Failed to create test database", exception);

			throw new DataSourceInitializerException("Failed to create test database", exception);
		} finally {
			close(conn);
		}
		
	}

	private String getDatabaseLocation() {
		if (databaseLocation == null) {
			String newLocation;
			if (!new File(getDatabaseName()).isAbsolute()) {
				newLocation = FilenameUtils.concat(properties.getProperty(TestConfig.PROPERTY_RUNNING_DIRECTORY), getDatabaseName());
			} else {
				newLocation = getDatabaseName();
			}
			databaseLocation = newLocation;
		}
		return databaseLocation;
	}

	@Override
	public String getConnectionUrl() {
		if (runMode.equals(RunMode.IN_MEMORY)) {
			return "jdbc:hsqldb:mem:" + getDatabaseName();
		} else if (runMode.equals(RunMode.SERVER_MODE)) {
			String host = properties.getProperty(TestConfig.PROPERTY_DB_CONNECTION_HOST);
			String port = properties.getProperty(TestConfig.PROPERTY_DB_CONNECTION_PORT);
			return "jdbc:hsqldb:hsql://" + host + ":" + port + "/" + getDatabaseName();
		}
		return "jdbc:hsqldb:file:" + getDatabaseLocation();
	}

}
