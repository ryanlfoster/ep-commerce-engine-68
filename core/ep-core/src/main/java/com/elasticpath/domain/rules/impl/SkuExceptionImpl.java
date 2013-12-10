/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;

/**
 * Rule exception that excludes a product sku to be qualified for a rule condition or a rule action. Requires parameter with key
 * RuleParameter.SKU_CODE_KEY to specify the sku code.
 */
@Entity
@DiscriminatorValue("skuException")
@DataCache(enabled = false)
public class SkuExceptionImpl extends AbstractRuleExceptionImpl implements RuleException {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final RuleExceptionType EXCEPTION_TYPE = RuleExceptionType.SKU_EXCEPTION;

	private static final String[] PARAMETER_KEYS = new String[] { RuleParameter.SKU_CODE_KEY };

	/**
	 * Returns the <code>RuleExceptionType</code> associated with this <code>RuleException</code> subclass. The <code>RuleExceptionType</code>'s
	 * property key must match this class' discriminator-value and the spring context bean id for this <code>RuleException</code> implementation.
	 * 
	 * @return the <code>RuleExceptionType</code> associated with this <code>RuleException</code> subclass.
	 */
	@Override
	@Transient
	public RuleExceptionType getExceptionType() {
		return EXCEPTION_TYPE;
	}

	/**
	 * Check if this <code>RuleException</code> is valid in the specified scenario.
	 *
	 * @param scenarioId the Id of the scenario to check (defined in RuleScenarios)
	 * @return true if the <code>RuleException</code> is applicable in the given scenario
	 */
	@Override
	public boolean appliesInScenario(final int scenarioId) {
		return scenarioId == RuleScenarios.CART_SCENARIO || scenarioId == RuleScenarios.CATALOG_BROWSE_SCENARIO;
	}

	// /**
	// * Dummy implementation here since a rule exception will be part of a rule condition or rule action.
	// * @return the rule code.
	// * @throws EpDomainException if the object model is not well formed
	// */
	// public String getRuleCode() throws EpDomainException {
	// // validate();
	// // return "\t\truleExceptions.addSkuCode(\"" + getParamValue(RuleParameter.SKU_CODE_KEY) + "\");\n";
	// return "";
	// }

	/**
	 * Return the array of the required parameter keys for the rule.
	 *
	 * @return an array of String of the required parameter keys for the rule.
	 */
	@Override
	@Transient
	public String[] getParameterKeys() {
		return PARAMETER_KEYS.clone();
	}
}
