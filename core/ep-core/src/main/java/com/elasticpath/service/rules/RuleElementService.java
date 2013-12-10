/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules;

import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides rule element (eligibility, condition and action) related services.
 *
 */
public interface RuleElementService extends EpPersistenceService  {
	/**
	 * Adds the given ruleElement.
	 *
	 * @param ruleElement the ruleElement to add
	 * @return the persisted instance of ruleElement
	 * @throws EpServiceException - in case of any errors
	 */
	RuleElement add(final RuleElement ruleElement) throws EpServiceException;

	/**
	 * Updates the given ruleElement.
	 *
	 * @param ruleElement the ruleElement to update
	 * @return RuleElement the updated ruleElement
	 * @throws EpServiceException - in case of any errors
	 */
	RuleElement update(final RuleElement ruleElement) throws EpServiceException;

	/**
	 * Delete the ruleElement.
	 *
	 * @param ruleElement the ruleElement to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final RuleElement ruleElement) throws EpServiceException;
	
	/**
	 * Load the ruleElement with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param ruleElementUid the ruleElement UID
	 *
	 * @return the rule if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	RuleElement load(final long ruleElementUid) throws EpServiceException;
	
	/**
	 * Load the ruleElement with the given UID if it is greater than 0; 
	 * otherwise get new instance of the ruleElement of the given ruleElementType
	 * from the spring beanFactory (assuming bean id is the same as the ruleElementType).
	 * 
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param ruleElementUid the ruleElement UID
	 * @param ruleElementType the ruleElement type
	 * @return the rule if it exists, otherwise null.
	 * @throws EpServiceException in case of errors.
	 */
	RuleElement load(final long ruleElementUid, final String ruleElementType) throws EpServiceException;
	
	/**
	 * Get the ruleElement with the given UID. 
	 * Return null if no matching record exists.
	 *
	 * @param ruleElementUid the ruleElement UID
	 *
	 * @return the ruleElement if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	RuleElement get(final long ruleElementUid) throws EpServiceException;
	
	/**
	 * Get the ruleElement with the given UID if it is greater than 0; 
	 * otherwise get new instance of the ruleElement of the given ruleElementType
	 * from the spring beanFactory (assuming bean id is the same as the ruleElementType).
	 * 
	 * Return null if no matching record exists.
	 *
	 * @param ruleElementUid the ruleElement UID
	 * @param ruleElementType the ruleElement type
	 * @return the rule if it exists, otherwise null.
	 * @throws EpServiceException in case of errors.
	 */
	RuleElement get(final long ruleElementUid, final String ruleElementType) throws EpServiceException;
}
