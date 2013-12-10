/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductSkuLoadTuner;
import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.domain.search.ObjectDeleted;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.dao.ProductBundleDao;
import com.elasticpath.service.DirectedSortingFieldException;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.pricing.dao.BaseAmountDao;

/**
 * The default implementation of <code>ProductSkuService</code>.
 */
public class ProductSkuServiceImpl extends AbstractEpPersistenceServiceImpl implements ProductSkuService {
	private static final String PLACE_HOLDER_FOR_LIST = "list";

	private ProductSkuLoadTuner productSkuLoadTunerAll;

	private ProductSkuLoadTuner productSkuLoadTunerMinimal;

	private ProductLoadTuner productLoadTunerAll;

	private FetchPlanHelper fetchPlanHelper;

	private CategoryService categoryService;
	
	private ProductService productService;
	
	private CategoryLoadTuner categoryLoadTuner;
	
	private BaseAmountDao baseAmountDao;
	
	private ProductBundleDao productBundleDao;
	
	/**
	 * Get the productSku with the given UID. Return null if no matching record exists.
	 *
	 * @param productSkuUid the ProductSku UID.
	 * @return the productSku if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public ProductSku get(final long productSkuUid) throws EpServiceException {
		sanityCheck();
		ProductSku productSku = null;
		if (productSkuUid <= 0) {
			productSku = getBean(ContextIdNames.PRODUCT_SKU);
		} else {
			fetchPlanHelper.configureProductSkuFetchPlan(productSkuLoadTunerAll);
			productSku = getPersistentBeanFinder().get(ContextIdNames.PRODUCT_SKU, productSkuUid);
			fetchPlanHelper.clearFetchPlan();
		}
		return productSku;
	}
	
 	@Override
	public ProductSku getTuned(final long productSkuUid, final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		sanityCheck();
		if (productSkuUid <= 0) {
			return null;
		}
		getFetchPlanHelper().configureFetchGroupLoadTuner(loadTuner);
		final ProductSku productSku = getPersistentBeanFinder().get(ContextIdNames.PRODUCT_SKU, productSkuUid);
		getFetchPlanHelper().clearFetchPlan();
		return productSku;
	}
	
	

	/**
	 * Get the productSku with the given UID and load the parent product as well. Return null if no matching record exists.
	 *
	 * @param productSkuUid the ProductSku UID.
	 * @return the productSku if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public ProductSku getWithProduct(final long productSkuUid) throws EpServiceException {
		sanityCheck();
		ProductSku productSku = null;
		if (productSkuUid <= 0) {
			productSku = getBean(ContextIdNames.PRODUCT_SKU);
		} else {
			fetchPlanHelper.configureProductFetchPlan(productLoadTunerAll);
			productSku = getPersistentBeanFinder().get(ContextIdNames.PRODUCT_SKU, productSkuUid);
			fetchPlanHelper.clearFetchPlan();
		}
		return productSku;

	}

	/**
	 * Generic get method for all persistable domain models.
	 *
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * {@inheritDoc}
	 * This implementation calls {@link #removePricesForProductSku(String)}.
	 */
	public void removeProductSkuTree(final long productSkuUid) throws EpServiceException {
		sanityCheck();
		List<Long> productSkuUidList = new ArrayList<Long>();
		productSkuUidList.add(new Long(productSkuUid));
		ProductSku productSku = getPersistentBeanFinder().get(ContextIdNames.PRODUCT_SKU, productSkuUid);

		// delete cart items referring to this product sku
		getPersistenceEngine().executeNamedQuery("CARTITEM_DELETE_BY_SKU_UID",
				new Long(productSku.getUidPk()));

		//Only one product may related to this sku.
		if (productSku.getProduct().getDefaultSku() == productSku) {
			productSku.getProduct().setDefaultSku(null);
			getPersistenceEngine().saveOrMerge(productSku.getProduct());
		}

		// delete the sku
		removePricesForProductSku(productSku.getSkuCode());
		getPersistenceEngine().delete(productSku);
	}

