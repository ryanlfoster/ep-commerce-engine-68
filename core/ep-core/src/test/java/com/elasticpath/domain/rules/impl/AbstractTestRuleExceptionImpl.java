/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.rules.RuleParameter;

/**
 * Base class for <code>RuleException</code> implementation test cases. 
 * The awkward class name is because the class can't end in "Test" because it's abstract.
 */
public abstract class AbstractTestRuleExceptionImpl {

	private AbstractRuleExceptionImpl exceptionImpl;

	private static final String KEY1 = "testKey1";

	private static final String VALUE1 = "testValue1";

	private static final String KEY2 = "testKey2";

	private static final String VALUE2 = "testValue2";

	/**
	 * Prepare for each test.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		exceptionImpl = createTestException();
		exceptionImpl.initialize();
	}

	/** 
	 * Factory method implemented by subclasses to specify the <code>RuleException</code> implementation to test.
	 * @return the RuleException to test 
	 */
	protected abstract AbstractRuleExceptionImpl createTestException();

	/** 
	 * Factory method implemented by subclasses to specify a valid set of parameters.
	 * @return a valid set of parameters 
	 */
	protected abstract Set<RuleParameter> getValidParameterSet();

	/** 
	 * Implemented by subclasses to check that the code is valid.
	 * @param code the generated code to test 
	 */
	protected abstract void checkGeneratedCode(final String code);

	/**
	 * Test method for getType().
	 */	
	@Test
	public void testGetType() {
		assertNotNull(exceptionImpl.getType());
	}

	/**
	 * Dummy driver for this method.
	 */
	@Test
	public void testScenarioApplication() {
		assertFalse(exceptionImpl.appliesInScenario(-1));
	}
	
//	/**
//	 * Test method for 'com.elasticpath.domain.rules.impl.ProductCategoryConditionImpl.getRuleCode()'. Simple smoke test only.
//	 */
//	public void testGetRuleCode() {
//
//		// Should fail because expected parameter isn't set
//		try {
//			exceptionImpl.getRuleCode();
//			fail("EpDomain exception expected");
//		} catch (EpDomainException epde) {
//			assertNotNull(epde);
//		}
//
//		// Add the needed parameter
//		exceptionImpl.setParameters(getValidParameterSet());
//		assertEquals(exceptionImpl.getParameters().size(), exceptionImpl.getParameterKeys().length);
//
//		checkGeneratedCode(exceptionImpl.getRuleCode());
//	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.ProductCategoryConditionImpl.validate()'.
	 */
	@Test
	public void testValidate() {
		try {
			exceptionImpl.validate();
			fail("EpDomain exception expected");
		} catch (EpDomainException epde) {
			assertNotNull(epde);
		}

		exceptionImpl.setParameters(getGenericParameterSet());

		// Should still fail because the needed parameter isn't set
		try {
			exceptionImpl.validate();
			fail("EpDomain exception expected");
		} catch (EpDomainException epde) {
			assertNotNull(epde);
		}

		// Add the needed parameter
		exceptionImpl.setParameters(getValidParameterSet());

		exceptionImpl.validate(); // Should not throw exception
	}

	private Set<RuleParameter> getGenericParameterSet() {
		Set<RuleParameter> paramSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(KEY1);
		ruleParameter.setValue(VALUE1);
		paramSet.add(ruleParameter);

		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(KEY2);
		ruleParameter.setValue(VALUE2);
		paramSet.add(ruleParameter);

		return paramSet;
	}
}