package com.elasticpath.domain.customer.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Result object so we can check what happened granularly. Useful in testing. 
 */
public class SessionCleanupResult {
	private final Map<String, Integer> batchesPerStore = new HashMap<String, Integer>();
	private final Map<String, Integer> cleanedPerStore = new HashMap<String, Integer>();
	private final Map<String, Long> durationPerStore = new HashMap<String, Long>();
	
	/**
	 * @param storeCode the store code
	 * @param batches number of batches
	 */
	public void setBatchesForStore(final String storeCode, final int batches) {
		batchesPerStore.put(storeCode, batches);
	}
	
	/**
	 * @param storeCode the store code
	 * @param cleaned the number cleaned up
	 */
	public void setCleanedForStore(final String storeCode, final int cleaned) {
		cleanedPerStore.put(storeCode, cleaned);
	}

	/**
	 * @param storeCode the store code
	 * @param duration the duration in millis
	 */
	public void setDurationForStore(final String storeCode, final long duration) {
		durationPerStore.put(storeCode, duration);
	}

	/**
	 * Store all the results for a single store.  
	 *
	 * @param storeCode the store code
	 * @param batches number of batches
	 * @param cleaned the number cleaned up
	 * @param duration the duration in millis
	 */
	public void setResultsForStore(final String storeCode, final int batches, final int cleaned, final long duration) {
		setBatchesForStore(storeCode, batches);
		setCleanedForStore(storeCode, cleaned);
		setDurationForStore(storeCode, duration);
	}
	
	/**
	 * @return the batchsPerStore
	 */
	public Map<String, Integer> getBatchesPerStore() {
		return batchesPerStore;
	}

	/**
	 * @return the cleanedPerStore
	 */
	public Map<String, Integer> getCleanedPerStore() {
		return cleanedPerStore;
	}

	/**
	 * @return the durationPerStore
	 */
	public Map<String, Long> getDurationPerStore() {
		return durationPerStore;
	}
	
	/**
	 * Get the total duration in millis.
	 *
	 * @return sum of all store durations.
	 */
	public long getTotalDuration() {
		long total = 0;
		for (Long duration : durationPerStore.values()) {
			total += duration;
		}
		return total;
	}
		
	/**
	 * @param storeCode the store code
	 * @return the batchsPerStore
	 */
	public Integer getBatchesPerStore(final String storeCode) {
		return batchesPerStore.get(storeCode);
	}
	
	/**
	 * @param storeCode the store code
	 * @return the cleanedPerStore
	 */
	public Integer getCleanedPerStore(final String storeCode) {
		return cleanedPerStore.get(storeCode);
	}
	
	/**
	 * @param storeCode the store code
	 * @return the durationPerStore
	 */
	public Long getDurationPerStore(final String storeCode) {
		return durationPerStore.get(storeCode);
	}
}

