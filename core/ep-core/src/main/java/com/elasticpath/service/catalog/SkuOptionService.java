package com.elasticpath.service.catalog;

import java.util.List;

import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provide sku option related business service.
 */
public interface SkuOptionService extends EpPersistenceService {

	/**
	 * Adds the given sku option.
	 *
	 * @param skuOption the sku option to add
	 * @return the persisted instance of sku option
	 * @throws SkuOptionKeyExistException - if the sepcified skuOptionKey is already in use.
	 */
	SkuOption add(final SkuOption skuOption) throws SkuOptionKeyExistException;

	/**
	 * Updates the given sku option.
	 *
	 * @param skuOption the sku option to update
	 * @return skuOtion as <code>SkuOption</code>
	 * @throws SkuOptionKeyExistException - if the sepcified skuOptionKey is already in use.
	 */
	SkuOption update(final SkuOption skuOption) throws SkuOptionKeyExistException;

	/**
	 * Delete the sku option.
	 *
	 * @param skuOption the sku option to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final SkuOption skuOption) throws EpServiceException;

	/**
	 * Load the sku option with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param skuOptionUid the sku option UID
	 *
	 * @return the sku option if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	SkuOption load(final long skuOptionUid) throws EpServiceException;

	/**
	 * Get the sku option with the given UID.
	 * Return null if no matching record exists.
	 *
	 * @param skuOptionUid the sku option UID
	 *
	 * @return the sku option if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	SkuOption get(final long skuOptionUid) throws EpServiceException;

	/**
	 * Lists all sku option stored in the database.
	 *
	 * @return a list of sku option
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	List<SkuOption> list() throws EpServiceException;
	
	/**
	 * Finds all the {@link SkuOption}s for the specified catalog UID.
	 *
	 * @param catalogUid the catalog UID
	 * @return a {@link List} of {@link SkuOption}s
	 * @throws EpServiceException in case of any errors
	 */
	List<SkuOption> findAllSkuOptionFromCatalog(final long catalogUid) throws EpServiceException;

	/**
	 * Checks whether the given sku option key exists or not.
	 *
	 * @param key the sku option key.
	 * @return true if the given key exists.
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	boolean keyExists(final String key) throws EpServiceException;

	/**
	 * Check whether the given sku option's key exists or not.
	 *
	 * @param skuOption the sku option to check
	 * @return true if a different sku option with the given sku option's key exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean keyExists(final SkuOption skuOption) throws EpServiceException;

	/**
	 * Find the sku option with the given key.
	 *
	 * @param key the sku option key.
	 * @return the sku option that matches the given key, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	SkuOption findByKey(final String key) throws EpServiceException;

	/**
	 * Return a list of uids for all sku options in use.
	 *
	 * @return a list of uids for all sku options in use
	 */
	List<Long> getSkuOptionInUseUidList();
	
	/**
	 * Returns whether the given {@link SkuOption} is in use.
	 *
	 * @param skuOptionUid the {@link SkuOption} UID
	 * @return whether the given {@link SkuOption} option is in use
	 * @throws EpServiceException in case of any errors
	 */
	boolean isSkuOptionInUse(final long skuOptionUid) throws EpServiceException;
	
	/**
	 * Returns whether the given {@link SkuOptionValue} is in use.
	 *
	 * @param skuOptionValueUid the {@link SkuOptionValue} UID
	 * @return whether the given {@link SkuOptionValue} option is in use
	 * @throws EpServiceException in case of any errors
	 */
	boolean isSkuOptionValueInUse(final long skuOptionValueUid) throws EpServiceException;

	/**
	 * Save or update the given skuOption.
	 *
	 * @param skuOption the brand to save or update
	 * @return skuOption the updated skuoption
	 * @throws EpServiceException - in case of any errors
	 */
	SkuOption saveOrUpdate(final SkuOption skuOption) throws EpServiceException;

	/**
	 * Return a list of uids for all sku options value in use.
	 *
	 * @return a list of uids for all sku options value in use
	 */
	List<Long> getSkuOptionValueInUseUidList();

	/**
	 * Find the sku option with the given key.
	 *
	 * @param key the sku option key.
	 * @return the sku option uid that matches the given key, otherwise return -1
	 * @throws EpServiceException - in case of any errors
	 */
	long findUidPkByKey(final String key) throws EpServiceException;

	/**
	 * Add the sku option value.
	 *
	 * @param skuOptionValue the sku option value to add
	 * @param skuOption the sku option to update
	 * @return skuOtion as <code>SkuOption</code>
	 * @throws SkuOptionKeyExistException - if the specified skuOptionKey is already in use.
	 */
	SkuOption addOptionValue(final SkuOptionValue skuOptionValue, final SkuOption skuOption) throws SkuOptionKeyExistException;

	/**
	 * Check whether the given sku option value's key exists or not.
	 *
	 * @param skuOptionValueKey the sku option value to check
	 * @return true if a different sku option value with the given sku option value's key exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean optionValueKeyExists(final String skuOptionValueKey) throws EpServiceException;

	/**
	 * Finds <code>SkuOptionValue</code> instance by SKU option value key.
	 * 
	 * @param key uniquely identifying SKU option value
	 * @return <code>SkuOptionValue</code> instance if exists, null otherwise
	 * @throws EpServiceException if data is inconsistent in database
	 */
	SkuOptionValue findOptionValueByKey(final String key) throws EpServiceException;
	
    /**
     * Notifies the product service that a <code>SkuOption</code> has been updated.
     * 
     * @param skuOption the skuOption that was updated
     */
	void notifySkuOptionUpdated(SkuOption skuOption);
	
	/**
	 * Adds the given sku option value.
	 *
	 * @param skuOptionValue the sku option value to update
	 * @throws SkuOptionKeyExistException - if the specified skuOptionKey is already in use.
	 */
	void add(SkuOptionValue skuOptionValue) throws SkuOptionKeyExistException;
	
	/**
	 * Updates the given sku option value.
	 *
	 * @param skuOptionValue the sku option value to update
	 * @return skuOptionValue as <code>SkuOptionValue</code>
	 * @throws SkuOptionKeyExistException - if the specified skuOptionKey is already in use.
	 * 
	 */
	SkuOptionValue update(final SkuOptionValue skuOptionValue) throws SkuOptionKeyExistException;

	/**
	 * Delete the sku option value.
	 *
	 * @param skuOptionValue the sku option value to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 * 
	 */
	void remove(final SkuOptionValue skuOptionValue) throws EpServiceException;
	
	
	
}
