/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.rules.RuleConditionService;

/**
 * Provides rule condition component related services.
 *
 */
public class RuleConditionServiceImpl extends AbstractEpPersistenceServiceImpl implements RuleConditionService  {
	/**
	 * Adds the given ruleCondition.
	 *
	 * @param ruleCondition the ruleCondition to add
	 * @return the persisted instance of ruleCondition.
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleCondition add(final RuleCondition ruleCondition) throws EpServiceException { 
		sanityCheck();
		getPersistenceEngine().save(ruleCondition);
		return ruleCondition;
	}

	/**
	 * Updates the given ruleConditionle.
	 *
	 * @param ruleCondition the ruleCondition to update
	 * @return RuleCondition the updated rule condtion
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleCondition update(final RuleCondition ruleCondition) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().update(ruleCondition);
	}

	/**
	 * Delete the ruleCondition.
	 *
	 * @param ruleCondition the ruleCondition to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final RuleCondition ruleCondition) throws EpServiceException { 
		sanityCheck();
		getPersistenceEngine().delete(ruleCondition);
	}
	
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
	public RuleCondition load(final long ruleConditionUid) throws EpServiceException { 
		return this.load(ruleConditionUid, null);
	}
	
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
	public RuleCondition load(final long ruleConditionUid, final String ruleConditionType) throws EpServiceException { 
		sanityCheck();
		RuleCondition ruleCondition = null;
		if (ruleConditionUid > 0) {
			ruleCondition = getPersistenceEngine().load(RuleCondition.class, ruleConditionUid);
		} else if (ruleConditionType != null) { 
			ruleCondition = getBean(ruleConditionType);
		}
		return ruleCondition;
	}
	
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
	public RuleCondition get(final long ruleConditionUid) throws EpServiceException { 
		return this.get(ruleConditionUid, null);
	}
	
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
	public RuleCondition get(final long ruleConditionUid, final String ruleConditionType) throws EpServiceException { 
		sanityCheck();
		RuleCondition ruleCondition = null;
		if (ruleConditionUid > 0) {
			ruleCondition = getPersistenceEngine().get(RuleCondition.class, ruleConditionUid);
		} else if (ruleConditionType != null) { 
			ruleCondition = getBean(ruleConditionType);
		}
		return ruleCondition;
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
	
}
