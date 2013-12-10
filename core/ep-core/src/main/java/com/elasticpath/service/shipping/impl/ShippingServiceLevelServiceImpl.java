/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shipping.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.IndexNotification.AffectedEntityType;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.rules.RuleParameterService;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.query.ShippingLevelsSearchCriteriaImpl;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * Provide shipping service level-related business service.
 */
public class ShippingServiceLevelServiceImpl extends AbstractEpPersistenceServiceImpl implements ShippingServiceLevelService {

	private final List<ShippingCostCalculationMethod> allShippingCostCalculationMethods = new ArrayList<ShippingCostCalculationMethod>();

	private RuleParameterService ruleParameterService;

	private TimeService timeService;

	private IndexNotificationService indexNotificationService;

	private Geography geography;

	/**
	 * Adds the given shippingServiceLevel.
	 *
	 * @param shippingServiceLevel the shippingServiceLevel to add
	 * @return the persisted instance of shippingServiceLevel
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public ShippingServiceLevel add(final ShippingServiceLevel shippingServiceLevel) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().save(shippingServiceLevel);
		indexNotificationService.addNotificationForEntityIndexUpdate(IndexType.SHIPPING_SERVICE_LEVEL, shippingServiceLevel.getUidPk());
		return shippingServiceLevel;
	}

	/**
	 * Updates the given shippingServiceLevel.
	 *
	 * @param shippingServiceLevel the shippingServiceLevel to update
	 * @return ShippingServiceLevel the updated ShippingServiceLevel
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public ShippingServiceLevel update(final ShippingServiceLevel shippingServiceLevel) throws EpServiceException {
		sanityCheck();
		shippingServiceLevel.setLastModifiedDate(timeService.getCurrentTime());
		ShippingServiceLevel persistedShippingServiceLevel = getPersistenceEngine().merge(shippingServiceLevel);
		indexNotificationService.addNotificationForEntityIndexUpdate(IndexType.SHIPPING_SERVICE_LEVEL, persistedShippingServiceLevel.getUidPk());
		return persistedShippingServiceLevel;
	}

	/**
	 * Delete the shippingServiceLevel.
	 *
	 * @param shippingServiceLevel the shippingServiceLevel to remove
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public void remove(final ShippingServiceLevel shippingServiceLevel) throws EpServiceException {
		sanityCheck();
		final long deletedShippingServiceLevelUid = shippingServiceLevel.getUidPk();
		getPersistenceEngine().delete(shippingServiceLevel);
		notifyShippingServiceLevelDeleted(deletedShippingServiceLevelUid);
	}

	/**
	 * Stores notification about deleted shipping service level to update indexes accordingly.
	 *
	 * @param deletedShippingServiceLevelUid UID of shipping service level that has been deleted
	 */
	void notifyShippingServiceLevelDeleted(final long deletedShippingServiceLevelUid) {
		final IndexNotification notification = getBean(ContextIdNames.INDEX_NOTIFICATION);
		notification.setAffectedEntityType(AffectedEntityType.SINGLE_UNIT);
		notification.setAffectedUid(deletedShippingServiceLevelUid);
		notification.setIndexType(IndexType.SHIPPING_SERVICE_LEVEL);
		notification.setUpdateType(UpdateType.DELETE);
		indexNotificationService.add(notification);
	}

