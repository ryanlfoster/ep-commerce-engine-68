/*
 * Copyright (c) Elastic Path Software Inc., 2007
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

/** Test cases for <code>CatalogCurrencyAmountDiscountActionImpl</code>.*/
public class CatalogCurrencyAmountDiscountActionImplTest extends AbstractTestRuleElementImpl {
	
	private static final String DISCOUNT_AMOUNT = "100.00";
	
	private static final String CURRENCY_VALUE = "CAD";
	
	/** 
	 * Factory method called by subclasses to specify the <code>RuleElement</code> implementation to test. 
	 * @return the RuleElement to test
	 */
	@Override
	protected RuleElement createTestRuleElement() {
		return new CatalogCurrencyAmountDiscountActionImpl();
	}
	
	/** 
	 * Factory method called by subclasses to specify a valid set of parameters. 
	 * @return the valid parameter set
	 */
	@Override
	protected Set<RuleParameter> getValidParameterSet() {
		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();
		
		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.DISCOUNT_AMOUNT_KEY);
		ruleParameter.setValue(DISCOUNT_AMOUNT);
		parameterSet.add(ruleParameter);
		
		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.CURRENCY_KEY);
		ruleParameter.setValue(CURRENCY_VALUE);
		parameterSet.add(ruleParameter);
		
		return parameterSet;
	}
	
	/** 
	 * Implemented by subclasses to check that the code is valid.
	 * @param code the generated code to be checked
	 */
	@Override
	protected void checkGeneratedCode(final String code) {
		assertTrue(code.indexOf("delegate") > 0);
		assertTrue(code.indexOf(DISCOUNT_AMOUNT) > 0);
		assertTrue(code.indexOf(CURRENCY_VALUE) > 0);
	}
	
	/**
	 * Test case for validate().
	 */
	@Test
	public void testValidationFailure1() {
		RuleAction action = (RuleAction) createTestRuleElement();
		
		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.CURRENCY_KEY);
		ruleParameter.setValue(CURRENCY_VALUE);
		parameterSet.add(ruleParameter);
		
		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.DISCOUNT_AMOUNT_KEY);
		ruleParameter.setValue("0");
		parameterSet.add(ruleParameter);
		
		action.setParameters(parameterSet);
		
		try {
			action.validate();
			fail("Expected an EpDomainException because the Amount was 0.");
		} catch (EpDomainException epde) {
			//success
			assertNotNull(epde);
		}
	}
	
	/**
	 * Test case for validate().
	 */
	@Test
	public void testValidationPass() {
		RuleAction action = (RuleAction) createTestRuleElement();
		
		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.CURRENCY_KEY);
		ruleParameter.setValue(CURRENCY_VALUE);
		parameterSet.add(ruleParameter);
		
		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.DISCOUNT_AMOUNT_KEY);
		ruleParameter.setValue(DISCOUNT_AMOUNT);
		parameterSet.add(ruleParameter);
		
		action.setParameters(parameterSet);
		
		// This should pass with no exceptions.
		action.validate();
	}

}
