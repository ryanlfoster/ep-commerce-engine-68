package com.elasticpath.service.catalog.impl;

import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.SkuOptionKeyExistException;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * Default implementation for <code>SkuOptionService</code>.
 */
public class SkuOptionServiceImpl extends AbstractEpPersistenceServiceImpl implements SkuOptionService {

	private ProductDao productDao;

	/**
	 * Adds the given sku option.
	 *
	 * @param skuOption the sku option to add
	 * @return the persisted instance of sku option
	 * @throws SkuOptionKeyExistException - if the specified skuOptionKey is already in use.
	 */
	public SkuOption add(final SkuOption skuOption) throws SkuOptionKeyExistException {
		sanityCheck();
		if (keyExists(skuOption.getOptionKey())) {
			throw new SkuOptionKeyExistException("SKU Option with the given key already exists");
		}
		getPersistenceEngine().save(skuOption);
		return skuOption;
	}

	/**
	 * Updates the given sku option.
	 *
	 * @param skuOption the sku option to update
	 *
	 * @return skuOtion as <code>SkuOption</code>
	 * @throws SkuOptionKeyExistException - if the sepcified skuOptionKey is already in use.
	 */
	public SkuOption update(final SkuOption skuOption) throws SkuOptionKeyExistException {
		sanityCheck();

		if (keyExists(skuOption)) {
			throw new SkuOptionKeyExistException("SKU Option with the given key already exists");
		}

		final SkuOption updatedSkuOption = getPersistenceEngine().update(skuOption);
		notifySkuOptionUpdated(updatedSkuOption);
		return updatedSkuOption;
	}

	/**
	 * Save or update the given skuOption.
	 *
	 * @param skuOption the brand to save or update
	 * @return skuOption the updated sku option
	 * @throws EpServiceException - in case of any errors
	 */
	public SkuOption saveOrUpdate(final SkuOption skuOption) throws EpServiceException {
		SkuOption updatedSkuOption = this.getPersistenceEngine().saveOrUpdate(skuOption);
		notifySkuOptionUpdated(skuOption);
		return updatedSkuOption;
	}

	/**
	 * Delete the sku option.
	 *
	 * @param skuOption the sku option to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final SkuOption skuOption) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(skuOption);
	}

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
	public SkuOption load(final long skuOptionUid) throws EpServiceException {
		sanityCheck();
		SkuOption skuOption = null;
		if (skuOptionUid <= 0) {
			skuOption = getBean(ContextIdNames.SKU_OPTION);
		} else {
			skuOption = getPersistentBeanFinder().load(ContextIdNames.SKU_OPTION, skuOptionUid);
		}
		return skuOption;
	}

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
	public SkuOption get(final long skuOptionUid) throws EpServiceException {
		sanityCheck();
		SkuOption skuOption = null;
		if (skuOptionUid <= 0) {
			skuOption = getBean(ContextIdNames.SKU_OPTION);
		} else {
			skuOption = getPersistentBeanFinder().get(ContextIdNames.SKU_OPTION, skuOptionUid);
		}
		return skuOption;
	}

	/**
	 * Generic load method for all persistable domain models.
	 *
	 * @param uid
	 *            the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * Find the sku option with the given key.
	 *
	 * @param key the sku option key.
	 * @return the sku option that matches the given key, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public SkuOption findByKey(final String key) throws EpServiceException {
		sanityCheck();
		if (key == null) {
			throw new EpServiceException("Cannot retrieve null key.");
		}

		final List<SkuOption> results = getPersistenceEngine().retrieveByNamedQuery("SKU_OPTION_FIND_BY_KEY", key);
		SkuOption skuOption = null;
		if (results.size() == 1) {
			skuOption = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate sku option key exist -- " + key);
		}
		return skuOption;
	}

	/**
	 * Find the sku option with the given key.
	 *
	 * @param key the sku option key.
	 * @return the sku option uid that matches the given key, otherwise return -1
	 * @throws EpServiceException - in case of any errors
	 */
	public long findUidPkByKey(final String key) throws EpServiceException {
		sanityCheck();
		if (key == null) {
			throw new EpServiceException("Cannot retrieve null key.");
		}

		final List<Long> results = getPersistenceEngine().retrieveByNamedQuery("SKU_OPTION_FIND_UIDPK_BY_KEY", key);
		long uidPk = -1;
		if (results.size() == 1) {
			uidPk = results.get(0).longValue();
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate sku option key exist -- " + key);
		}
		return uidPk;
	}

