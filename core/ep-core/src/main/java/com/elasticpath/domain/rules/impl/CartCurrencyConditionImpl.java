/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;

/**
 * Rule condition that requires a the currency set in the customer's cart to be a particular value. Requires parameter with key CURRENCY_KEY to
 * specify the currency code, e.g. CAD
 */
@Entity
@DiscriminatorValue("cartCurrencyCondition")
@DataCache(enabled = false)
public class CartCurrencyConditionImpl extends AbstractRuleElementImpl implements RuleCondition {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final RuleElementType RULE_ELEMENT_TYPE = RuleElementType.CART_CURRENCY_CONDITION;
	
	private static final String[] PARAMETER_KEYS = new String[] { RuleParameter.CURRENCY_KEY };

	/** Set of <code>RuleExcetion</code> allowed for this <code>RuleAction</code>. */
	// private static final RuleExceptionType[] ALLOWED_EXCEPTIONS = new RuleExceptionType[] {};

	/**
	 * Returns the <code>RuleElementType</code> associated with this <code>RuleElement</code> subclass. The <code>RuleElementType</code>'s
	 * property key must match this class' discriminator-value and the spring context bean id for this <code>RuleElement</code> implementation.
	 * 
	 * @return the <code>RuleElementType</code> associated with this <code>RuleElement</code> subclass.
	 */
	@Override
	@Transient
	public RuleElementType getElementType() {
		return RULE_ELEMENT_TYPE;
	}

	/**
	 * Returns the kind of this <code>RuleElement</code> (e.g. eligibility, condition, action).
	 *
	 * @return the kind
	 */
	@Override
	@Transient
	protected String getElementKind() {
		return CONDITION_KIND;
	}

	/**
	 * Check if this rule element is valid in the specified scenario.
	 *
	 * @param scenarioId the Id of the scenario to check (defined in RuleScenarios)
	 * @return true if the rule element is applicable in the given scenario
	 */
	@Override
	public boolean appliesInScenario(final int scenarioId) {
		return scenarioId == RuleScenarios.CART_SCENARIO;
	}

	/**
	 * Return the array of the allowed <code>RuleException</code> types for the rule.
	 *
	 * @return an array of String of the allowed <code>RuleException</code> types for the rule.
	 */
	@Override
	@Transient
	public RuleExceptionType[] getAllowedExceptions() {
		return null;
		// return (RuleExceptionType[]) ALLOWED_EXCEPTIONS.clone();
	}

	/**
	 * Returns the Drools code corresponding to this rule condition.
	 *
	 * @return the rule code.
	 * @throws EpDomainException if the rule is not well formed
	 */
	@Override
	@Transient
	public String getRuleCode() throws EpDomainException {
		validate();
		StringBuffer sbf = new StringBuffer();
		sbf.append(" delegate.cartCurrencyMatches(cart, \"");
		sbf.append(this.getParamValue(RuleParameter.CURRENCY_KEY)).append("\") ");
		return sbf.toString();
	}

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
