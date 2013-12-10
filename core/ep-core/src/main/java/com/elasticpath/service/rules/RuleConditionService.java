/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules;

import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides rule condition component related services.
 *
 */
public interface RuleConditionService extends EpPersistenceService  {
	/**
	 * Adds the given ruleCondition.
	 *
	 * @param ruleCondition the ruleCondition to add
	 * @return the persisted instance of ruleCondition
	 * @throws EpServiceException - in case of any errors
	 */
	RuleCondition add(final RuleCondition ruleCondition) throws EpServiceException;

	/**
	 * Updates the given ruleConditionle.
	 *
	 * @param ruleCondition the ruleCondition to update
	 * @return RuleCondition the updated rule condtion
	 * @throws EpServiceException - in case of any errors
	 */
	RuleCondition update(final RuleCondition ruleCondition) throws EpServiceException;

	/**
	 * Delete the ruleCondition.
	 *
	 * @param ruleCondition the ruleCondition to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final RuleCondition ruleCondition) throws EpServiceException;
	
	/**
	 * Load the ruleCondition with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param ruleConditionUid the ruleCondition UID
	 *
	 * @return the rule if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	RuleCondition load(final long ruleConditionUid) throws EpServiceException;
	
	/**
	 * Load the ruleCondition with the given UID if it is greater than 0; 
	 * otherwise get new instance of the ruleCondition of the given ruleConditionType
	 * from the spring beanFactory (assuming bean id is the same as the ruleConditionType).
	 * 
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param ruleConditionUid the ruleCondition UID
	 * @param ruleConditionType the ruleCondition type
	 * @return the rule if it exists, otherwise null.
	 * @throws EpServiceException in case of errors.
	 */
	RuleCondition load(final long ruleConditionUid, final String ruleConditionType) throws EpServiceException;
	
	/**
	 * Get the ruleCondition with the given UID. 
	 * Return null if no matching record exists.
	 *
	 * @param ruleConditionUid the ruleCondition UID
	 *
	 * @return the ruleCondition if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	RuleCondition get(final long ruleConditionUid) throws EpServiceException;
	
	/**
	 * Get the ruleCondition with the given UID if it is greater than 0; 
	 * otherwise get new instance of the ruleCondition of the given ruleConditionType
	 * from the spring beanFactory (assuming bean id is the same as the ruleConditionType).
	 * 
	 * Return null if no matching record exists.
	 *
	 * @param ruleConditionUid the ruleCondition UID
	 * @param ruleConditionType the ruleCondition type
	 * @return the rule if it exists, otherwise null.
	 * @throws EpServiceException in case of errors.
	 */
	RuleCondition get(final long ruleConditionUid, final String ruleConditionType) throws EpServiceException;
	
}
