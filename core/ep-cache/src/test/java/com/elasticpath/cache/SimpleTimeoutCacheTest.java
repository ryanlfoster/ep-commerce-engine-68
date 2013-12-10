package com.elasticpath.cache;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test that the cache works as intended.
 */
public class SimpleTimeoutCacheTest {

	private static final String BOB = "bob";
	private static final String BOBS_INFO = "bob's info";
	private static final String FRED = "fred";
	private static final String FREDS_INFORMATION = "fred's information";

	
	/**
	 * Simple test for putting and getting from the cache.
	 */
	@Test
	public void testSimpleCaching() {
		final long timeout = 10000;
		
		SimpleTimeoutCache<String, String> cache = new SimpleTimeoutCache<String, String>(timeout);
		cache.put(FRED, FREDS_INFORMATION);
		TestCase.assertEquals(FREDS_INFORMATION, cache.get(FRED));
		
		// Make sure it's available after the first call
		TestCase.assertEquals(FREDS_INFORMATION, cache.get(FRED));
		TestCase.assertEquals(FREDS_INFORMATION, cache.get(FRED));
		TestCase.assertEquals(FREDS_INFORMATION, cache.get(FRED));
	}
	
	/** 
	 * Test that with a zero timeout nothing is cached.
	 */
	@Test
	public void testZeroTimeout() {
		final long timeout = 0;
		
		SimpleTimeoutCache<String, String> cache = new SimpleTimeoutCache<String, String>(timeout);
		cache.put(FRED, FREDS_INFORMATION);
		TestCase.assertNull("Information should not have been cached", cache.get(FRED));
	}
	
	/** 
	 * Test that with a negative timeout nothing is cached.
	 */
	@Test
	public void testNegativeTimeout() {
		final long timeout = -50;
		
		SimpleTimeoutCache<String, String> cache = new SimpleTimeoutCache<String, String>(timeout);
		cache.put(FRED, FREDS_INFORMATION);
		TestCase.assertNull("Information should not have been cached", cache.get(FRED));
	}
	
	
	/**
	 * Test the timeout works as expected - we will control the return value of 
	 * getCurrentTimeMillis() in order to do this test (hence testing with an 
	 * anonymous subclass).
	 */
	@Test
	public void testNormalCacheTimeout() {
		final long timeout = 5;
		final long three = 3;
		final long four = 4;
		final long five = 5;
		
		final long [] currentTime = new long [] {0};
		
		SimpleTimeoutCache<String, String> cache = new SimpleTimeoutCache<String, String>(timeout) {
			@Override
			long getCurrentTimeMillis() {
				return currentTime[0];
			}
		};
		cache.put(FRED, FREDS_INFORMATION);  // put in at time=0 - should expire at time=5
		TestCase.assertEquals(FREDS_INFORMATION, cache.get(FRED));
		
		currentTime[0] = three;
		TestCase.assertEquals(FREDS_INFORMATION, cache.get(FRED));
		currentTime[0] = four;
		TestCase.assertEquals(FREDS_INFORMATION, cache.get(FRED));
		currentTime[0] = five;
		TestCase.assertNull("Cached entry should have expired", cache.get(FRED));
	}

	/**
	 * Test that setting the timeout only affects new entries into the cache.
	 */
	@Test
	public void testChangeTimeout() {
		final long timeout = 5;
		final long five = 5;
		final long eight = 8;
		final long ten = 10;
		
		final long [] currentTime = new long [] {0};
		
		SimpleTimeoutCache<String, String> cache = new SimpleTimeoutCache<String, String>(timeout) {
			@Override
			long getCurrentTimeMillis() {
				return currentTime[0];
			}
		};
		cache.put(FRED, FREDS_INFORMATION);  // put in at time=0 - should expire at time=5
		TestCase.assertEquals(FREDS_INFORMATION, cache.get(FRED));

		cache.setTimeout(ten);
		cache.put(BOB, BOBS_INFO); // should expire at time=10
		
		currentTime[0] = five;
		TestCase.assertNull("Cached entry should have expired - unaffected by setTimeout", cache.get(FRED));
		TestCase.assertEquals(BOBS_INFO, cache.get(BOB));
		
		currentTime[0] = eight;
		TestCase.assertEquals(BOBS_INFO, cache.get(BOB));
				
		currentTime[0] = ten;
		TestCase.assertNull("New entry (after setTimeout) should have expired", cache.get(BOB));
	}
	
	
	
}