	/**
	 * List all shippingServiceLevels stored in the database for the specified store with storeCode.
	 *
	 * @param storeCode the store code
	 * @return a list of shippingServiceLevels
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public List<ShippingServiceLevel> findByStore(final String storeCode) throws EpServiceException {
		sanityCheck();

		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_SELECT_BY_STORE", storeCode);
	}

	/**
	 * List all shippingServiceLevels stored in the database for the specified store with storeCode.
	 *
	 * @param storeCode the store code
	 * @param active should only active service levels be retrieved or not
	 * @return a list of shippingServiceLevels
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public List<ShippingServiceLevel> findByStoreAndState(final String storeCode, final boolean active) throws EpServiceException {
		sanityCheck();

		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_SELECT_BY_STORE_AND_STATE", storeCode, active);
	}

	@Override
	public List<Long> findUidsByStoreAndState(final String storeCode, final boolean active) throws EpServiceException {
		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_UIDS_SELECT_BY_STORE_AND_STATE", storeCode, active);
	}

	/**
	 * Load the shippingServiceLevel with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param shippingServiceLevelUid the shippingServiceLevel UID
	 * @return the shippingServiceLevel if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public ShippingServiceLevel load(final long shippingServiceLevelUid) throws EpServiceException {
		sanityCheck();
		ShippingServiceLevel shippingServiceLevel = null;
		if (shippingServiceLevelUid <= 0) {
			shippingServiceLevel = getBean(ContextIdNames.SHIPPING_SERVICE_LEVEL);
		} else {
			shippingServiceLevel = getPersistentBeanFinder().load(ContextIdNames.SHIPPING_SERVICE_LEVEL, shippingServiceLevelUid);
		}
		return shippingServiceLevel;
	}

	/**
	 * Get the shippingServiceLevel with the given UID. Return null if no matching record exists.
	 *
	 * @param shippingServiceLevelUid the shippingServiceLevel UID
	 * @return the shippingServiceLevel if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public ShippingServiceLevel get(final long shippingServiceLevelUid) throws EpServiceException {
		sanityCheck();
		ShippingServiceLevel shippingServiceLevel = null;
		if (shippingServiceLevelUid <= 0) {
			shippingServiceLevel = getBean(ContextIdNames.SHIPPING_SERVICE_LEVEL);
		} else {
			shippingServiceLevel = getPersistentBeanFinder().get(ContextIdNames.SHIPPING_SERVICE_LEVEL, shippingServiceLevelUid);
		}
		return shippingServiceLevel;
	}

	@Override
	public List<ShippingServiceLevel> findByUids(final Collection<Long> shippingServiceLevelsUids) {
		sanityCheck();

		if (shippingServiceLevelsUids == null || shippingServiceLevelsUids.isEmpty()) {
			return Collections.emptyList();
		}

		return getPersistenceEngine().retrieveByNamedQueryWithList("SHIPPINGSERVICELEVELS_BY_UIDS", "list", shippingServiceLevelsUids);
	}

	/**
	 * Generic load method for all persistable domain models.
	 *
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * Get all the available shipping cost calculation methods configured in the system.
	 *
	 * @return the list available shipping cost calculation methods.
	 */
	@Override
	public List<ShippingCostCalculationMethod> getAllShippingCostCalculationMethods() {
		return this.allShippingCostCalculationMethods;
	}

	/**
	 * Set all the available shipping cost calculation methods configured in the system.
	 *
	 * @param allShippingCostCalculationMethodsName - names of the available shipping cost calculation methods.
	 */
	@Override
	public void setAllShippingCostCalculationMethods(final List<String> allShippingCostCalculationMethodsName) {
		for (final String name : allShippingCostCalculationMethodsName) {
			final ShippingCostCalculationMethod method = getBean(name);
			this.allShippingCostCalculationMethods.add(method);
		}
	}

	/**
	 * Get the list of uids of <code>ShippingRegion</code> used by existing <code>ShippingServiceLevel</code>s.
	 *
	 * @return the list of uids of <code>ShippingRegion</code>s in use.
	 */
	@Override
	public List<Long> getShippingRegionInUseUidList() {
		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGREGION_UID_IN_USE");
	}

