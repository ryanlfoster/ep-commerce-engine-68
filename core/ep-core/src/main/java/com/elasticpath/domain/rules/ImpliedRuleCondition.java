/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.domain.rules;

/**
 * Represents a rule condition that is implied rather than being explicitly stated.
 * 
 * For example, a coupon code or promotion usage limit will be controlled by 
 * another part of the UI but still used as a condition behind the scenes. 
 */
public interface ImpliedRuleCondition extends RuleCondition {
	// Currently this is a marker interface only
}
