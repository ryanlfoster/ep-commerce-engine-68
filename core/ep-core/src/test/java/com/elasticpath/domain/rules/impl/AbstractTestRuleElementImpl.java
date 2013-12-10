/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleParameter;

/**
 * Base class for <code>RuleElement</code> test cases. 
 * The awkward class name is because the class can't end in "Test" because it's abstract.
 */
public abstract class AbstractTestRuleElementImpl {

	private RuleElement ruleElementImpl;

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
		ruleElementImpl = createTestRuleElement();
	}

	/** 
	 * Factory method called by subclasses to specify the <code>RuleElement</code> implementation to test. 
	 * @return the RuleElement to test
	 */
	protected abstract RuleElement createTestRuleElement();
	
	/** 
	 * Factory method called by subclasses to specify a valid set of parameters. 
	 * @return a set of parameters 
	 */
	protected abstract Set<RuleParameter> getValidParameterSet();

	/** 
	 * Implemented by subclasses to check that the code is valid. 
	 * @param code the generated code
	 */
	protected abstract void checkGeneratedCode(final String code);
	
	/**
	 * Test method for rule element kind.
	 */
	@Test
	public void testRuleClassWElementKind() { 
		String elementKind = ruleElementImpl.getKind();
		assertNotNull(elementKind);
		
		if (elementKind.equals(RuleCondition.CONDITION_KIND)) { 
			assertTrue(RuleCondition.class.isAssignableFrom(ruleElementImpl.getClass()));
		} else if (elementKind.equals(RuleAction.ACTION_KIND)) { 
			assertTrue(RuleAction.class.isAssignableFrom(ruleElementImpl.getClass()));
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleElementImpl.getElementType()'.
	 */
	@Test
	public void testGetElementType() {
		assertNotNull(ruleElementImpl.getType());
	}
	
	/**
	 * Dummy driver for this method.
	 */
	@Test
	public void testScenarioApplication() {
		assertFalse(ruleElementImpl.appliesInScenario(-1));
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.ProductCategoryConditionImpl.getRuleCode()'. Simple smoke test only.
	 */
	@Test
	public void testGetRuleCode() {
		
		// Should fail because expected parameter isn't set
		try {
			ruleElementImpl.getRuleCode();
			if (ruleElementImpl.getParameterKeys().length > 0) {
				fail("EpDomain exception expected");
			}
		} catch (EpDomainException epde) {
			assertNotNull(epde);
		}

		// Add the needed parameter
		ruleElementImpl.setParameters(getValidParameterSet());

		checkGeneratedCode(ruleElementImpl.getRuleCode());
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.ProductCategoryConditionImpl.validate()'.
	 */
	@Test
	public void testValidate() {
		try {
			ruleElementImpl.validate();
			if (ruleElementImpl.getParameterKeys().length > 0) {
				fail("EpDomain exception expected");
			}
		} catch (EpDomainException epde) {
			assertNotNull(epde);
		}

		ruleElementImpl.setParameters(getGenericParameterSet());

		// Should still fail because the needed parameter isn't set
		try {
			ruleElementImpl.validate();
			if (ruleElementImpl.getParameterKeys().length > 0) {
				fail("EpDomain exception expected");
			}
		} catch (EpDomainException epde) {
			assertNotNull(epde);
		}

		// Add the needed parameter
		ruleElementImpl.setParameters(getValidParameterSet());

		ruleElementImpl.validate(); // Should not throw exception
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
