package com.elasticpath.domain.catalog;

import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents a tuner to control category type load. A category type load tuner can be used in some services to fine control what data to be loaded
 * for a category type. The main purpose is to achieve maximum performance for some specific performance-critical pages.
 */
public interface CategoryTypeLoadTuner extends LoadTuner {

	/**
	 * Return <code>true</code> if attributes is requested.
	 * 
	 * @return <code>true</code> if attributes is requested.
	 */
	boolean isLoadingAttributes();

	/**
	 * Sets the flag of loading attributes.
	 * 
	 * @param flag sets it to <code>true</code> to request loading attributes.
	 */
	void setLoadingAttributes(boolean flag);
	
	/**
	 * Returns <code>true</code> if this load tuner is super set of the given load tuner,
	 * otherwise, <code>false</code>.
	 * 
	 * @param categoryTypeLoadTuner the category type load tuner
	 * @return <code>true</code> if this load tuner is super set of the given load tuner,
	 *         otherwise, <code>false</code>
	 */
	boolean contains(CategoryTypeLoadTuner categoryTypeLoadTuner);

	/**
	 * Merges the given load tuner with this one and returns the merged load tuner.
	 * 
	 * @param categoryTypeLoadTuner the category type load tuner
	 * @return the merged load tuner
	 */
	CategoryTypeLoadTuner merge(CategoryTypeLoadTuner categoryTypeLoadTuner);
}
