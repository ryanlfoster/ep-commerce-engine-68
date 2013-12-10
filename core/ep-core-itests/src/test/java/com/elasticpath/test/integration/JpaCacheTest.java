/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

import com.elasticpath.persistence.openjpa.impl.JpaPersistenceEngineImpl;
import com.elasticpath.persistence.openjpa.impl.JpaSessionFactoryImpl;
import com.elasticpath.test.support.DataSourceJndiBinderImpl;

/**
 * Test the Persistence Engine JPA Caching methods act as expected.
 */
public class JpaCacheTest extends BasicSpringContextTest {

	private static final String OPENJPA_DATA_CACHE_PROPERTY = "openjpa.DataCache";
	
	private static final String OPENJPA_CACHE_MANAGER_PROPERTY = "openjpa.DataCacheManager";

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private JpaSessionFactoryImpl  sessionFactory;

	@Autowired
	private DataSource dataSource;

	private JpaPersistenceEngineImpl persistenceEngine;

	private Map<String, String> jpaPropertyOverrides;

	private DataSourceJndiBinderImpl datasourceJndiBinder;

	/**
	 * Set up objects required by all tests.
	 *  
	 * @throws java.lang.Exception in case of errors setting up objects
	 */
	@Before
	public void setUp() throws Exception {
		sessionFactory = new JpaSessionFactoryImpl();
		sessionFactory.setTransactionManager(transactionManager);
		
		persistenceEngine = new JpaPersistenceEngineImpl();
		persistenceEngine.setTransactionManager(transactionManager);
		persistenceEngine.setSessionFactory(sessionFactory);
		
		jpaPropertyOverrides = new HashMap<String, String>();
		datasourceJndiBinder = new DataSourceJndiBinderImpl();
		datasourceJndiBinder.bindEpDatasourceInJndi(dataSource);
	}

	@After
	public void tearDown() throws Exception {
		datasourceJndiBinder.emptyJndiContext();
	}

	/**
	 * Test that isCacheEnabled is false with cache turned off.
	 */
	@DirtiesDatabase
	@Test
	public void testIsCacheEnabledWithCacheOff() {
		jpaPropertyOverrides.put(OPENJPA_CACHE_MANAGER_PROPERTY, "default");
		jpaPropertyOverrides.put(OPENJPA_DATA_CACHE_PROPERTY, "false");
		initializePersistenceEngine();
		assertFalse("isCacheEnabled should return false when cache is not enabled" , persistenceEngine.isCacheEnabled());
	}
	
	/**
	 * Test that isCacheEnabled is true with basic caching on.
	 */
	@DirtiesDatabase
	@Test
	public void testIsCacheEnabledWithCacheOnNoOptions() {
		jpaPropertyOverrides.put(OPENJPA_DATA_CACHE_PROPERTY, "true");
		initializePersistenceEngine();
		assertTrue("isCacheEnabled should return true when cache is enabled with no options" , persistenceEngine.isCacheEnabled());
	}
	
	/**
	 * Test that isCacheEnabled is true with cache turned on with caching options.
	 */
	@DirtiesDatabase
	@Test
	public void testIsCacheEnabledWithCacheOnOptions() {
		jpaPropertyOverrides.put(OPENJPA_DATA_CACHE_PROPERTY, "true(CacheSize=2000, SoftReferenceSize=0)");
		initializePersistenceEngine();
		assertTrue("isCacheEnabled should return true when cache is enabled with no options", persistenceEngine.isCacheEnabled());
	}

	private void initializePersistenceEngine() {
		EntityManagerFactory entityManagerFactory = OpenJPAPersistence.createEntityManagerFactory(
				null, "META-INF/jpa-persistence.xml", jpaPropertyOverrides);
		sessionFactory.setEntityManagerFactory(entityManagerFactory);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		persistenceEngine.setEntityManager(entityManager);
	}
}
