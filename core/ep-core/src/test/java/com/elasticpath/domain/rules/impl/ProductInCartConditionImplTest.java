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

/** Test cases for <code>ProductInCartConditionImpl</code>.*/
public class ProductInCartConditionImplTest extends AbstractTestRuleElementImpl {

	private static final String NUM_ITEMS = "3";
	private static final String PRODUCT_CODE = "10";
	private static final String NUM_ITEMS_QUANTIFIER = "AT_LEAST";

	
	/** 
	 * Factory method called by subclasses to specify the <code>RuleElement</code> implementation to test. 
	 * @return the RuleElement to test
	 */
	@Override
	protected RuleElement createTestRuleElement() {
		return new ProductInCartConditionImpl();
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

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.NUM_ITEMS_QUANTIFIER_KEY);
		ruleParameter.setValue(NUM_ITEMS_QUANTIFIER);
		parameterSet.add(ruleParameter);
		
		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.PRODUCT_CODE_KEY);
		ruleParameter.setValue(PRODUCT_CODE);
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
		assertEquals(-1, code.indexOf("eval"));
		assertTrue(code.indexOf(PRODUCT_CODE) > 0);
	}
	
	/**
	 * Fake test to keep PMD happy.
	 */
	@Test
	public void testNull() {
		//Implement at least one test to keep PMD happy/
	}
	

}
