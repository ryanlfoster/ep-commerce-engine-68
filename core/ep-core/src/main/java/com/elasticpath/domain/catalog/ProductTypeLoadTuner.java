package com.elasticpath.domain.catalog;

import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents a tuner to control product type load. A product type load tuner can be used in some services to fine control what data to be loaded for
 * a product type. The main purpose is to achieve maximum performance for some specific performance-critical pages.
 */
public interface ProductTypeLoadTuner extends LoadTuner {

	/**
	 * Return <code>true</code> if attributes is requested.
	 * 
	 * @return <code>true</code> if attributes is requested.
	 */
	boolean isLoadingAttributes();

	/**
	 * Return <code>true</code> if sku options is requested.
	 * 
	 * @return <code>true</code> if sku options is requested.
	 */
	boolean isLoadingSkuOptions();

	/**
	 * Sets the flag of loading attributes.
	 * 
	 * @param flag sets it to <code>true</code> to request loading attributes.
	 */
	void setLoadingAttributes(boolean flag);

	/**
	 * Sets the flag of loading sku options.
	 * 
	 * @param flag sets it to <code>true</code> to request loading sku options.
	 */
	void setLoadingSkuOptions(boolean flag);

	/**
	 * Returns <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>.
	 * 
	 * @param productTypeLoadTuner the product type load tuner
	 * @return <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>
	 */
	boolean contains(ProductTypeLoadTuner productTypeLoadTuner);

	/**
	 * Merges the given product type load tuner with this one and returns the merged load tuner.
	 * 
	 * @param productTypeLoadTuner the product type load tuner
	 * @return the merged load tuner
	 */
	ProductTypeLoadTuner merge(ProductTypeLoadTuner productTypeLoadTuner);
}
