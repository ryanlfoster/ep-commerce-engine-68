/**
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.service.catalogview.impl;

import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.catalogview.AvailabilityStrategy;

/**
 * An {@link AvailabilityStrategy} that handles the {@link AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK} by
 * looking at the passed in {@code isAvailable} flag.
 * 
 * <ul>
 * <li>AVAILABLE_WHEN_IN_STOCK && isAvailable => AVAILABLE
 * <li>AVAILABLE_WHEN_IN_STOCK && !isAvailable => NOT_AVAILABLE
 * <li>All other criteria => null
 * </ul>
 */
public class WhenInStockAvailabilityStrategyImpl implements AvailabilityStrategy {

	@Override
	public Availability getAvailability(final Product product, final boolean isAvailable, final boolean isDisplayable, final boolean isPurchasable) {
		Availability availability = null;
		if (AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK.equals(product.getAvailabilityCriteria())) {
			if (isAvailable) {
				availability = Availability.AVAILABLE;
			} else {
				availability = Availability.NOT_AVAILABLE;
			}
		}
		return availability;
	}

}
