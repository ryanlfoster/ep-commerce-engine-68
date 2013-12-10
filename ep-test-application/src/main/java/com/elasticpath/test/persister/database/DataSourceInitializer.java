package com.elasticpath.test.persister.database;

import com.elasticpath.test.persister.database.DataSourceConfiguration;

/**
 * Interface for the simple persistent test scenarios that force a clean test DB schema once for ALL (not each)
 * test method within the test in which DataSourceService is used.
 */
public interface DataSourceInitializer extends DataSourceConfiguration {

	public void dropAndCreateDatabase();
}
