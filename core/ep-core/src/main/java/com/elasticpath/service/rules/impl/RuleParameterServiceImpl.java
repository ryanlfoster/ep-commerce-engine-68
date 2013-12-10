/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.rules.RuleParameterService;

/**
 * Provides rule parameter component related services.
 *
 */
public class RuleParameterServiceImpl extends AbstractEpPersistenceServiceImpl implements RuleParameterService  {
	/**
	 * Adds the given ruleParameter.
	 *
	 * @param ruleParameter the ruleParameter to add
	 * @return the persisted instance of ruleParameter.
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleParameter add(final RuleParameter ruleParameter) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().save(ruleParameter);
		return ruleParameter;
	}

	/**
	 * Updates the given ruleParameterle.
	 *
	 * @param ruleParameter the ruleParameter to update
	 * @return RuleParameter the updated ruleParameter
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleParameter update(final RuleParameter ruleParameter) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().update(ruleParameter);
	}

	/**
	 * Delete the ruleParameter.
	 *
	 * @param ruleParameter the ruleParameter to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final RuleParameter ruleParameter) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(ruleParameter);
	}

	/**
	 * Load the ruleParameter with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param ruleParameterUid the ruleParameter UID
	 *
	 * @return the rule if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleParameter load(final long ruleParameterUid) throws EpServiceException {
		sanityCheck();
		RuleParameter ruleParameter = null;
		if (ruleParameterUid > 0) {
			ruleParameter = getPersistentBeanFinder().load(ContextIdNames.RULE_PARAMETER, ruleParameterUid);
		} else {
			ruleParameter = getBean(ContextIdNames.RULE_PARAMETER);
		}
		return ruleParameter;
	}

	/**
	 * Get the ruleParameter with the given UID.
	 * Return null if no matching record exists.
	 *
	 * @param ruleParameterUid the ruleParameter UID
	 *
	 * @return the ruleParameter if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleParameter get(final long ruleParameterUid) throws EpServiceException {
		sanityCheck();
		RuleParameter ruleParameter = null;
		if (ruleParameterUid > 0) {
			ruleParameter = getPersistentBeanFinder().get(ContextIdNames.RULE_PARAMETER, ruleParameterUid);
		} else {
			ruleParameter = getBean(ContextIdNames.RULE_PARAMETER);
		}
		return ruleParameter;
	}

	/**
	 * Generic get method for all persistable domain models.
	 *
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * Get the list of unique <code>RuleParameter</code> value for the given parameter key.
	 * @param parameterKey - the <code>RuleParameter</code> key.
	 * @return the list of <code>RuleParameter</code>s for the given parameter key.
	 * @throws EpServiceException in case of errors.
	 */
	public List<String> findUniqueParametersWithKey(final String parameterKey) throws EpServiceException {
		if (parameterKey == null || parameterKey.length() == 0) {
			throw new EpServiceException("Rule parameter key not set");
		}
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("RULE_PARAMETER_VALUE_BY_KEY", parameterKey);
	}

}
