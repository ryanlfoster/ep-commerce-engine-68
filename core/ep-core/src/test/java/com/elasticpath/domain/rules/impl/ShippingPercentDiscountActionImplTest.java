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

/** Test cases for <code>ShippingPercentDiscountActionImpl</code>.*/
public class ShippingPercentDiscountActionImplTest extends AbstractTestRuleElementImpl {

	private static final String DISCOUNT_PERCENT = "50";
	private static final String SHIPPING_METHOD_CODE = "Code123";
	private static final String EXPECT_DOMAIN_EXCEPTION = "Expected an EpDomainException";


	/**
	 * Factory method called by subclasses to specify the <code>RuleElement</code> implementation to test.
	 * @return the RuleElement to test
	 */
	@Override
	protected RuleElement createTestRuleElement() {
		return new ShippingPercentDiscountActionImpl();
	}

	/**
	 * Factory method called by subclasses to specify a valid set of parameters.
	 * @return the valid parameter set
	 */
	@Override
	protected Set<RuleParameter> getValidParameterSet() {

		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl(RuleParameter.DISCOUNT_PERCENT_KEY, DISCOUNT_PERCENT);
		parameterSet.add(ruleParameter);

		ruleParameter = new RuleParameterImpl(RuleParameter.SHIPPING_SERVICE_LEVEL_CODE_KEY, SHIPPING_METHOD_CODE);
		parameterSet.add(ruleParameter);

		return parameterSet;
	}

	/**
	 * Test case for validate().
	 */
	@Test
	public void testValidation() {
		RuleAction action = (RuleAction) createTestRuleElement();
		action.setParameters(getValidParameterSet());
		action.validate();
	}

	/**
	 * Test case for validate().
	 */
	@Test
	public void testValidationFailure1() {
		RuleAction action = (RuleAction) createTestRuleElement();

		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.DISCOUNT_AMOUNT_KEY);
		ruleParameter.setValue("0");
		parameterSet.add(ruleParameter);

		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.SHIPPING_SERVICE_LEVEL_CODE_KEY);
		ruleParameter.setValue(SHIPPING_METHOD_CODE);
		parameterSet.add(ruleParameter);

		action.setParameters(parameterSet);

		try {
			action.validate();
			fail(EXPECT_DOMAIN_EXCEPTION);
		} catch (EpDomainException epde) {
			//success
			assertNotNull(epde);
		}
	}

	/**
	 * Test case for validate().
	 */
	@Test
	public void testValidationFailure2() {
		RuleAction action = (RuleAction) createTestRuleElement();

		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.DISCOUNT_AMOUNT_KEY);
		ruleParameter.setValue("-1");
		parameterSet.add(ruleParameter);

		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.SHIPPING_SERVICE_LEVEL_CODE_KEY);
		ruleParameter.setValue(SHIPPING_METHOD_CODE);
		parameterSet.add(ruleParameter);

		action.setParameters(parameterSet);

		try {
			action.validate();
			fail(EXPECT_DOMAIN_EXCEPTION);
		} catch (EpDomainException epde) {
			//success
			assertNotNull(epde);
		}
	}

	/**
	 * Test case for validate().
	 */
	@Test
	public void testValidation2() {
		RuleAction action = (RuleAction) createTestRuleElement();

		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.DISCOUNT_PERCENT_KEY);
		ruleParameter.setValue("10");
		parameterSet.add(ruleParameter);

		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.SHIPPING_SERVICE_LEVEL_CODE_KEY);
		ruleParameter.setValue(SHIPPING_METHOD_CODE);
		parameterSet.add(ruleParameter);

		action.setParameters(parameterSet);
		action.validate();
	}

	/**
	 * Test case for validate().
	 */
	@Test
	public void testValidation3() {
		RuleAction action = (RuleAction) createTestRuleElement();

		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.DISCOUNT_PERCENT_KEY);
		ruleParameter.setValue("112");
		parameterSet.add(ruleParameter);

		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.SHIPPING_SERVICE_LEVEL_CODE_KEY);
		ruleParameter.setValue(SHIPPING_METHOD_CODE);
		parameterSet.add(ruleParameter);

		action.setParameters(parameterSet);

		try {
			action.validate();
			fail(EXPECT_DOMAIN_EXCEPTION);
		} catch (EpDomainException epde) {
			//success
			assertNotNull(epde);
		}
	}

	/**
	 * Test case for validate().
	 * Shipping service level 0 means "ANY" shipping service level.
	 */
	@Test
	public void testValidation5() {
		RuleAction action = (RuleAction) createTestRuleElement();

		Set<RuleParameter> parameterSet = new HashSet<RuleParameter>();

		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.DISCOUNT_PERCENT_KEY);
		ruleParameter.setValue("10");
		parameterSet.add(ruleParameter);

		ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.SHIPPING_SERVICE_LEVEL_CODE_KEY);
		ruleParameter.setValue("0");
		parameterSet.add(ruleParameter);

		action.setParameters(parameterSet);
		action.validate();
	}

	/**
	 * Implemented by subclasses to check that the code is valid.
	 * @param code the generated code to be checked
	 */
	@Override
	protected void checkGeneratedCode(final String code) {
		assertTrue(code.indexOf("delegate") > 0);
		assertTrue(code.indexOf(DISCOUNT_PERCENT) > 0);
		assertTrue(code.indexOf(SHIPPING_METHOD_CODE) > 0);
	}

}
