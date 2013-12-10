/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util;

/**
 * Provides an interface for invalidating various caches.
 */
public interface InvalidatableCache {
	/**
	 * Invalidate (clear) the cache.
	 */
	void invalidate();

	/**
	 * Removes from cache the element with key: objectUid.
	 * @param objectUid - the key associated with our object in the cache
	 */
	void invalidate(final Object objectUid);
}