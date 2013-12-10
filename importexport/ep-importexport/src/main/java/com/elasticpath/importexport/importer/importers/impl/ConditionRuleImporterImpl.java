package com.elasticpath.importexport.importer.importers.impl;

import java.util.HashSet;
import java.util.Set;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.promotion.RuleAdapter;
import com.elasticpath.importexport.common.dto.promotion.rule.RuleDTO;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.service.rules.RuleService;

/**
 * Promotion condition rules importer implementation.
 */
public class ConditionRuleImporterImpl extends AbstractImporterImpl<Rule, RuleDTO> {

	private RuleAdapter ruleAdapter;

	private RuleService ruleService;

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<Rule, RuleDTO> savingStrategy) {
		super.initialize(context,
				AbstractSavingStrategy.<Rule, RuleDTO> createStrategy(ImportStrategyType.UPDATE, savingStrategy.getSavingManager()));
	}

	@Override
	protected Rule findPersistentObject(final RuleDTO dto) {
		return ruleService.findByRuleCode(dto.getCode());
	}

	@Override
	protected String getDtoGuid(final RuleDTO dto) {
		return dto.getCode();
	}

	@Override
	protected void setImportStatus(final RuleDTO object) {
		getStatusHolder().setImportStatus("(for promotion rule " + object.getCode() + ")");
	}

	/**
	 * Returns the collections strategy for condition rules.
	 * 
	 * @return appropriate collections strategy
	 */
	@Override
	protected CollectionsStrategy<Rule, RuleDTO> getCollectionsStrategy() {
		return new ConditionRuleCollectionStrategy();
	}

	@Override
	protected DomainAdapter<Rule, RuleDTO> getDomainAdapter() {
		return ruleAdapter;
	}

	@Override
	public String getImportedObjectName() {
		return RuleDTO.ROOT_ELEMENT;
	}

	/**
	 * @return the promotionAdapter
	 */
	public RuleAdapter getRuleAdapter() {
		return ruleAdapter;
	}

	/**
	 * @param promotionAdapter the promotionAdapter to set
	 */
	public void setRuleAdapter(final RuleAdapter promotionAdapter) {
		this.ruleAdapter = promotionAdapter;
	}

	/**
	 * @return the ruleService
	 */
	public RuleService getRuleService() {
		return ruleService;
	}

	/**
	 * @param ruleService the ruleService to set
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	@Override
	public Class<? extends RuleDTO> getDtoClass() {
		return RuleDTO.class;
	}

	/**
	 * Clear Collection strategy is always used for conditions and eligibilities.
	 */
	class ConditionRuleCollectionStrategy implements CollectionsStrategy<Rule, RuleDTO> {

		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}

		@Override
		public void prepareCollections(final Rule domainObject, final RuleDTO dto) {
			final Set<RuleCondition> conditions = new HashSet<RuleCondition>(domainObject.getConditions());

			for (RuleCondition condition : conditions) {
				domainObject.removeCondition(condition);
			}
		}
	}
}
