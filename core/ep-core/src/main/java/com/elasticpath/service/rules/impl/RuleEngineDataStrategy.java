package com.elasticpath.service.rules.impl;

import java.util.List;

/**
 * Retrieval strategy for data required by the rule engine.
 */
public interface RuleEngineDataStrategy {
	
	/**
	 * @param scenario rule scenario
	 * @return list of entries with rule id and its selling context
	 */
	List <Object []> findRuleIdSellingContextByScenario(final int scenario);
}
