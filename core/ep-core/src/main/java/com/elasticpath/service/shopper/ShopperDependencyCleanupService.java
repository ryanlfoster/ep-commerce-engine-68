package com.elasticpath.service.shopper;

import java.util.List;

/**
 * Removes dependents from a shopper to prepare it to be removed. 
 */
public interface ShopperDependencyCleanupService {
	
	/** 
	 * Removes the elements that have FK dependencies on shopper.
	 *  
	 * @param mementoUidsToRemove list of CustomerSessoinMemento UIDs to be removed
	 */
	void removeDependantsFromShopper(final List<Long> mementoUidsToRemove);
}
