/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleParameter;

/** Test cases for <code>AbstractRuleElementImpl</code>. */
public class AbstractRuleElementImplTest {

	private AbstractRuleElementImpl ruleElementImpl;

	private static final String CONDITION_KIND = "testConditionKind";
	
	private static final RuleElementType CONDITION_TYPE = RuleElementType.BRAND_CONDITION;
	
	private static final String KEY1 = "testKey1";

	private static final String VALUE1 = "testValue1";

	private static final String KEY2 = "testKey2";

	private static final String VALUE2 = "testValue2";
	
	private static final String TEST_DROOLS_CODE = "testDroolsCode";
	
	private static final String CATEGORY_CODE = "1";
	
	private static final String PRODUCT_CODE = "2";
	
	private static final String SKU_CODE = "A001";
	
	private static final String RULE_EXCEPTION_STR1 = "CategoryCodes:1,ProductCodes:ProductSkuCodes:";
	
	private static final String RULE_EXCEPTION_STR2 = "CategoryCodes:1,ProductCodes:2,ProductSkuCodes:A001,";

	@Before
	public void setUp() throws Exception {
		ruleElementImpl = new AbstractRuleElementImpl() {

			private static final long serialVersionUID = -8811497252516931468L;

			@Override
			public String getElementKind() {
				return CONDITION_KIND;
			}
			
			@Override
			public RuleElementType getElementType() {
				return CONDITION_TYPE;
			}
			
			@Override
			public String[] getParameterKeys() {
				return new String[0];
			}
			
			@Override
			public boolean appliesInScenario(final int scenarioId) {
				return false;
			}
			
			@Override
			public String getRuleCode() { 
				return TEST_DROOLS_CODE;
			}
			
			@Override
			public RuleExceptionType[] getAllowedExceptions() {
				return null;
			}
		};
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleElementImpl.getKind()'.
	 */
	@Test
	public void testGetSetKind() {
		assertEquals(ruleElementImpl.getKind(), CONDITION_KIND);
		final String kind = "testKind";
		ruleElementImpl.setKind(kind);
		assertEquals(ruleElementImpl.getKind(), kind);
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleElementImpl.getParameters()'.
	 */
	@Test
	public void testGetSetParameters() {
		Set<RuleParameter> paramSet = getParameterSet();
		ruleElementImpl.setParameters(paramSet);
		assertEquals(ruleElementImpl.getParameters(), paramSet);
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleElementImpl.addParameter(RuleParameter)'.
	 */
	@Test
	public void testAddParameter() {
		Set<RuleParameter> paramSet = getParameterSet();
		ruleElementImpl.setParameters(paramSet);

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(KEY1);
		ruleParameter.setValue(VALUE1);

		int numParams = paramSet.size();
		ruleElementImpl.addParameter(ruleParameter);

		assertEquals(ruleElementImpl.getParameters().size(), numParams + 1);
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleElementImpl.getParamValue(String)'.
	 */
	@Test
	public void testGetParamValue() {
		ruleElementImpl.setParameters(getParameterSet());
		assertEquals(ruleElementImpl.getParamValue(KEY1), VALUE1);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleElementImpl.getType()'.
	 */
	@Test
	public void testGetType() {
		assertTrue(ruleElementImpl.getType().equals(CONDITION_TYPE.getPropertyKey()));
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleElementImpl.getElementType()'.
	 */
	@Test
	public void testGetElementType() {
		assertNotNull(ruleElementImpl.getElementType());
	}
	
	/**
	 * Dummy driver for this method.
	 */
	@Test
	public void testScenarioApplication() {
		assertFalse(ruleElementImpl.appliesInScenario(-1));
	}
	
	private Set<RuleParameter> getParameterSet() {
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
	
	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleElementImpl.getExceptionStr()'.
	 */
	@Test
	public void testGetExceptionStr() {
		Set<RuleException> ruleExceptions = new HashSet<RuleException>();
		
		RuleException categoryException = new CategoryExceptionImpl();
		Set<RuleParameter> paramSet1 = new HashSet<RuleParameter>();
		RuleParameter ruleParameter1 = new RuleParameterImpl();
		ruleParameter1.setKey(RuleParameter.CATEGORY_CODE_KEY);
		ruleParameter1.setValue(CATEGORY_CODE);
		paramSet1.add(ruleParameter1);
		categoryException.setParameters(paramSet1);
		ruleExceptions.add(categoryException);
		ruleElementImpl.setExceptions(ruleExceptions);
		assertEquals(RULE_EXCEPTION_STR1, ruleElementImpl.getExceptionStr());
		
		RuleException productException = new ProductExceptionImpl();
		Set<RuleParameter> paramSet2 = new HashSet<RuleParameter>();
		RuleParameter ruleParameter2 = new RuleParameterImpl();
		ruleParameter2.setKey(RuleParameter.PRODUCT_CODE_KEY);
		ruleParameter2.setValue(PRODUCT_CODE);
		paramSet2.add(ruleParameter2);
		productException.setParameters(paramSet2);
		
		RuleException skuException = new SkuExceptionImpl();
		Set<RuleParameter> paramSet3 = new HashSet<RuleParameter>();
		RuleParameter ruleParameter3 = new RuleParameterImpl();
		ruleParameter3.setKey(RuleParameter.SKU_CODE_KEY);
		ruleParameter3.setValue(SKU_CODE);
		paramSet3.add(ruleParameter3);
		skuException.setParameters(paramSet3);
		
		ruleExceptions.add(productException);
		ruleExceptions.add(skuException);
		ruleElementImpl.setExceptions(ruleExceptions);
		assertEquals(RULE_EXCEPTION_STR2, ruleElementImpl.getExceptionStr());
	}
}
