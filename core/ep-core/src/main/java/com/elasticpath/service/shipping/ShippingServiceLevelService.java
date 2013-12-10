/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shipping;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.query.ShippingLevelsSearchCriteriaImpl;

/**
 * Provide ShippingServiceLevel-related business service.
 */
public interface ShippingServiceLevelService extends EpPersistenceService {
	/**
	 * Adds the given shippingServiceLevel.
	 * 
	 * @param shippingServiceLevel the shippingServiceLevel to add
	 * @return the persisted instance of shippingServiceLevel
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingServiceLevel add(final ShippingServiceLevel shippingServiceLevel) throws EpServiceException;

	/**
	 * Updates the given shippingServiceLevel.
	 * 
	 * @param shippingServiceLevel the shippingServiceLevel to update
	 * @return ShippingServiceLevel the updated ShippingServiceLevel
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingServiceLevel update(final ShippingServiceLevel shippingServiceLevel) throws EpServiceException;

	/**
	 * Delete the shippingServiceLevel.
	 * 
	 * @param shippingServiceLevel the shippingServiceLevel to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final ShippingServiceLevel shippingServiceLevel) throws EpServiceException;

	/**
	 * Lists all shippingServiceLevels stored in the database for the specific store.
	 * 
	 * @param storeCode the store code
	 * @return a list of shippingServiceLevels
	 * @throws EpServiceException - in case of any errors
	 */
	List<ShippingServiceLevel> findByStore(String storeCode) throws EpServiceException;

	/**
	 * List all shippingServiceLevels stored in the database for the specified store with storeCode and specific state.
	 * 
	 * @param storeCode the store code
	 * @param active should only active service levels be retrieved or not 
	 * @return a list of shippingServiceLevels
	 * @throws EpServiceException - in case of any errors
	 */
	List<ShippingServiceLevel> findByStoreAndState(String storeCode, boolean active) throws EpServiceException;

	/**
	 * List the uidpks of all shippingServiceLevels stored in the database for the specified store with storeCode and specific state.
	 *
	 * @param storeCode the store code
	 * @param active should only active service levels be retrieved or not
	 * @return a list of uidpks
	 * @throws EpServiceException - in case of any errors
	 */
	List<Long> findUidsByStoreAndState(String storeCode, boolean active) throws EpServiceException;

	/**
	 * Load the shippingServiceLevel with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 * 
	 * @param shippingServiceLevelUid the shippingServiceLevel UID
	 * @return the shippingServiceLevel if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingServiceLevel load(final long shippingServiceLevelUid) throws EpServiceException;

	/**
	 * Get the shippingServiceLevel with the given UID. Return null if no matching record exists.
	 * 
	 * @param shippingServiceLevelUid the shippingServiceLevel UID
	 * @return the shippingServiceLevel if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingServiceLevel get(final long shippingServiceLevelUid) throws EpServiceException;

	/**
	 * Get all the available shipping cost calculation methods configured in the system.
	 * 
	 * @return the list available shipping cost calculation methods.
	 */
	List<ShippingCostCalculationMethod> getAllShippingCostCalculationMethods();

	/**
	 * Set all the available shipping cost calculation methods configured in the system.
	 * 
	 * @param allShippingCostCalculationMethods - the available shipping cost calculation methods.
	 */
	void setAllShippingCostCalculationMethods(final List<String> allShippingCostCalculationMethods);

	/**
	 * Get the list of uids of <code>ShippingRegion</code> used by existing <code>ShippingServiceLevel</code>s.
	 * 
	 * @return the list of uids of <code>ShippingRegion</code>s in use.
	 */
	List<Long> getShippingRegionInUseUidList();

	/**
	 * Gets a Map of CountryCode -> (Map of SubCountryName to SubCountryCode, Sorted by Name) where the countryCode has a ShippingServiceLevel
	 * configured at the country level OR the subCountry level.
	 * 
	 * @param locale the locale into which the subCountry codes must be translated
	 * @return Map of CountryCode -> Sorted Map(SubCountryName -> SubCountryCode)
	 */
	Map<String, Map<String, String>> getCountrySubCountryMapWithShippingService(final Locale locale);

