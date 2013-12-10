/**
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.service.catalogview;

import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.Product;

/**
 * Methods for getting a product's availability status.
 */
public interface AvailabilityStrategy {

	/**
	 * Gets the availability.
	 *
	 * @param product the product
	 * @param isAvailable whether the product is available
	 * @param isDisplayable whether the product is displayable
	 * @param isPurchasable whether the product is purchasable
	 * @return the availability
	 */
	Availability getAvailability(Product product, boolean isAvailable, boolean isDisplayable, boolean isPurchasable);
}