	/**
	 * Get a Sorted Map of CountryName -> CountryCode, where the countries in the map have Shipping service, and the names are appropriate for the
	 * given locale.
	 *
	 * @param locale the locale with which to fetch country names
	 * @param store the store to get shipping service levels for
	 * @return sorted Map of country names to country codes.
	 */
	@Override
	public SortedMap<String, String> getSortedCountriesWithShippingAllowed(final Locale locale, final Store store) {
		List<ShippingRegion> availableShippingRegions = new ArrayList<ShippingRegion>();
		List<ShippingServiceLevel> shippingServiceLevels = this.findByStoreAndState(store.getCode(), true);
		for (ShippingServiceLevel shippingServiceLevel : shippingServiceLevels) {
			availableShippingRegions.add(shippingServiceLevel.getShippingRegion());
		}

		Set<String> countryCodes = getCountryCodesForShippingRegions(availableShippingRegions);
		SortedMap<String, String> sortedMap = new TreeMap<String, String>();
		for (String code : countryCodes) {
			sortedMap.put(geography.getCountryDisplayName(code, locale), code);
		}
		return Collections.unmodifiableSortedMap(sortedMap);
	}

	private Set<String> getCountryCodesForShippingRegions(final List<ShippingRegion> shippingRegions) {
		Set<String> countryCodes = new HashSet<String>();
		for (ShippingRegion region : shippingRegions) { // for each shipping region
			Set<String> regionStringCodes = region.getRegionMap().keySet(); // get the country codes for the ShippingRegion
			for (String code : regionStringCodes) { // for each country code in the given shippingRegion
				countryCodes.add(code); // Add the code to our set;
			}
		}
		return countryCodes;
	}

	/**
	 * Gets a Map of CountryCode -> (Map of SubCountryName to SubCountryCode, Sorted by Name) where the countryCode has a ShippingServiceLevel
	 * configured at the country level OR the subCountry level.
	 *
	 * @param locale the locale into which the subCountry codes must be translated
	 * @return Map of CountryCode -> Sorted Map(SubCountryName -> SubCountryCode)
	 */
	@Override
	public Map<String, Map<String, String>> getCountrySubCountryMapWithShippingService(final Locale locale) {
		List<ShippingRegion> shippingRegionsInUse = getPersistenceEngine().retrieveByNamedQuery("SHIPPINGREGION_IN_USE");
		Map<String, List<String>> countryCodeMap = new TreeMap<String, List<String>>();
		for (final ShippingRegion shippingRegion : shippingRegionsInUse) {
			Set<String> curCountryCodeSet = shippingRegion.getRegionMap().keySet();
			for (String countryCode : curCountryCodeSet) {
				final Region curRegion = shippingRegion.getRegionMap().get(countryCode);
				if (countryCodeMap.get(countryCode) == null) {
					countryCodeMap.put(countryCode, new ArrayList<String>(curRegion.getSubCountryCodeList()));
				} else {
					final List<String> newSubCountries = curRegion.getSubCountryCodeList();
					List<String> existingSubCountries = countryCodeMap.get(countryCode);
					for (String subCountryCode : newSubCountries) {
						if (!existingSubCountries.contains(subCountryCode)) {
							existingSubCountries.add(subCountryCode);
						}
					}
					countryCodeMap.put(countryCode, existingSubCountries);
				}
			}
		}
		// Now we have a map of CountryCode --> List(subCountryCodes).
		Map<String, Map<String, String>> codesMap = translateSubCountryCodesToLocalizedSortedMap(countryCodeMap, locale);
		return Collections.unmodifiableMap(codesMap);
	}

	/**
	 * Given a map of countryCodes -> List(subCountryCodes), translate the Lists of subCountryCodes into Sorted Maps of subCountry Names specific to
	 * the given locale.
	 *
	 * @param countryCodeMap the map of country codes to subCountryCode Lists
	 * @param locale the locale into which the names should be translated
	 */
	private Map<String, Map<String, String>> translateSubCountryCodesToLocalizedSortedMap(final Map<String, List<String>> countryCodeMap,
			final Locale locale) {
		Map<String, Map<String, String>> codesMap = new TreeMap<String, Map<String, String>>();
		for (String code : countryCodeMap.keySet()) {
			codesMap.put(code, getSortedLocalizedMapOfSubCountries(code, countryCodeMap.get(code), locale));
		}
		return codesMap;
	}

