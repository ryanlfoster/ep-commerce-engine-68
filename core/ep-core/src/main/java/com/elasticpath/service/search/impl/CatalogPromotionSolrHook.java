package com.elasticpath.service.search.impl;

import java.util.Arrays;
import java.util.Date;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.impl.ProcessingHookAdapterImpl;
import com.elasticpath.service.search.CatalogPromoQueryComposerHelper;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.query.FilteredSearchCriteria;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.ConditionEvaluatorService;

/**
 * The catalog promotion hook creates a index query which is passed off to the index notification
 * service for the query runner.
 */
public class CatalogPromotionSolrHook extends ProcessingHookAdapterImpl {
	
	private CatalogPromoQueryComposerHelper catalogPromoQueryComposerHelper;
	
	private IndexNotificationService indexNotificationService;
	
	private ConditionEvaluatorService conditionEvaluatorService;
	
	@Override
	public void postAdd(final Persistable domain) {
		final Rule rule = (Rule) domain;
		
		// we only need to update if the rule is already active
		if (isRuleActive(rule)) {
			notifyOfRuleUpdate((Rule) domain);
		}
	}

	@Override
	public void postUpdate(final Persistable oldObject, final Persistable newObject) {
		if (oldObject == null) {
			throw new UnsupportedOperationException(
					"This hook can only be applied to a service with an old object that is not null.");
		}
		
		final Rule oldRule = (Rule) oldObject;
		final Rule newRule = (Rule) newObject;

		// we want to update both rules so that products affected by the old conditions are
		// changed back to their original price
		if (isRuleActive(oldRule)) {
			notifyOfRuleUpdate((Rule) oldObject);
		}
		
		
		// we only need to update if the rule is already active
		if (isRuleActive(newRule)) {
			notifyOfRuleUpdate(newRule);
		}
	}

	@Override
	public void postDelete(final Persistable object) {
		notifyOfRuleUpdate((Rule) object);
	}

	private void notifyOfRuleUpdate(final Rule rule) {
		FilteredSearchCriteria< ? > criteria = catalogPromoQueryComposerHelper.constructSearchCriteria(Arrays.asList(rule));
		if (!criteria.isEmpty()) {
			indexNotificationService.addViaQuery(UpdateType.UPDATE, criteria, false);
		}
	}
	
	/**
	 * Is rule active determination. 
	 * Start/end date will be used for catalogue browse scenarios.
	 * Selling context TIME dictionary will be used for shopping cart promotions.  
	 * @param rule rule 
	 * @return true is promotion is active
	 */
	private boolean isRuleActive(final Rule rule) {
		// can't use Rule#getState() because ACTIVE also corresponds to promotions that have a
		// start date in the future
		final Date currentTime = new Date();
		if (rule.getRuleSet().getScenario() == RuleScenarios.CATALOG_BROWSE_SCENARIO) {
			return rule.isEnabled() && currentTime.after(rule.getStartDate())
				&& (rule.getEndDate() == null || currentTime.before(rule.getEndDate()));		
		}
		//Shopping cart scenario
		if (rule.getSellingContext() != null) {
			TagSet tagSet = new TagSet();
			tagSet.addTag("SHOPPING_START_TIME", new Tag(currentTime.getTime()));
			return rule.isEnabled() 
				&& 
				rule.getSellingContext().isSatisfied(conditionEvaluatorService, tagSet, TagDictionary.DICTIONARY_TIME_GUID);
			
		}
		return rule.isEnabled();
	}	

	/**
	 * Sets the {@link CatalogPromoQueryComposerHelper} instance to use.
	 * 
	 * @param catalogPromoQueryComposerHelper the {@link CatalogPromoQueryComposerHelper} instance
	 *            to use
	 */
	public void setCatalogPromoQueryComposerHelper(final CatalogPromoQueryComposerHelper catalogPromoQueryComposerHelper) {
		this.catalogPromoQueryComposerHelper = catalogPromoQueryComposerHelper;
	}
	
	/**
	 * Sets the {@link IndexNotificationService} instance to use.
	 *
	 * @param indexNotificationService the {@link IndexNotificationService} instance to use
	 */
	public void setIndexNotificationService(final IndexNotificationService indexNotificationService) {
		this.indexNotificationService = indexNotificationService;
	}

	/**
	 * Set tag condition dsl builder {@link ConditionsEvaluationService} for work with time conditions.
	 * 
	 * @param conditionEvaluatorService to use
	 */	
	public void setConditionEvaluatorService(
			final ConditionEvaluatorService conditionEvaluatorService) {
		this.conditionEvaluatorService = conditionEvaluatorService;
	}

	
	
	
	
	
}
