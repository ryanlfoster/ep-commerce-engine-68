package com.elasticpath.service.pricing.dao;

import java.util.Collection;
import java.util.List;

import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.pricing.exceptions.BaseAmountNotExistException;

/**
 * DAO for persisting base amounts.
 */
public interface BaseAmountDao {

	/**
	 * Delete the BaseAmount from data stores.
	 *
	 * @param baseAmount to delete.
	 */
	void delete(BaseAmount baseAmount);

	/**
	 * Delete the all BaseAmounts, that belong to price list descriptor.
	 *
	 * @param priceListDescriptiptorGuid guid of price list.
	 */
	void delete(final String priceListDescriptiptorGuid);


	/**
	 * Deletes all base amount corresponding to the given object identifier and object type.
	 * @param objectGuid the object identifier
	 * @param objectType the object type
	 */
	void deleteBaseAmounts(String objectGuid, String objectType);

	/**
	 * Save the new BaseAmount or update if already persisted.
	 *
	 * @param baseAmount to update to data stores.
	 * @return merged BaseAmount.
	 * @throws EpPersistenceException in case of error.
	 */
	BaseAmount add(BaseAmount baseAmount) throws EpPersistenceException;

	/**
	 * Updates the BaseAmount.
	 *
	 * @param updatedBaseAmount the updated <code>BaseAmount</code>
	 * @return persisted BaseAmount from DB
	 * @throws BaseAmountNotExistException if a BaseAmount with the given GUID does not exist
	 */
	BaseAmount update(BaseAmount updatedBaseAmount) throws BaseAmountNotExistException;


	/**
	 * Query for BaseAmount objects with the given BaseAmountFilter.
	 *
	 * @param filter for query.
	 * @return collection of results, or empty collection if none found.
	 */
	Collection<BaseAmount> findBaseAmounts(BaseAmountFilter filter);

	/**
	 * Get the base amounts  according to a search criteria.
	 *
	 * @param namedQuery named query.
	 * @param searchCriteria search criteria, that include following fields in given order
	 *        priceListDescriptorGuid optional price list guid;
	 *        objectType optional object type;
	 *        objectGuid optional object guid or his part in lowercase;
	 *        lowestPrice optional lowest price border;
	 *        highestPrice  optional highest price border;
	 *        quantity optional quantity.
	 * @param limit limit of records to return.
	 * @param guids sku guids used if sku objects need to be appended to results
	 * @return set of base amounts matching the search criteria.
	 */
	List<BaseAmount> findBaseAmounts(
			String namedQuery,
			Object[] searchCriteria,
			int limit , List<String> guids);

	/**
	 * Get the base amounts according to a search criteria.
	 *
	 * @param namedQuery named query.
	 * @param searchCriteria search criteria, that include following fields in given order
	 *        priceListDescriptorGuid optional price list guid;
	 *        objectType optional object type;
	 *        objectGuid optional object guid or his part in lowercase;
	 *        lowestPrice optional lowest price border;
	 *        highestPrice  optional highest price border;
	 *        quantity optional quantity.
	 * @param startIndex limit of records to return.
	 * @param endIndex limit of records to return.
	 * @param guids sku guids used if sku objects need to be appended to results
	 * @return set of base amounts matching the search criteria.
	 */
	List<BaseAmount> findBaseAmounts(
			final String namedQuery,
			final Object[] searchCriteria,
			final int startIndex, final int endIndex,
			final List<String> guids);

	/**
	 * Get the base amounts for given object guids for a price list.
	 *
	 * @param priceListDescriptorGuid optional price list guid.
	 * @param objectType object type.
	 * @param objectGuids object guids.
	 * @return list of base amounts matching the search criteria.
	 */
	List<BaseAmount> findBaseAmounts(String priceListDescriptorGuid,
			String objectType, String ... objectGuids);

	/**
	 * Get a BaseAmount by its GUID.
	 *
	 * @param guid of the BaseAmount
	 * @return the retrieved BaseAmount
	 */
	BaseAmount findBaseAmountByGuid(String guid);

	/**
	 *
	 * Get the ascending <i>ordered</i> by price collection of {@link BaseAmount}s.
	 * Given price list descriptors must be with the same currency.
	 *
	 * @param plGuids price list guids
	 * @param objectGuids product and his sku guids.
	 * @return ascending <i>ordered</i> collection of BaseAmounts.
	 */
	List<BaseAmount> getBaseAmounts(List<String> plGuids, List<String> objectGuids);

	/**
     * Checks whether the given base amount guid exists or not.
     *
     * @param guid the base amount code.
     * @return true if the given guid(code) exists.
     */
    boolean guidExists(String guid);
}
