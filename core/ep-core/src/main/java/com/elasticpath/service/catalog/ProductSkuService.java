/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductSkuLoadTuner;
import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides productSku-related business services.
 */
public interface ProductSkuService extends EpPersistenceService {

	/**
	 * Get the productSku with the given UID. Return null if no matching record exists.
	 *
	 * @param productSkuUid the ProductSku UID.
	 * @return the productSku if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	ProductSku get(final long productSkuUid) throws EpServiceException;

	
	
 	/**
	 * Gets the sku with the given UID. Return null if no matching records exist. You can
	 * given a fetch group load tuner to fine control what data to get populated of the returned
	 * product.
	 * 
	 * @param productSkuUid the product sku UID
	 * @param loadTuner the load tuner user to fine tune what is loaded.
	 * @return the product if it exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	ProductSku getTuned(final long productSkuUid, final FetchGroupLoadTuner loadTuner) throws EpServiceException;

	/**
	 * Get the productSku with the given UID and load the parent product as well. Return null if no matching record exists.
	 *
	 * @param productSkuUid the ProductSku UID.
	 * @return the productSku if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	ProductSku getWithProduct(final long productSkuUid) throws EpServiceException;

	/**
	 * Generic get method for all persistable domain models.
	 *
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Object getObject(final long uid) throws EpServiceException;

	/**
	 * Deletes the product sku, all of its associations, and all of its {@code BaseAmount}s.
	 *
	 * @param productSkuUid the uid of product sku to remove
	 * @throws EpServiceException in case of any errors
	 */
	void removeProductSkuTree(final long productSkuUid) throws EpServiceException;

	/**
	 * Retrieve the list of productSkus, whose specified property contain the given criteria value.
	 *
	 * @param propertyName productSku property to search on.
	 * @param criteriaValue criteria value to be used for searching.
	 * @return list of productSkus matching the given criteria.
	 * @throws EpServiceException in case of any error
	 */
	List<ProductSku> findProductSkuLike(final String propertyName, final String criteriaValue) throws EpServiceException;

	/**
	 * Retrieve the list of productSkus, whose name matches the given criteria and belongs to direct or indirect subcategory of the specified parent
	 * category.
	 *
	 * @param criteriaValue criteria value to be used for searching.
	 * @param parentCategoryUid Parent Category used to restricted the search results.
	 * @return list of productSkus matching the given criteria.
	 * @throws EpServiceException in case of any error
	 */
	List<ProductSku> findProductSkuCodeLikeWithRestriction(final String criteriaValue, final long parentCategoryUid) throws EpServiceException;

	/**
	 * Returns a list of <code>ProductSku</code> based on the given product Uid.
	 *
	 * @param productUid the product Uid
	 * @return a list of <code>ProductSku</code>
	 */
	List<ProductSku> findByProductUid(final long productUid);

	/**
	 * Save or update the given product sku.
	 *
	 * @param sku the product sku to save or update
	 * @return the updated object instance
	 * @throws EpServiceException - in case of any errors
	 */
	ProductSku saveOrUpdate(final ProductSku sku) throws EpServiceException;

	/**
	 * Finds a <code>ProductSku</code> by its SKU Code.
	 *
	 * @param skuCode the sku code
	 * @return a <code>ProductSku</code> or null if no matching SKU is found
	 */
	ProductSku findBySkuCode(final String skuCode);

	/**
	 * Returns a list of <code>ProductSku</code> based on the given guids.
	 *
	 * @param skuCodes a collection of sku guids
	 * @return a list of <code>ProductSku</code>s
	 */
	List<ProductSku> findBySkuCodes(final Collection<String> skuCodes);

	/**
	 * Returns a list of <code>ProductSku</code> based on the given uids. The returned skus
	 * will be populated based on the given load tuner. If a given sku Uid is not found, it
	 * won't be included in the return list.
	 * 
	 * @param productSkuUids a collection of productSku uids
	 * @param loadTuner the load tuner
	 * @return a list of <code>ProductSku</code>s
	 */
	List<ProductSku> findByUids(Collection<Long> productSkuUids, ProductSkuLoadTuner loadTuner);
	
	
	/**
	 * Returns a list of <code>ProductSku</code> based on the given uids. The returned skus
	 * will be populated based on the given fetch group load tuner. If a given sku Uid is not found, it
	 * won't be included in the return list.
	 * 
	 * @param productSkuUids a collection of productSku uids
	 * @param loadTuner the fetch group load tuner
	 * @return a list of <code>ProductSku</code>s
	 */
	List<ProductSku> findByUids(final Collection<Long> productSkuUids, final FetchGroupLoadTuner loadTuner);

