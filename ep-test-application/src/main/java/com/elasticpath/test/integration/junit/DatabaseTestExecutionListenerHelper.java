package com.elasticpath.test.integration.junit;

import com.elasticpath.test.persister.DatasourceInitializerFactory;
import com.elasticpath.test.persister.TestConfig;
import com.elasticpath.test.persister.TestConfigurationFactory;
import com.elasticpath.test.persister.database.DataSourceInitializer;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.TestContext;

import javax.naming.NamingException;

/**
 * A helper class for test execution listeners that manage databases within the test context.
 */
public class DatabaseTestExecutionListenerHelper {

    /**
     * Re-initializes a new database for a TestContext.
     *
     * @param testContext the test context to re-initiliaze the database for
     * @throws NamingException
     */
    public static void resetDatabase(final TestContext testContext) throws NamingException {
        testContext.markApplicationContextDirty();

        TestConfig testConfig = new TestConfig(new TestConfigurationFactory.ClassPathResourceProvider());
        DataSourceInitializer initializer = new DatasourceInitializerFactory().getInstance(testConfig.getDatabaseProperties());
        initializer.dropAndCreateDatabase();
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(initializer.getDriverClass().getName());
        dataSource.setUrl(initializer.getConnectionUrl());
        dataSource.setUsername(initializer.getUsername());
        dataSource.setPassword(initializer.getPassword());
        SimpleNamingContextBuilder.emptyActivatedContextBuilder().bind("java:comp/env/jdbc/epjndi", dataSource);
    }
}
