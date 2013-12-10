package com.elasticpath.service.catalog.impl;

import java.util.Collection;
import java.util.List;

import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.persistence.dao.ProductTypeDao;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * Default implementation for <code>AttributeService</code>.
 */
public class ProductTypeServiceImpl extends AbstractEpPersistenceServiceImpl implements ProductTypeService {

	private ProductTypeDao productTypeDao;

	/**
	 * Performs a sanity check on the object state.
	 *
	 * @throws EpServiceException if sanity check fails.
	 */
	@Override
	protected void sanityCheck() throws EpServiceException {
		if (productTypeDao == null) {
			throw new EpServiceException("The productTypeDao is not correctly initialized.");
		}
	}

	/**
	 * Lists all ProductType stored in the database.
	 *
	 * @return a list of ProductType
	 * @throws EpServiceException - in case of any errors
	 * @deprecated This has been replaced by <code>ProductTypeDao</code>
	 */
	@Deprecated
	public List<ProductType> list() throws EpServiceException {
		sanityCheck();

		// delegate
		return productTypeDao.list();
	}

	/**
	 * Finds all the {@link ProductType}s for the specified catalog UID.
	 *
	 * @param catalogUid the catalog UID
	 * @return a {@link List} of {@link ProductType}s
	 * @throws EpServiceException in case of any errors
	 * @deprecated This has been replaced by <code>ProductTypeDao</code>
	 */
	@Deprecated
	public List<ProductType> findAllProductTypeFromCatalog(final long catalogUid) throws EpServiceException {
		sanityCheck();

		// delegate
		return productTypeDao.findAllProductTypeFromCatalog(catalogUid);
	}

	/**
	 * Lists all productType uids used by categories.
	 *
	 * @return a list of used productType uids
	 * @deprecated This has been replaced by <code>ProductTypeDao</code>
	 */
	@Deprecated
	public List<Long> listUsedUids() {
		sanityCheck();

		// delegate
		return productTypeDao.listUsedUids();
	}

	/**
	 * Checks whether the given UID is in use.
	 *
	 * @param uidToCheck the UID to check that is in use
	 * @return whether the UID is currently in use or not
	 * @throws EpServiceException in case of any errors
	 * @deprecated This has been replaced by <code>ProductTypeDao</code>
	 */
	@Deprecated
	public boolean isInUse(final long uidToCheck) throws EpServiceException {
		sanityCheck();

		// delegate
		return productTypeDao.isInUse(uidToCheck);
	}

	/**
	 * Adds the given attribute.
	 *
	 * @param productType the attribute to add
	 * @return the persisted instance of ProductType
	 * @throws DuplicateKeyException - if a productType with the speicifed key already exists.
	 * @deprecated This has been replaced by <code>ProductTypeDao</code>
	 */
	@Deprecated
	public ProductType add(final ProductType productType) throws DuplicateKeyException {
		sanityCheck();

		// TODO: productTypeDao returns a productType, which this method does not.
		//       We should evaluate whether or not productTypeDao needs to return anything.
		productTypeDao.add(productType);

		return productType;
	}

	/**
	 * Updates the given ProductType. Will also remove attribute values for attributes which were removed. There is no need to update sku options
	 * since they shouldn't be able change on edit.
	 *
	 * @param productType the ProductType to update
	 * @return the updated product type
	 * @throws DuplicateKeyException - if a productType with the speicifed key already exists.
	 * @deprecated This has been replaced by <code>ProductTypeDao</code>
	 */
	@Deprecated
	public ProductType update(final ProductType productType) throws DuplicateKeyException {
		sanityCheck();

		return productTypeDao.update(productType);
	}

	/**
	 * Delete the ProductType.
	 *
	 * @param productType the ProductType to remove
	 * @throws EpServiceException - in case of any errors
	 * @deprecated This has been replaced by <code>ProductTypeDao</code>
	 */
	@Deprecated
	public void remove(final ProductType productType) throws EpServiceException {
		sanityCheck();

		productTypeDao.remove(productType);
	}

	/**
	 * Initialize (fill in) category attributes for the given <code>ProductType</code>. DWR outbound conversion will fail on lazy load errors if
	 * the attributes themselves are not loaded also.
	 *
	 * @return productType with attributeGroup filled in.
	 * @param productType productType that needs attributes filled in.
	 * @deprecated This has been replaced by <code>ProductTypeDao</code>
	 */
	@Deprecated
	public ProductType initialize(final ProductType productType) {
		sanityCheck();

		return productTypeDao.initialize(productType);
	}

	/**
	 * Get the productType with the given UID. Return null if no matching record exists.
	 *
	 * @param uid the ProductType UID
	 * @return the ProductType if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 * @deprecated This has been replaced by <code>ProductTypeDao</code>
	 */
	@Deprecated
	public ProductType get(final long uid) throws EpServiceException {
		sanityCheck();

		// delegate
		return productTypeDao.get(uid);
	}

	/**
	 * Generic load method for all persistable domain models.
	 *
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 * @deprecated This has been replaced by <code>ProductTypeDao</code>
	 */
	@Deprecated
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * Load method for all persistable domain models specifying fields to be loaded.
	 *
	 * @param uid the persisted instance uid
	 * @param fieldsToLoad the fields of this object that need to be loaded
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Object getObject(final long uid, final Collection<String> fieldsToLoad) throws EpServiceException {
		sanityCheck();

		// delegate
		return productTypeDao.getObject(uid, fieldsToLoad);
	}

	/**
	 * Finds productType for given name.
	 *
	 * @param name product type name.
	 * @return product type
	 * @deprecated This has been replaced by <code>ProductTypeDao</code>
	 */
	@Deprecated
    public ProductType findProductType(final String name) {
		sanityCheck();

		// delegate
		return productTypeDao.findProductType(name);
    }

	/**
	 * Sets the product type DAO.
	 *
	 * @param productTypeDao The product type DAO.
	 */
	public void setProductTypeDao(final ProductTypeDao productTypeDao) {
		this.productTypeDao = productTypeDao;
	}

	/**
	 * Gets the {@link ProductTypeDao} in use.
	 *
	 * @return The {@link ProductTypeDao}.
	 */
	public ProductTypeDao getProductTypeDao() {
		return productTypeDao;
	}

	/**
     * Finds a productType for a given guid.
     *
     * @param guid the guid
     * @return the productType
     * @deprecated This has been replaced by <code>ProductTypeDao</code>
     */
	@Deprecated
    public ProductType findByGuid(final String guid) {
		sanityCheck();

		return productTypeDao.findByGuid(guid);
	}

	@Override
	public ProductType findBySkuCode(final String skuCode) {
		return productTypeDao.findBySkuCode(skuCode);
	}

}
