package com.elasticpath.domain.catalog;

import com.elasticpath.persistence.api.LoadTuner;


/**
 * Represents a tuner to control product association load. A product association load tuner can be used in some services to 
 * fine control what data to be loaded for a product association. The main purpose is to achieve maximum performance
 * for some specific performance-critical pages.
 */
public interface ProductAssociationLoadTuner extends LoadTuner {

	/**
	 * Return <code>true</code> if the catalog is requested. 
	 * 
	 * @return true if the catalog should be loaded.
	 */
	boolean isLoadingCatalog();

	/**
	 * Set the flag indicating whether catalog should be loaded.
	 * 
	 * @param loadingCatalog should be set to true if the catalog should be loaded 
	 */
	void setLoadingCatalog(final boolean loadingCatalog);

	/**
	 * Get the <code>ProductLoadTuner</code> that should be used for loading the products.
	 *  
	 * @return the productLoadTuner
	 */
	ProductLoadTuner getProductLoadTuner();

	/**
	 * Set the <code>ProductLoadTuner</code> that should be used for loading the products in the association.
	 * 
	 * @param productLoadTuner the productLoadTuner to set
	 */
	void setProductLoadTuner(final ProductLoadTuner productLoadTuner);

}