/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import java.math.BigDecimal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.rules.DiscountType;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;

/**
 * Rule action that discounts shipping cost by a given amount.
 */
@Entity
@DiscriminatorValue("shippingAmountDiscountAction")
@DataCache(enabled = false)
public class ShippingAmountDiscountActionImpl extends AbstractRuleActionImpl implements RuleAction {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private static final RuleElementType RULE_ELEMENT_TYPE = RuleElementType.SHIPPING_AMOUNT_DISCOUNT_ACTION;

	private static final String[] PARAMETER_KEYS = new String[] { RuleParameter.DISCOUNT_AMOUNT_KEY, RuleParameter.SHIPPING_SERVICE_LEVEL_CODE_KEY };

	private static final DiscountType DISCOUNT_TYPE = DiscountType.SHIPPING_DISCOUNT;
	
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
		return ACTION_KIND;
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
	 * @return an array of RuleExceptionType of the allowed <code>RuleException</code> types for the rule.
	 */
	@Override
	@Transient
	public RuleExceptionType[] getAllowedExceptions() {
		return null;
	}

	/**
	 * Return the Drools code corresponding to this action.
	 *
	 * @return the Drools code
	 * @throws EpDomainException if the rule is not well formed
	 */
	@Override
	@Transient
	public String getRuleCode() throws EpDomainException {
		validate();
		StringBuffer code = new StringBuffer();
		code.append("\t\tdelegate.applyShippingDiscountAmount(cart, ");
		code.append(this.getRuleId()).append("L, ");
		code.append(this.getUidPk()).append("L, \"");
		code.append(this.getParamValue(RuleParameter.DISCOUNT_AMOUNT_KEY));
		code.append("\", \"");
		code.append(this.getParamValue(RuleParameter.SHIPPING_SERVICE_LEVEL_CODE_KEY));
		code.append("\");\n");
		return code.toString();
	}

	/**
	 * Checks that the rule set domain model is well formed. For example, rule conditions must have all required parameters specified.
	 *
	 * @throws EpDomainException if the structure is not correct.
	 */
	@Override
	public void validate() {
		super.validate();

		BigDecimal amount = new BigDecimal(this.getParamValue(RuleParameter.DISCOUNT_AMOUNT_KEY));
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new EpDomainException("Invalid amount parameter: " + amount + ".");
		}

		
		String sslCode = this.getParamValue(RuleParameter.SHIPPING_SERVICE_LEVEL_CODE_KEY);
		if (sslCode == null || sslCode.equals("")) {			
			throw new EpDomainException("Invalid Shipping Method code parameter. Missing");
		}
	}

	/**
	 * Return an array of parameter keys required by this rule action.
	 *
	 * @return the parameter key array
	 */
	@Override
	@Transient
	public String[] getParameterKeys() {
		return PARAMETER_KEYS.clone();
	}

	/**
	 * Must be implemented by subclasses to return their type. Get the <code>DiscountType</code> associated with this RuleAction.
	 * 
	 * @return the <code>DiscountType</code> associated with this RuleAction
	 */
	@Transient
	public DiscountType getDiscountType() {
		return DISCOUNT_TYPE;
	}
}
