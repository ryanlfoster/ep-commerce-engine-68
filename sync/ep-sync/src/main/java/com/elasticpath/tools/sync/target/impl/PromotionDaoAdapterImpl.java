package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Promotion dao adapter.
 */
public class PromotionDaoAdapterImpl extends AbstractDaoAdapter<Rule> {

	private BeanFactory beanFactory;

	private RuleService ruleService;

	@Override
	public void add(final Rule newPersistence) throws SyncToolRuntimeException {
		// Refresh rule elements in order to set newRuleElementsSet flag in promotion to true 
		// otherwise rule.validate() method will throw an exception
		newPersistence.setRuleElements(newPersistence.getRuleElements());
		ruleService.add(newPersistence);
	}

	@Override
	public Rule createBean(final Rule rule) {
		return beanFactory.getBean(ContextIdNames.PROMOTION_RULE);
	}

	@Override
	public Rule get(final String guid) {
		try {		
			return (Rule) getEntityLocator().locatePersistence(guid, Rule.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final Rule findByRuleCode = ruleService.findByRuleCode(guid);
		if (findByRuleCode == null) {
			// TODO: think of error collection to receive a notification about inexisting product here.
			return false;
		}
		ruleService.remove(findByRuleCode);
		return true;
	}

	@Override
	public Rule update(final Rule mergedPersistence) throws SyncToolRuntimeException {
		return ruleService.update(mergedPersistence);
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param ruleService the ruleService to set
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}
}