	/**
	 * Finds a <code>ProductSku</code> by its SKU Code and populates all lazy-loaded references.
	 * @param skuCode the sku code
	 * @return a <code>ProductSku</code> or null if no matching SKU is found
	 */
	ProductSku findBySkuCodeWithAll(final String skuCode);

	/**
	 * Adds the given product sku.
	 *
	 * @param productSku the product sku to add
	 * @return the persisted instance of product sku
	 * @throws EpServiceException - in case of any errors
	 */
	ProductSku add(final ProductSku productSku) throws EpServiceException;

	/**
	 * Checks to see if any of the given SKU codes exist. Will exclude those SKUs for the given
	 * product UID.
	 *
	 * @param skuCodes a {@link Collection} of SKU codes
	 * @param productUid the UID of the product to exclude
	 * @return the list of SKU codes that exist
	 * @throws EpServiceException in case of any errors
	 */
	List<String> skuExists(final Collection<String> skuCodes, final long productUid) throws EpServiceException;

	/**
	 * Checks whether the product SKU can be deleted or not.
	 * @param productSku the SKU
	 * @return true if it can be deleted
	 */
	boolean canDelete(final ProductSku productSku);


	/**
	 * Checks is a given ProductSku is used within any bundles.
	 * @param productSku the ProductSku
	 * @return true if productSku is used by any bundle.
	 */
    boolean isInBundle(ProductSku productSku);
	
    /**
     * Finds a collection of ProductBundles that contain the specified ProductSku.
     * @param productSku the ProductSku.
     * @return the ProductBundles that contain the ProductSku.
     */
    Collection<ProductBundle> findProductBundlesContaining(ProductSku productSku);
    
	/**
	 * Finds SKUs by their product relationship.
	 *
	 * @param productCode the product code
	 * @param startIndex the starting index for the result set
	 * @param maxResults the max returned results
	 * @param sortingFields the fields to order by
	 * @param loadTuner the load tuner
	 * @return a list of product SKUs
	 */
	List<ProductSku> findSkusByProductCode(String productCode, int startIndex, int maxResults,
			DirectedSortingField [] sortingFields, LoadTuner loadTuner);

	/**
	 * Gets the total number of SKUs for a product.
	 *
	 * @param productCode the product code
	 * @return the number of SKUs belonging to the product with the given product code
	 */
	long getProductSkuCount(String productCode);

	/**
	 * Get the sku UID of the given sku identifier. The given sku identifier will first be dealt as a guid to try to find a sku UID.
	 *
	 * @param skuCode the SKU Code or Guid.
	 * @return the sku UID, otherwise 0
	 * @throws EpServiceException - in case of any errors
	 */
	long findUidBySkuCode(final String skuCode) throws EpServiceException;

	/**
	 * Find the UIDs of skus whose parent product was modified since the given date.
	 *
	 * @param lastModifiedDate the date to check
	 * @return a list of uids
	 */
	List<Long> findUidsByProductLastModifiedDate(Date lastModifiedDate);

	/**
	 * Find all sku UIDs. <b>NOTE:</b> this may return a very large number of results.
	 *
	 * @return all sku uids.
	 */
	List<Long> findAllUids();
	
	/**
	 * Retrieves list of sku uids where the deleted date is later than the specified date.
	 * 
	 * @param date date to compare with the deleted date
	 * @return list of sku uids whose deleted date is later than the specified date
	 */
	List<Long> findSkuUidsByDeletedDate(final Date date);

	/**
	 * Retrieves the PreOrBackOrder details for the given product SKU.
	 * 
	 * @param skuCode the code of the product SKU
	 * @return the {@link PreOrBackOrderDetails}
	 */
	PreOrBackOrderDetails getPreOrBackOrderDetails(String skuCode);

	/**
	 * Get the sku by the given guid.
	 *
	 * @param guid Guid.
	 * @return the sku or null
	 * @throws EpServiceException - in case of any errors
	 */
	ProductSku findByGuid(String guid);

}
