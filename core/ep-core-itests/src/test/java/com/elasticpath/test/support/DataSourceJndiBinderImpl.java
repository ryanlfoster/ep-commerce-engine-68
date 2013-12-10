package com.elasticpath.test.support;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * Binds a {@link DataSource} to the default EP JNDI location ({@link DataSourceJndiBinderImpl#JNDI_NAME}. Also provides
 * a method to clear the JNDI context.
 */
public class DataSourceJndiBinderImpl {
	private final SimpleNamingContextBuilder builder;

	/**
	 * The default JNDI name for an EP DataSource.
	 */
	public String JNDI_NAME = "java:comp/env/jdbc/epjndi";

	/**
	 * Creates a new DatasourceJndiBinder().
	 * @throws NamingException if it cannot create a {@link SimpleNamingContextBuilder}.
	 */
	public DataSourceJndiBinderImpl() throws NamingException {
		builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
	}

	/**
	 * Binds the given DataSource in JNDI with the default name.
	 * @param dataSource the DataSource to bind
	 */
	public void bindEpDatasourceInJndi(final DataSource dataSource) throws NamingException {
		builder.bind(JNDI_NAME, dataSource);
	}

	/**
	 * Clears the JNDI binding.
	 */
	public void emptyJndiContext() {
		builder.clear();
	}
}