	private Map<String, String> getSortedLocalizedMapOfSubCountries(final String countryCode, final List<String> subCountryCodes,
			final Locale locale) {
		SortedMap<String, String> sortedMap = new TreeMap<String, String>();
		for (String subCountryCode : subCountryCodes) {
			sortedMap.put(geography.getSubCountryDisplayName(countryCode, subCountryCode, locale), subCountryCode);
		}
		return sortedMap;
	}

	/**
	 * Retrieve the list of valid <code>ShippingServiceLevel</code> based on the region info inside the given <code>ShoppingCart</code>.
	 *
	 * @param shoppingCart -- the shoppingCart to be used to retrieve shippingServiceLevel info.
	 * @return he list of valid <code>ShippingServiceLevel</code> for the given shoppingCart.
	 */
	@Override
	public List<ShippingServiceLevel> retrieveShippingServiceLevel(final ShoppingCart shoppingCart) {
		return retrieveShippingServiceLevel(shoppingCart.getStore().getCode(), shoppingCart.getShippingAddress());
	}

	/**
	 * Retrieve the list of valid <code>ShippingServiceLevel</code> based on the region info inside the given <code>Address</code>.
	 *
	 * @param storeCode the store that the shippingServiceLevels are valid in
	 * @param address -- the address to be used to retrieve shippingServiceLevel info.
	 * @return he list of valid <code>ShippingServiceLevel</code> for the given orderAddress.
	 */
	@Override
	public List<ShippingServiceLevel> retrieveShippingServiceLevel(final String storeCode, final Address address) {
		if (address == null) {
			return Collections.emptyList();
		}

		List<ShippingServiceLevel> validShippingServiceList = new ArrayList<ShippingServiceLevel>();
		List<ShippingServiceLevel> allShippingServiceList = findByStoreAndState(storeCode, true);

		for (ShippingServiceLevel shippingServiceLevel : allShippingServiceList) {
			if (shippingServiceLevel.isApplicable(storeCode, address)) {
				validShippingServiceList.add(shippingServiceLevel);
			}
		}
		return validShippingServiceList;
	}

	/**
	 * Check if shipping service level is in use.
	 *
	 * @param shippingServiceLevelUidpk the shipping service level uidPk
	 * @return true if shipping service level is in use, false otherwise
	 */
	@Override
	public boolean isShippingServiceLevelInUse(final Long shippingServiceLevelUidpk) {
		sanityCheck();
		if (getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_UID_IN_USE").contains(shippingServiceLevelUidpk)) {
			return true;
		}

		List<String> code = getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_CODE_BY_UID", shippingServiceLevelUidpk);
		if (code.isEmpty()) {
			return false;
		}
		return ruleParameterService.findUniqueParametersWithKey(RuleParameter.SHIPPING_SERVICE_LEVEL_CODE_KEY).contains(code.get(0));
	}

	/**
	 * Set the <code>RuleParameterService</code>.
	 *
	 * @param ruleParameterService the <code>RuleParameterService</code> instance.
	 */
	public void setRuleParameterService(final RuleParameterService ruleParameterService) {
		this.ruleParameterService = ruleParameterService;
	}

