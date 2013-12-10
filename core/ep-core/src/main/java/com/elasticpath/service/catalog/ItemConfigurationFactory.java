package com.elasticpath.service.catalog;

import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.shopper.Shopper;

/**
 * A factory for creating {@link ItemConfiguration} objects.
 */
public interface ItemConfigurationFactory {

	/**
	 * Creates a new {@link ItemConfiguration} object.
	 *
	 * @param product the product
	 * @param shopper the shopper
	 * @return the item configuration
	 */
	ItemConfiguration createItemConfiguration(Product product, Shopper shopper);
}
