/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Ensure the ThreadLocalStoreConfig works as intended.
 */
public class ThreadLocalStorageImplTest {

	private final ThreadLocalStorageImpl storeConfig = new ThreadLocalStorageImpl();
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setThreadingPolicy(new Synchroniser());
		}
	};

	private final StoreService mockStoreService = context.mock(StoreService.class);
	private final Store initialStore = new StoreImpl();

	
	/**
	 * Test throwing an exception when store code not set and getStore() is called.
	 */
	@SuppressWarnings("PMD.EmptyCatchBlock")
	@Test
	public void testGetStoreWhenStoreCodeNotSet() {
		try {
			storeConfig.getStore();
			fail("No store code has been set, exception expected.");
		} catch (EpServiceException expected) {
			// This is expected.
		}
	}
	
	/**
	 * Test that getStoreCode returns the store code when one is set.
	 */
	@Test
	public void testGetStoreCode() {
		String storeCode = "MyStoreCode";
		storeConfig.setStoreCode(storeCode);
		assertEquals(storeCode, storeConfig.getStoreCode());
	}	
	
	/**
	 * Test that the correct store is returned when the store code has been set.
	 */
	@Test
	public void testThatStoreIsReturnedCorrectly() {
		context.checking(new Expectations() {
			{

				allowing(mockStoreService).findStoreWithCode("store1");
				will(returnValue(initialStore));
			}
		});
		storeConfig.setStoreService(mockStoreService);
		storeConfig.setStoreCode("store1");
		
		Store returnedStore = storeConfig.getStore();
		assertSame(initialStore, returnedStore);
	}
	
	/**
	 * Make sure that caching works as expected. 
	 */
	@Test
	public void testThatStoreIsCachedAfterFirstAccess() {
		context.checking(new Expectations() {
			{

				oneOf(mockStoreService).findStoreWithCode("store2");
				will(returnValue(initialStore));
			}
		});
		storeConfig.setStoreService(mockStoreService);
		storeConfig.setStoreCode("store2");
		
		Store returnedStore = storeConfig.getStore();
		assertSame(initialStore, returnedStore);

		// Second call should not cause 'findStoreWithCode' a second time
		returnedStore = storeConfig.getStore();
		assertSame("Should same store that was returned for the first call", initialStore, returnedStore);

		// Once more to check we haven't invalidated the cache.
		returnedStore = storeConfig.getStore();
		assertSame("Should have same store that was returned for the first call", initialStore, returnedStore);
	}
	
	/**
	 * Test that the correct store is returned when the store code has been set.
	 */
	@SuppressWarnings("PMD.EmptyCatchBlock")
	@Test
	public void testForExceptionIfStoreCannotBeFound() {
		context.checking(new Expectations() {
			{

				allowing(mockStoreService).findStoreWithCode("store3");
				will(returnValue(null));
			}
		});
		storeConfig.setStoreService(mockStoreService);
		storeConfig.setStoreCode("store3");
		
		try {
			storeConfig.getStore();
			fail("Should have received an exception - store wasn't found");
		} catch (EpServiceException expected) {
			// This is expected
		}
	}
	
	/**
	 * Test retrieving the store with multiple threads running.
	 */
	@Test
	public void testGetStoreDifferentThreads() {
		
		final int numberOfThreadsToTest = 200;
		storeConfig.setStoreService(mockStoreService);

		// Buckets for the test data and test results
		final Map<String, Store> resultMap = Collections.synchronizedMap(new HashMap<String, Store>());
		final List<Store> stores = new ArrayList<Store>();
		final List<Thread> threads = new ArrayList<Thread>();
		
		// The runnable that will actually do the work
		final Runnable getStoreRunnable = new Runnable() {
			/** Get the thread-local store */
			public void run() {
				storeConfig.setStoreCode(Thread.currentThread().getName());
				resultMap.put(Thread.currentThread().getName(), storeConfig.getStore());
			}
		};

		context.checking(new Expectations() {
			{
			// Set up the test data (put it in the buckets)
			for (int x = 0; x < numberOfThreadsToTest; x++) {
				stores.add(new StoreImpl());

				allowing(mockStoreService).findStoreWithCode("store" + x);
				will(returnValue(stores.get(x)));

				threads.add(new Thread(getStoreRunnable, "store" + x));
			}
			}
		});

		// Start and wait for all threads to complete
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException ie) {
				// Doesn't really matter for the test
			}
		}

		// Check each of the threads got the expected results
		for (int x = 0; x < numberOfThreadsToTest; x++) {
			assertSame("Incorrect store returned for thread #" + x, stores.get(x), resultMap.get("store" + x));
		}
	}
	
	/**
	 * Test that getSetting throws an EpServiceException if the store code has not been initialized.
	 */
	@SuppressWarnings("PMD.EmptyCatchBlock")
	@Test
	public void testGetSettingThrowsException() {
		try {
			storeConfig.getSetting(null);
			fail("Expected EpServiceException because store code has not been set.");
		} catch (EpServiceException expected) {
			//Expected
		}
	}
	
	/**
	 * Test that getSetting calls the setting service with the given path and the threadlocal store code,
	 * and returns the service's setting value.
	 */
	@Test
	public void testGetSetting() {
		final String storeCode = "myStoreCode";
		final String path = "COMMERCE/STORE/theme";
		storeConfig.setStoreCode(storeCode);
		final SettingsService mockSettingsService = context.mock(SettingsService.class);
		storeConfig.setSettingsService(mockSettingsService);
		final SettingValue settingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsService).getSettingValue(path, storeCode);
				will(returnValue(settingValue));
			}
		});
		assertSame(settingValue, storeConfig.getSetting(path));
	}
}
