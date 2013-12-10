package com.elasticpath.sfweb.service;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.util.InvalidatableCache;
import com.elasticpath.persistence.api.Persistable;

/**
 * Generic, threadsafe cache that can cache Persistable entities by multiple keys simultaneously.
 * To ensure thread safety, objects are detached before being put into cache.
 *
 * @param <P> The object class being cached
 */
public interface EntityCache<P extends Persistable> extends InvalidatableCache {
	/**
	 * Caches the given object by all configured keys.
	 *
	 * @param obj the object to cache
	 * @return the (detached) cached object, which may be a different reference that what was given as a parameter
	 *
	 * @throws IllegalArgumentException if obj is null
	 * @throws EpServiceException if the cache is not configured correctly
	 */
	P put(P obj) throws EpServiceException;

	/**
	 * Retrieves an object from cache using the given key.
	 *
	 * @param keyName the name of the key to retrieve the object by
	 * @param keyValue the value of the key to retrieve
	 * @return the cached object
	 *
	 * @throws NullPointerException if either argument is null
	 */
	P get(String keyName, Object keyValue);
}
