/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import java.util.HashSet;
import java.util.Set;

import com.elasticpath.domain.rules.RuleScenarios;

/**
 * Represents a collection of available scenarios.
 */
public class RuleScenariosImpl implements RuleScenarios {
	/**
	 * Serial version id.
	 */
	public static final long serialVersionUID = 5000000001L;

	private Set<Integer> scenarios;

	/**
	 * Get a list of the ids as Integers for all implemented scenarios.
	 *
	 * @return a list of scenario ids as Integers
	 */
	public Set<Integer> getAvailableScenarios() {
		if (scenarios == null) {
			scenarios = new HashSet<Integer>();
			scenarios.add(RuleScenarios.CART_SCENARIO);
			scenarios.add(RuleScenarios.CATALOG_BROWSE_SCENARIO);
		}
		return scenarios;
	}

}
