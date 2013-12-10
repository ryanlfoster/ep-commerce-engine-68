package com.elasticpath.commons.util.impl;

import java.io.Serializable;

/**
 * Wraps items that you want to put in an SimpleCache so that metadata/information can be added to the object easily.
 *
 * @param <T> Type of item in cache.
 */
public class CacheItem<T> implements Serializable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	
	private final T item;
	private final String key;
	private boolean invalidated;

	/**
	 * Creates a cacheItem.
	 *
	 * @param key key you want to cache.
	 * @param item item to cache.
	 */
	public CacheItem(final String key, final T item) {
		this.key = key;
		this.item = item;
		invalidated = false;
	}

	/**
	 * Gets the cache item.
	 *
	 * @return the cache item.
	 */
	public T getItem() {
		return item;
	}

	/**
	 * Gets the key of the cache item.
	 *
	 * @return the key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Returns the validation state of the cache item.
	 *
	 * @return true if the cache item has been invalidated.
	 */
	public boolean isInvalidated() {
		return invalidated;
	}

	/**
	 * Invalidates the cache item.
	 */
	public void invalidate() {
		this.invalidated = true;
	}

	/**
	 * Validates the cache item.
	 */
	public void validate() {
		this.invalidated = false;
	}
}
