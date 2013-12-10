/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.persister;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.test.common.exception.TestApplicationException;
import com.elasticpath.test.persister.database.DataSourceInitializer;
import com.elasticpath.test.persister.database.DerbyDataSourceInitializerImpl;
import com.elasticpath.test.persister.database.HSQLDbDataSourceInitializerImpl;
import com.elasticpath.test.persister.database.MySQLDataSourceInitializerImpl;
import com.elasticpath.test.persister.database.OracleDataSourceInitializerImpl;
import com.elasticpath.test.persister.database.SqlServerDataSourceInitializerImpl;

/**
 * Factory for {@link DataSourceInitializer}.
 */
public class DatasourceInitializerFactory {

	private static final Logger LOG = Logger.getLogger(DatasourceInitializerFactory.class);
	
	private static final String RDBMS_MYSQL = "mysql";
	private static final String RDBMS_MSSQL = "sqlserver";
	private static final String RDBMS_ORACLE = "oracle";
	private static final String RDBMS_HSQLDB = "hsqldb";
	private static final String RDBMS_DERBY = "derby";

	@Autowired
	private TestConfig testConfig;
	
	/**
	 * Returns an instance of {@link DataSourceInitializer} initialized with default properties from {@link TestConfig}.
	 * @return an instance of {@link DataSourceInitializer} with the default DB properties from {@link TestConfig}.
	 */
	public DataSourceInitializer getInstanceWithTestConfigDefaults() {
		return getInstance(testConfig.getDatabaseProperties());
	}
	
	/**
	 * Returns an instance of {@link DataSourceInitializer} initialized with given {@link Properties}.
	 * @param dbProperties the DB properties to use
	 * @return an instance of {@link DataSourceInitializer} with the given {@code dbProperties}
	 */
	public DataSourceInitializer getInstance(Properties dbProperties) {
		final String rdbms = dbProperties.getProperty(TestConfig.PROPERTY_RDBMS);
		LOG.debug(String.format("Initialising datasource [%s]...", rdbms));

		if (null == rdbms) {
			throw new TestApplicationException(
					"Failed to initialize datasource, rdbms not recognized: "
							+ rdbms);
		}

		DataSourceInitializer initializer;
		if (rdbms.startsWith(RDBMS_HSQLDB)) {
			initializer = new HSQLDbDataSourceInitializerImpl(dbProperties);
		} else if (RDBMS_MYSQL.equals(rdbms)) {
			initializer = new MySQLDataSourceInitializerImpl(dbProperties);
		} else if (RDBMS_MSSQL.equals(rdbms)) {
			initializer = new SqlServerDataSourceInitializerImpl(dbProperties);
		} else if (RDBMS_ORACLE.equals(rdbms)) {
			initializer = new OracleDataSourceInitializerImpl(dbProperties);
		} else if (RDBMS_DERBY.equals(rdbms)) {
			initializer = new DerbyDataSourceInitializerImpl(dbProperties);
		} else {
			throw new TestApplicationException(
					"Failed to initialize datasource, rdbms not recognized: "
							+ rdbms);
		}

		LOG.debug(String.format("Completed initialising datasource [%s]", rdbms));
		return initializer;
	}
}
