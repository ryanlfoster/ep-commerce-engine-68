/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleParameter;

/** Test cases for <code>CartContainsItemsOfCategoryConditionImpl</code>.*/
public class CartContainsItemsOfCategoryConditionImplTest extends AbstractTestRuleElementImpl {

	private static final String NUM_ITEMS = "3";
	private static final String CATEGORY_CODE = "8";
	private static final String NUM_ITEMS_QUANTIFIER = "AT_LEAST";
	

	
	/** 
	 * Factory method called by subclasses to specify the <code>RuleElement</code> implementation to test. 
	 * @return the RuleElement to test
	 */
	@Override
	protected RuleElement createTestRuleElement() {
		return new CartContainsItemsOfCategoryConditionImpl();
	}
	
	/** 
	 * Factory method called by subclasses to specify the kind of condition. 
	 * @return the condition kind string
	 */
	protected String getElementKind() {
		return RuleCondition.CONDITION_KIND;
	}
	
	/** 
	 * Factory method called by subclasses to specify a valid set of parameters. 
	 * @return the valid parameter set
	 */
	@Override
	protected Set<RuleParameter> getValidParameterSet() {

		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();
		
		parameterSet.add(new RuleParameterImpl(RuleParameter.NUM_ITEMS_QUANTIFIER_KEY, NUM_ITEMS_QUANTIFIER));
		parameterSet.add(new RuleParameterImpl(RuleParameter.NUM_ITEMS_KEY, NUM_ITEMS));
		parameterSet.add(new RuleParameterImpl(RuleParameter.CATEGORY_CODE_KEY, CATEGORY_CODE));
		
		return parameterSet;
	}
	
	/** 
	 * Implemented by subclasses to check that the code is valid.
	 * @param code the generated code to be checked
	 */
	@Override
	protected void checkGeneratedCode(final String code) {
		assertEquals(-1, code.indexOf("eval"));
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
