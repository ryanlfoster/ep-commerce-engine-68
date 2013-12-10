package com.elasticpath.importexport.importer.importers.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.promotion.PromotionAdapter;
import com.elasticpath.importexport.common.dto.promotion.cart.PromotionDTO;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.RuleWrapper;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.sellingcontext.SellingContextService;

/**
 * Shopping Cart promotion importer implementation.
 */
public class ShoppingCartPromotionImporterImpl extends AbstractImporterImpl<Rule, PromotionDTO> {
	
	private PromotionAdapter promotionAdapter;
	private RuleService ruleService;
	private SellingContextService sellingContextService;
	
	@Override
	public void initialize(final ImportContext context, final SavingStrategy<Rule, PromotionDTO> savingStrategy) {
		super.initialize(context, savingStrategy);
		getSavingStrategy().setSavingManager(new SavingManager<Rule>() {

			@Override
			public Rule update(final Rule persistable) {
				return ruleService.update(persistable);
			}

			@Override
			public void save(final Rule persistable) {
				ruleService.add(persistable);
			}

		});
	}

	@Override
	protected Rule findPersistentObject(final PromotionDTO dto) {
		return ruleService.findByRuleCode(dto.getCode());
	}

	@Override
	protected String getDtoGuid(final PromotionDTO dto) {
		return dto.getCode();
	}

	@Override
	protected void setImportStatus(final PromotionDTO object) {
		getStatusHolder().setImportStatus("(for promotion " + object.getCode() + ")");
	}

	/**
	 * Returns the collections strategy for promotions.
	 * 
	 * @return appropriate collections strategy
	 */
	@Override
	protected CollectionsStrategy<Rule, PromotionDTO> getCollectionsStrategy() {
		return new PromotionCollectionsStrategy();
	}

	@Override
	protected DomainAdapter<Rule, PromotionDTO> getDomainAdapter() {
		return promotionAdapter;
	}

	@Override
	public String getImportedObjectName() {
		return PromotionDTO.ROOT_ELEMENT;
	}

	/**
	 * @return the promotionAdapter
	 */
	public PromotionAdapter getPromotionAdapter() {
		return promotionAdapter;
	}

	/**
	 * @param promotionAdapter the promotionAdapter to set
	 */
	public void setPromotionAdapter(final PromotionAdapter promotionAdapter) {
		this.promotionAdapter = promotionAdapter;
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

	/**
	 * Sets the selling context service.
	 * 
	 * @param sellingContextService selling context service
	 */
	public void setSellingContextService(final SellingContextService sellingContextService) {
		this.sellingContextService = sellingContextService;
	}
	
	@Override
	public Class<? extends PromotionDTO> getDtoClass() {
		return PromotionDTO.class;
	}

	/**
	 * Clear Collection strategy is always used for actions.
	 */
	class PromotionCollectionsStrategy implements CollectionsStrategy<Rule, PromotionDTO> {
		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}

		@Override
		public void prepareCollections(final Rule rule, final PromotionDTO dto) {
			Rule domainObject = null;
			if (rule instanceof RuleWrapper) {
				domainObject = ((RuleWrapper) rule).getUpdatedRule();
			} else {
				domainObject = rule;
			}
			
			final List<RuleAction> actions = new ArrayList<RuleAction>(domainObject.getActions());
			for (RuleAction action : actions) {
				domainObject.removeAction(action);
			}
			
			SellingContext sellingContext = domainObject.getSellingContext();
			if (sellingContext != null) {
				// This solves the problem of orphaned conditions.
				sellingContextService.remove(sellingContext);
			}
			domainObject.setSellingContext(null);
		}
	}
}