	/**
	 * Removes all {@code BaseAmount} records for the given sku's GUID.
	 * Calls {@link #getBaseAmountDao()}.
	 * @param guid the sku guid
	 */
	protected void removePricesForProductSku(final String guid) {
		getBaseAmountDao().deleteBaseAmounts(guid, BaseAmountObjectType.SKU.getName());
	}

	/**
	 * Retrieve the list of productSkus, whose specified property contain the given criteria value.
	 *
	 * @param propertyName productSku property to search on.
	 * @param criteriaValue criteria value to be used for searching.
	 * @return list of productSkus matching the given criteria.
	 * @throws EpServiceException in case of any error
	 */
	public List<ProductSku> findProductSkuLike(final String propertyName, final String criteriaValue) throws EpServiceException {
		if (propertyName == null || propertyName.length() == 0) {
			throw new EpServiceException("propertyName not set");
		}
		if (criteriaValue == null || criteriaValue.trim().length() == 0) {
			return null;
		}
		sanityCheck();
		fetchPlanHelper.configureProductSkuFetchPlan(this.productSkuLoadTunerAll);
		List<ProductSku> result = getPersistenceEngine().retrieve("SELECT ps FROM ProductSkuImpl ps WHERE ps." + propertyName + " LIKE ?1",
				"%" + criteriaValue + "%");
		fetchPlanHelper.clearFetchPlan();
		return result;
	}

	/**
	 * Retrieve the list of productSkus, whose name matches the given criteria and belongs to direct or indirect subcategory of the specified parent
	 * category.
	 *
	 * @param criteriaValue criteria value to be used for searching.
	 * @param parentCategoryUid Parent Category used to restricted the search results.
	 * @return list of productSkus matching the given criteria.
	 * @throws EpServiceException in case of any error
	 */
	public List<ProductSku> findProductSkuCodeLikeWithRestriction(final String criteriaValue,
			final long parentCategoryUid) throws EpServiceException {
		if (criteriaValue == null || criteriaValue.trim().length() == 0) {
			return null;
		}
		sanityCheck();
		final List<Long> categoryUids = this.categoryService.findDescendantCategoryUids(parentCategoryUid);
		fetchPlanHelper.configureProductSkuFetchPlan(this.productSkuLoadTunerAll);
		final List<ProductSku> productSkus = getPersistenceEngine().retrieveByNamedQueryWithList(
				"PRODUCTSKU_SELECT_BY_SKUCODE_LIKE_AND_CATEGORY_UIDS",
				"list",
				categoryUids,
				"%" + criteriaValue + "%");
		fetchPlanHelper.clearFetchPlan();
		return productSkus;
	}

	/**
	 * Sets the <code>ProductSkuLoadTuner</code> for populating all data.
	 *
	 * @param productSkuLoadTunerAll the <code>ProductSkuLoadTuner</code> for populating all data.
	 */
	public void setProductSkuLoadTunerAll(final ProductSkuLoadTuner productSkuLoadTunerAll) {
		this.productSkuLoadTunerAll = productSkuLoadTunerAll;
	}

	/**
	 * Sets the <code>ProductSkuLoadTuner</code> to populate the <code>SkuOptionValue</code>s.
	 *
	 * @param productSkuLoadTunerMinimal the <code>ProductSkuLoadTuner</code> for populating only the <code>SkuOptionValue</code>s.
	 */
	public void setProductSkuLoadTunerMinimal(final ProductSkuLoadTuner productSkuLoadTunerMinimal) {
		this.productSkuLoadTunerMinimal = productSkuLoadTunerMinimal;
	}

	/**
	 * Sets the <code>ProductLoadTuner</code> for populating all data.
	 *
	 * @param productLoadTunerAll the <code>ProductLoadTuner</code> for populating all data.
	 */

	public void setProductLoadTunerAll(final ProductLoadTuner productLoadTunerAll) {
		this.productLoadTunerAll = productLoadTunerAll;
	}

	/**
	 * Returns a list of <code>ProductSku</code> based on the given product Uid.
	 *
	 * @param productUid the product Uid
	 * @return a list of <code>ProductSku</code>
	 */

