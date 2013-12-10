package com.elasticpath.importexport.client;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * DataSource implementation for importexport project. JPA does not support pool of connections, so given datasource is used for this point. 
 * All properties are initialized from <code>DataSourceProperties</code> class, so <code>DataSourceProperties</code> class must be initialized before.
 */
public class JPADataSource extends BasicDataSource {

	@Override
	protected DataSource createDataSource() throws SQLException {
		synchronized (this) {
			DataSourceProperties properties = DataSourceProperties.getInstance();
			setDriverClassName(properties.getDriverClassName());
			setUrl(properties.getUrl());
			setMaxActive(properties.getMaxActive());
			setMaxWait(properties.getMaxWait());
			setTestOnBorrow(properties.getTestOnBorrow());
			setUsername(properties.getUsername());
			setPassword(properties.getPassword());
			return super.createDataSource();
		}
	}
}
