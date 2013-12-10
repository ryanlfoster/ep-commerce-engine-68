package com.elasticpath.domain.rules;

/**
 * 
 * Simple rule id wrapper for drools, that indicates applicable rule. 
 *
 */
public interface ActiveRule {
	
	/** 
	 * @return rule id 
	 */
	long getRuleId();

	/**
	 * Set rule id.
	 * @param ruleId rule id. 
	 */
	void setRuleId(long ruleId);	

}
