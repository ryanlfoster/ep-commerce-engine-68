/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules;

import java.util.Set;

/**
 * Defines constants that identify valid rule scenarios.
 */
public interface RuleScenarios {

	/**
	 * CART Scenario.
	 * Customer views their shopping cart.
	 */
	int CART_SCENARIO = 1;
	
	/**
	 * Catalog Scenario.
	 * Customer views products and product variations.
	 */
	int CATALOG_BROWSE_SCENARIO = 2;
	
	/**
	 * Get a list of the ids as Integers for all
	 * implemented scenarios.
	 * @return a list of scenario ids as Integers
	 */
	Set<Integer> getAvailableScenarios();
	
}
