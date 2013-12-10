/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.rules.RuleActionService;

/**
 * Provides rule action component related services.
 *
 */
public class RuleActionServiceImpl extends AbstractEpPersistenceServiceImpl implements RuleActionService  {
	/**
	 * Adds the given ruleAction.
	 *
	 * @param ruleAction the ruleAction to add
	 * @return the persisted instance of ruleAction
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleAction add(final RuleAction ruleAction) throws EpServiceException { 
		sanityCheck();
		getPersistenceEngine().save(ruleAction);
		return ruleAction;
	}

	/**
	 * Updates the given ruleActionle.
	 *
	 * @param ruleAction the ruleAction to update
	 * @return RuleAction the updated rule action
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleAction update(final RuleAction ruleAction) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().update(ruleAction);
	}

	/**
	 * Delete the ruleAction.
	 *
	 * @param ruleAction the ruleAction to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final RuleAction ruleAction) throws EpServiceException { 
		sanityCheck();
		getPersistenceEngine().delete(ruleAction);
	}
	
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
	public RuleAction load(final long ruleActionUid) throws EpServiceException { 
		return this.load(ruleActionUid, null);
	}
	
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
	public RuleAction load(final long ruleActionUid, final String ruleActionType) throws EpServiceException { 
		sanityCheck();
		RuleAction ruleAction = null;
		if (ruleActionUid > 0) {
			ruleAction = getPersistenceEngine().load(RuleAction.class, ruleActionUid);
		} else if (ruleActionType != null) { 
			ruleAction = getBean(ruleActionType);
		}
		return ruleAction;
	}
	
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
	public RuleAction get(final long ruleActionUid) throws EpServiceException { 
		return this.get(ruleActionUid, null);
	}
	
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
	public RuleAction get(final long ruleActionUid, final String ruleActionType) throws EpServiceException { 
		sanityCheck();
		RuleAction ruleAction = null;
		if (ruleActionUid > 0) {
			ruleAction = getPersistenceEngine().get(RuleAction.class, ruleActionUid);
		} else if (ruleActionType != null) { 
			ruleAction = getBean(ruleActionType);
		}
		return ruleAction;
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
