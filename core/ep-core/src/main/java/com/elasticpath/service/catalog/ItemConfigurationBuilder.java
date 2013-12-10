package com.elasticpath.service.catalog;

import java.util.List;

import com.elasticpath.domain.catalog.ItemConfiguration;

/**
 * Allows building an {@link ItemConfiguration}. It is intended to be used as a prototype scoped bean, and might not be 
 * thread-safe.
 */
public interface ItemConfigurationBuilder {

	/**
	 * Builds the item configuration.
	 *
	 * @return the item configuration
	 */
	ItemConfiguration build();
	
	/**
	 * Chooses the SKU identified by the sku code for the item at the given path.
	 *
	 * @param path a list of Strings denoting the IDs of the nodes leading to the item
	 * @param skuCode the SKU code to be selected
	 * @return the item configuration builder instance
	 */
	ItemConfigurationBuilder select(List<String> path, String skuCode);

	/**
	 * De-selects the item at the given path.
	 *
	 * @param path a list of Strings denoting the IDs of the nodes leading to the item
	 * @return the item configuration builder instance
	 */
	ItemConfigurationBuilder deselect(List<String> path);
	
	/**
	 * Validates the state of the builder.
	 *
	 * @return the item configuration validation result
	 */
	ItemConfigurationValidationResult validate();
}
