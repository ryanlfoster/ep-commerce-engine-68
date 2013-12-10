package com.elasticpath.persistence.dao;


import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Interface for Product DAO operations.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface ProductDao {

	/**
	 * Adds the given product.
	 *
	 * @param product the product to add
	 * @return the persisted instance of product
	 * @throws EpServiceException - in case of any errors
	 */
	Product add(final Product product) throws EpServiceException;

	/**
	 * Returns all product uids as a list.
	 *
	 * @return all product uids as a list
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Long> findAllUids();

	/**
	 * Returns all available product uids as a list.
	 *
	 * @return all available product uids as a list
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Long> findAvailableUids();

	/**
	 * Retrieves list of <code>Product</code> uids where the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Product</code> whose last modified date is later than the specified date
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Long> findAvailableUidsByModifiedDate(final Date date);

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
	 * Returns a list of <code>Product</code> based on the given brand Uid. The returned products will be populated based on the given load tuner.
	 *
	 * @param brandUid the brand Uid
	 * @param loadTuner the load tuner, give <code>null</code> to populate all related data
	 * @return a list of <code>Product</code>
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByBrandUid(final long brandUid, final ProductLoadTuner loadTuner);

	/**
	 * Returns a list of <code>Product</code> based on the given category Uid.
	 *
	 * @param categoryUid the category Uid
	 * @return a list of <code>Product</code>
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByCategoryUid(final long categoryUid);

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
	Collection<Product> findByCategoryUid(final long categoryUid, final FetchGroupLoadTuner loadTuner);

	/**
	 * Returns a list of <code>Product</code> based on the given category Uid. The returned products will be populated based on the given load
	 * tuner.
	 *
	 * @param categoryUid the category Uid
	 * @param loadTuner the load tuner, give <code>null</code> to populate all related data
	 * @return a list of <code>Product</code>
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByCategoryUid(final long categoryUid, final ProductLoadTuner loadTuner);

	/**
	 * Returns a list of <code>Product</code>s based on the given category Uid. The returned products will be populated based on the given load
	 * tuner. This method allows a subset of the products to be returned at a time by giving the starting index of the first product and the number
	 * of products to be returned.
	 *
	 * @param categoryUid the category Uid
	 * @param startIndex the starting index of the first product to be returned
	 * @param numProducts the number of products to be returned, starting from the start index
	 * @param loadTuner the load tuner, give <code>null</code> to populate all related data
	 * @return a list of <code>Product</code>s
	 */
	List<Product> findByCategoryUidPaginated(final long categoryUid, final int startIndex, final int numProducts, final ProductLoadTuner loadTuner);

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
	Product findByGuid(final String guid, final LoadTuner productLoadTuner);

	/**
	 * Retrieves list of <code>Product</code> where the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Product</code> whose last modified date is later than the specified date
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByModifiedDate(final Date date);

	/**
	 * Returns a list of <code>Product</code> based on the given uids. The returned products will be populated based on the given load tuner.
	 *
	 * @param productUids a collection of product uids
	 * @param productLoadTuner the load tuner
	 * @return a list of <code>Product</code>s
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByUids(final Collection<Long> productUids, final ProductLoadTuner productLoadTuner);

	/**
	 * Returns a list of <code>Product</code> based on the given uids.
	 * The returned products will be populated based on the given {@link FetchGroupLoadTuner}.
	 *
	 * @param productUids a collection of product uids
	 * @param fetchGroupLoadTuner the load tuner
	 * @return a list of <code>Product</code>s
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByUidsWithFetchGroupLoadTuner(final Collection<Long> productUids, final FetchGroupLoadTuner fetchGroupLoadTuner);

	/**
	 * Returns a list of <code>Product</code> based on the given guids.
	 *
	 * @param productGuids a collection of product uids
	 * @return a list of <code>Product</code>s
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Product> findByGuids(final Collection<String> productGuids);

	/**
	 * Find a list of order skus that reference the given product sku.
	 *
	 * @param productSku the product sku to check
	 * @return the list of order skus
	 * TODO : Fortification : This should probably be in OrderDao or OrderSkuDao.
	 */
	List<OrderSku> findOrderSkuByProductSku(final ProductSku productSku);

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
	 * Retrieve the list of top selling products that belong to the listed categories. Usually this list will be a
	 * root categories and all its subcategories.
	 *
	 * @param categoryUids the category ids
	 * @param topSellerMaxCount the maximum count of top sellers
	 * @return the list of top selling products that belongs to the categories
	 * @throws EpServiceException in case of any error
	 */
	List<Product> findProductTopSellerForCategories(final List<Long> categoryUids, final int topSellerMaxCount) throws EpServiceException;

	/**
	 * Get the product UID of the given product identifier. The given product identifier will frist be dealt as a guid to try to find a product UID.
	 * It no product UID is found and the given identifier is a <code>long</code> value, itself will be dealt as UID.
	 *
	 * @param productId the Product Guid or UID.
	 * @return the product UID, otherwise 0
	 * @throws EpServiceException - in case of any errors
	 */
	long findUidById(final String productId) throws EpServiceException;

	/**
	 * Get the Product Codes for a list of Product Uids.
	 * @param productUids The list of Product Uids. Cannot be null.
	 * @return A map of Product Uids to Product Codes.
	 */
	Map<Long, String> findCodesByUids(final List<Long> productUids);
	
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
	 * Retrieves list of product uids where the deleted date is later than the specified date.
	 *
	 * @param date date to compare with the deleted date
	 * @return list of product uids whose deleted date is later than the specified date
	 */
	List<Long> findUidsByDeletedDate(final Date date);

	/**
	 * Retrieves list of <code>Product</code> uids where the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Long</code> whose last modified date is later than the specified date
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Long> findUidsByModifiedDate(final Date date);

	/**
	 * Find a list of product uids that use the given sku option.
	 *
	 * @param skuOption the sku option to search by
	 * @return a list of product uids.
	 */
	List<Long> findUidsBySkuOption(final SkuOption skuOption);

	/**
	 * Retrieves list of product UIDs that belongs to the given store UID.
	 *
	 * @param storeUid the store UID
	 * @return list of product UIDs
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<Long> findUidsByStoreUid(final long storeUid);

	/**
	 * Get the product with the given UID. Return null if no matching record exists.
	 *
	 * @param productUid the Product UID.
	 * @return the product if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Product get(final long productUid) throws EpServiceException;

	/**
	 * Check with database to find maximum featured product order for the given category.
	 * @param categoryUid the categoryUid of the give category.
	 * @return the maximum featured product order number.
	 */
	int getMaxFeaturedProductOrder(final long categoryUid);

	/**
	 * Returns the <code>ProductLoadTuner</code> for populating a minimal data set.
	 *
	 * @return a <code>ProductLoadTuner</code> for populating a minimal data set.
	 */
	ProductLoadTuner getProductLoadTunerMinimal();

	/**
	 * Get a count of <code>ProductSku</code>s belong to this product.
	 *
	 * @param productUid the uid of the product.
	 * @return a count of <code>ProductSku</code>s belong to this product.
	 */
	int getProductSkuCount(final long productUid);

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
	 * Get the product with the given UID. Return null if no matching record exists. You can give a product load tuner to fine control what data get
	 * populated of the returned product.
	 *
	 * @param productUid the Product UID.
	 * @param loadTuner the product load tuner
	 * @return the product if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Product getTuned(final long productUid, final ProductLoadTuner loadTuner) throws EpServiceException;

	/**
	 * Load the product with the given UID. Also load all the categories that the product belongs to.
	 *
	 * @param productUid the product UID
	 * @return the product if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Product getWithCategories(final long productUid) throws EpServiceException;

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
	 * Quick check if category contains any Products without loading products.
	 *
	 * @param categoryUid the category uid
	 * @return has products in category
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	boolean hasProductsInCategory(final long categoryUid);

	/**
	 * Load the product with the given Uid.
	 *
	 * @param productUid the product Uid
	 * @return the product if ID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Product load(final long productUid) throws EpServiceException;

	/**
	 * Deletes the list of products.
	 *
	 * @param productUidList the product Uid List to be removed
	 * @throws EpServiceException - in case of any errors
	 */
	void removeProductList(final List<Long> productUidList) throws EpServiceException;

	/**
	 * Deletes the product and all it associations.
	 *
	 * @param productUid the uid of product to remove
	 * @throws EpServiceException in case of any errors
	 */
	void removeProductTree(final long productUid) throws EpServiceException;

	/**
	 * Save or update the given product.
	 *
	 * @param product the product to save or update
	 * @return the merged object if it is merged, or the persisted object for save action
	 * @throws EpServiceException - in case of any errors
	 */
	Product saveOrUpdate(final Product product) throws EpServiceException;

	/**
	 * Update last modified times for the given list of product uids.
	 *
	 * @param affectedProductUids the list of uids identifying the products to update
	 */
	void updateLastModifiedTimes(final Collection<Long> affectedProductUids);

	/**
	 * Update Product last modified date based by brand.
	 *
	 * @param brand the brand that was updated
	 */
	void updateProductLastModifiedTime(final Brand brand);

	/**
	 * Update Product last modified time for the given product.
	 *
	 * @param product the <code>Product</code> that was updated
	 */
	void updateProductLastModifiedTime(final Product product);

	/**
	 * Updates Product last modified time based on product type.
	 *
	 * @param productType the productType that was updated
	 */
	void updateProductLastModifiedTime(final ProductType productType);

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