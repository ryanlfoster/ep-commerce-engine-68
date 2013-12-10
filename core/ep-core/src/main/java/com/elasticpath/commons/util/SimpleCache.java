package com.elasticpath.commons.util;

import java.io.Serializable;


/**
 * Provides access to items that are cached.
 */
public interface SimpleCache extends Serializable {

	/**
	 * Gets the CacheItem.
	 *
	 * @param <T> type of item to return.
	 * @param key key of the cache item.
	 * @return the item or null if not found.
	 */
	<T> T getItem(String key);

	/**
	 * Puts a CacheItem into the attribute map.
	 *
	 * @param <T> type of item to put in cache.
	 * @param key key to associate with cache item.
	 * @param item item to cache.
	 */
	<T> void putItem(String key, T item);

	/**
	 * Removes the item associated with the key.
	 *
	 * @param <T> type of item to return.
	 * @param key key associated with cache item.
	 * @return cache item that was associated to key or null if there was no association.
	 */
	<T> T removeItem(String key);

	/**
	 * Clears the cache.
	 */
	void clear();

	/**
	 * Checks to see if the item has been invalidated.
	 *
	 * @param key key that cached item is associated with.
	 * @return true if invalidated, false otherwise.
	 */
	boolean isInvalidated(String key);

	/**
	 * Flags the underlying cache item as "invalidated" if it exists.
	 *
	 * @param key key that cached item is associated with.
	 */
	void cacheInvalidate(String key);

	/**
	 * Flags the underlying cache item as "validated" if it exists.
	 *
	 * @param key key that cached item is associated with.
	 */
	void cacheValidate(String key);
}
