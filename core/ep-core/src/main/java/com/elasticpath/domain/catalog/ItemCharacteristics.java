package com.elasticpath.domain.catalog;


/**
 * Specifies characteristics of a specifically configured item.
 */
public interface ItemCharacteristics {

	/**
	 * Is the item shippable?
	 *
	 * @return true, if is shippable
	 */
	boolean isShippable();
	
	/**
	 * Checks if the item can have multiple configurations.
	 *
	 * @return true, if successful
	 */
	boolean hasMultipleConfigurations();

}