    // ---- DOCfindByProductUid
	public List<ProductSku> findByProductUid(final long productUid) {
		sanityCheck();
		fetchPlanHelper.configureProductSkuFetchPlan(this.productSkuLoadTunerMinimal);
		List<ProductSku> result = getPersistenceEngine().retrieveByNamedQuery("PRODUCTSKU_SELECT_BY_PRODUCT_UID", new Long(productUid));
		fetchPlanHelper.clearFetchPlan();
		return result;
	}
	// ---- DOCfindByProductUid

	/**
	 * Finds a <code>ProductSku</code> by its SKU Code.
	 * @param skuCode the sku code
	 * @return a <code>ProductSku</code> or null if no matching SKU is found
	 */
	public ProductSku findBySkuCode(final String skuCode) {
		sanityCheck();
		configureLoadTuners();
		List<ProductSku> result = getPersistenceEngine().retrieveByNamedQuery("PRODUCTSKU_SELECT_BY_SKU_CODE", skuCode);
		if (result.isEmpty()) {
			return null;
		}
		ProductSku productSku = result.get(0);
		fetchPlanHelper.clearFetchPlan();
		return productSku;
	}
	
	/**
	 * Returns a list of <code>ProductSku</code> based on the given guids.
	 * 
	 * @param skuCodes a collection of sku guids
	 * @return a list of <code>ProductSku</code>s
	 */	
	public List<ProductSku> findBySkuCodes(final Collection<String> skuCodes) {
		sanityCheck();
		configureLoadTuners();
		
		List<ProductSku> result = getPersistenceEngine().retrieveByNamedQueryWithList(
				"PRODUCT_SKUS_BY_SKU_CODES", PLACE_HOLDER_FOR_LIST, skuCodes);
		
		fetchPlanHelper.clearFetchPlan();
		return result;
	}

	/**
	 * Finds a <code>ProductSku</code> by its SKU Code and populates all lazy-loaded references.
	 * @param skuCode the sku code
	 * @return a <code>ProductSku</code> or null if no matching SKU is found
	 */
	public ProductSku findBySkuCodeWithAll(final String skuCode) {
		sanityCheck();
		configureLoadTuners();
		List<ProductSku> result = getPersistenceEngine().retrieveByNamedQuery("PRODUCTSKU_SELECT_BY_SKU_CODE", skuCode);
		if (result.isEmpty()) {
			return null;
		}
		ProductSku productSku = result.get(0);
		fetchPlanHelper.clearFetchPlan();
		return productSku;
	}
	
	/**
	 * Configure load tuners for findBySkuCode... operations. 
	 */
	protected void configureLoadTuners() {
		fetchPlanHelper.configureProductFetchPlan(productLoadTunerAll);
		fetchPlanHelper.configureProductSkuFetchPlan(productSkuLoadTunerAll);
		fetchPlanHelper.configureCategoryFetchPlan(categoryLoadTuner);
	}
	

	/**
	 * Save or update the given product sku.
	 *
	 * @param sku the product sku to save or update
	 * @return the updated object instance
	 * @throws DuplicateKeyException - when sku code is duplicate in the db
	 */
	public ProductSku saveOrUpdate(final ProductSku sku) throws DuplicateKeyException {
		sanityCheck();
		throwExceptionIfDuplicate(sku);

		ProductSku updatedSku = this.getPersistenceEngine().saveOrMerge(sku);
		getProductService().notifySkuUpdated(updatedSku);
		return updatedSku;
	}

	/**
	 * Set the <code>CategoryService</code> singleton.
	 *
	 * @param categoryService the <code>CategoryService</code> singleton.
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * Check if the ProductSku type is existed.
	 * @param sku the sku to be checked
	 */
	protected void throwExceptionIfDuplicate(final ProductSku sku) {
		Long count = getPersistenceEngine().<Long>retrieveByNamedQuery("PRODUCTSKU_COUNT_BY_NAME", sku.getSkuCode(), sku.getUidPk()).get(0);
		if (count.intValue() != 0) {
			throw new DuplicateKeyException("ProductSku code '" + sku.getSkuCode()
				+ "' already exists.");
		}
	}

	@Override
	public long findUidBySkuCode(final String skuCode) throws EpServiceException {
		final List<Long> result = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_SKU_UID_SELECT_BY_GUID", skuCode);
		
		if (result.isEmpty()) {
			return 0L;
			
		} else if (result.size() > 1) {
			throw new EpServiceException("Inconsistent data, duplicat product sku code:" + skuCode);
		}
		
		return result.iterator().next();
	}
	
