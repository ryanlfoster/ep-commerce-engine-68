/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides product-related business services. This service currently handles products, product
 * types, and attributes for products although this may be moved later.
 * 
 * Do not add findXXX methods to this interface. The ProductQueryService should be used instead
 * as this allows flexible queries using the Query Object Pattern and Builder Pattern.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface ProductService extends EpPersistenceService {

	/** Reason for {@link CanDeleteObjectResult} when product is part of bundle and cannot be deleted. */
	int CANNOT_DELETE_PART_OF_BUNDLE = 1;

	/** Reason for {@link CanDeleteObjectResult} when product's SKU is in shipment. */
	int CANNOT_DELETE_SKU_IS_IN_USE = 2;

	/**
	 * Load the product with the given UID.
	 *
	 * @param productUid the product UID
	 * @return the product if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Product load(final long productUid) throws EpServiceException;

	/**
	 * Get the product with the given UID. Return null if no matching record exists.
	 *
	 * @param productUid the Product UID.
	 * @return the product if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Product get(final long productUid) throws EpServiceException;

	/**
	 * Retrieve the list of top selling products that belongs to the category or its
	 * subcategories.
	 *
	 * @param categoryUid the category id
	 * @param topCount the number of top seller products to retrieve
	 * @return the list of top selling products that belongs to the category or its subcategories
	 * @throws EpServiceException in case of any error
	 */
	List<Product> findProductTopSellerForCategory(final long categoryUid, final int topCount) throws EpServiceException;

	/**
	 * Retrieve a list of top sellers of the whole store.
	 *
	 * @param topCount the number of top seller products to retrieve
	 * @param productLoadTuner the product load tunner to control data get loaded
	 * @return the list of top sellers of the whole store
	 * @throws EpServiceException in case of any error
	 */
	List<Product> findProductTopSeller(final int topCount, final ProductLoadTuner productLoadTuner) throws EpServiceException;

	/**
	 * Save or update the given product.
	 *
	 * @param product the product to save or update
	 * @return the merged product if it is merged, or the persisted object for save action
	 * @throws EpServiceException - in case of any errors
	 * @throws com.elasticpath.domain.catalog.CyclicBundleException if the given product is a ProductBundle and it has cyclic constituents
	 */
	Product saveOrUpdate(Product product) throws EpServiceException;

	/**
	 * Load the product with the given UID. Also load all the categories that the product belongs
	 * to.
	 *
	 * @param productUid the product UID
	 * @return the product if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Product getWithCategories(final long productUid) throws EpServiceException;

	/**
	 * Deletes the product, its associations, its skus, its BaseAmounts, and all of its skus' BaseAmounts.
	 *
	 * @param productUid the uid of product to remove
	 * @throws EpServiceException in case of any errors
	 */
	void removeProductTree(final long productUid) throws EpServiceException;

	/**
	 * Retrieves list of <code>Product</code> where the last modified date is later than the
	 * specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Product</code> whose last modified date is later than the specified
	 *         date
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByModifiedDate(final Date date);

	/**
	 * Quick check if category contains any Products without loading products.
	 *
	 * @param categoryUid the category uid
	 * @return has products in category
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	boolean hasProductsInCategory(final long categoryUid);

	/**
	 * Retrieves list of product uids where the deleted date is later than the specified date.
	 *
	 * @param date date to compare with the deleted date
	 * @return list of product uids whose deleted date is later than the specified date
	 */
	List<Long> findUidsByDeletedDate(final Date date);

	/**
	 * Returns a list of <code>Product</code> based on the given uids. The returned products
	 * will be populated based on the given load tuner. If a given product Uid is not found, it
	 * won't be included in the return list.
	 *
	 * @param productUids a collection of product uids
	 * @param loadTuner the load tuner
	 * @return a list of <code>Product</code>s
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByUids(Collection<Long> productUids, ProductLoadTuner loadTuner);

	/**
	 * Returns a list of <code>Product</code> based on the given uids. The returned products
	 * will be populated based on the given {@link FetchGroupLoadTuner}. If a given product Uid is not found, it
	 * won't be included in the return list.
	 *
	 * @param productUids a collection of product uids
	 * @param fetchGroupLoadTuner the load tuner
	 * @return a list of <code>Product</code>s
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByUidsWithFetchGroupLoadTuner(Collection<Long> productUids, FetchGroupLoadTuner fetchGroupLoadTuner);


	/**
	 * Returns a list of <code>Product</code> based on the given guids.
	 *
	 * @param productGuids a collection of product guids
	 * @return a list of <code>Product</code>s
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByGuids(final Collection<String> productGuids);

	/**
	 * Returns a list of <code>Product</code> based on the given brand Uid.
	 *
	 * @param brandUid the brand Uid
	 * @return a list of <code>Product</code>
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByBrandUid(final long brandUid);

	/**
	 * Returns a list of <code>Product</code> based on the given category Uid.
	 *
	 * @param categoryUid the category Uid
	 * @return a list of <code>Product</code>
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByCategoryUid(long categoryUid);

	/**
	 * Returns a collection of {@link Product}s that are directly within the given
	 * {@code categoryUid}. Give a load tuner (or {@code null} to use the default load tuner) to
	 * tune the result set.
	 *
	 * @param categoryUid the category UID to retrieve products for
	 * @param loadTuner the load tuner to use or {@code null}
	 * @return a collection of {@link Product}s directly within the category
	 * @throws EpServiceException in case of any errors
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	Collection<Product> findByCategoryUid(long categoryUid, FetchGroupLoadTuner loadTuner);

	/**
	 * Returns a list of <code>Product</code> based on the given category Uid. The returned
	 * products will be populated based on the given load tuner.
	 *
	 * @param categoryUid the category Uid
	 * @param loadTuner the load tuner, give <code>null</code> to populate all related data
	 * @return a list of <code>Product</code>
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByCategoryUid(final long categoryUid, final ProductLoadTuner loadTuner);

	/**
	 * Returns a list of <code>Product</code>s based on the given category Uid. The returned
	 * products will be populated based on the given load tuner. This method allows a subset of
	 * the products to be returned at a time by giving the starting index of the first product and
	 * the number of products to be returned.
	 *
	 * @param categoryUid the category Uid
	 * @param startIndex the starting index of the first product to be returned
	 * @param numProducts the number of products to be returned, starting from the start index
	 * @param loadTuner the load tuner, give <code>null</code> to populate all related data
	 * @return a list of <code>Product</code>
	 */
	List<Product> findByCategoryUidPaginated(final long categoryUid, final int startIndex, final int numProducts,
			final ProductLoadTuner loadTuner);

	/**
	 * Returns a list of <code>Product</code> based on the given brand Uid. The returned
	 * products will be populated based on the given load tuner.
	 *
	 * @param brandUid the brand Uid
	 * @param loadTuner the load tuner, give <code>null</code> to populate all related data
	 * @return a list of <code>Product</code>
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByBrandUid(final long brandUid, final ProductLoadTuner loadTuner);

	/**
	 * Returns all product uids as a list.
	 *
	 * @return all product uids as a list
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Long> findAllUids();

	/**
	 * Gets the product with the given UID. Return null if no matching records exist. You can
	 * given a fetch group load tuner to fine control what data to get populated of the returned
	 * product.
	 *
	 * @param productUid the product UID
	 * @param loadTuner the load tuner user to fine tune what is loaded.
	 * @return the product if it exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	Product getTuned(final long productUid, final FetchGroupLoadTuner loadTuner) throws EpServiceException;

	/**
	 * Get the product with the given UID. Return null if no matching record exists. You can give
	 * a product load tuner to fine control what data get populated of the returned product.
	 * <p>
	 *
	 * @param productUid the Product UID.
	 * @param loadTuner the product load tuner
	 * @return the product if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Product getTuned(long productUid, ProductLoadTuner loadTuner) throws EpServiceException;

	/**
	 * Returns all available product uids as a list.
	 *
	 * @return all available product uids as a list
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Long> findAvailableUids();

	/**
	 * Retrieves list of <code>Product</code> uids where the last modified date is later than
	 * the specified date. Products refered by the returned uids are all available.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Product</code> uids whose last modified date is later than the
	 *         specified date
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Long> findAvailableUidsByModifiedDate(final Date date);

	/**
	 * Retrieves list of <code>Product</code> uids where the last modified date is later than
	 * the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Long</code> UIDs whose last modified date is later than the
	 *         specified date
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Long> findUidsByModifiedDate(Date date);

	/**
	 * Get the product UID of the given product identifier. The given product identifier will
	 * frist be dealt as a guid to try to find a product UID. It no product UID is found and the
	 * given identifier is a <code>long</code> value, itself will be dealt as UID.
	 *
	 * @param productId the Product Guid or UID.
	 * @return the product UID, otherwise 0
	 * @throws EpServiceException - in case of any errors
	 */
	long findUidById(String productId) throws EpServiceException;

	/**
	 * Get the Product Codes for a list of Product Uids.
	 * @param productUids The list of Product Uids. Cannot be null.
	 * @return A map of Product Uids to Product Codes.
	 */
	Map<Long, String> findCodesByUids(final List<Long> productUids);
	
	/**
	 * Checks whether the given product guid exists or not, for product, i.e. product code.
	 *
	 * @param guid the product code.
	 * @return true if the given guid(code) exists.
	 * @throws EpServiceException - in case of any errors
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	boolean guidExists(final String guid) throws EpServiceException;

	/**
	 * Find the product with the given guid, for product, i.e. product code.
	 *
	 * @param guid the product code.
	 * @return the product that matches the given guid (code), otherwise null
	 * @throws EpServiceException - in case of any errors
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	Product findByGuid(final String guid) throws EpServiceException;

	/**
	 * Find the product with the given guid, for product, i.e. product code, with
	 * a product load tuner.  If product load tuner is null, then ProductLoadTunerAll
	 * will be used.
	 * @param guid the product code
	 * @param productLoadTuner the load tuner, give <code>null</code> to populate all related data
	 * @return the product that matches the given guid (code), otherwise null
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	Product findByGuid(final String guid, final ProductLoadTuner productLoadTuner);

	/**
	 * Deletes the list of products.
	 *
	 * @param productUidList the product Uid List to be removed
	 * @throws EpServiceException - in case of any errors
	 */
	void removeProductList(final List<Long> productUidList) throws EpServiceException;

	/**
	 * Retrieves list of product uids belongs to either category uids given.
	 *
	 * @param categoryUids category uids
	 * @return list of product uids
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Long> findUidsByCategoryUids(final Collection<Long> categoryUids);

	/**
	 * Get a count of <code>ProductSku</code>s belong to this product.
	 *
	 * @param productUid the uid of the product.
	 * @return a count of <code>ProductSku</code>s belong to this product.
	 */
	int getProductSkuCount(final long productUid);

	/**
	 * Set a <code>ProductCategory</code> as a featured product, if the featuredProductOrder is
	 * set to be greater than 0, means this productCategory is featured.
	 *
	 * @param productUid the unique identifier for the product
	 * @param categoryUid the unique identifier for the category
	 * @return featuredProductOrder the featured product order of the <code>ProductCategory</code>
	 */
	int setProductCategoryFeatured(final long productUid, final long categoryUid);

	/**
	 * Disable a <code>ProductCategory</code> as a featured product, if the featuredProductOrder
	 * is set to 0, means this productCategory is not featured.
	 *
	 * @param productUid the unique identifier for the product
	 * @param categoryUid the unique identifier for the category
	 */
	void resetProductCategoryFeatured(final long productUid, final long categoryUid);

	/**
	 * Swap two <code>ProductCategory</code> featured product orders.
	 *
	 * @param productUid the unique identifier for the product
	 * @param categoryUid the unique identifier for the category
	 * @param productUid2 the unique identifier for the product to be swaped
	 */
	void updateFeaturedProductOrder(final long productUid, final long categoryUid, final long productUid2);

	/**
	 * Update the product's last modified timestamp.
	 *
	 * @param product the product whose timestamp is to be updated.
	 */
	void updateLastModifiedTime(final Product product);

	/**
	 * Notifies the product service that a <code>Brand</code> has been updated.
	 *
	 * @param brand the brand that was updated
	 */
	void notifyBrandUpdated(final Brand brand);

	/**
	 * Notifies the product service that a <code>Category</code> has been updated.
	 *
	 * @param category the category that was updated
	 */
	void notifyCategoryUpdated(final Category category);

	/**
	 * Notifies the product service that a <code>ProductSku</code> has been updated.
	 *
	 * @param productSku the productSku that was updated
	 */
	void notifySkuUpdated(final ProductSku productSku);

	/**
	 * Notifies the product service that a <code>ProductType</code> has been updated.
	 *
	 * @param productType the productType that was updated
	 */
	void notifyProductTypeUpdated(final ProductType productType);

	/**
	 * Returns tuned product by given product entity.
	 * @param product product entity.
	 * @return tuned product.
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	Product getProductWithSkus(final Product product) throws EpServiceException;

	/**
	 * Checks whether the product can be deleted or not.
	 * @param product the product
	 * @return true if it can be deleted
	 */
	boolean canDelete(final Product product);

	/**
     * Returns if product is in a bundle or not.
     *
     * @param product the product
     * @return if product is in a bundle or not
     */
    boolean isInBundle(Product product);

	/**
	 * Check with database to find maximum featured product order for the given category.
	 * @param categoryUid the categoryUid of the give category.
	 * @return the maximum featured product order number.
	 */
	int getMaxFeaturedProductOrder(final long categoryUid);

	/**
	 * Retrieves list of product UIDs that belongs to the given store UID.
	 *
	 * @param storeUid the store UID
	 * @return list of product UIDs
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Long> findUidsByStoreUid(long storeUid);

	/**
	 * Retrieve all descendant bundle UIDs of the given constituent UIDs.
	 *
	 * @param constituentUids the constituent UIDs.
	 * @return the list of UIDs of the direct and indirect parent bundle of the given start
	 *         constituent UIDs.
	 */
	List<Long> findBundleUids(final List<Long> constituentUids);

	/**
	 * Find the enriching data within given named query.
	 * @param queryName name of named query
	 * @param guids collection of product codes
	 * @param locale locale
	 * @return list of object array, that represent selected by named query fileds.
	 */
	List<Object[]> findEnrichingData(String queryName, Collection<String> guids,
			Locale locale);

	/**
	 * Get guids of <code>ProductSku</code>s belonging to this product.
	 *
	 * @param productGuid the guid of the product.
	 * @return guids of <code>ProductSku</code>s belonging to this product.
	 */
	List<String> getProductSkuGuids(String productGuid);

	/**
     * Get the product UID of the given sku identifier.
     *
     * @param skuCode the SKU Code or Guid.
     * @return the product UID, otherwise 0
	 * @deprecated Use the QueryService instead.
     */
	@Deprecated
    long findUidBySkuCode(String skuCode);
}
