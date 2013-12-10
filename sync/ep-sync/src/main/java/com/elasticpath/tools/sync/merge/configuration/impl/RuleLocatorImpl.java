package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * 
 * The rule locator class.
 *
 */
public class RuleLocatorImpl extends AbstractEntityLocator {
	
	private RuleService ruleService;
	
	/**
	 * @param ruleService the ruleService to set
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return Rule.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz)
			throws SyncToolConfigurationException {
		return ruleService.findByRuleCode(guid);
	}


}
