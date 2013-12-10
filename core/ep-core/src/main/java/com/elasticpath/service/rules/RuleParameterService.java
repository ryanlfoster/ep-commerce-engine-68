/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules;

import java.util.List;

import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides rule parameter related services.
 *
 */
public interface RuleParameterService extends EpPersistenceService  {
	/**
	 * Adds the given ruleParameter.
	 *
	 * @param ruleParameter the ruleParameter to add
	 * @return the persisted instance of ruleParameter
	 * @throws EpServiceException - in case of any errors
	 */
	RuleParameter add(final RuleParameter ruleParameter) throws EpServiceException;

	/**
	 * Updates the given ruleParameterle.
	 *
	 * @param ruleParameter the ruleParameter to update
	 * @return RuleParameter the updated ruleParameter
	 * @throws EpServiceException - in case of any errors
	 */
	RuleParameter update(final RuleParameter ruleParameter) throws EpServiceException;

	/**
	 * Delete the ruleParameter.
	 *
	 * @param ruleParameter the ruleParameter to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final RuleParameter ruleParameter) throws EpServiceException;

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
	RuleParameter load(final long ruleParameterUid) throws EpServiceException;

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
	RuleParameter get(final long ruleParameterUid) throws EpServiceException;

	/**
	 * Get the list of unique <code>RuleParameter</code> value for the given parameter key.
	 * @param parameterKey - the <code>RuleParameter</code> key.
	 * @return the list of <code>RuleParameter</code>s for the given parameter key.
	 * @throws EpServiceException in case of errors.
	 */
	List<String> findUniqueParametersWithKey(final String parameterKey) throws EpServiceException;

}
