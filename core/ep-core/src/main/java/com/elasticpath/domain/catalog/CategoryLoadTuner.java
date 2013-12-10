package com.elasticpath.domain.catalog;

import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents a tuner to control category load. A category load tuner can be used in some services to fine control what data to be loaded for a
 * category. The main purpose is to achieve better performance for some specific performance-critical pages.
 */
public interface CategoryLoadTuner extends LoadTuner {

	/**
	 * Return <code>true</code> if children is requested.
	 * 
	 * @return <code>true</code> if children is requested.
	 */
	boolean isLoadingChildren();

	/**
	 * Return <code>true</code> if parent is requested.
	 * 
	 * @return <code>true</code> if parent is requested.
	 */
	boolean isLoadingParent();

	/**
	 * Return <code>true</code> if master is requested.
	 * 
	 * @return <code>true</code> if master is requested.
	 */
	boolean isLoadingMaster();
	
	/**
	 * Return <code>true</code> if category type requested.
	 * 
	 * @return <code>true</code> if category type requested.
	 */
	boolean isLoadingCategoryType();

	/**
	 * Return <code>true</code> if attribute value is requested.
	 * 
	 * @return <code>true</code> if attribute value is requested.
	 */
	boolean isLoadingAttributeValue();

	/**
	 * Sets the flag of loading children.
	 * 
	 * @param flag sets it to <code>true</code> to request loading children.
	 */
	void setLoadingChildren(boolean flag);

	/**
	 * Sets the flag of loading parent.
	 * 
	 * @param flag sets it to <code>true</code> to request loading parent.
	 */
	void setLoadingParent(boolean flag);
	
	/**
	 * Sets the flag of loading master.
	 * 
	 * @param flag sets it to <code>true</code> to request loading master.
	 */
	void setLoadingMaster(final boolean flag);

	/**
	 * Sets the flag of loading category type.
	 * 
	 * @param flag sets it to <code>true</code> to request loading category type.
	 */
	void setLoadingCategoryType(boolean flag);

	/**
	 * Sets the flag of loading attribute values.
	 * 
	 * @param flag sets it to <code>true</code> to request loading attribute values.
	 */
	void setLoadingAttributeValue(boolean flag);

	/**
	 * Sets the <code>CategoryTypeLoadTuner</code>.
	 * 
	 * @param tuner the <code>CategoryTypeLoadTuner</code>
	 */
	void setCategoryTypeLoadTuner(CategoryTypeLoadTuner tuner);

	/**
	 * Returns the <code>CategoryTypeLoadTuner</code>.
	 * 
	 * @return the <code>CategoryTypeLoadTuner</code>
	 */
	CategoryTypeLoadTuner getCategoryTypeLoadTuner();
	
	/**
	 * Gets whether we are loading locale dependant fields.
	 *
	 * @return whether we are loading locale dependant fields
	 */
	boolean isLoadingLocaleDependantFields();

	/**
	 * Sets whether we are loading locale dependant fields.
	 *
	 * @param loadingLocaleDependantFields whether we are loading locale dependant fields
	 */
	void setLoadingLocaleDependantFields(final boolean loadingLocaleDependantFields);
	
	/**
	 * Returns <code>true</code> if this load tuner is super set of the given load tuner,
	 * otherwise, <code>false</code>.
	 * 
	 * @param categoryLoadTuner the category load tuner
	 * @return <code>true</code> if this load tuner is super set of the given load tuner,
	 *         otherwise, <code>false</code>
	 */
	boolean contains(CategoryLoadTuner categoryLoadTuner);

	/**
	 * Merges the given load tuner with this one and returns the merged load tuner.
	 * 
	 * @param categoryLoadTuner the category load tuner
	 * @return the merged load tuner
	 */
	CategoryLoadTuner merge(CategoryLoadTuner categoryLoadTuner);
	
	/**
	 * Set the child category level. Currently, only support level 1 or infinite (-1). Default is 1.
	 * @param childCategoryLevel the child category level to be loaded recursively
	 */
	void setChildCategoryLevel(int childCategoryLevel);
	
	/**
	 * Get the child category level to be loaded, default is 1.
	 * @return child category level
	 */
	int getChildCategoryLevel();
	
	/**
	 * Set the parent category level. Currently, only support level 1 or infinite (-1). Default is 1.
	 * @param parentCategoryLevel the parent category level to be loaded recursively
	 */
	void setParentCategoryLevel(int parentCategoryLevel);
	
	/**
	 * Get the parent category level to be loaded, default is 1.
	 * @return parent category level
	 */
	int getParentCategoryLevel();

}
