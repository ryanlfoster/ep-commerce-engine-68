package com.elasticpath.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a simple timeout cache. 
 * An item is removed from cache upon a call to <tt>get()</tt> if the item has
 * reached the timeout set.  <tt>null</tt> cannot sensibly be cached.
 * 
 * @param <K> the type used as a key into the cache.
 * @param <V> the type to actually cache.
 */
@SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
public class SimpleTimeoutCache<K, V> {

	private final Map<K, ExpiringCacheObject<V>> cache = new HashMap<K, ExpiringCacheObject<V>>();
	private long timeout;
	
	/**
	 * Creates an instance of <tt>TimeLimitedCache</tt> with the
	 * given <tt>timeout</tt>.
	 *
	 * @param timeout - The time in milliseconds that an item in cache will become stale.
	 */
	public SimpleTimeoutCache(final long timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * Returns the value associated with the specified <tt>key</tt>. Returns
	 * <tt>null</tt> if the cache contains no association for this key or if
	 * the cached item associated with the key has timed out.
	 *
	 * @param key - Key whose associated value will be returned.
	 * @return The value associated with the key if found, or
	 *         <tt>null</tt> if there is no associated value or it is timed out.
	 */
	public synchronized V get(final K key) {
		
		ExpiringCacheObject<V> cacheObject = cache.get(key);
		
		if (cacheObject == null) {
			return null;
		}
		
		if (cacheObject.getValue() == null || needsRefresh(cacheObject)) {
			final ExpiringCacheObject<V> cacheRemovedObject = cache.remove(key);
			beforeRemoveHook(key, cacheRemovedObject.getValue());
			return null;
		}
		
		return cacheObject.getValue();
	}
	
	/**
	 * Hook to perform last minute processing of the cached value to be removed.
	 * 
	 * @param key the value key
	 * @param value the cached value itself
	 */
	protected void beforeRemoveHook(final K key, final V value) {
		// no default action
	}
	
	/**
	 * Stores the <tt>key</tt>/<tt>value</tt> association into the cache.
	 *
	 * @param key - The identifier to associate the value with in the cache.
	 * @param value - The value to be stored in the cache.
	 */
	public synchronized void put(final K key, final V value) {
		cache.put(key, new ExpiringCacheObject<V>(value, getCurrentTimeMillis() + timeout)); 
	}
	
	/**
	 * Returns true if the cached item associated with the <tt>key</tt>
	 * has timed out and needs to be removed from the cache.
	 *
	 * @param ket - The key associated with the item to check whether it needs to be refreshed. 
	 * @return <tt>true</tt> if the cached item associated with the <tt>key</tt> needs to be removed from cache, or
	 *         <tt>false</tt> otherwise.
	 */
	private boolean needsRefresh(final ExpiringCacheObject<V> cacheObject) {
		return getCurrentTimeMillis() >= cacheObject.getExpiryTime();
	}
	
	/**
	 * Returns the current time in milliseconds. This method exists to make testing easier.
	 *
	 * @return Current time in milliseconds.
	 */
	long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * Set the expiry milliseconds for cache entries added after this call - current 
	 * entry's expiry timeout will be unaffected (it was determined when the entry was
	 * added).
	 *
	 * @param cacheTimeoutMillis the number of milliseconds after which new cache entries
	 *        will be expired from the cache.
	 */
	public synchronized void setTimeout(final long cacheTimeoutMillis) {
		this.timeout = cacheTimeoutMillis;
	}
	
	/**
	 * Clears the cache.
	 */
	public synchronized void clear() {
		this.cache.clear();
	}
	
	/**
	 * Simple object to put in cache that groups a timeout and the object 
	 * to actual cache.
	 *
	 * @param <T> the type of the object to cache.
	 */
	private static class ExpiringCacheObject<T> {
		
		private final long expiryTime;
		private final T value;

		/**
		 * 
		 * @param value the actual value to cache
		 * @param expiryTime the time when the value should be considered 'expired'.
		 */
		public ExpiringCacheObject(final T value, final long expiryTime) {
			this.value = value;
			this.expiryTime = expiryTime;
		}
		
		public T getValue() {
			return value;
		}
		
		public long getExpiryTime() {
			return expiryTime;
		}
	}
}
