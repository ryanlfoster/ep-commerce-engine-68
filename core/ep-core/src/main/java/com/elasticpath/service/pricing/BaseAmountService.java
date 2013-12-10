package com.elasticpath.service.pricing;

import java.util.Collection;
import java.util.List;

import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSource;
import com.elasticpath.service.pricing.exceptions.BaseAmountNotExistException;

/**
 * Service for managing price list {@link com.elasticpath.domain.pricing.BaseAmount BaseAmount}.
 * <p>
 * Refer to {@link com.elasticpath.service.pricing.PriceListDescriptorService PriceListDescriptorService} for related price list management.
 * </p>
 */
public interface BaseAmountService extends BaseAmountDataSource {

	/**
	 * Save the base amount.
	 *
	 * @param baseAmount the <code>BaseAmount</code> to update or save if not persisted
	 * @return persisted BaseAmount from DB
	 * @throws com.elasticpath.domain.pricing.exceptions.DuplicateBaseAmountException if a BaseAmount equal to the given BaseAmount already exists.
	 * @throws EpServiceException in case of other errors
	 */
	BaseAmount add(final BaseAmount baseAmount) throws EpServiceException;

	/**
	 * Update a BaseAmount with a new BaseAmount.
	 * First loads the BaseAmount from the db.
	 * Then copies key values: List and Sale Values across.
	 *
     * @deprecated use updateWithoutLoad instead
	 * @param newBaseAmount the updated <code>BaseAmount</code>
	 * @return persisted BaseAmount from DB
	 * @throws com.elasticpath.service.pricing.exceptions.BaseAmountNotExistException if the given BaseAmount doesn't exist
	 * @throws EpServiceException in case of other errors
	 */
	@Deprecated
    BaseAmount update(final BaseAmount newBaseAmount) throws EpServiceException;

	/**
	 * Update a BaseAmount with a pre-loaded and modified base amount.
	 *
	 * @param baseAmountToUpdate the updated <code>BaseAmount</code>
	 * @return persisted BaseAmount from DB
	 * @throws com.elasticpath.service.pricing.exceptions.BaseAmountNotExistException if the given BaseAmount doesn't exist
	 * @throws EpServiceException in case of other errors
	 */
    BaseAmount updateWithoutLoad(final BaseAmount baseAmountToUpdate) throws BaseAmountNotExistException;

	/**
	 * Delete the BaseAmount.
	 *
	 * @param baseAmount to delete
	 * @throws EpServiceException in case of error
	 */
	void delete(final BaseAmount baseAmount) throws EpServiceException;

	/**
	 * Delete the all BaseAmounts, that belong to price list descriptor.
	 *
	 * @param priceListDescriptorGuid guid of price list.
	 */
	void delete(final String priceListDescriptorGuid);


	/**
	 * Get a BaseAmount by its GUID.
	 *
	 * @param guid of the BaseAmount
	 * @return the retrieved BaseAmount
	 */
	BaseAmount findByGuid(String guid);

	/**
	 * Get base amounts according to a filter.
	 *
	 * @param filter criteria for searching.
	 * @return set of base amounts matching the filter.
	 */
	Collection<BaseAmount> findBaseAmounts(final BaseAmountFilter filter);

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
	 * Determines whether a BaseAmount equal to the given BaseAmount exists in the persistence layer.
	 *
	 * @param baseAmount the BaseAmount to test for existence
	 * @return true if a BaseAmount whose key fields match the given BaseAmount exists in the persistence layer
	 */
	boolean exists(final BaseAmount baseAmount);

	/**
	 * Get the base amounts  according to a search criteria.
	 *
	 * @param namedQuery the name of named query.
	 * @param searchCriteria search criteria, that include following fields in given order
	 *        priceListDescriptorGuid optional price list guid;
	 *        objectType optional object type;
	 *        objectGuid optional object guid or his part in lowercase;
	 *        lowestPrice optional lowest price border;
	 *        highestPrice  optional highest price border;
	 *        quantity optional quantity.
	 * @param limit limit of records to return.
	 * @param guids sku guids used if sku objects need to be appended to results.
	 * @return set of base amounts matching the search criteria.
	 */
	List<BaseAmount> findBaseAmounts(
			String namedQuery,
			Object [] searchCriteria,
			int limit, List<String> guids);

	/**
	 * Get the base amounts  according to a search criteria.
	 *
	 * @param namedQuery the name of named query.
	 * @param searchCriteria search criteria, that include following fields in given order
	 *        priceListDescriptorGuid optional price list guid;
	 *        objectType optional object type;
	 *        objectGuid optional object guid or his part in lowercase;
	 *        lowestPrice optional lowest price border;
	 *        highestPrice  optional highest price border;
	 *        quantity optional quantity.
	 * @param startIndex The offset or first row to include.
	 * @param pageSize the number of records to return.
	 * @param guids sku guids used if sku objects need to be appended to results.
	 * @return set of base amounts matching the search criteria.
	 */
	List<BaseAmount> findBaseAmounts(
			final String namedQuery,
			final Object [] searchCriteria,
			final int startIndex, final int pageSize, final List<String> guids);

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
     * Checks whether the given base amount guid exists or not.
     *
     * @param guid the base amount code.
     * @return true if the given guid(code) exists.
     */
    boolean guidExists(final String guid);

}
