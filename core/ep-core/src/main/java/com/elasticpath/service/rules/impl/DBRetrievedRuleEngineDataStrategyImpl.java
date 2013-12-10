package com.elasticpath.service.rules.impl;

import java.util.List;

import com.elasticpath.service.rules.RuleService;

/**
 * Rule engine data strategy that retrieves data from the database.
 */
public class DBRetrievedRuleEngineDataStrategyImpl implements RuleEngineDataStrategy {

	private RuleService ruleService;

	@Override
	public List<Object[]> findRuleIdSellingContextByScenario(final int scenario) {
		return getRuleService().findRuleIdSellingContextByScenario(scenario);
	}
	
	/**
	 *
	 * @param ruleService the ruleService to set
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 *
	 * @return the ruleService
	 */
	public RuleService getRuleService() {
		return ruleService;
	}

}
