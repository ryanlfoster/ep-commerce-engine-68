package com.elasticpath.domain.catalog;

import java.io.Serializable;


/**
 * Methods to describe the characteristics of a product.
 */
public interface ProductCharacteristics extends Serializable {

	/**
	 * Checks for multiple configurations.
	 *
	 * @return true, if successful
	 */
	boolean hasMultipleConfigurations();
	
	/**
	 * @return <code>true</code> iff the product is a bundle
	 */
	boolean isBundle();
	
	/**
	 * @return <code>true</code> iff the product is a calculated bundle
	 */
	boolean isCalculatedBundle();
	
	/**
	 * @return <code>true</code> iff the product is a dynamic bundle
	 */
	boolean isDynamicBundle();
	
	/**
	 * Gets the uid for the bundle.
	 *
	 * @return the bundle uid, or null if not a bundle.
	 */
	Long getBundleUid();
	
}
