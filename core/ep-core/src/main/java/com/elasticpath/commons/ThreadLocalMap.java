package com.elasticpath.commons;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * The class contains Thread Local variable.
 * 
 * @param <K> the type of key
 * @param <V> the type of value
 */
public class ThreadLocalMap <K, V> implements Map<K, V> {
	private final ThreadLocal<Map<K, V>> tlMap = new ThreadLocal<Map<K, V>>() {
	    @Override
		protected Map<K, V> initialValue() {	    	
	    	synchronized (tlMap) {
	    		return new HashMap<K, V>();
			}	    			   
	    }
	};
	
	/**
	 * Put the key/value pair into the map in thread local.
	 * 
	 * @param key the key 
	 * @param value the value
	 * @return previous value associated with specified key, or null  if there was no mapping for key.
	 */
	public V put(final K key, final V value) {
		return tlMap.get().put(key, value);
	}
	
	/**
	 * Get the value by the key from the map in the thread local.
	 * 
	 * @param key the key
	 * @return the value
	 */
	public V get(final Object key) {
		return tlMap.get().get(key);
	}
	
	/**
	 * Remove all mappings from the map in the thread local. 
	 */
	public void clear() {
		tlMap.get().clear();
	}

	/**
	 * Returns true if the map in the thread local contains a mapping for the specified key.
	 * 
	 * @param key The key whose presence in this map is to be tested
	 * @return true if the map contains a mapping for the specified key.
	 */
	public boolean containsKey(final Object key) {
		return tlMap.get().containsKey(key);
	}

	/**
	 * Returns true if the map in the thread local maps one or more keys to the specified value.
	 * 
	 * @param value value whose presence in this map is to be tested.
	 * @return true if one or more keys are mapped to the specified value.
	 */
	public boolean containsValue(final Object value) {
		return tlMap.get().containsValue(value);
	}

	/**
	 * Returns a collection view of the mappings contained in the map in the thread local.
	 * 
	 * @return a collection view of the mappings.
	 */
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return tlMap.get().entrySet();
	}

	/**
	 * Returns true if the map in the thread local contains no key-value mappings.
	 *  
	 * @return true if there are no key-value mappings.
	 */
	public boolean isEmpty() {
		return tlMap.get().isEmpty();
	}

	/**
	 * Returns a set view of the keys contained in the map in the thread local.
	 * 
	 * @return a set view of the keys
	 */
	public Set<K> keySet() {
		return tlMap.get().keySet();
	}

	/**
	 * Copies all of the mappings from the specified map to the map in the thread local.
	 * 
	 * @param sourceMap mappings to be stored in this map
	 */
	public void putAll(final Map< ? extends K, ? extends V> sourceMap) {
		tlMap.get().putAll(sourceMap);
	}

	/**
	 * Removes the mapping for this key from the map in the thread local if present.
	 * 
	 * @param key key whose mapping is to be removed from the map.
	 * @return previous value associated with specified key, or null  if there was no mapping for key.
	 */
	public V remove(final Object key) {
		return tlMap.get().remove(key);
	}

	/**
	 * Returns the number of key-value mappings in the map in the thread local.
	 * 
	 * @return the number of key-value mappings
	 */
	public int size() {
		return tlMap.get().size();
	}

	/**
	 * Returns a collection view of the values contained in the map in the thread local.
	 * 
	 * @return a collection view of the values
	 */
	public Collection<V> values() {
		return tlMap.get().values();
	}
}
