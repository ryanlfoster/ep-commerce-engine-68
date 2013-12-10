package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.rules.RuleSetService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * 
 * The rule set locator class.
 *
 */
public class RuleSetLocatorImpl extends AbstractEntityLocator {
	
	private RuleSetService ruleSetService;

	/**
	 * @param ruleSetService the ruleSetService to set
	 */
	public void setRuleSetService(final RuleSetService ruleSetService) {
		this.ruleSetService = ruleSetService;
	}
	
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return RuleSet.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz)
			throws SyncToolConfigurationException {
		return ruleSetService.findByName(guid);
	}

}
