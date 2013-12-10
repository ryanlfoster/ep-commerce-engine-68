/**
 * 
 */
package com.elasticpath.domain.catalog;

/**
 * Adds functionality to the ProductLoadTuner that is specific for Stores.
 * Since a Store knows what Catalog it's using, you can specify whether
 * or not to load the ProductAssociations from that catalog for the 
 * StoreProduct. 
 */
public interface StoreProductLoadTuner extends ProductLoadTuner {

	/**
	 * Return <code>true</code> if product association is requested.
	 * 
	 * @return <code>true</code> if product association is requested.
	 */
	boolean isLoadingProductAssociations();
	
	/**
	 * Sets the flag of loading product associations.
	 * 
	 * @param flag sets it to <code>true</code> to request loading product associations.
	 */
	void setLoadingProductAssociations(boolean flag);
}
