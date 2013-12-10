package com.elasticpath.domain.rules.impl;

import com.elasticpath.domain.rules.ActiveRule;

/**
 * 
 * Simple rule id wrapper for drools, that indicates applicable rule. 
 *
 */
public class ActiveRuleImpl implements ActiveRule {
	
	private long ruleId;

	/** 
	 * @return rule id 
	 */
	public long getRuleId() {
		return ruleId;
	}

	/**
	 * Set rule id.
	 * @param ruleId rule id. 
	 */
	public void setRuleId(final long ruleId) {
		this.ruleId = ruleId;
	}

	/**
	 * Construct simple rule id wrapper for drools.
	 * @param ruleId rule id.
	 */
	public ActiveRuleImpl(final long ruleId) {
		super();
		this.ruleId = ruleId;
	}
	
	

}
