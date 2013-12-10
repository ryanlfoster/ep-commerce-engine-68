package com.elasticpath.domain.shipping.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Test assertions for shipping cost calculations. 
 */
public final class ShippingAsserts {

	private ShippingAsserts() {
		// Prevent external instantiations
	}
	
	/**
	 * Assert that the shipping cost is as expected.
	 * @param expectedCost the expected shipping cost.
	 * @param method the method to ask to calculate the shipping cost.
	 * @param shoppingCart the cart to calculate the shipping cost for.
	 */
	public static void assertShippingCost(final String expectedCost, final ShippingCostCalculationMethod method, final ShoppingCart shoppingCart) { 
		assertEquals(
				new BigDecimal(expectedCost), 
				method.calculateShippingCost(shoppingCart).getAmount());
	}
	
}
