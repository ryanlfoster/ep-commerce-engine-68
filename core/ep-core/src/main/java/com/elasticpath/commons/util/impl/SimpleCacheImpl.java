package com.elasticpath.commons.util.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.commons.util.SimpleCache;

/**
 * Implements the {@link SimpleCache} interface.
 */
public class SimpleCacheImpl implements SimpleCache {

	private static final long serialVersionUID = 1L;

	private final Map<String, CacheItem<?>> cache;

	/**
	 * Default constructor.
	 */
	public SimpleCacheImpl() {
		cache = Collections.synchronizedMap(new HashMap<String, CacheItem<?>>());
	}

	@Override
	public <T> T getItem(final String key) {
		return getCacheItem(cache.get(key));
	}

	@Override
	public <T> void putItem(final String key, final T item) {
		final CacheItem<T> cacheItem = new CacheItem<T>(key, item);
		cache.put(key, cacheItem);
	}

	@Override
	public <T> T removeItem(final String key) {
		return getCacheItem(cache.remove(key));
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public boolean isInvalidated(final String key) {
		final CacheItem<?> item = cache.get(key);
		if (item == null) {
			return true;
		}
		return item.isInvalidated();
	}

	@Override
	public void cacheInvalidate(final String key) {
		final CacheItem<?> item = cache.get(key);
		if (item != null) {
			item.invalidate();
		}
	}

	@Override
	public void cacheValidate(final String key) {
		final CacheItem<?> item = cache.get(key);
		if (item != null) {
			item.validate();
		}
	}

	private <T> T getCacheItem(final CacheItem<?> item) {
		@SuppressWarnings("unchecked")
		final CacheItem<T> cacheItem = (CacheItem<T>) item;
		if (cacheItem == null) {
			return null;
		}
		return cacheItem.getItem();
	}
}
