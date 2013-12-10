/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules;

import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.exception.DuplicateNameException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.rules.EpRuleBase;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/***
 * Provides rule engine-related services for each rule.
 *
 */
public interface RuleService extends EpPersistenceService  {
	/**
	 * Adds the given rule.
	 *
	 * @param rule the rule to add
	 * @return the persisted instance of rule
	 * @throws DuplicateNameException - if the specified promoCode is already in use.
	 * @throws DuplicatePromoCodeException - if the specified promoCode is already in use.
	 */
	Rule add(final Rule rule) throws DuplicateNameException, DuplicatePromoCodeException;

	/**
	 * Updates the given rule.
	 *
	 * @param rule the rule to update
	 * @return the updated object instance
	 * @throws DuplicateNameException - if the specified promoCode is already in use.
	 * @throws DuplicatePromoCodeException - if the specified promoCode is already in use.
	 */
	Rule update(final Rule rule) throws DuplicateNameException, DuplicatePromoCodeException;

	/**
	 * Delete the rule.
	 *
	 * @param rule the rule to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final Rule rule) throws EpServiceException;

	/**
	 * Load the rule with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param ruleUid the rule UID
	 *
	 * @return the rule if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	Rule load(final long ruleUid) throws EpServiceException;

	/**
	 * Get the rule with the given UID.
	 * Return null if no matching record exists.
	 *
	 * @param ruleUid the rule UID
	 *
	 * @return the rule if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	Rule get(final long ruleUid) throws EpServiceException;
	
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
	Rule get(final long ruleUid, final FetchGroupLoadTuner loadTuner) throws EpServiceException;
	
	/**
	 * Retrieves the rule with the given code. There should only be 1 rule with the given code.
	 * 
	 * @param code the rule code
	 * @return a rule with the given code
	 * @throws EpServiceException if the code occurs more than once
	 */
	Rule findByRuleCode(final String code) throws EpServiceException;
	
	/**
	 * Retrieves the limited use promotion code associated with a given promotion rule.
	 * 
	 * @param code the rule code is used to uniquely identify a rule.
	 * @return Long - Limited Use Promotion number.
	 * @throws EpServiceException If the rule code is not unique.
	 */
	Long findLupByRuleCode(final String code) throws EpServiceException;

	/**
	 * Returns all <code>Rule</code> uids as a list.
	 *
	 * @return all <code>Rule</code> uids as a list
	 */
	List<Long> findAllUids();
	
	/**
	 * Returns a list of <code>Rule</code>s based on the given uids. The returned rules will be populated based on the
	 * given load tuner.
	 * 
	 * @param ruleUids a collection of rule uids
	 * @return a list of <code>Rule</code>s
	 */
	List<Rule> findByUids(final Collection<Long> ruleUids);
	
	/**
	 * Returns a list of <code>Rule</code>s based on the given uids. The returned rules will be populated based on the
	 * given {@link FetchGroupLoadTuner}.
	 * 
	 * @param ruleUids a collection of rule uids
	 * @param fetchGroupLoadTuner a {@link FetchGroupLoadTuner}.
	 * @return a list of <code>Rule</code>s
	 */
	List<Rule> findByUidsWithFetchGroupLoadTuner(final Collection<Long> ruleUids, final FetchGroupLoadTuner fetchGroupLoadTuner);
	
	/**
	 * Retrieves list of <code>Rule</code> uids where the last modified date is later than
	 * the specified date.
	 * 
	 * @param date date to compare with the last modified date
	 * @return list of <code>Rule</code> whose last modified date is later than the
	 *         specified date
	 */
	List<Long> findUidsByModifiedDate(final Date date);
	
	/**
	 * Get all the available conditions configured in the
	 * system.
	 *
	 * @return Map of scenario ids as Integers to the corresponding rule elements
	 */
	Map<Integer, List<RuleCondition>> getAllConditionsMap();

	/**
	 * Get all the available actions configured in the
	 * system.
	 *
	 * @return Map of scenario ids as Integers to the corresponding rule elements
	 */
	Map<Integer, List<RuleAction>> getAllActionsMap();

	/**
	 * Get all the avaiable exceptions configured in the
	 * system.
	 *
	 * @return Map of scenario ids as Integers to the corresponding rule elements
	 */
	Map<Integer, List<RuleException>> getAllExceptionsMap();
	
	/**
	 * @param locale locale for which to find promotion names
	 * @param codes codes for which to retrieve promotion names
	 * 
	 * @return map of coupon codes as keys and localized promotion names as values.
	 */
	Map<String, String> getPromotionNamesForCouponCodes(final Locale locale, final Collection<String> codes);
	
