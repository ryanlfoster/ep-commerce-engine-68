package com.elasticpath.persistence.dao;

import java.util.Collection;
import java.util.List;

import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.misc.TimeService;

/**
 * Product type interface.
 */
public interface ProductTypeDao {

	/**
	 * Delete the ProductType.
	 *
	 * @param productType the ProductType to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final ProductType productType) throws EpServiceException;

	/**
	 * Load method for all persistable domain models specifying fields to be loaded.
	 *
	 * @param uid the persisted instance UID
	 * @param fieldsToLoad the fields of this object that need to be loaded
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Object getObject(final long uid, final Collection<String> fieldsToLoad) throws EpServiceException;

	/**
	 * Finds productType for given name.
	 *
	 * @param name product type name.
	 * @return product type
	 */
    ProductType findProductType(final String name);

    /**
	 * Finds productType for given name.
	 *
	 * @param name product type name.
	 * @return product type with related attributes
	 */
    ProductType findProductTypeWithAttributes(final String name);

	/**
	 * Initialize (fill in) category attributes for the given <code>ProductType</code>.
	 * DWR out-bound conversion will fail on lazy load errors if the attributes
	 * themselves are not loaded also.
	 *
	 * @return productType with attributeGroup filled in.
	 * @param productType productType that needs attributes filled in.
	 */
	ProductType initialize(final ProductType productType);

	/**
	 * Checks whether the given UID is in use.
	 *
	 * @param uidToCheck the UID to check that is in use
	 * @return whether the UID is currently in use or not
	 * @throws EpServiceException in case of any errors
	 */
	boolean isInUse(final long uidToCheck) throws EpServiceException;

	/**
	 * Lists all productType uids used by categories.
	 *
	 * @return a list of used productType uids
	 */
	List<Long> listUsedUids();

	/**
	 * Finds all the {@link ProductType}s for the specified catalog UID.
	 *
	 * @param catalogUid the catalog UID
	 * @return a {@link List} of {@link ProductType}s
	 * @throws EpServiceException in case of any errors
	 */
	List<ProductType> findAllProductTypeFromCatalog(final long catalogUid) throws EpServiceException;

	/**
	 * Lists all ProductType stored in the database.
	 *
	 * @return a list of ProductType
	 * @throws EpServiceException - in case of any errors
	 */
	List<ProductType> list() throws EpServiceException;

	/**
	 * Set the time service.
	 *
	 * @param timeService the <code>TimeService</code> instance.
	 */
	void setTimeService(final TimeService timeService);

	/**
	 * Sets the fetch plan helper.
	 *
	 * @param fetchPlanHelper the fetch plan helper
	 */
	void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper);

	/**
	 * Updates the given ProductType. Will also remove attribute values for attributes which were removed. There is no need to update sku options
	 * since they shouldn't be able change on edit.
	 *
	 * @param productType the ProductType to update
	 * @return the updated product type
	 * @throws DuplicateKeyException - if a productType with the specified key already exists.
	 */
	ProductType update(final ProductType productType) throws DuplicateKeyException;

	/**
	 * Get the productType with the given UID. Return null if no matching record exists.
	 *
	 * @param uid the ProductType UID
	 * @return the ProductType if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	ProductType get(final long uid) throws EpServiceException;

	/**
	 * Adds the given attribute.
	 *
	 * @param productType the attribute to add
	 * @return the persisted instance of ProductType
	 * @throws DuplicateKeyException - if a productType with the specified key already exists.
	 */
	ProductType add(final ProductType productType) throws DuplicateKeyException;

	/**
     * Finds a productType for a given guid.
     *
     * @param guid the guid
     * @return the productType
     */
    ProductType findByGuid(String guid);

	/**
	 * Find a productType for the given sku code.
	 *
	 * @param skuCode the sku code
	 * @return the product type
	 */
	ProductType findBySkuCode(String skuCode);
}