package com.elasticpath.service.catalog;

import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ItemConfigurationMemento.ItemConfigurationId;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.EpService;


/**
 * Provides operations on {@link ItemConfiguration}s.
 */
public interface ItemConfigurationService extends EpService {

	/**
	 * Gets the item configuration ID for the default configuration of the given product.
	 *
	 * @param product the product
	 * @param shopper the shopper
	 * @return the default configuration id
	 */
	ItemConfigurationId getDefaultItemConfigurationId(Product product, Shopper shopper);

	/**
	 * Save configured item.
	 *
	 * @param itemConfiguration the item configuration
	 * @return the ID of the persisted root item configuration
	 */
	ItemConfigurationId saveConfiguredItem(ItemConfiguration itemConfiguration);

	/**
	 * Loads an {@link ItemConfiguration} from the given id, and initializes a builder with the configuration.
	 *
	 * @param itemConfigurationId the item configuration ID
	 * @return the item configuration builder, if an item configuration is found, <code>null</code> otherwise.
	 */
	ItemConfigurationBuilder loadBuilder(ItemConfigurationId itemConfigurationId);
	
	/**
	 * Load an {@link ItemConfiguration}.
	 * 
	 * @param itemConfigurationId the item configuration id
	 * @return the item configuration, if an item configuration is found, <code>null</code> otherwise.
	 */
	ItemConfiguration load(ItemConfigurationId itemConfigurationId);
}
