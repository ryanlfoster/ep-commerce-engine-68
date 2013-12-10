/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateNameException;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.EpRuleBase;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.ProcessingHook;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.DuplicatePromoCodeException;
import com.elasticpath.service.rules.ReportingRuleService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.rules.RuleSetService;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;

/**
 * Provides Rule Engine related services.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class RuleServiceImpl extends AbstractEpPersistenceServiceImpl implements RuleService {
	
	private static final String PLACEHOLDER_FOR_LIST = "list";

	private List<String> allConditions;

	private List<String> allActions;

	private List<String> allExceptions;

	private RuleSetService ruleSetService;
	
	private TimeService timeService;
	
	private FetchPlanHelper fetchPlanHelper;
	
	private ProcessingHook processingHook;

	private static final String QUOTE = "\"";
	
	private CouponConfigService couponConfigService;
	
	private ReportingRuleService reportingRuleService;

	private IndexNotificationService indexNotificationService;
	
	/**
	 * Adds the given rule.
	 *
	 * @param rule the rule to add
	 * @return the persisted instance of rule.
	 * @throws DuplicateNameException - if the specified promoCode is already in use.
	 * @throws DuplicatePromoCodeException - if the specified promoCode is already in use.
	 */
	public Rule add(final Rule rule) throws DuplicateNameException, DuplicatePromoCodeException {
		sanityCheck();
		try {
			rule.validate();
		} catch (EpDomainException epde) {
			throw new EpServiceException("Rule set not valid.", epde);
		}
		checkNameUnique(rule);
		
		if (processingHook != null) {
			processingHook.preAdd(rule);
		}
		
		rule.setLastModifiedDate(timeService.getCurrentTime());
		getPersistenceEngine().save(rule);
		ruleSetService.updateLastModifiedTime(rule.getRuleSet());
		
		if (processingHook != null) {
			processingHook.postAdd(rule);
		}
		indexNotificationService.addNotificationForEntityIndexUpdate(IndexType.PROMOTION, rule.getUidPk());
		return rule;
	}

	/**
	 * Updates the given rule.
	 *
	 * @param rule the rule to update]
	 * @return the updated object instance
	 * @throws DuplicateNameException - if the specified promoCode is already in use.
	 * @throws DuplicatePromoCodeException - if the specified promoCode is already in use.
	 */
	public Rule update(final Rule rule) throws DuplicateNameException, DuplicatePromoCodeException {
		sanityCheck();
		try {
			rule.validate();
		} catch (EpDomainException epde) {
			throw new EpServiceException("Rule set not valid.", epde);
		}
		checkNameUnique(rule);

		final Rule oldRule = getOldRule(rule);
		
		if (processingHook != null) {
			processingHook.preUpdate(oldRule, rule);
		}

		rule.setLastModifiedDate(timeService.getCurrentTime());
		Rule updatedRule = updateInternal(rule);
		ruleSetService.updateLastModifiedTime(updatedRule.getRuleSet());
		
		if (processingHook != null) {
			processingHook.postUpdate(oldRule, updatedRule);
		}
		
		indexNotificationService.addNotificationForEntityIndexUpdate(IndexType.PROMOTION, updatedRule.getUidPk());
		return updatedRule;
	}

	/**
	 * Updates persistent rule.
	 * 
	 * @param rule the rule to be updated
	 * @return updated rule
	 */
	protected Rule updateInternal(final Rule rule) {
		return getPersistenceEngine().merge(rule);
	}

	/**
	 * We must retrieve the old rule within a separate JPA session otherwise the oldRule will be updated with new values after save.
	 * 
	 * @param rule rule to get old version for
	 * @return copy of rule as it was before update
	 */
	protected Rule getOldRule(final Rule rule) {
		return getPersistentBeanFinder().loadWithNewSession(ContextIdNames.PROMOTION_RULE, rule.getUidPk());
	}

	/**
	 * Delete the rule.
	 *
	 * @param rule the rule to remove
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final Rule rule) throws EpServiceException {
		sanityCheck();
		
		if (processingHook != null) {
			processingHook.preDelete(rule);
		}
		
		// Remove any coupon configuration, usage, coupons if there are some associated with this rule.
		CouponConfig couponConfig = getCouponConfigService().findByRuleCode(rule.getCode());
		if (couponConfig != null) {
			couponConfigService.delete(couponConfig);
			getPersistenceEngine().flush();
		}
		
		getPersistenceEngine().delete(rule);
		
		if (processingHook != null) {
			processingHook.postDelete(rule);
		}
		
		ruleSetService.updateLastModifiedTime(rule.getRuleSet());
	}

	/**
	 * Load the rule with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param ruleUid the rule UID
	 * @return the rule if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Rule load(final long ruleUid) throws EpServiceException {
		sanityCheck();

		if (ruleUid <= 0) {
			return getBean(ContextIdNames.PROMOTION_RULE);
		}

		return getPersistentBeanFinder().load(ContextIdNames.PROMOTION_RULE, ruleUid);
	}

	/**
	 * Get the rule with the given UID. Return null if no matching record exists.
	 *
	 * @param ruleUid the rule UID
	 * @return the rule if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Rule get(final long ruleUid) throws EpServiceException {
		return get(ruleUid, null);
	}
	
	/**
	 * Gets the rule with the given UID. Returns <code>null</code> if no matching records exist.
	 * Give a load tuner to fine tune the parameters that are loaded into the retrieved object. If
	 * no tuner is given the default fields will be loaded.
	 * 
	 * @param ruleUid the rule UID to fetch
	 * @param loadTuner the load tuner
	 * @return the rule if the UID exists, otherwise <code>null</code>
	 * @throws EpServiceException in case of any errors
	 */
	public Rule get(final long ruleUid, final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		sanityCheck();
		Rule rule = null;
		if (ruleUid <= 0) {
			rule = getBean(ContextIdNames.PROMOTION_RULE);
		} else {
			fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);
			rule = getPersistentBeanFinder().get(ContextIdNames.PROMOTION_RULE, ruleUid);
			fetchPlanHelper.clearFetchPlan();
		}
		return rule;
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
	 * Retrieves the rule with the given code. There should only be 1 rule with the given code.
	 * 
	 * @param code the rule code
	 * @return a rule with the given code
	 * @throws EpServiceException if the code occurs more than once
	 */
	public Rule findByRuleCode(final String code) throws EpServiceException {
		sanityCheck();

		final List<Rule> results = getPersistenceEngine().retrieveByNamedQuery("RULE_FIND_BY_CODE", code);

		if (results == null || results.isEmpty()) {
			return null;
		}
		if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate rule code " + QUOTE + code + QUOTE + ".");
		}

		return results.get(0);
	}

	/**
	 * Retrieves the limited use promotion code associated with a given promotion rule.
	 * 
	 * @param code the rule code is used to uniquely identify a rule.
	 * @return Long - Limited Use Promotion number.
	 * @throws EpServiceException If the rule code is not unique.
	 */
	public Long findLupByRuleCode(final String code) throws EpServiceException {
		sanityCheck();

		final List<Long> results = getPersistenceEngine().retrieveByNamedQuery("FIND_LUP_BY_RULECODE", code);
		
		if (results == null || results.isEmpty()) {
			return null;
		}
		
		if (results.size() > 1) {
				throw new EpServiceException("Inconsistent data -- duplicate rule code \"" + code + "\".");
 		}

		return results.get(0);
 	}
	
	/**
	 * Returns all <code>Rule</code> uids as a list.
	 *
	 * @return all <code>Rule</code> uids as a list
	 */
	public List<Long> findAllUids() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("RULE_UIDS_ALL");
	}
	
	/**
	 * Returns a list of <code>Rule</code>s based on the given uids. 
	 * 
	 * @param ruleUids a collection of rule uids
	 * @return a list of <code>Rule</code>s
	 */
	public List<Rule> findByUids(final Collection<Long> ruleUids) {
		sanityCheck();

		if (ruleUids == null || ruleUids.isEmpty()) {
			return Collections.emptyList();
		}

		return getPersistenceEngine().retrieveByNamedQueryWithList("RULE_FIND_BY_UIDS", "list", ruleUids);
	}
	
	/**
	 * Returns a list of <code>Rule</code>s based on the given uids. The returned rules will be populated based on the
	 * given {@link FetchGroupLoadTuner}.
	 * 
	 * @param ruleUids a collection of rule uids
	 * @param fetchGroupLoadTuner a {@link FetchGroupLoadTuner}.
	 * @return a list of <code>Rule</code>s
	 */
	public List<Rule> findByUidsWithFetchGroupLoadTuner(final Collection<Long> ruleUids, final FetchGroupLoadTuner fetchGroupLoadTuner) {
		sanityCheck();

		if (ruleUids == null || ruleUids.isEmpty()) {
			return Collections.emptyList();
		}

		fetchPlanHelper.configureFetchGroupLoadTuner(fetchGroupLoadTuner);
		final List<Rule> result = getPersistenceEngine().retrieveByNamedQueryWithList("RULE_FIND_BY_UIDS", "list", ruleUids);
		fetchPlanHelper.clearFetchPlan();
		return result;
	}
	
	/**
	 * Retrieves list of <code>Rule</code> uids where the last modified date is later than
	 * the specified date.
	 * 
	 * @param date date to compare with the last modified date
	 * @return list of <code>Rule</code> whose last modified date is later than the
	 *         specified date
	 */
	public List<Long> findUidsByModifiedDate(final Date date) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("RULE_UIDS_SELECT_BY_MODIFIED_DATE", date);
	}
	
	/**
	 * Saves the given {@link EpRuleBase}. The given object should never be referenced again, as
	 * the result object may be a different object.
	 * 
	 * @param ruleBase the rule base to save
	 * @return the saved rule base
	 * @throws EpServiceException in case of any errors
	 */
	public EpRuleBase saveOrUpdateRuleBase(final EpRuleBase ruleBase) {
		sanityCheck();
		return getPersistenceEngine().saveOrUpdate(ruleBase);
	}
	
	/**
	 * Deletes the given {@link EpRuleBase}.
	 *
	 * @param ruleBase the rule base to delete
	 * @throws EpServiceException in case of any errors
	 */
	public void deleteRuleBase(final EpRuleBase ruleBase) {
		sanityCheck();
		getPersistenceEngine().delete(ruleBase);
	}
	
	/**
	 * Finds the given rule base by {@link Store}, {@link Catalog} and scenario ID. It is OK to
	 * set either {@link Store} or {@link Catalog} to {@code null}.
	 * 
	 * @param store the store
	 * @param catalog the catalog
	 * @param scenarioId the scenario ID
	 * @return a rule base if it exists, otherwise {@code null}
	 * @throws EpServiceException in case of any errors
	 */
	public EpRuleBase findRuleBaseByScenario(final Store store, final Catalog catalog, final int scenarioId) {
		sanityCheck();
		
		List<EpRuleBase> result = Collections.emptyList();
		if (store == null && catalog == null) {
			throw new EpServiceException("store and catalog cannot both be null");
		} else if (store == null) {
			result = getPersistenceEngine().retrieveByNamedQuery("EP_RULE_BASE_FIND_BY_CATALOG_SCENARIO", catalog.getUidPk(), scenarioId);
		} else if (catalog == null) {
			result = getPersistenceEngine().retrieveByNamedQuery("EP_RULE_BASE_FIND_BY_STORE_SCENARIO", store.getUidPk(), scenarioId);
		} else {
			result = getPersistenceEngine().retrieveByNamedQuery("EP_RULE_BASE_FIND_BY_STORE_CATALOG_SCENARIO",
					store.getUidPk(),
					catalog.getUidPk(),
					scenarioId);
		}
		
		if (result == null || result.isEmpty()) {
			return null;
		} else if (result.size() > 1) {
			// CHECKSTYLE:OFF
			throw new EpServiceException(String.format(
					"Inconsistent data, found more than 1 item, expected 1 with store %1$s, "
							+ "catalog %2$s and scenario %3$d", store, (catalog == null) ? "null" : catalog.getCode(), scenarioId));
			// CHECKSTYLE:ON
		}
		return result.get(0);
	}
	
	/**
	 * Finds the {@link EpRuleBase} for the given {@code storeCode} and {@code scenarioId} where
	 * the rule base has changed since the given {@link Date}. If a rule base has not changed
	 * since the given date, {@code null} will be returned. If the rule base does not exist
	 * {@code null} will also be returned. Store rules do not have a catalog set.
	 * 
	 * @param storeCode the store code to use
	 * @param scenarioId the scenario ID of the rule base
	 * @param date the date from which changes should be recognized
	 * @return a rule base if it exists and was modified
	 * @throws EpServiceException in case of any errors
	 */
	public EpRuleBase findChangedStoreRuleBases(final String storeCode, final int scenarioId, final Date date) {
		sanityCheck();
		final List<EpRuleBase> result = getPersistenceEngine().retrieveByNamedQuery("EP_RULE_BASE_FIND_CHANGED_STORECODE_SCENARIO",
				storeCode,
				scenarioId,
				date);
		if (result == null || result.isEmpty()) {
			return null;
		} else if (result.size() > 1) {
			throw new EpServiceException(String.format(
					"Inconsistent data, found more than 1 item, expected 1 with store code %1$s, "
							+ "no catalog and scenario %2$d", storeCode, scenarioId));
		}
		return result.get(0);
	}
	
	/**
	 * Finds the {@link EpRuleBase} for the given {@code catalogCode} and {@code scenarioId} where
	 * the rule base has changed since the given {@link Date}. If a rule base has not changed
	 * since the given date, {@code null} will be returned. If the rule base does not exist
	 * {@code null} will also be returned. Catalog rules do not have a store set.
	 * 
	 * @param catalogCode the catalog code to use
	 * @param scenarioId the scenario ID of the rule base
	 * @param date the date from which changes should be recognized
	 * @return a rule base if it exists and was modified
	 * @throws EpServiceException in case of any errors
	 */
	public EpRuleBase findChangedCatalogRuleBases(final String catalogCode, final int scenarioId, final Date date) {
		sanityCheck();
		final List<EpRuleBase> result = getPersistenceEngine().retrieveByNamedQuery("EP_RULE_BASE_FIND_CHANGED_CATALOGCODE_SCENARIO",
				catalogCode,
				scenarioId,
				date);
		if (result == null || result.isEmpty()) {
			return null;
		} else if (result.size() > 1) {
			throw new EpServiceException(String.format(
					"Inconsistent data, found more than 1 item, expected 1 with catalog code %1$s, "
							+ "no catalog and scenario %2$d", catalogCode, scenarioId));
		}
		return result.get(0);
	}

	/**
	 * Set all the available conditions configured in the system.
	 *
	 * @param conditions all the available conditions.
	 */
	public void setAllConditions(final List<String> conditions) {
		this.allConditions = conditions;
	}

	/**
	 * Set all the available actions configured in the system.
	 *
	 * @param actions all the available actions.
	 */
	public void setAllActions(final List<String> actions) {
		this.allActions = actions;
	}

	/**
	 * Set all the available exceptions configured in the system.
	 *
	 * @param exceptions all the available actions.
	 */
	public void setAllExceptions(final List<String> exceptions) {
		this.allExceptions = exceptions;
	}

	/**
	 * Creates a map of scenario Integers to a list of rule elements that are valid in those
	 * scenarios.
	 * 
	 * @param ruleElements a list of rule elements to put in the map
	 * @return the scenarioId Integer -> rule element List Map.
	 */
	private <T extends RuleElement> Map<Integer, List<T>> createElementMap(final List<String> ruleElements) {
		RuleScenarios ruleScenarios = getBean(ContextIdNames.RULE_SCENARIOS);
		Set<Integer> ruleScenarioSet = ruleScenarios.getAvailableScenarios();
		Map<Integer, List<T>> ruleElementMap = new HashMap<Integer, List<T>>();

		// Create a map of scenario id integers -> empty array lists.
		for (Integer currScenarioId : ruleScenarioSet) {
			ruleElementMap.put(currScenarioId, new ArrayList<T>());
		}

		for (String currElementName : ruleElements) {
			T currElement = this.<T> getBean(currElementName);

			for (Integer currScenarioId : ruleScenarioSet) {
				if (currElement.appliesInScenario(currScenarioId.intValue())) {
					List<T> elementList = ruleElementMap.get(currScenarioId);
					elementList.add(currElement);
				}
			}
		}

		return ruleElementMap;
	}

	/**
	 * Get all the available conditions configured in the system.
	 *
	 * @return Map of scenario ids as Integers to the corresponding rule elements
	 */
	public Map<Integer, List<RuleCondition>> getAllConditionsMap() {
		return createElementMap(allConditions);
	}

	/**
	 * Get all the available actions configured in the system.
	 *
	 * @return Map of scenario ids as Integers to the corresponding rule elements
	 */
	public Map<Integer, List<RuleAction>> getAllActionsMap() {
		return createElementMap(allActions);
	}

	/**
	 * Get all the available exceptions configured in the system.
	 *
	 * @return Map of scenario ids as Integers to the corresponding rule elements
	 */
	public Map<Integer, List<RuleException>> getAllExceptionsMap() {
		RuleScenarios ruleScenarios = getBean(ContextIdNames.RULE_SCENARIOS);
		Set<Integer> ruleScenarioSet = ruleScenarios.getAvailableScenarios();
		Map<Integer, List<RuleException>> ruleElementMap = new HashMap<Integer, List<RuleException>>();

		// Create a map of scenario id integers -> empty array lists.
		for (Integer currScenarioId : ruleScenarioSet) {
			ruleElementMap.put(currScenarioId, new ArrayList<RuleException>());
		}

		for (String currExceptionName : allExceptions) {
			RuleException currRuleException = getBean(currExceptionName);

			for (Integer currScenarioId : ruleScenarioSet) {
				if (currRuleException.appliesInScenario(currScenarioId.intValue())) {
					List<RuleException> elementList = ruleElementMap.get(currScenarioId);
					elementList.add(currRuleException);
				}
			}
		}
		return ruleElementMap;
	}
	
	/**
	 * Retrieves the rule with the given name. There should only be 1 rule with the given name.
	 * 
	 * @param name the name
	 * @return a rule with the given name
	 * @throws DuplicateNameException if the name occurs more than once
	 */
	public Rule findByName(final String name) throws DuplicateNameException {
		sanityCheck();

		final List<Rule> results = getPersistenceEngine().retrieveByNamedQuery("RULE_FIND_BY_NAME", name);

		if (results == null || results.isEmpty()) {
			return null;
		}
		if (results.size() > 1) {
			throw new DuplicateNameException("Inconsistent data -- duplicate rule name " + QUOTE + name + QUOTE + ".");
		}

		return results.get(0);
	}
	
	/**
	 * Checks that the specified rule's name does not already exist.
	 *
	 * @param rule the rule whose name is to be checked
	 * @throws DuplicateNameException if the name exists
	 */
	private void checkNameUnique(final Rule rule) throws DuplicateNameException {
		final Rule ruleWithSameName = findByName(rule.getName());

		// Name can only be the same if the UID is the same (i.e. same rule)
		if ((ruleWithSameName != null) && (ruleWithSameName.getUidPk() != rule.getUidPk())) {
			throw new DuplicateNameException(String.format("Rule Name " + QUOTE + "%1$s" + QUOTE + " already exists.", rule.getName()));
		}
	}

	/**
	 * Retrieves the rule with the given promotion code. There should only be 1 rule with the
	 * given promotion code.
	 * 
	 * @param promoCode the promotion code
	 * @return a rule with the given promotion code
	 * @throws DuplicatePromoCodeException if the code occurs more than once
	 */
	public Rule findByPromoCode(final String promoCode) throws DuplicatePromoCodeException {
		sanityCheck();

		List<Rule> results = getPersistenceEngine().retrieveByNamedQuery("RULE_FIND_BY_COUPON_CODE", promoCode);

		if (results.isEmpty()) {
			return null;
		}
		if (results.size() > 1) {
			throw new DuplicateNameException("Inconsistent data -- duplicate promotion code " + QUOTE + promoCode + QUOTE + ".");
		}

		return results.get(0);
	}

	/**
	 * Returns map of promotion codes with associated rules identifiers.
	 * 
	 * @param codes - set of promotion codes. 
	 * @return map of promotion codes with associated rules identifiers
	 */
	public Map<String, Long> getRuleIdsForCouponCodes(final Set<String> codes) {
		if (CollectionUtils.isNotEmpty(codes)) {
			
			// Ask the database for a set of code, rule pairs
			List<Object[]> results = getPersistenceEngine().retrieveByNamedQueryWithList("RULES_MAPPED_BY_COUPON_CODE",
					PLACEHOLDER_FOR_LIST , codes);
			
			// Create a map object of code to display name
			final Map<String, Long> promoCodesWithRuleIds = new HashMap<String, Long>(codes.size());
			for (Object[] codeRulePair : results) {
				String code = (String) codeRulePair[0];
				Rule rule = (Rule) codeRulePair[1];
				promoCodesWithRuleIds.put(code, rule.getUidPk());
			}
						
			return promoCodesWithRuleIds;
			
		}
		return Collections.emptyMap();
	}

	
	@Override
	public Map<String, String> getPromotionNamesForCouponCodes(final Locale locale, final Collection<String> codes) {
		if (CollectionUtils.isNotEmpty(codes)) {
			
			// Ask the database for a set of code, rule pairs
			List<Object[]> results = getPersistenceEngine().retrieveByNamedQueryWithList("RULES_MAPPED_BY_COUPON_CODE",
					PLACEHOLDER_FOR_LIST , codes);
			
			// Create a map object of code to display name
			final Map<String, String> promoNames = new HashMap<String, String>(codes.size());
			for (Object[] codeRulePair : results) {
				String code = (String) codeRulePair[0];
				Rule rule = (Rule) codeRulePair[1];
				if (rule.getDisplayName(locale) == null) {
					promoNames.put(code, StringUtils.EMPTY);
				} else {
					promoNames.put(code, rule.getDisplayName(locale));
				}
				
			}
			
			// For codes that didn't return a corresponding rule, return an empty string for the name
			@SuppressWarnings("unchecked")
			Collection<String> codesWithNoRule = CollectionUtils.subtract(codes, promoNames.keySet());
			for (String code : codesWithNoRule) {
				promoNames.put(code, StringUtils.EMPTY);
			}
			
			return promoNames;
			
		}
		return Collections.emptyMap();
	}

	/**
	 * Set the rule set service.
	 *
	 * @param ruleSetService the rule set service.
	 */
	public void setRuleSetService(final RuleSetService ruleSetService) {
		this.ruleSetService = ruleSetService;
	}

	/**
	 * Finds {@link Rule} UIDs that have been changed since current, excluding those date that are
	 * before the given exclude date. Changed Rules have had either their start date or end date
	 * passed by the current date. Passing <code>null</code> the exclude date will not exclude
	 * any dates.
	 * 
	 * @param excludeFrom any date before this will be excluded
	 * @param ruleScenario the scenario to find changes for
	 * @return a collection of {@link Rule} UIDs that have been changed since the current date
	 * @throws EpServiceException in case of any errors
	 */
	public Collection<Long> findChangedPromoUids(final Date excludeFrom, final int ruleScenario) throws EpServiceException {
		sanityCheck();
		final Date currentDate = new Date();
		Date dateToExclude = excludeFrom;
		if (dateToExclude == null) {
			dateToExclude = new Date(0);
		}

		if (currentDate.before(dateToExclude)) {
			// we are excluding all dates, don't need a DB call
			return Collections.emptyList();
		}
		return getPersistenceEngine().retrieveByNamedQuery("RULE_UIDS_SCOPE_CHANGED", currentDate, dateToExclude, ruleScenario);
	}

	/**
	 * Set the time service.
	 * 
	 * @param timeService the <code>TimeService</code> instance.
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * Sets the {@link FetchPlanHelper} instance to use.
	 *
	 * @param fetchPlanHelper the {@link FetchPlanHelper} instance to use
	 */
	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}

	/**
	 * Sets the processing hook to use.
	 *
	 * @param processingHook the processing hook to use
	 */
	public void setProcessingHook(final ProcessingHook processingHook) {
		this.processingHook = processingHook;
	}

	@Override
	public String findRuleCodeById(final long ruleId) {
		List<String> results = getPersistenceEngine().retrieveByNamedQuery("RULE_CODE_BY_UID", Long.valueOf(ruleId));
		if (!results.isEmpty()) {
			return results.get(0);
		}
		return null;
	}

	@Override
	public Collection<Rule> findByScenarioAndStore(final int scenarioId, final String storeCode) {
		return getPersistenceEngine().retrieveByNamedQuery("RULES_BY_SCENARIO_AND_STORE", scenarioId, storeCode);
	}
	
	@Override
	public List<Object[]> getPromotionUsageData(
			final Collection<Long> storeUids,
			final Currency currency,
			final Date startDate,
			final Date endDate,
			final Boolean withCouponCodesOnly) {
		return getReportingRuleService().getPromotionUsageData(storeUids, currency, startDate, endDate, withCouponCodesOnly);
	}
	

	/**
	 * Set the coupon config service.
	 * 
	 * @param couponConfigService the couponConfigService to set
	 */
	public void setCouponConfigService(final CouponConfigService couponConfigService) {
		this.couponConfigService = couponConfigService;
	}

	/**
	 * Get the coupon config service.
	 * 
	 * @return the couponConfigService
	 */
	public CouponConfigService getCouponConfigService() {
		return couponConfigService;
	}

	/**
	 * 
	 * @return reporting rule service.
	 */
	public ReportingRuleService getReportingRuleService() {
		return reportingRuleService;
	}

	/**
	 * Set reporting rule service.
	 * @param reportingRuleService reporting rule service to use.
	 */
	public void setReportingRuleService(final ReportingRuleService reportingRuleService) {
		this.reportingRuleService = reportingRuleService;
	}

	/**
	 * @param indexNotificationService instance to set
	 */
	public void setIndexNotificationService(
			final IndexNotificationService indexNotificationService) {
		this.indexNotificationService = indexNotificationService;
	}

	@Override
	public List<Object[]> findRuleIdSellingContextByScenario(final int scenario) {
		List<Object[]> results = getPersistenceEngine().retrieveByNamedQuery("RULEID_AND_SELLINGCONTEXT_FIND_BY_SCENARIO", scenario);
		for (Object[] ruleIdSellingContext : results) {
			ruleIdSellingContext[1] = getPersistenceEngine().detach(ruleIdSellingContext[1]);
		}
		return results;
	}
	
	@Override
	public Set<Long> retrievePromotionDependencies(final Set<Long> promos) {
		final Set<Long> resultSet = new LinkedHashSet<Long>();
		final List<Long> dependents = getPersistenceEngine().retrieveByNamedQueryWithList("FIND_ALL_COUPON_ASSIGNMENT_PROMOS_FOR_A_UIDPK_LIST",
				PLACEHOLDER_FOR_LIST,
				promos,
				RuleParameter.RULE_CODE_KEY);
		if (!dependents.isEmpty()) {
			resultSet.addAll(retrievePromotionDependencies(new HashSet<Long>(dependents)));
			resultSet.addAll(dependents);
		}
		
		return resultSet;
	}

}
