/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.domain.rules.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.rules.ImpliedRuleCondition;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleScenarios;

/**
 * Rule condition that requires coupon code to be entered for promotion application.
 *
 */
@Entity
@DiscriminatorValue("limitedUseCouponCodeCondition")
@DataCache(enabled = false)
public class LimitedUseCouponCodeConditionImpl extends AbstractRuleElementImpl implements ImpliedRuleCondition {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final RuleElementType RULE_ELEMENT_TYPE = RuleElementType.LIMITED_USE_COUPON_CODE_CONDITION;

	/**
	 * This condition applied to the cart scenario.
	 * 
	 * @param scenarioId the id of the current scenario
	 * @return true if the scenario is the cart scenario
	 */
	@Override
	public boolean appliesInScenario(final int scenarioId) {
		return scenarioId == RuleScenarios.CART_SCENARIO;
	}

	/**
	 * There are no allowed exceptions.
	 * 
	 * @return null
	 */
	@Override
	@Transient
	public RuleExceptionType[] getAllowedExceptions() {
		return null;
	}

	/**
	 * This is a condition.
	 * 
	 * @return the condition kind enum
	 */
	@Override
	@Transient
	protected String getElementKind() {
		return CONDITION_KIND;
	}

	/**
	 * This element type is Limited Use Coupon Code Condition.
	 * @return the constant {@code RuleElementType.LIMITED_USE_COUPON_CODE_CONDITION}
	 */
	@Override
	@Transient
	public RuleElementType getElementType() {
		return RULE_ELEMENT_TYPE;
	}

	/**
	 * There are no parameters for this condition.
	 * 
	 * @return null as there are no parameters
	 */
	@Override
	@Transient
	public String[] getParameterKeys() {
		return null;
	}

	@Override
	@Transient
	public String getRuleCode() throws EpDomainException {
		validate();
		StringBuffer sbf = new StringBuffer();
		sbf.append(" delegate.cartHasValidLimitedUseCouponCode(cart, ");
		sbf.append(getRuleId());
		sbf.append("L) ");
		return sbf.toString();
	}

	
}
