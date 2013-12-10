package com.elasticpath.search.index.loader.impl;

import java.util.Collection;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.rules.RuleService;

/**
 * Fetches a batch of {@link Rule}s.
 */
public class BatchRuleLoader extends AbstractEntityLoader<Rule> {

	private RuleService ruleService;

	private FetchGroupLoadTuner ruleLoadTuner;

	/**
	 * Loads the {@link Rule}s for the batched ids and loads each batch in bulk.
	 * 
	 * @return the loaded {@link Rule}s
	 */
	public Collection<Rule> loadBatch() {
		return getRuleService().findByUidsWithFetchGroupLoadTuner(getUidsToLoad(), ruleLoadTuner);
	}

	/**
	 * @param ruleService the ruleService to set
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 * @return the ruleService
	 */
	public RuleService getRuleService() {
		return ruleService;
	}

	/**
	 * @param ruleLoadTuner the ruleLoadTuner to set
	 */
	public void setRuleLoadTuner(final FetchGroupLoadTuner ruleLoadTuner) {
		this.ruleLoadTuner = ruleLoadTuner;
	}

	/**
	 * @return the ruleLoadTuner
	 */
	public FetchGroupLoadTuner getRuleLoadTuner() {
		return ruleLoadTuner;
	}

}
