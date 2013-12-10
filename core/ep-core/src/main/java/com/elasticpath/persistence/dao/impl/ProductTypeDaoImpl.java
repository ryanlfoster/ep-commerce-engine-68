package com.elasticpath.persistence.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.ProductTypeLoadTuner;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.persistence.dao.ProductTypeDao;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.misc.TimeService;

/**
 * Provides <code>ProductType</code> data access methods.
 */
public class ProductTypeDaoImpl extends AbstractDaoImpl implements ProductTypeDao {

	private TimeService timeService;

	private FetchPlanHelper fetchPlanHelper;

	private ProductTypeLoadTuner productTypeLoadTunerAll;

	private ProductTypeLoadTuner productTypeLoadTunerAttributes;

	/**
	 * Delete the ProductType.
	 *
	 * @param productType the ProductType to remove
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final ProductType productType) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(productType);
	}

	/**
	 * Load method for all persistable domain models specifying fields to be loaded.
	 *
	 * @param uid the persisted instance uid
	 * @param fieldsToLoad the fields of this object that need to be loaded
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Object getObject(final long uid, final Collection<String> fieldsToLoad) throws EpServiceException {
		fetchPlanHelper.addFields(ProductTypeImpl.class, fieldsToLoad);

		Object object = null;
		try {
			object = get(uid);
		} finally {
			fetchPlanHelper.clearFetchPlan();
		}

		return object;
	}

	/**
	 * Finds productType for given name.
	 *
	 * @param name product type name.
	 * @return product type
	 */
    public ProductType findProductType(final String name) {
        sanityCheck();

        if (name == null) {
            throw new EpServiceException("Cannot retrieve null name.");
        }

		final List<ProductType> typeList = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_TYPE_FIND_BY_NAME", name);

        ProductType productType = null;
        if (typeList.size() == 1) {
            productType = typeList.get(0);

        } else if (typeList.size() > 1) {
            throw new EpServiceException("Inconsistent data -- duplicate product type name exist -- " + name);
        }

        return productType;
    }

    @Override
    public ProductType findByGuid(final String guid) {
		final List<ProductType> result = getPersistenceEngine().retrieveByNamedQuery("FIND_PRODUCT_TYPE_BY_GUID", guid);
		if (CollectionUtils.isNotEmpty(result)) {
			return result.iterator().next();
		}
		return null;

    }

    /**
	 * Finds productType for given name.
	 *
	 * @param name product type name.
	 * @return product type with related attributes
	 */
    public ProductType findProductTypeWithAttributes(final String name) {
        sanityCheck();

        if (name == null) {
            throw new EpServiceException("Cannot retrieve null name.");
        }

        fetchPlanHelper.configureProductTypeFetchPlan(productTypeLoadTunerAttributes);
		final List<ProductType> typeList = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_TYPE_FIND_BY_NAME", name);
        fetchPlanHelper.clearFetchPlan();

        ProductType productType = null;
        if (typeList.size() == 1) {
            productType = typeList.get(0);

        } else if (typeList.size() > 1) {
            throw new EpServiceException("Inconsistent data -- duplicate product type name exist -- " + name);
        }

        return productType;
    }

	/**
	 * Checks whether the given UID is in use.
	 *
	 * @param uidToCheck the UID to check that is in use
	 * @return whether the UID is currently in use or not
	 * @throws EpServiceException in case of any errors
	 */
	public boolean isInUse(final long uidToCheck) throws EpServiceException {
		sanityCheck();
		return !getPersistenceEngine().retrieveByNamedQuery("PRODUCT_TYPE_IN_USE", uidToCheck).isEmpty();
	}

