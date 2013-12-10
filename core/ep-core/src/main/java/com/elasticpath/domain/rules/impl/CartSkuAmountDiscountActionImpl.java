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
 * Rule action that discounts N products with a given UID by the given amount.
 */
@Entity
@DiscriminatorValue("cartSkuAmountDiscountAction")
@DataCache(enabled = false)
public class CartSkuAmountDiscountActionImpl extends AbstractRuleActionImpl implements RuleAction {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final RuleElementType RULE_ELEMENT_TYPE = RuleElementType.CART_SKU_AMOUNT_DISCOUNT_ACTION;

	private static final String[] PARAMETER_KEYS = new String[] { RuleParameter.DISCOUNT_AMOUNT_KEY, RuleParameter.NUM_ITEMS_KEY,
			RuleParameter.SKU_CODE_KEY };

	/** Set of <code>RuleExcetion</code> allowed for this <code>RuleAction</code>. */
	private static final RuleExceptionType[] ALLOWED_EXCEPTIONS = new RuleExceptionType[] { };

	private static final DiscountType DISCOUNT_TYPE = DiscountType.CART_ITEM_DISCOUNT;

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
		return ALLOWED_EXCEPTIONS.clone();
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
		StringBuffer sbf = new StringBuffer();
		sbf.append("\n\t insert(new CartSkuAmountDiscountImpl(\"").append(RULE_ELEMENT_TYPE).append("\", ");
		sbf.append(this.getRuleId()).append("L, ");
		sbf.append(this.getUidPk()).append("L, \"");
		sbf.append(this.getParamValue(RuleParameter.SKU_CODE_KEY)).append("\", \"");
		sbf.append(this.getParamValue(RuleParameter.DISCOUNT_AMOUNT_KEY));
		sbf.append("\", \"").append(this.getExceptionStr());
		sbf.append("\", delegate.calculateAvailableDiscountQuantity(cart, ");
		sbf.append(this.getRuleId()).append("L, ");
		sbf.append(this.getParamValue(RuleParameter.NUM_ITEMS_KEY));
		sbf.append(")));\n");
		return sbf.toString();
	}

	/**
	 * Checks that the rule set domain model is well formed. For example, rule conditions must have all required parameters specified.
	 *
	 * @throws EpDomainException if the structure is not correct.
	 */
	@Override
	public void validate() throws EpDomainException {
		super.validate();

		BigDecimal discountAmount = new BigDecimal(this.getParamValue(RuleParameter.DISCOUNT_AMOUNT_KEY));
		if (discountAmount.doubleValue() <= 0) {
			throw new EpDomainException("Amount parameter: + " + discountAmount + " must be greater than zero");
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

	@Override
	@Transient
	public int getDiscountQuantityPerCoupon() {
		return Integer.parseInt(this.getParamValue(RuleParameter.NUM_ITEMS_KEY));
	}
}
