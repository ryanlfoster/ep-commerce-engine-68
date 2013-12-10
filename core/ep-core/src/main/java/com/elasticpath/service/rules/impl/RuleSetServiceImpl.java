/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import java.util.Date;
import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.rules.RuleSetService;

/**
 * Provides Rule Engine related services.
 *
 */
public class RuleSetServiceImpl  extends AbstractEpPersistenceServiceImpl implements RuleSetService {

	private TimeService timeService;

	/**
	 * Adds the given ruleSet.
	 *
	 * @param ruleSet the ruleSet to add
	 * @return the persisted instance of ruleSet
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleSet add(final RuleSet ruleSet) throws EpServiceException {
		sanityCheck();
		try {
			ruleSet.validate();
		} catch (EpDomainException epde) {
			throw new EpServiceException("Rule set not valid.", epde);
		}
		getPersistenceEngine().save(ruleSet);
		return ruleSet;
	}

	/**
	 * Updates the given ruleset.
	 *
	 * @param ruleSet the ruleSet to update
	 * @return RuleSet the updated RuleSet
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleSet update(final RuleSet ruleSet) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().merge(ruleSet);
	}

	/**
	 * Delete the ruleSet.
	 *
	 * @param ruleSet the ruleSet to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final RuleSet ruleSet) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(ruleSet);
	}


	/**
	 * Load the ruleSet with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param ruleSetUid the ruleSet UID
	 *
	 * @return the ruleSet if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleSet load(final long ruleSetUid) throws EpServiceException {
		sanityCheck();
		RuleSet ruleSet = null;
		if (ruleSetUid <= 0) {
			ruleSet = getBean(ContextIdNames.RULE_SET);
		} else {
			ruleSet = getPersistentBeanFinder().load(ContextIdNames.RULE_SET, ruleSetUid);
		}
		return ruleSet;
	}

	/**
	 * Find the rule set by its scenario id.
	 *
	 * @param scenarioId the id of the scenario (see <code>Scenarios</code> interface)
	 * @return the Rule Set if it exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleSet findByScenarioId(final int scenarioId) throws EpServiceException {
		sanityCheck();

		final List<RuleSet> results = getPersistenceEngine().retrieveByNamedQuery("RULESET_FIND_BY_SCENARIO", Integer.valueOf(scenarioId));
		RuleSet ruleSet = null;
		if (results.size() == 1) {
			ruleSet = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate rule set: -- " + scenarioId);
		}

		return ruleSet;
	}
	
	/**
	 * Find the rule set by its name.
	 *
	 * @param name the name of the scenario (see <code>Scenarios</code> interface)
	 * @return the Rule Set if it exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleSet findByName(final String name) throws EpServiceException {
		sanityCheck();

		final List<RuleSet> results = getPersistenceEngine().retrieveByNamedQuery("RULESET_FIND_BY_NAME", name);
		RuleSet ruleSet = null;
		if (results.size() == 1) {
			ruleSet = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate rule set: -- " + name);
		}

		return ruleSet;
	}


	/**
	 * Get the ruleSet with the given UID.
	 * Return null if no matching record exists.
	 *
	 * @param ruleSetUid the ruleSet UID
	 *
	 * @return the ruleSet if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public RuleSet get(final long ruleSetUid) throws EpServiceException {
		sanityCheck();
		RuleSet ruleSet = null;
		if (ruleSetUid <= 0) {
			ruleSet = getBean(ContextIdNames.RULE_SET);
		} else {
			ruleSet = getPersistentBeanFinder().get(ContextIdNames.RULE_SET, ruleSetUid);
		}
		return ruleSet;
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
	 * List all ruleSets stored in the database.
	 *
	 * @return a list of ruleSets
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public List<RuleSet> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("RULESET_SELECT_ALL");
	}

	/**
	 * Retrieves list of <code>RuleSet</code>s where the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>RuleSet</code> whose last modified date is later than the specified date
	 */
	public List<RuleSet> findByModifiedDate(final Date date) {
		return getPersistenceEngine().retrieveByNamedQuery("RULE_SET_SELECT_BY_MODIFIED_DATE", date);
	}

	/**
	 * Update the <code>ruleSet</code>'s last modified timestamp to the current time.
	 * 
	 * @param ruleSet the ruleSet whose timestamp is to be updated.
	 */
	public void updateLastModifiedTime(final RuleSet ruleSet) {
		Date currentDateTime = timeService.getCurrentTime();
		getPersistenceEngine().executeNamedQuery("RULE_SET_UPDATE_LAST_MODIFIED_DATE",
				new Long(ruleSet.getUidPk()), currentDateTime);
	}

	/**
	 * Returns the time service.
	 *
	 * @return the time service.
	 */
	public TimeService getTimeService() {
		return timeService;
	}

	/**
	 * Sets the time service.
	 *
	 * @param timeService the time service
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}
}
