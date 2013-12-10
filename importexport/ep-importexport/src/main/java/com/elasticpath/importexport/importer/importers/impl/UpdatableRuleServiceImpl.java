package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.importexport.common.ImportExportContextIdNames;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.importer.importers.RuleWrapper;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.rules.impl.RuleServiceImpl;

/**
 * Creates promotion rules with snapshots taken before rule has been changed.
 */
public class UpdatableRuleServiceImpl extends RuleServiceImpl {

	@Override
	public Rule get(final long ruleUid, final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		return wrap(super.get(ruleUid, loadTuner));
	}

	@Override
	public Rule findByRuleCode(final String code) throws EpServiceException {
		return wrap(super.findByRuleCode(code));
	}

	/**
	 * Wraps rule to <code>RuleWrapper</code> with rule snapshot inside.
	 * 
	 * @param rule the rule to wrap
	 * @return rule wrapper prepared for update
	 */
	RuleWrapper wrap(final Rule rule) {
		if (rule == null) {
			return null;
		}
		RuleWrapper ruleWrapper = getBean(ImportExportContextIdNames.RULE_WRAPPER);
		ruleWrapper.setUpdatedRule(rule);
		ruleWrapper.takeSnapshot();
		return ruleWrapper;
	}

	/**
	 * Returns rule snapshot taken before.
	 * 
	 * @param rule rule to take old version of
	 * @return old rule snapshot
	 */
	protected Rule getOldRule(final Rule rule) {
		if (rule instanceof RuleWrapper) {
			RuleWrapper ruleWrapper = (RuleWrapper) rule;
			return ruleWrapper.getOldRule();
		}
		throw new ImportRuntimeException("IE-30504");
	}

	/**
	 * Updates a rule.
	 * 
	 * @param rule to be updated
	 * @return updated rule
	 */
	protected Rule updateInternal(final Rule rule) {
		if (rule instanceof RuleWrapper) {
			RuleWrapper ruleWrapper = (RuleWrapper) rule;
			return getPersistenceEngine().merge(ruleWrapper.getUpdatedRule());
		}
		throw new ImportRuntimeException("IE-30505", rule.getCode());
	}
}