	/**
	 * Checks whether the given sku option key exists or not.
	 *
	 * @param key the sku option key.
	 * @return true if the given key exists.
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public boolean keyExists(final String key) throws EpServiceException {
		if (key == null) {
			return false;
		}
		/*
		final SkuOption skuOption = this.findByKey(key);
		boolean keyExists = false;
		if (skuOption != null) {
			keyExists = true;
		}
		*/
		final long uidPk = this.findUidPkByKey(key);
		boolean keyExists = false;
		if (uidPk >= 0) {
			keyExists = true;
		}

		return keyExists;
	}

	/**
	 * Check whether the given sku option's key exists or not.
	 *
	 * @param skuOption the sku option to check
	 * @return true if a different sku option with the given sku option's key exists
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean keyExists(final SkuOption skuOption) throws EpServiceException {
		if (skuOption.getOptionKey() == null) {
			return false;
		}
		/*
		final SkuOption existingSkuOption = this.findByKey(skuOption.getOptionKey());
		boolean keyExists = false;
		if (existingSkuOption != null && existingSkuOption.getUidPk() != skuOption.getUidPk()) {
			keyExists = true;
		}
		*/
		final long uidPk = this.findUidPkByKey(skuOption.getOptionKey());
		boolean keyExists = false;
		if (uidPk >= 0 && uidPk != skuOption.getUidPk()) {
			keyExists = true;
		}
		return keyExists;
	}

	/**
	 * Lists all sku option stored in the database.
	 *
	 * @return a list of sku option
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public List<SkuOption> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("SKU_OPTION_SELECT_ALL_EAGER");
	}
	
	/**
	 * Finds all the {@link SkuOption}s for the specified catalog UID.
	 * 
	 * @param catalogUid the catalog UID
	 * @return a {@link List} of {@link SkuOption}s
	 * @throws EpServiceException in case of any errors
	 */
	public List<SkuOption> findAllSkuOptionFromCatalog(final long catalogUid) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("SKU_OPTION_SELECT_CATALOG_ALL_EAGER", catalogUid);
	}

	/**
	 * Return a list of uids for all sku options in use.
	 *
	 * @return a list of uids for all sku options in use
	 */
	public List<Long> getSkuOptionInUseUidList() {
		return getPersistenceEngine().retrieveByNamedQuery("SKU_OPTION_IN_USE_PRODUCT_TYPE");
	}
	
	/**
	 * Returns whether the given {@link SkuOption} is in use.
	 *
	 * @param skuOptionUid the {@link SkuOption} UID
	 * @return whether the given {@link SkuOption} option is in use
	 * @throws EpServiceException in case of any errors
	 */
	public boolean isSkuOptionInUse(final long skuOptionUid) throws EpServiceException {
		return !getPersistenceEngine().retrieveByNamedQuery("SKU_OPTION_IN_USE_PRODUCT_TYPE_SINGLE", skuOptionUid)
				.isEmpty();
	}
	
	/**
	 * Returns whether the given {@link SkuOptionValue} is in use.
	 *
	 * @param skuOptionValueUid the {@link SkuOptionValue} UID
	 * @return whether the given {@link SkuOptionValue} option is in use
	 * @throws EpServiceException in case of any errors
	 */
	public boolean isSkuOptionValueInUse(final long skuOptionValueUid) throws EpServiceException {
		return !getPersistenceEngine().retrieveByNamedQuery("SKU_OPTIONVALUE_IN_USE_PRODUCT_TYPE_SINGLE", skuOptionValueUid).isEmpty();
	}

	/**
	 * Return a list of uids for all sku options value in use.
	 *
	 * @return a list of uids for all sku options value in use
	 */
	public List<Long> getSkuOptionValueInUseUidList() {
		return getPersistenceEngine().retrieveByNamedQuery("SKU_OPTIONVALUE_IN_USE_PRODUCT_TYPE");
	}

	/**
	 * Add the sku option value.
	 *
	 * @param skuOptionValue the sku option value to add
	 * @param skuOption the sku option to update
	 * @return skuOtion as <code>SkuOption</code>
	 * @throws SkuOptionKeyExistException - if the sepcified skuOptionKey is already in use.
	 */
	public SkuOption addOptionValue(final SkuOptionValue skuOptionValue, final SkuOption skuOption) throws SkuOptionKeyExistException {
		sanityCheck();
		if (optionValueKeyExists(skuOptionValue)) {
			throw new SkuOptionKeyExistException("SKU Option Value with the given key already exists");
		}
		return update(skuOption);

	}

	/**
	 * Check whether the given sku option value's key exists or not.
	 *
	 * @param skuOptionValue
	 *            the sku option value to check
	 * @return true if a different sku option value with the given sku option
	 *         value's key exists
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	protected boolean optionValueKeyExists(final SkuOptionValue skuOptionValue)
			throws EpServiceException {
		if (skuOptionValue.getOptionValueKey() == null) {
			return false;
		}
		return optionValueKeyExists(skuOptionValue.getOptionValueKey());
	}

	/**
	 * Check whether the given sku option value's key exists or not.
	 *
	 * @param skuOptionValueKey
	 *            the sku option value to check
	 * @return true if a different sku option value with the given sku option
	 *         value's key exists
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public boolean optionValueKeyExists(final String skuOptionValueKey) {
		final long uidPk = this.findOptionValueUidPkByKey(skuOptionValueKey);
		boolean keyExists = false;
		if (uidPk >= 0) {
			keyExists = true;
		}
		return keyExists;
	}

	/**
	 * Find the sku option value with the given key.
	 *
	 * @param key the sku option value key.
	 * @return the sku option value uid that matches the given key, otherwise return -1
	 * @throws EpServiceException - in case of any errors
	 */
	public long findOptionValueUidPkByKey(final String key) throws EpServiceException {
		sanityCheck();
		if (key == null) {
			throw new EpServiceException("Cannot retrieve null key.");
		}
		final List<Long> results = getPersistenceEngine().retrieveByNamedQuery("SKU_OPTIONVALUE_FIND_UIDPK_BY_KEY", key);
		long uidPk = -1;
		if (results.size() == 1) {
			uidPk = results.get(0).longValue();
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate sku option value key exist -- " + key);
		}
		return uidPk;
	}

	@Override
	public SkuOptionValue findOptionValueByKey(final String key) throws EpServiceException {
		sanityCheck();
		if (key == null) {
			throw new EpServiceException("Cannot retrieve sku option value with null key");
		}

		final List<SkuOptionValue> results = getPersistenceEngine().retrieveByNamedQuery("SKU_OPTIONVALUE_FIND_BY_KEY", key);

		if (results.size() == 1) {
			return results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicated sku option value key -- " + key);
		}
		return null;
	}

    @Override
    public void notifySkuOptionUpdated(final SkuOption skuOption) {
        final List<Long> productUids = productDao.findUidsBySkuOption(skuOption);
        productDao.updateLastModifiedTimes(productUids);
    }
	
	/**
	 * Set the <code>ProductDao</code>.
	 *
	 * @param productDao the <code>ProductDao</code>
	 */
	public void setProductDao(final ProductDao productDao) {
		this.productDao = productDao;
	}

    @Override
	public void add(final SkuOptionValue skuOptionValue) throws SkuOptionKeyExistException {
		getPersistenceEngine().save(skuOptionValue);
	}

    @Override
	public SkuOptionValue update(final SkuOptionValue skuOptionValue) throws SkuOptionKeyExistException {
		return getPersistenceEngine().update(skuOptionValue);
	}

    @Override
	public void remove(final SkuOptionValue skuOptionValue) throws EpServiceException {
		getPersistenceEngine().delete(skuOptionValue);
	}
	
}
