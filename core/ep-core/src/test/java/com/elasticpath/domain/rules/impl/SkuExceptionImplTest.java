/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.elasticpath.domain.rules.RuleParameter;

/** Test cases for <code>SkuExceptionImpl</code>.*/
public class SkuExceptionImplTest extends AbstractTestRuleExceptionImpl {

	private static final String SKU_CODE = "BBJ0001";
	
	/**
	 * Factory method to create the exception to test.
	 * @return the exception
	 */
	@Override
	protected AbstractRuleExceptionImpl createTestException() {
		return new SkuExceptionImpl();
	}
	
	/** 
	 * Factory method called by subclasses to specify a valid set of parameters. 
	 * @return the valid parameter set
	 */
	@Override
	protected Set<RuleParameter> getValidParameterSet() {

		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.SKU_CODE_KEY);
		ruleParameter.setValue(SKU_CODE);
		parameterSet.add(ruleParameter);
		
		return parameterSet;
	}
	
	/** 
	 * Implemented by subclasses to check that the code is valid.
	 * @param code the generated code to be checked
	 */
	@Override
	protected void checkGeneratedCode(final String code) {
		assertTrue(code.indexOf("ruleExceptions.addSkuCode") > 0);
		assertTrue(code.indexOf(SKU_CODE) > 0);
	}
	
	/**
	 * Fake test to keep PMD happy.
	 */
	@Test
	public void testNull() {
		//Implement at least one test to keep PMD happy/
	}
	

}
