/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.elasticpath.domain.rules.RuleParameter;

/** Test cases for <code>CategoryExceptionImpl</code>.*/
public class CategoryExceptionImplTest extends AbstractTestRuleExceptionImpl {

	private static final String CATEGORY_CODE = "10";
	

	
	/**
	 * Factory method to create the exception to test.
	 * @return the exception
	 */
	@Override
	protected AbstractRuleExceptionImpl createTestException() {
		return new CategoryExceptionImpl();
	}
	
	/** 
	 * Factory method called by subclasses to specify a valid set of parameters. 
	 * @return the valid parameter set
	 */
	@Override
	protected Set<RuleParameter> getValidParameterSet() {

		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.CATEGORY_CODE_KEY);
		ruleParameter.setValue(CATEGORY_CODE);
		parameterSet.add(ruleParameter);
		
		return parameterSet;
	}
	
	/** 
	 * Implemented by subclasses to check that the code is valid.
	 * @param code the generated code to be checked
	 */
	@Override
	protected void checkGeneratedCode(final String code) {
		assertTrue(code.indexOf("ruleExceptions.addCategory") > 0);
		assertTrue(code.indexOf(CATEGORY_CODE) > 0);
	}
	
	/**
	 * Fake test to keep PMD happy.
	 */
	@Test
	public void testNull() {
		//Implement at least one test to keep PMD happy/
	}
	

}
