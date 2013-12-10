/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.persister;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.elasticpath.test.persister.database.DataSourceConfiguration;

/**
 * Factory for {@link DataSource} that returns a {@link DataSource} based on its {@link DataSourceConfiguration}.
 * The same {@link DataSource} will be returned until the factory's {@link DataSourceConfiguration} is updated.
 * Then a new one is returned.
 */
public class DynamicDataSourceFactory {

	private BasicDataSource dataSource;
	private DataSourceConfiguration dataSourceConfig;
	
	/**
	 * Returns a {@link DataSource} configured with the current {@link DataSourceConfiguration}.
	 * @return {@link DataSource} representing the current database configuration
	 * @throws Exception in case of errors
	 */
	public synchronized DataSource getObject() throws Exception {
		if (dataSource == null || dataSource.isClosed()) {
			dataSource = new BasicDataSource();
			dataSource.setUrl(dataSourceConfig.getConnectionUrl());
			dataSource.setUsername(dataSourceConfig.getUsername());
			dataSource.setPassword(dataSourceConfig.getPassword());
			dataSource.setDriverClassName(dataSourceConfig.getDriverClass().getName());
		}
		return dataSource;
	}
	
	/**
	 * Reset {@link DataSourceConfiguration}. 
	 * @param dataSourceConfig the {@link DataSourceConfiguration}
	 */
	public synchronized void reset(DataSourceConfiguration dataSourceConfig) {
		setDataSourceConfig(dataSourceConfig);
		closeExistingDataSource();
	}

	/**
	 * Set {@link DataSource} configuration.
	 * @param dataSourceConfig the {@link DataSourceConfiguration}.
	 */
	public void setDataSourceConfig(DataSourceConfiguration dataSourceConfig) {
		this.dataSourceConfig = dataSourceConfig;
	}
	
	private synchronized void closeExistingDataSource() {
		if (dataSource != null) {
			try {
				dataSource.close();
			} catch (SQLException e) {
				// ignore
			} finally {
				dataSource = null;
			}
		}
	}

}
