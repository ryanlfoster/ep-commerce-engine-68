/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.test.persister.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.elasticpath.test.common.exception.DataSourceInitializerException;
import com.elasticpath.test.common.exception.TestApplicationException;

/**
 * Derby implementation of AbstractDataSourceInitializer.<br>
 * Runs in the derby embedded mode. Be careful with running embedded derby data source in 
 * product since there are some limitations for the database.<br>
 * The scripts should be generated using <code>ant create-derby</code> command 
 * under codebase/database folder.<br>
 *
 * # database configuration settings for Derby:<br>
 * db.rdbms=derby<br>
 * db.name=derby<br>
 * db.connection.driver_class=org.apache.derby.jdbc.EmbeddedDriver<br>
 * db.script.dir=../database/target/derby<br>
 * 
 */
public class DerbyDataSourceInitializerImpl extends AbstractDataSourceInitializer {
	private static final Logger LOG = Logger.getLogger(DerbyDataSourceInitializerImpl.class);
	
	/**
	 * Create an instance that initializes a Derby db.
	 * 
	 * @param properties the properties to initialize this instance with.
	 * @param resourceProvider the resource provider to be used for loading script files
	 */
	public DerbyDataSourceInitializerImpl(final Properties properties) {
		super(properties);
	}

	private boolean isNetworkMode() {
		if ("org.apache.derby.jdbc.ClientDriver".equals(getDriverClass().getName())) {
			return true;
		} else if ("org.apache.derby.jdbc.EmbeddedDriver".equals(getDriverClass().getName())) {
			return false;
		} else {
			throw new RuntimeException("You have specified a Derby Driver that is neither the EmbeddedDriver nor the ClientDriver, so I can't tell if it's a client-server connection or a local-only connection."); //NOPMD
		}
		
	}

	@Override
	public String getConnectionUrl() {
		if (isNetworkMode()) {
			return "jdbc:derby://" + host + ":" + port + "/" + getDatabaseName()
					+ ";create=true";
		} else {
		return "jdbc:derby:" + getDatabaseName() + ";create=true";
		}
	}

	@Override
	public void dropAndCreateDatabase() {
		LOG.info("Using Derby database.");
		LOG.info("Resetting the database: " + getDatabaseName());

		if (isNetworkMode()) {
			throw new TestApplicationException("Cannot re-initialize Derby in server mode.");
		}
		
		// Shutdown derby
		try {
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		} catch (SQLException e) {
			// Derby always throws a XJ015 exception when it is shut down, so let's ignore it.
			LOG.info("Derby has been shut down");
		}
		
		// The only way to drop a Derby database is to delete the directory
		File dbFile = new File(getDatabaseName());
		try {
			FileUtils.deleteDirectory(dbFile);
		} catch (IOException e) {
			LOG.info("Could not delete derby folder " + getDatabaseName() + ", perhaps it doesn't exist yet");
		}
		
		Connection conn = null;
		
		try {
			// We need a new instance of the driver ever time we "reboot" derby.
			getDriverClass().newInstance();
			conn = DriverManager.getConnection(getConnectionUrl(), username, password);
		} catch (Exception exception) {
			LOG.fatal("Failed to create test database", exception);
			throw new DataSourceInitializerException("Failed to create test database", exception);
		} finally {
			close(conn);
		}
		

	}

}
