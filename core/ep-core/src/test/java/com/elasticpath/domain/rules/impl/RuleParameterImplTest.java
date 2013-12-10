/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/** Test cases for <code>RuleParameterImpl</code>.*/
public class RuleParameterImplTest {

	private RuleParameterImpl ruleParameterImpl;

	@Before
	public void setUp() throws Exception {
		ruleParameterImpl = new RuleParameterImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.RuleParameterImpl.getKey()'.
	 */
	@Test
	public void testGetSetKey() {
		final String key = "testKey";
		ruleParameterImpl.setKey(key);
		assertEquals(key, ruleParameterImpl.getKey());
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.RuleParameterImpl.getValue()'.
	 */
	@Test
	public void testGetSetValue() {
		final String value = "testValue";
		ruleParameterImpl.setValue(value);
		assertEquals(value, ruleParameterImpl.getValue());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.RuleParameterImpl.getValue()'.
	 */
	@Test
	public void testGetDisplayText() {
		final String displayText = "testText";
		ruleParameterImpl.setDisplayText(displayText);
		assertEquals(displayText, ruleParameterImpl.getDisplayText());
	}
}