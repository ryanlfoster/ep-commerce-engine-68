/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.rules.RuleScenarios;

/** Test cases for <code>RuleScenariosImpl</code>. */
public class RuleScenariosImplTest {

	private static final int NUM_IMPLEMENTED_SCENARIOS = 2;
	private RuleScenarios ruleScenarios;

	@Before
	public void setUp() throws Exception {
		ruleScenarios = new RuleScenariosImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.RuleScenariosImpl.getAvailableScenarios()'.
	 */
	@Test
	public void testGetAvailableScenarios() {
		assertTrue(ruleScenarios.getAvailableScenarios().size() >= NUM_IMPLEMENTED_SCENARIOS);
	}

}
