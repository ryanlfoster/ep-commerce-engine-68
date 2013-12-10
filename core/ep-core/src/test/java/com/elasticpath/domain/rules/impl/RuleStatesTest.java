/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.domain.rules.RuleState;

/** Test cases for <code>RuleState</code>. */
public class RuleStatesTest {

	private static final int ACTIVE_INDEX = 0;
	private static final int DISABLED_INDEX = 1;
	private static final int EXPIRED_INDEX = 2;
	

	/**
	 * Test for setting/getting the rule name.
	 */
	@Test
	public void testSetRuleState() {
		
		RuleState state = RuleState.ACTIVE;
		assertEquals("ACTIVE", state.toString());
		assertEquals(ACTIVE_INDEX, state.getIndex());
		
		state = RuleState.DISABLED;
		assertEquals("DISABLED", state.toString());
		assertEquals(DISABLED_INDEX, state.getIndex());

		state = RuleState.EXPIRED;
		assertEquals("EXPIRED", state.toString());
		assertEquals(EXPIRED_INDEX, state.getIndex());
	}
}
