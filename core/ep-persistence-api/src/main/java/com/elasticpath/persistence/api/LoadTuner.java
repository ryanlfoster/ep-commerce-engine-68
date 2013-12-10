package com.elasticpath.persistence.api;

/**
 * A tagging interface for a load tuner.
 */
public interface LoadTuner {

	/**
	 * Returns <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>.
	 *
	 * @param loadTuner the load tuner
	 * @return <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>
	 */
	boolean contains(LoadTuner loadTuner);

	/**
	 * Merges the given load tuner with this one and returns the merged load tuner.
	 *
	 * @param loadTuner the load tuner
	 * @return the merged load tuner
	 */
	LoadTuner merge(LoadTuner loadTuner);

}
