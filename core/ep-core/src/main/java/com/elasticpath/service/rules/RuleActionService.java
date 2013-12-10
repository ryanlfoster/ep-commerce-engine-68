/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules;

import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides rule action component related services.
 *
 */
public interface RuleActionService extends EpPersistenceService  {
	/**
	 * Adds the given ruleAction.
	 *
	 * @param ruleAction the ruleAction to add
	 * @return the persisted instance of ruleAction
	 * @throws EpServiceException - in case of any errors
	 */
	RuleAction add(final RuleAction ruleAction) throws EpServiceException;

	/**
	 * Updates the given ruleActionle.
	 *
	 * @param ruleAction the ruleAction to update
	 * @return RuleAction the updated rule action
	 * @throws EpServiceException - in case of any errors
	 */
	RuleAction update(final RuleAction ruleAction) throws EpServiceException;

	/**
	 * Delete the ruleAction.
	 *
	 * @param ruleAction the ruleAction to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final RuleAction ruleAction) throws EpServiceException;
	
	/**
	 * Load the ruleAction with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param ruleActionUid the ruleAction UID
	 *
	 * @return the rule if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	RuleAction load(final long ruleActionUid) throws EpServiceException;
	
	/**
	 * Load the ruleAction with the given UID if it is greater than 0; 
	 * otherwise get new instance of the ruleAction of the given ruleActionType
	 * from the spring beanFactory (assuming bean id is the same as the ruleActionType).
	 * 
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param ruleActionUid the ruleAction UID
	 * @param ruleActionType the ruleAction type
	 * @return the rule if it exists, otherwise null.
	 * @throws EpServiceException in case of errors.
	 */
	RuleAction load(final long ruleActionUid, final String ruleActionType) throws EpServiceException;
	
	/**
	 * Get the ruleAction with the given UID. 
	 * Return null if no matching record exists.
	 *
	 * @param ruleActionUid the ruleAction UID
	 *
	 * @return the ruleAction if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	RuleAction get(final long ruleActionUid) throws EpServiceException;
	
	/**
	 * Get the ruleAction with the given UID if it is greater than 0; 
	 * otherwise get new instance of the ruleAction of the given ruleActionType
	 * from the spring beanFactory (assuming bean id is the same as the ruleActionType).
	 * 
	 * Return null if no matching record exists.
	 *
	 * @param ruleActionUid the ruleAction UID
	 * @param ruleActionType the ruleAction type
	 * @return the rule if it exists, otherwise null.
	 * @throws EpServiceException in case of errors.
	 */
	RuleAction get(final long ruleActionUid, final String ruleActionType) throws EpServiceException;
	
}
