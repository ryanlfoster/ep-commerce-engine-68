/**
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.service.catalogview.AvailabilityStrategy;

/**
 * Test that {@link WhenInStockAvailabilityStrategyImpl} behaves as expected.
 */
public class WhenInStockAvailabilityStrategyImplTest {

	private final AvailabilityStrategy strategy = new WhenInStockAvailabilityStrategyImpl();
	
	/**
	 * Test that get availability is null for non stock related criteria.
	 */
	@Test
	public void testGetAvailabilityIsNullForNonStockRelatedCriteria() {
		Product product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER);
		Availability availability = strategy.getAvailability(product, true, true, true);
		assertNull("A Null should be returned when using a non stock-related criteria", availability);
	}
	
	/**
	 * Test the behaviour of get availability when the product is not available.
	 */
	@Test
	public void testGetAvailabilityWhenNotAvailable() {
		Product product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		Availability availability = strategy.getAvailability(product, false, true, true);
		assertEquals("The product should not be available", Availability.NOT_AVAILABLE, availability);
	}
	
	/**
	 * Test the behaviour of get availability when the product is in stock.
	 */
	@Test
	public void testGetAvailabilityWhenInStock() {
		Product product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		Availability availability = strategy.getAvailability(product, true, true, true);
		assertEquals("The product should be available", Availability.AVAILABLE, availability);
	}

}