	/**
	 * Generates JPA query and fills-in parameters list for searchCriteria.
	 *
	 * @param searchCriteria search criteria.
	 * @param params query parameters (in/out).
	 * @return JPA query.
	 */
	String buildQueryAndParamsForCriteria(final ShippingLevelsSearchCriteriaImpl searchCriteria, final List<Object> params) {
		// prepare to generate query
		StringBuilder queryBuilder = new StringBuilder("SELECT ssl FROM ShippingServiceLevelImpl ssl");
		boolean condition = false;
		int paramNum = 0;

		// check shippingRegionUid filtering parameter
		if (searchCriteria.getShippingRegionUid() != null) {
			condition = addRelationClause(queryBuilder, condition);
			queryBuilder.append(" ssl.shippingRegion.uidPk = ?").append(++paramNum);
			params.add(searchCriteria.getShippingRegionUid());
		}

		// check storeUid filtering parameter
		if (searchCriteria.getStoreUid() != null) {
			condition = addRelationClause(queryBuilder, condition);
			queryBuilder.append(" ssl.store.uidPk = ?").append(++paramNum);
			params.add(searchCriteria.getStoreUid());
		}

		if (searchCriteria.getState() != ShippingLevelsSearchCriteriaImpl.STATE_UNDEFINED) {
			condition = addRelationClause(queryBuilder, condition);
			queryBuilder.append(" ssl.enabled = ?").append(++paramNum);
			params.add(searchCriteria.getState() == ShippingLevelsSearchCriteriaImpl.STATE_ACTIVE);
		}

		return queryBuilder.toString();
	}

	private boolean addRelationClause(final StringBuilder queryBuilder, final boolean condition) {
		boolean result = condition;
		if (condition) {
			queryBuilder.append(" AND");
		} else {
			result = true;
			queryBuilder.append(" WHERE");
		}
		return result;
	}

	@Override
	public List<ShippingServiceLevel> findByCriteria(final ShippingLevelsSearchCriteriaImpl searchCriteria) {
		sanityCheck();
		List<Object> params = new ArrayList<Object>();
		String query = buildQueryAndParamsForCriteria(searchCriteria, params);
		return getPersistenceEngine().retrieve(query, params.toArray());
	}

	/**
	 * Find the shipping service level with the given code.
	 *
	 * @param code the shipping service level code.
	 * @return the shipping service level that matches the given code, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public ShippingServiceLevel findByCode(final String code) throws EpServiceException {
		sanityCheck();
		if (code == null) {
			throw new EpServiceException("Cannot retrieve null code.");
		}

		final List<ShippingServiceLevel> results = getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_FIND_BY_CODE", code);
		ShippingServiceLevel shippingServiceLevel = null;
		if (results.size() == 1) {
			shippingServiceLevel = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate Shipping Service Level code exist -- " + code);
		}
		return shippingServiceLevel;
	}

	@Override
	public ShippingServiceLevel findByGuid(final String guid) throws EpServiceException {
		sanityCheck();
		if (guid == null) {
			throw new EpServiceException("Cannot retrieve null guid.");
		}

		ShippingServiceLevel shippingServiceLevel = null;

		final List<ShippingServiceLevel> results = getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_FIND_BY_GUID", guid);

		if (!results.isEmpty()) {
			shippingServiceLevel = results.get(0);
		}

		return shippingServiceLevel;
	}

	/**
	 * Checks the given code exists or not.
	 *
	 * @param code the code of shipping service level
	 * @return true if the given code exists
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public boolean codeExists(final String code) throws EpServiceException {
		sanityCheck();
		final List<String> codes = getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_CODE_EXIST", code);
		boolean codeExists = false;
		if (!codes.isEmpty()) {
			codeExists = true;
		}
		return codeExists;
	}

	@Override
	public List<Long> findUidsByDeletedDate(final Date date) {
		return Collections.emptyList();
	}

	/**
	 * @param timeService service supplying information about time on the server
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	@Override
	public List<Long> findUidsByModifiedDate(final Date date) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_UIDS_SELECT_BY_MODIFIED_DATE", date);
	}

	@Override
	public List<Long> findAllUids() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_UIDS_ALL");
	}

	/**
	 * @param indexNotificationService to create notifications about actions with shipping service levels
	 */
	public void setIndexNotificationService(final IndexNotificationService indexNotificationService) {
		this.indexNotificationService = indexNotificationService;
	}

	/**
	 * @param geography The Geography to set.
	 */
	public void setGeography(final Geography geography) {
		this.geography = geography;
	}

	/**
	 * @return The Geography that was set.
	 */
	protected Geography getGeography() {
		return geography;
	}
}
