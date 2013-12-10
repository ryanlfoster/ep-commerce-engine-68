package com.elasticpath.domain.catalog;

import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents a tuner to control productsku load. A product load tuner can be used in some services to fine control what data to be loaded for a
 * productsku. The main purpose is to achieve maximum performance for some specific performance-critical pages.
 */
public interface ProductSkuLoadTuner extends LoadTuner {

	/**
	 * Return <code>true</code> if attribute value is requested.
	 *
	 * @return <code>true</code> if attribute value is requested.
	 */
	boolean isLoadingAttributeValue();

	/**
	 * Return <code>true</code> if option value is requested.
	 *
	 * @return <code>true</code> if option value is requested.
	 */
	boolean isLoadingOptionValue();

	/**
	 * Return <code>true</code> if product is requested.
	 *
	 * @return <code>true</code> if product is requested.
	 */
	boolean isLoadingProduct();

	/**
	 * Return <code>true</code> if digital asset is requested.
	 *
	 * @return <code>true</code> if digital asset is requested.
	 */
	boolean isLoadingDigitalAsset();

	/**
	 * Sets the flag of loading attribute values.
	 *
	 * @param flag sets it to <code>true</code> to request loading attribute values.
	 */
	void setLoadingAttributeValue(boolean flag);

	/**
	 * Sets the flag of loading option values.
	 *
	 * @param flag sets it to <code>true</code> to request loading option values.
	 */
	void setLoadingOptionValue(boolean flag);

	/**
	 * Sets the flag of loading product.
	 *
	 * @param flag sets it to <code>true</code> to request loading product.
	 */
	void setLoadingProduct(boolean flag);

	/**
	 * Sets the flag of loading digital asset.
	 *
	 * @param flag sets it to <code>true</code> to request loading digital asset.
	 */
	void setLoadingDigitalAsset(boolean flag);

	/**
	 * Returns <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>.
	 *
	 * @param productSkuLoadTuner the sku load tuner
	 * @return <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>
	 */
	boolean contains(ProductSkuLoadTuner productSkuLoadTuner);

	/**
	 * Merges the given load tuner with this one and returns the merged load tuner.
	 *
	 * @param productSkuLoadTuner the product sku load tuner
	 * @return the merged load tuner
	 */
	ProductSkuLoadTuner merge(ProductSkuLoadTuner productSkuLoadTuner);
}
