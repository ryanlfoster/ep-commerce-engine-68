/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleParameter;

/** Test cases for <code>CartCategoryPercentDiscountActionImpl</code>.*/
public class CartCategoryPercentDiscountActionImplTest extends AbstractTestRuleElementImpl {

	private static final String DISCOUNT_CATEGORY = "10";
	private static final String DISCOUNT_PERCENT = "50";
	private static final String NUM_ITEMS = "3";

	
	/**
	 * Factory method called by subclasses to specify the <code>RuleElement</code> implementation to test.
	 * @return the RuleElement to test
	 */
	@Override
	protected RuleElement createTestRuleElement() {
		return new CartCategoryPercentDiscountActionImpl();
	}
	
	/**
	 * Factory method called by subclasses to specify a valid set of parameters.
	 * @return the valid parameter set
	 */
	@Override
	protected Set<RuleParameter> getValidParameterSet() {
		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.DISCOUNT_PERCENT_KEY);
		ruleParameter.setValue(DISCOUNT_PERCENT);
		parameterSet.add(ruleParameter);
		
		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.CATEGORY_CODE_KEY);
		ruleParameter.setValue(DISCOUNT_CATEGORY);
		parameterSet.add(ruleParameter);
		
		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.NUM_ITEMS_KEY);
		ruleParameter.setValue(NUM_ITEMS);
		parameterSet.add(ruleParameter);
		
		return parameterSet;
	}
	
	/**
	 * Implemented by subclasses to check that the code is valid.
	 * @param code the generated code to be checked
	 */
	@Override
	protected void checkGeneratedCode(final String code) {
		assertTrue(code.indexOf("new CartCategoryPercentDiscountImpl") > 0);
		assertTrue(code.indexOf(DISCOUNT_PERCENT) > 0);
	}
	
	/**
	 * Test case for CartCategoryPercentDiscountActionImpl.validate().
	 */
	@Test
	public void testValidation() {
		RuleAction action = (RuleAction) createTestRuleElement();
		action.setParameters(getValidParameterSet());
		action.validate();
	}
	
	/**
	 * Test case for CartCategoryPercentDiscountActionImpl.validate().
	 */
	@Test
	public void testValidationFailure() {
		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.DISCOUNT_PERCENT_KEY);
		ruleParameter.setValue("101");
		parameterSet.add(ruleParameter);
		
		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.CATEGORY_CODE_KEY);
		ruleParameter.setValue(DISCOUNT_CATEGORY);
		parameterSet.add(ruleParameter);
		
		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.NUM_ITEMS_KEY);
		ruleParameter.setValue(NUM_ITEMS);
		parameterSet.add(ruleParameter);
		
		RuleAction action = (RuleAction) createTestRuleElement();
		action.setParameters(parameterSet);
		try {
			action.validate();
			fail("Expected a domain exception");
		} catch (EpDomainException epde) {
			//success
			assertNotNull(epde);
		}
	}
	
	/**
	 * Test case for CartCategoryPercentDiscountActionImpl.validate().
	 */
	@Test
	public void testValidationFailure2() {
		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.DISCOUNT_PERCENT_KEY);
		ruleParameter.setValue("0");
		parameterSet.add(ruleParameter);
		
		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.CATEGORY_CODE_KEY);
		ruleParameter.setValue(DISCOUNT_CATEGORY);
		parameterSet.add(ruleParameter);
		
		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.NUM_ITEMS_KEY);
		ruleParameter.setValue(NUM_ITEMS);
		parameterSet.add(ruleParameter);
		
		RuleAction action = (RuleAction) createTestRuleElement();
		action.setParameters(parameterSet);
		try {
			action.validate();
			fail("Expected a domain exception");
		} catch (EpDomainException epde) {
			//success
			assertNotNull(epde);
		}
	}
	
	/**
	 * Test case for CartCategoryPercentDiscountActionImpl.validate().
	 */
	@Test
	public void testValidationFailure3() {
		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.DISCOUNT_PERCENT_KEY);
		ruleParameter.setValue("-1");
		parameterSet.add(ruleParameter);
		
		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.CATEGORY_CODE_KEY);
		ruleParameter.setValue(DISCOUNT_CATEGORY);
		parameterSet.add(ruleParameter);
		
		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.NUM_ITEMS_KEY);
		ruleParameter.setValue(NUM_ITEMS);
		parameterSet.add(ruleParameter);
		
		RuleAction action = (RuleAction) createTestRuleElement();
		action.setParameters(parameterSet);
		try {
			action.validate();
			fail("Expected a domain exception");
		} catch (EpDomainException epde) {
			//success
			assertNotNull(epde);
		}
	}
	
}