	/**
	 * Lists all productType uids used by categories.
	 *
	 * @return a list of used productType uids
	 * @deprecated Questionable method, which does not appear to be used.
	 */
	@Deprecated
	public List<Long> listUsedUids() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("PRODUCT_TYPE_USED_UIDS");
	}

	/**
	 * Finds all the {@link ProductType}s for the specified catalog UID.
	 *
	 * @param catalogUid the catalog UID
	 * @return a {@link List} of {@link ProductType}s
	 * @throws EpServiceException in case of any errors
	 */
	public List<ProductType> findAllProductTypeFromCatalog(final long catalogUid) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("PRODUCT_TYPE_SELECT_CATALOG_ALL", catalogUid);
	}

	/**
	 * Lists all ProductType stored in the database.
	 *
	 * @return a list of ProductType
	 * @throws EpServiceException - in case of any errors
	 */
	public List<ProductType> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("PRODUCT_TYPE_SELECT_ALL");
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
	 * Notifies the product service that a <code>ProductType</code> has been updated.
	 *
	 * @param productType the productType that was updated
	 */
	public void notifyProductTypeUpdated(final ProductType productType) {
		final Date currentTime = timeService.getCurrentTime();
		getPersistenceEngine().executeNamedQuery("PRODUCT_UPDATE_MODIFIED_TIME_BY_PRODUCT_TYPE",
				currentTime, Long.valueOf(productType.getUidPk()));
	}

	/**
	 * Initialize (fill in) category attributes for the given <code>ProductType</code>. DWR outbound conversion will fail on lazy load errors if
	 * the attributes themselves are not loaded also.
	 *
	 * @return productType with attributeGroup filled in.
	 * @param productType productType that needs attributes filled in.
	 */
	public ProductType initialize(final ProductType productType) {
		sanityCheck();
		fetchPlanHelper.configureProductTypeFetchPlan(productTypeLoadTunerAll);
		final ProductType freshProductType = getPersistentBeanFinder().load(ContextIdNames.PRODUCT_TYPE, productType.getUidPk());
		fetchPlanHelper.clearFetchPlan();
		if (freshProductType == null) {
			return null;
		}
		return freshProductType;
	}

	/**
	 * Updates the given ProductType. Will also remove attribute values for attributes which were removed. There is no need to update sku options
	 * since they shouldn't be able change on edit.
	 *
	 * @param productType the ProductType to update
	 * @return the updated product type
	 * @throws DuplicateKeyException - if a productType with the speicifed key already exists.
	 */
	public ProductType update(final ProductType productType) throws DuplicateKeyException {
		sanityCheck();

		// make copy of original
		ProductType original = get(productType.getUidPk());
		original = initialize(original);
		final Set<AttributeGroupAttribute> beforeProductSet =
			new HashSet<AttributeGroupAttribute>(original.getProductAttributeGroup().getAttributeGroupAttributes());
		final Set<AttributeGroupAttribute> beforeSkuSet =
			new HashSet<AttributeGroupAttribute>(original.getSkuAttributeGroup().getAttributeGroupAttributes());

		throwExceptionIfDuplicate(productType);
		final ProductType updatedProductType = getPersistenceEngine().merge(productType);

		// lookup removed product attributes
		Set<Attribute> removedAttributes = productType.getProductAttributeGroup().getRemovedAttributes(beforeProductSet);
		if (removedAttributes != null && !removedAttributes.isEmpty()) {
			// removed values for removed attributes for all products in type
			final List<Product> products = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_LIST_PRODUCT_TYPE", productType.getUidPk());
			for (final Product product : products) {
				product.getAttributeValueGroup().removeByAttributes(removedAttributes);
				product.setAttributeValueMap(product.getAttributeValueGroup().getAttributeValueMap());
				getPersistenceEngine().merge(product);
			}
		}

		// lookup removed product SKU attributes
		removedAttributes = productType.getSkuAttributeGroup().getRemovedAttributes(beforeSkuSet);
		if (removedAttributes != null && !removedAttributes.isEmpty()) {
			// removed values for removed attributes for all product SKUs in type
			final List<ProductSku> productskus = getPersistenceEngine().retrieveByNamedQuery("PRODUCTSKU_LIST_PRODUCT_TYPE", productType.getUidPk());
			for (final ProductSku productSku : productskus) {
				productSku.getAttributeValueGroup().removeByAttributes(removedAttributes);
				productSku.setAttributeValueMap(productSku.getAttributeValueGroup().getAttributeValueMap());
				getPersistenceEngine().merge(productSku);
			}
		}

		notifyProductTypeUpdated(updatedProductType);

		return updatedProductType;
	}

	/**
	 * Get the productType with the given UID. Return null if no matching record exists.
	 *
	 * @param uid the ProductType UID
	 * @return the ProductType if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public ProductType get(final long uid) throws EpServiceException {
		sanityCheck();

		ProductType productType = null;
		if (uid <= 0) {
			productType = getBean(ContextIdNames.PRODUCT_TYPE);
		} else {
			productType = getPersistentBeanFinder().get(ContextIdNames.PRODUCT_TYPE, uid);
		}

		return productType;
	}

	/**
	 * Adds the given attribute.
	 *
	 * @param productType the attribute to add
	 * @return the persisted instance of ProductType
	 * @throws DuplicateKeyException - if a productType with the specified key already exists.
	 */
	public ProductType add(final ProductType productType) throws DuplicateKeyException {
		sanityCheck();
		throwExceptionIfDuplicate(productType);
		getPersistenceEngine().save(productType);
		return productType;
	}
	/**
	 * Sanity check of this service instance.
	 * @throws EpServiceException - if something goes wrong.
	 */
	protected void sanityCheck() throws EpServiceException {
		if (getPersistenceEngine() == null) {
			throw new EpServiceException("The persistence engine is not correctly initialized.");
		}
	}

	/**
	 * Returns whether the product type exists or not.
	 *
	 * @param type - The product type.
	 * @return true if the product type does not exist, false otherwise.
	 */
	private boolean productTypeExists(final ProductType type) {
		// Check for an existing ProductType with the same name, but different uidPk
		final Long count = getPersistenceEngine().<Long>retrieveByNamedQuery("PRODUCT_TYPE_COUNT_BY_NAME", type.getName(), type.getUidPk()).get(0);

		if (count.longValue() == 0) {
			// No such ProductType found
			return false;
		}

		// ProductType found
		return true;
	}


	/**
	 * Check if the productType type is existed.
	 * @param type the productType to be checked
	 */
	protected void throwExceptionIfDuplicate(final ProductType type) {

		if (productTypeExists(type)) {
			throw new DuplicateKeyException("ProductType name '" + type.getName() + "' already exists.");
		}
	}

	/**
	 * Sets the fetch plan helper.
	 *
	 * @param fetchPlanHelper the fetch plan helper
	 */
	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}

	/**
	 * Sets the load tuner for loading all product type data.
	 *
	 * @param productTypeLoadTunerAll the productTypeLoadTunerAll to set
	 */
	public void setProductTypeLoadTunerAll(final ProductTypeLoadTuner productTypeLoadTunerAll) {
		this.productTypeLoadTunerAll = productTypeLoadTunerAll;
	}

	/**
	 * Sets the load tuner for loading the attributes for a product type.
	 *
	 * @param productTypeLoadTunerAttributes the productTypeLoadTunerAttributes to set
	 */
	public void setProductTypeLoadTunerAttributes(final ProductTypeLoadTuner productTypeLoadTunerAttributes) {
		this.productTypeLoadTunerAttributes = productTypeLoadTunerAttributes;
	}

	@Override
	public ProductType findBySkuCode(final String skuCode) {
		final List<ProductType> typeList = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_TYPE_FIND_BY_SKUCODE", skuCode);
        if (typeList.isEmpty()) {
        	return null;
        }
        return typeList.get(0);
	}
}
