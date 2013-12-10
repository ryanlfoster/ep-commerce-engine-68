/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.rules.RuleElementService;

/**
 * Provides rule condition component related services.
 *
 */
public class RuleElementServiceImpl extends AbstractEpPersistenceServiceImpl implements RuleElementService  {
	/**
	 * Adds the given ruleElement.
	 *
	 * @param ruleElement the ruleElement to add
	 * @return the persisted instance of ruleElement.
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleElement add(final RuleElement ruleElement) throws EpServiceException { 
		sanityCheck();
		getPersistenceEngine().save(ruleElement);
		return ruleElement;
	}

	/**
	 * Updates the given ruleElement.
	 *
	 * @param ruleElement the ruleElement to update
	 * @return RuleElement the updated ruleElement
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleElement update(final RuleElement ruleElement) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().update(ruleElement);
	}

	/**
	 * Delete the ruleElement.
	 *
	 * @param ruleElement the ruleElement to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final RuleElement ruleElement) throws EpServiceException { 
		sanityCheck();
		getPersistenceEngine().delete(ruleElement);
	}
	
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
	public RuleElement load(final long ruleElementUid) throws EpServiceException { 
		return this.load(ruleElementUid, null);
	}
	
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
	public RuleElement load(final long ruleElementUid, final String ruleElementType) throws EpServiceException { 
		sanityCheck();
		RuleElement ruleElement = null;
		if (ruleElementUid > 0) {
			ruleElement = getPersistentBeanFinder().load(ContextIdNames.ABSTRACT_RULE_ELEMENT, ruleElementUid);
		} else if (ruleElementType != null) { 
			ruleElement = getBean(ruleElementType);
		}
		return ruleElement;
	}
	
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
	public RuleElement get(final long ruleElementUid) throws EpServiceException { 
		return this.get(ruleElementUid, null);
	}
	
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
	public RuleElement get(final long ruleElementUid, final String ruleElementType) throws EpServiceException { 
		sanityCheck();
		RuleElement ruleElement = null;
		if (ruleElementUid > 0) {
			ruleElement = getPersistentBeanFinder().get(ContextIdNames.ABSTRACT_RULE_ELEMENT, ruleElementUid);
		} else if (ruleElementType != null) { 
			ruleElement = getBean(ruleElementType);
		}
		return ruleElement;
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
