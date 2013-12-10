package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.elasticpath.commons.util.SimpleCache;

/**
 * Tests the implemented {@link SimpleCacheImpl}.
 */
public class SimpleCacheImplTest {
	
	private final SimpleCache simpleCache = new SimpleCacheImpl();
	
	/**
	 * Tests that cache items are created validated and that invalidate/validate works
	 * as expected.
	 */
	@Test
	public void testInvalidateValidate() {
		
		simpleCache.clear();
		
		final String key = "key";
		final List<Long> items = new ArrayList<Long>();
		simpleCache.putItem(key, items);
		
		assertFalse("Newly created cache item should not be invalidated!", simpleCache.isInvalidated(key));
		
		simpleCache.cacheInvalidate(key);
		assertTrue("Invalidated cache item should be invalidated!", simpleCache.isInvalidated(key));
		
		simpleCache.cacheValidate(key);
		assertFalse("Revalidated cache item should not be invalidated!", simpleCache.isInvalidated(key));
	}

	/**
	 * Tests that existing cache items get replaced with a completely new one.
	 */
	@Test
	public void testReplaceCacheItem() {
		
		simpleCache.clear();
		
		final String key = "key";

		final List<Long> originalItem = new ArrayList<Long>();
		originalItem.add(1L);

		simpleCache.putItem(key, originalItem);
		final List<Long> storedOriginal = simpleCache.getItem(key);
		
		final String replacementItem =  "something";

		simpleCache.putItem(key, replacementItem);
		final String storedReplacement = simpleCache.getItem(key);
		
		assertFalse("Replacement item did not replace original!", storedOriginal.equals(storedReplacement));
	}
}
