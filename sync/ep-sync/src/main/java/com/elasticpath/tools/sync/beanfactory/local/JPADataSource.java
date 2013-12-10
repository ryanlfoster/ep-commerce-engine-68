/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.beanfactory.local;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * DataSource implementation for sync project. JPA does not support pool of connections, so given datasource is used for this point. All
 * properties are initialized from <code>DataSourceProperties</code> class, thus <code>DataSourceProperties</code> class must be initialized
 * before.
 */
@SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
public class JPADataSource extends BasicDataSource {

	/**
	 * This needs to be static as this class gets instantiated internally by OpenJPA.
	 * The properties have to be set before the class gets instantiated.
	 */
	private static DataSourceProperties dataSourceProperties;

	private DataSource localDataSource = null;

	@Override
	protected DataSource createDataSource() throws SQLException {

		if (localDataSource != null) {
			return localDataSource;
		}

		createLocalDataSource();
		return localDataSource;
	}

	private synchronized void createLocalDataSource() throws SQLException {
		if (localDataSource == null) {
			setDriverClassName(dataSourceProperties.getDriverClassName());
			setUrl(dataSourceProperties.getUrl());
			setMaxActive(dataSourceProperties.getMaxActive());
			setMaxWait(dataSourceProperties.getMaxWait());
			setTestOnBorrow(dataSourceProperties.isTestOnBorrow());
			setUsername(dataSourceProperties.getUsername());
			setPassword(dataSourceProperties.getPassword());
			setInitialSize(dataSourceProperties.getInitialSize());
			localDataSource = super.createDataSource();
		}
	}

	/**
	 *
	 * @return the dataSourceProperties
	 */
	protected DataSourceProperties getDataSourceProperties() {
		return dataSourceProperties;
	}

	/**
	 *
	 * @param dataSourceProperties the dataSourceProperties to set
	 */
	public static void setDataSourceProperties(final DataSourceProperties dataSourceProperties) {
		JPADataSource.dataSourceProperties = dataSourceProperties;
	}
}
