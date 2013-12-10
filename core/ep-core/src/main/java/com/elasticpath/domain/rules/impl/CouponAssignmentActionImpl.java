package com.elasticpath.domain.rules.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.DiscountType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.RuleService;

/**
 * Rule action that assigns the customer to a previously created coupon.
 */
@Entity
@DiscriminatorValue("couponAssignmentAction")
@DataCache(enabled = false)
public class CouponAssignmentActionImpl extends AbstractRuleActionImpl {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private static final RuleElementType RULE_ELEMENT_TYPE = RuleElementType.COUPON_ASSIGNMENT_ACTION;
	
	private static final String[] PARAMETER_KEYS = new String[] { RuleParameter.RULE_CODE_KEY, RuleParameter.COUPON_PREFIX };
	
	private static final DiscountType DISCOUNT_TYPE = DiscountType.COUPON_DISCOUNT;

	private transient RuleService ruleService;

	private transient CouponConfigService couponConfigService;
	
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
		code.append("\t\tdelegate.assignCouponToCustomer(cart, ");
		code.append(this.getRuleId());
		code.append("L);\n");
		return code.toString();
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

	/**
	 * Checks that the rule set domain model is well formed. For example, rule conditions must have all required parameters specified.
	 *
	 * @throws EpDomainException if the structure is not correct.
	 */
	@Override
	public void validate() {
		super.validate();
		
		Rule thisRule = getRuleService().get(getRuleId());
		
		String targetRuleCode = getParamValue(RuleParameter.RULE_CODE_KEY);
		
		// Ensure the parameter is not for the same rule this action is a part of
		// to prevent infinite loops.		
		if (thisRule.getCode().equals(targetRuleCode)) {
			throw new EpDomainException("Rule code parameter is for the rule this action is a part of.");
		}
		
		// make sure the targetRuleCode actually exists
		if (getRuleService().findByRuleCode(targetRuleCode) == null) {
			throw new EpDomainException("Rule for rule code parameter does not exist");
		}
		
		// A non user-specific limited use coupon would give the customer a coupon
		// that another customer might use up.
		CouponConfig couponConfig = getCouponConfigService().findByRuleCode(targetRuleCode);
		if (couponConfig != null && !CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(couponConfig.getUsageType())) {
			throw new EpDomainException("Coupon config for rule code parameter must be Limit Per Specified User");
		}
	}
	
	/**
	 * Get the rule service.
	 * 
	 * @return RuleService
	 */
	@Transient
	RuleService getRuleService() {
		if (ruleService == null) {
			ruleService = this.getBean(ContextIdNames.RULE_SERVICE);
		}
		return ruleService;
	}
	
	/**
	 * Get the coupon config service.
	 * 
	 * @return coupon config service
	 */
	@Transient
	CouponConfigService getCouponConfigService() {
		if (couponConfigService == null) {
			couponConfigService = this.getBean(ContextIdNames.COUPON_CONFIG_SERVICE);
		}
		return couponConfigService;
	}

}