	/**
	 * Retrieve the list of valid <code>ShippingServiceLevel</code> based on the region info inside the given <code>ShoppingCart</code>.
	 * 
	 * @param shoppingCart -- the shoppingCart to be used to retrieve shippingServiceLevel info.
	 * @return he list of valid <code>ShippingServiceLevel</code> for the given shoppingCart.
	 */
	List<ShippingServiceLevel> retrieveShippingServiceLevel(final ShoppingCart shoppingCart);

	/**
	 * Check if shipping service level is in use.
	 * 
	 * @param shippingServiceLevelUidpk the shipping service level uidPk
	 * @return true if shipping service level is in use, false otherwise
	 */
	boolean isShippingServiceLevelInUse(final Long shippingServiceLevelUidpk);

	/**
	 * Get a Sorted Map of CountryName -> CountryCode, where the countries in the map have Shipping service, and the names are appropriate for the
	 * given locale.
	 * 
	 * @param locale the locale with which to fetch country names
	 * @param store the store to get shipping service levels for
	 * @return sorted Map of country names to country codes.
	 */
	SortedMap<String, String> getSortedCountriesWithShippingAllowed(final Locale locale, final Store store);

	/**
	 * @param searchCriteria filtering parameters. Set parameter to null to ignore it.
	 * @return list of Shipping Service LEvels filtered according to searchCriteria.
	 */
	List<ShippingServiceLevel> findByCriteria(final ShippingLevelsSearchCriteriaImpl searchCriteria);
	
	/**
	 * Find the shipping service level with the given code.
	 *
	 * @param code the shipping service level code.
	 * @return the shipping service level that matches the given code, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingServiceLevel findByCode(final String code) throws EpServiceException;
	
	/**
	 * Find the shipping service level with the given guid.
	 *
	 * @param guid the shipping service level guid.
	 * @return the shipping service level that matches the given guid, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingServiceLevel findByGuid(final String guid) throws EpServiceException;
	
	/**
	 * Retrieve the list of valid <code>ShippingServiceLevel</code> based on the region info inside the given <code>Address</code>.
	 * 
	 * @param storeCode the store that the shippingServiceLevels are valid in
	 * @param address -- the address to be used to retrieve shippingServiceLevel info.
	 * @return he list of valid <code>ShippingServiceLevel</code> for the given orderAddress.
	 */
	List<ShippingServiceLevel> retrieveShippingServiceLevel(final String storeCode, final Address address);
	
	/**
	 * Checks the given code exists or not.
	 * 
	 * @param code the code of shipping service level
	 * @return true if the given code exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean codeExists(final String code) throws EpServiceException;

	/**
	 * Retrieves list of shipping service level UIDs where the deleted date is later than the specified date.
	 * 
	 * @param date date to compare with the deleted date
	 * @return list of shipping service level UIDs whose deleted date is later than the specified date
	 */
	List<Long> findUidsByDeletedDate(final Date date);

	/**
	 * Retrieves a list of <code>ShippingServiceLevel</code> UIDs where
	 * the last modified date is later than the specified date.
	 * 
	 * @param date date to compare with the last modified date
	 * @return list of <code>Long</code> UIDs whose last modified date is later than the date specified
	 */
	List<Long> findUidsByModifiedDate(final Date date);
	
	/**
	 * @return all <code>ShippingServiceLevel</code> UIDs
	 */
	List<Long> findAllUids();

	/**
	 * Retrieves a list of shipping service levels by the list of UIDs.
	 * 
	 * @param shippingServiceLevelsUids UIDs of shipping service levels to find
	 * @return a list of persistent shipping service level UIDs
	 */
	List<ShippingServiceLevel> findByUids(final Collection<Long> shippingServiceLevelsUids);
}
