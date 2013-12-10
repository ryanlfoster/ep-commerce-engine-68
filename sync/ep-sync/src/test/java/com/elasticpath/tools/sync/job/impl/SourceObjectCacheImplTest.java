package com.elasticpath.tools.sync.job.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Semaphore;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;

/**
 * Tests for {@link SourceObjectCacheImpl}.
 */
public class SourceObjectCacheImplTest {
	private static final int NUM_PERMITS = 10;
	private static final int CORE_POOL_SIZE = 10;
	private static final int MAX_POOL_SIZE = 10;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			// the task executor makes the unit tests concurrent, which requires a synchronised mockery
			setThreadingPolicy(new Synchroniser());
		}
	};
	
	private SourceObjectCacheImpl sourceObjectCache;
	private EntityLocator entityLocator;
	private Semaphore availablePermits;
	private ThreadPoolTaskExecutor taskExecutor;
	
	/**
	 * Sets up a test case.
	 */
	@Before
	public void setUp() {
		entityLocator = context.mock(EntityLocator.class);
		availablePermits = new Semaphore(NUM_PERMITS);
		taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setDaemon(true);
		taskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
		taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
		taskExecutor.initialize();
		
		sourceObjectCache = new SourceObjectCacheImpl();
		sourceObjectCache.setEntityLocator(entityLocator);
		sourceObjectCache.setSourceObjectLoadingTaskExecutor(taskExecutor);
		sourceObjectCache.setAvailablePermits(availablePermits);
	}
	
	/**
	 * Test load() and retrieve() methods.  Just tests the happy path.
	 */
	@Test
	public void testLoadAndRetrieve() {
		final Product aProduct = new ProductImpl();
		final String guid = "GUID";
		final Class<Product> type = Product.class;
		
		testLoadAndRetrieve(aProduct, guid, type);
	}
	
	/**
	 * Test remove() method.  Ensure object gets removed.
	 */
	@Test
	public void testRemove() {
		final Product aProduct = new ProductImpl();
		final String guid = "GUID";
		final Class<Product> type = Product.class;
		
		testLoadAndRetrieve(aProduct, guid, type);

		sourceObjectCache.remove(guid, type);
		
		testCacheMiss(guid, type);
	}

	private void testLoadAndRetrieve(final Product aProduct, final String guid, final Class<Product> type) {
		context.checking(new Expectations() { {
			oneOf(entityLocator).locatePersistence(with(guid), with(type)); will(returnValue(aProduct));
		} });
		
		sourceObjectCache.load(guid, type);
		
		final Persistable retrievedObject = sourceObjectCache.retrieve(guid, type);
		
		assertEquals(aProduct, retrievedObject);
	}

	private void testCacheMiss(final String guid, final Class<Product> type) {
		boolean cacheMissExceptionThrown = false;
		try {
			sourceObjectCache.retrieve(guid, type);
		} catch (final EpServiceException e) {
			cacheMissExceptionThrown = true;
		}
		
		assertTrue("Retrieving a non-existent object should throw an EpServiceException", cacheMissExceptionThrown);
	}
}