	@Override
	public List<Long> findSkuUidsByDeletedDate(final Date date) {
		return getPersistenceEngine().retrieveByNamedQuery("OBJECT_UIDS_SELECT_BY_DELETED_DATE", ObjectDeleted.OBJECT_DELETED_TYPE_SKU, date);
	}

	/**
	 * Set the product service.
	 * @param productService the product service
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	private ProductService getProductService() {
		if (this.productService == null) {
			this.productService = getBean(ContextIdNames.PRODUCT_SERVICE);
		}
		return this.productService;
	}

	/**
	 * Adds the given product sku.
	 *
	 * @param productSku the product sku to add
	 * @return the persisted instance of product sku
	 * @throws EpServiceException - in case of any errors
	 */
	public ProductSku add(final ProductSku productSku) throws EpServiceException {
		sanityCheck();
		throwExceptionIfDuplicate(productSku);
		getPersistenceEngine().save(productSku);
		getProductService().notifySkuUpdated(productSku);
		return productSku;
	}
	
	/**
	 * Checks to see if any of the given SKU codes exist. Will exclude those SKUs for the given
	 * product UID.
	 * 
	 * @param skuCodes a {@link Collection} of SKU codes
	 * @param productUid the UID of the product to exclude
	 * @return the list of SKU codes that exist
	 * @throws EpServiceException in case of any errors
	 */
	public List<String> skuExists(final Collection<String> skuCodes, final long productUid) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQueryWithList("PRODUCT_SKU_EXISTS", PLACE_HOLDER_FOR_LIST, skuCodes, productUid);
	}

	/**
	 * Get the fetch plan helper.
	 * @return the fetchPlanHelper
	 */
	public FetchPlanHelper getFetchPlanHelper() {
		return fetchPlanHelper;
	}

	/**
	 * Set the fetch plan helper.
	 * @param fetchPlanHelper the fetchPlanHelper to set
	 */
	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}
	
	
	/**
	 * Set the category load tuner.
	 * @param categoryLoadTuner category load tuner
	 */
	public void setCategoryLoadTuner(final CategoryLoadTuner categoryLoadTuner) {
		this.categoryLoadTuner = categoryLoadTuner;
	}

	/**
	 * Determine whether the given SKU can be deleted.
	 * 
	 * @param productSku the Sku to check
	 * @return true if the sku can be deleted.
	 */
	public boolean canDelete(final ProductSku productSku) {
	    if (isInBundle(productSku)) {
	        return false;
	    }
	    
		final Long count = getPersistenceEngine().<Long>retrieveByNamedQuery("COUNT_ORDER_SKUS_FOR_SKUCODE", productSku.getSkuCode()).get(0);
		if (count.intValue() > 0) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean isInBundle(final ProductSku productSku) {
        return !productBundleDao.findByProductSku(productSku.getSkuCode()).isEmpty();
	}
	
	@Override
    public Collection<ProductBundle> findProductBundlesContaining(final ProductSku productSku) {
        return productBundleDao.findByProductSku(productSku.getSkuCode());
    }
	
	/**
	 * <p>
	 * <b>Implementation Note:</b> Current implementation only supports a single sorting field. 
	 *
	 * @param productCode the product code
	 * @param startIndex the starting index for the result set
	 * @param maxResults the max returned results
	 * @param sortingFields the fields to order by
	 * @param loadTuner the load tuner
	 * @return a list of product SKUs
	 */
	public List<ProductSku> findSkusByProductCode(
			final String productCode, 
			final int startIndex, 
			final int maxResults, final DirectedSortingField [] sortingFields, 
			final LoadTuner loadTuner) {
		if (productCode == null || ArrayUtils.isEmpty(sortingFields)) {
			
			throw new DirectedSortingFieldException("Null-value argument", "productCode", sortingFields, productCode);
		}
		if (startIndex < 0 || maxResults < 0) {
			throw new IllegalArgumentException(
					String.format("Negative-value argument: startIndex=%d, maxResults=%d", startIndex, maxResults));
		}
		
		this.fetchPlanHelper.configureLoadTuner(loadTuner);
		
		List<ProductSku> result = getPersistenceEngine().retrieve(
				"SELECT ps FROM ProductSkuImpl ps WHERE ps.productInternal.code = ?1 ORDER BY ps." 
				+ sortingFields[0].getSortingField().getName()
				+ " "
				+ sortingFields[0].getSortingDirection(), 
				new Object[] { productCode }, startIndex, maxResults);
		this.fetchPlanHelper.clearFetchPlan();
		
		return result;
	}

	/**
	 * Gets the total number of SKUs for a product.
	 * 
	 * @param productCode the product code
	 * @return the number of SKUs belonging to the product with the given product code
	 */
	public long getProductSkuCount(final String productCode) {
		List<Long> result = getPersistenceEngine().retrieveByNamedQuery("PRODUCTSKU_COUNT_BY_PRODUCT_CODE", productCode);
		
		return result.iterator().next();

	}
	
	/**
	 * @param baseAmountDao the baseAmountDao to set
	 */
	public void setBaseAmountDao(final BaseAmountDao baseAmountDao) {
		this.baseAmountDao = baseAmountDao;
	}

	/**
	 * @return the baseAmountDao
	 */
	public BaseAmountDao getBaseAmountDao() {
		return baseAmountDao;
	}

	@Override
	public List<ProductSku> findByUids(final Collection<Long> productSkuUids, final ProductSkuLoadTuner loadTuner) {
		sanityCheck();
		if (productSkuUids == null || productSkuUids.isEmpty()) {
			return Collections.emptyList();
		}

		getFetchPlanHelper().configureProductSkuFetchPlan(loadTuner);		
		List<ProductSku> result = getPersistenceEngine().retrieveByNamedQueryWithList(
				"PRODUCT_SKUS_BY_SKU_UIDS", PLACE_HOLDER_FOR_LIST, productSkuUids);
		
		fetchPlanHelper.clearFetchPlan();
		return result;
	}

	@Override
	public List<ProductSku> findByUids(final Collection<Long> productSkuUids, final FetchGroupLoadTuner loadTuner) {
		sanityCheck();
		if (CollectionUtils.isEmpty(productSkuUids)) {
			return Collections.emptyList();
		}

		getFetchPlanHelper().configureFetchGroupLoadTuner(loadTuner);		
		List<ProductSku> result = getPersistenceEngine().retrieveByNamedQueryWithList(
				"PRODUCT_SKUS_BY_SKU_UIDS", PLACE_HOLDER_FOR_LIST, productSkuUids);
		fetchPlanHelper.clearFetchPlan();
		return result;
	}

	
	
	@Override
	public List<Long> findUidsByProductLastModifiedDate(final Date lastModifiedDate) {
		return getPersistenceEngine().retrieveByNamedQuery("PRODUCTSKU_UIDS_SINCE_PRODUCT_LAST_MODIFIED", lastModifiedDate);
	}

	@Override
	public List<Long> findAllUids() {
		return getPersistenceEngine().retrieveByNamedQuery("PRODUCTSKU_UIDS_ALL");
	}

    /**
     * Sets the ProductBundleDao.
     * @param productBundleDao the productBundleDao to set
     */
    public void setProductBundleDao(final ProductBundleDao productBundleDao) {
        this.productBundleDao = productBundleDao;
    }	

    @Override
	public PreOrBackOrderDetails getPreOrBackOrderDetails(final String skuCode) {
		final List<PreOrBackOrderDetails> results = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_SKU_PRE_OR_BACK_ORDER_DETAILS", skuCode);
		if (results.isEmpty()) {
			return null;
		}
		return results.get(0);
	}

    @Override
	public ProductSku findByGuid(final String guid) {
		configureLoadTuners();
		List<ProductSku> result = getPersistenceEngine().retrieveByNamedQuery("PRODUCTSKU_SELECT_BY_GUID", guid);
		if (result.isEmpty()) {
			return null;
		}
		ProductSku productSku = result.get(0);
		fetchPlanHelper.clearFetchPlan();
		return productSku;
	}
}