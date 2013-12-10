package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.promotion.RuleAdapter;
import com.elasticpath.importexport.common.dto.promotion.rule.RuleDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.rules.RuleService;

/**
 * Determines data required for export of condition rules.
 */
public class ConditionRuleExporterImpl extends AbstractExporterImpl<Rule, RuleDTO, Long> {

	private RuleAdapter ruleAdapter;

	private RuleService ruleService;

	@Override
	protected void initializeExporter(final ExportContext context) {
		// do nothing
	}

	@Override
	protected List<Rule> findByIDs(final List<Long> subList) {
		return ruleService.findByUids(subList);
	}

	@Override
	protected DomainAdapter<Rule, RuleDTO> getDomainAdapter() {
		return ruleAdapter;
	}

	@Override
	protected Class<? extends RuleDTO> getDtoClass() {
		return RuleDTO.class;
	}

	@Override
	public Class< ? >[] getDependentClasses() {
		return new Class< ? >[] {Rule.class};
	}

	@Override
	protected List<Long> getListExportableIDs() {
		return new ArrayList<Long>(getContext().getDependencyRegistry().getDependentUids(Rule.class));
	}

	/**
	 * @return  condition rule export job type.
	 */
	public JobType getJobType() {
		return JobType.CONDITIONRULE;
	}

	/**
	 * @param ruleAdapter condition rule adapter
	 */
	public void setRuleAdapter(final RuleAdapter ruleAdapter) {
		this.ruleAdapter = ruleAdapter;
	}

	/**
	 * @param ruleService the ruleService to set
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}
}