	/**
	 * Returns map of promotion codes with associated rules identifiers.
	 * 
	 * @param promoCodes - set of promotion codes. 
	 * @return map of promotion codes with associated rules identifiers
	 */
	Map<String, Long> getRuleIdsForCouponCodes(final Set<String> promoCodes); 
	
	/**
	 * Retrieves the rule with the given name. There should only be 1 rule with the given name.
	 * 
	 * @param name the name
	 * @return a rule with the given name
	 * @throws DuplicateNameException if the name occurs more than once
	 */
	Rule findByName(final String name) throws DuplicateNameException;
	
	/**
	 * Retrieves the rule with the given promotion code. There should only be 1 rule with the
	 * given promotion code.
	 * 
	 * @param promoCode the promotion code
	 * @return a rule with the given promotion code
	 * @throws DuplicatePromoCodeException if the code occurs more than once
	 */
	Rule findByPromoCode(final String promoCode) throws DuplicatePromoCodeException;

	/**
	 * Set all the avaiable conditions configured in the system.
	 *
	 * @param conditions all the avaiable conditions.
	 */
	void setAllConditions(final List<String> conditions);

	/**
	 * Set all the avaiable actions configured in the system.
	 *
	 * @param actions all the avaiable actions.
	 */
	void setAllActions(final List<String> actions);

	/**
	 * Set all the avaiable exceptions configured in the system.
	 *
	 * @param exceptions all the avaiable actions.
	 */
	void setAllExceptions(final List<String> exceptions);
	
	/**
	 * Set the rule set service.
	 *
	 * @param ruleSetService the rule set service.
	 */
	void setRuleSetService(final RuleSetService ruleSetService);
	
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
	Collection<Long> findChangedPromoUids(final Date excludeFrom, final int ruleScenario) throws EpServiceException;
	
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
	EpRuleBase findRuleBaseByScenario(Store store, Catalog catalog, int scenarioId);
	
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
	EpRuleBase findChangedStoreRuleBases(String storeCode, int scenarioId, Date date);

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
	EpRuleBase findChangedCatalogRuleBases(String catalogCode, int scenarioId, Date date);
	
	/**
	 * Saves the given {@link EpRuleBase}. The given object should never be referenced again, as
	 * the result object may be a different object.
	 *
	 * @param ruleBase the rule base to save
	 * @return the saved rule base
	 * @throws EpServiceException in case of any errors
	 */
	EpRuleBase saveOrUpdateRuleBase(EpRuleBase ruleBase);
	
	/**
	 * Deletes the given {@link EpRuleBase}.
	 *
	 * @param ruleBase the rule base to delete
	 * @throws EpServiceException in case of any errors
	 */
	void deleteRuleBase(EpRuleBase ruleBase);

	/**
	 * Find the rule code of a rule given the id.
	 * 
	 * @param ruleId the id of the rule
	 * @return the rule code
	 */
	String findRuleCodeById(long ruleId);

	/**
	 * Finds rules that match the specified scenario and store.
	 * 
	 * @param scenarioId the scenario id.
	 * @param storeCode the store code.
	 * @return a set of rules that match.
	 */
	Collection<Rule> findByScenarioAndStore(int scenarioId, String storeCode);
	
	/**
	 * Collect promotion usage report data.
	 * 
	 * @param storeUids store uids to wich user has access.
	 * @param currency currency code or null for all currencies
	 * @param startDate start date or null 
	 * @param endDate end date. Not nullable parameter 
	 * @param withCouponCodesOnly true in case if need promotions with configured coupons codes only. Not nullable parameter.
	 * @return list of report data.	
	 */
	List<Object[]> getPromotionUsageData(
			Collection<Long> storeUids,
			Currency currency,
			Date startDate,
			Date endDate,
			Boolean withCouponCodesOnly);

    /**
	 * Find the rule id-selling context combinations for the given scenario.
	 *
	 * @param scenario rule scenario
	 * @return list of entries with rule id and its selling context
	 */
	List<Object[]> findRuleIdSellingContextByScenario(int scenario);

	/**
	 * Retrieve all the dependent promotions and put the dependent before, e.g.
	 * rule1 depends on rule2
	 * rule2 depends on rule3
	 * then the result for call retrievePromotionDependencies(rule1.uidpk) will be rul3.uidpk, rule2.uidpk.
	 *
	 * @param promos - the promotions to check if they are dependent on other promotions
	 * @return a topologically sorted set of rule uids based on dependencies. 
	 * @see http://en.wikipedia.org/wiki/Topological_sorting
	*/
	Set<Long> retrievePromotionDependencies(Set<Long> promos);
}
