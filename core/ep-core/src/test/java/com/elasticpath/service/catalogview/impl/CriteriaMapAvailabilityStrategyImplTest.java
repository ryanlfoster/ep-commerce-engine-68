package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;

/**
 * Test that {@link CriteriaMapAvailabilityStrategyImpl} behaves as expected.
 */
public class CriteriaMapAvailabilityStrategyImplTest {

	private final CriteriaMapAvailabilityStrategyImpl strategy = new CriteriaMapAvailabilityStrategyImpl();
	private final Map<AvailabilityCriteria, Availability> criteriaMap = new HashMap<AvailabilityCriteria, Availability>();
	
	/**
	 * Setup required for each test.
	 */
	@Before
	public void setUp() {
		criteriaMap.put(AvailabilityCriteria.ALWAYS_AVAILABLE, Availability.AVAILABLE);
		criteriaMap.put(AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER, Availability.AVAILABLE_FOR_BACK_ORDER);
		criteriaMap.put(AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER, Availability.AVAILABLE_FOR_PRE_ORDER);
		strategy.setCriteriaMap(criteriaMap);
	}
	
	/**
	 * Test that get availability is null when criteria not in map.
	 */
	@Test
	public void testGetAvailabilityIsNullWhenCriteriaNotInMap() {
		Product product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		Availability availability = strategy.getAvailability(product, true, true, true);
		assertNull("The availability should be null if the criteria is not in the map", availability);
	}
	
	/**
	 * Test that get availability uses the criteria map.
	 */
	@Test
	public void testGetAvailabilityUsesMap() {
		Product product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);
		Availability availability = strategy.getAvailability(product, true, true, true);
		assertEquals("The availability should match the mapped value", Availability.AVAILABLE, availability);
	}

}
