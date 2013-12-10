/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.persistence.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductDeleted;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductLoadTunerImpl;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.misc.TimeService;

/**
 * Provides <code>Product</code> data access methods.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ProductDaoImpl extends AbstractDaoImpl implements ProductDao {

	private static final String PLACEHOLDER_FOR_LIST = "list";

	private ProductLoadTuner productLoadTunerAll;

	private ProductLoadTuner productLoadTunerMinimal;

	private ProductLoadTuner productLoadTunerForUpdate;

	private FetchPlanHelper fetchPlanHelper;

	private TimeService timeService;

	/**
	 * Adds the given product.
	 *
	 * @param product the product to add
	 * @return the persisted instance of product
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Product add(final Product product) throws EpServiceException {
		sanityCheck();
		product.setLastModifiedDate(timeService.getCurrentTime());
		getPersistenceEngine().save(product);
		return product;
	}

	/**
	 * Returns all product uids as a list.
	 *
	 * @return all product uids as a list
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Long> findAllUids() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("PRODUCT_UIDS_ALL");
	}

	/**
	 * Returns all available product uids as a list.
	 *
	 * @return all available product uids as a list
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Long> findAvailableUids() {
		sanityCheck();
		final Date now = timeService.getCurrentTime();
		return getPersistenceEngine().retrieveByNamedQuery("PRODUCT_UIDS_AVAILABLE", now, now);
	}

	/**
	 * Retrieves list of <code>Product</code> uids where the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Product</code> whose last modified date is later than the specified date
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Long> findAvailableUidsByModifiedDate(final Date date) {
		sanityCheck();
		final Date now = timeService.getCurrentTime();
		return getPersistenceEngine().retrieveByNamedQuery("PRODUCT_UIDS_AVAILABLE_SELECT_BY_MODIFIED_DATE", date, now, now);
	}

	/**
	 * Returns a list of <code>Product</code> based on the given brand Uid.
	 *
	 * @param brandUid the brand Uid
	 * @return a list of <code>Product</code>
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Product> findByBrandUid(final long brandUid) {
		return this.findByBrandUid(brandUid, productLoadTunerAll);
	}

	/**
	 * Returns a list of <code>Product</code> based on the given brand Uid. The returned products will be populated based on the given load tuner.
	 *
	 * @param brandUid the brand Uid
	 * @param loadTuner the load tuner, give <code>null</code> to populate all related data
	 * @return a list of <code>Product</code>
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Product> findByBrandUid(final long brandUid, final ProductLoadTuner loadTuner) {
		sanityCheck();
		if (loadTuner == null) {
			getFetchPlanHelper().configureProductFetchPlan(productLoadTunerAll);
		} else {
			getFetchPlanHelper().configureProductFetchPlan(loadTuner);
		}
		final List<Product> result =
				getPersistenceEngine().retrieveByNamedQuery("PRODUCT_SELECT_BY_BRAND_UID", Long.valueOf(brandUid));
		getFetchPlanHelper().clearFetchPlan();

		return result;
	}

	/**
	 * Returns a list of <code>Product</code> based on the given category Uid.
	 *
	 * @param categoryUid the category Uid
	 * @return a list of <code>Product</code>
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Product> findByCategoryUid(final long categoryUid) {
		return this.findByCategoryUid(categoryUid, productLoadTunerAll);
	}

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
	@Override
	@Deprecated
	public Collection<Product> findByCategoryUid(final long categoryUid, final FetchGroupLoadTuner loadTuner) {
		sanityCheck();
		final FetchGroupLoadTuner configuredLoadTuner = loadTuner;
		if (configuredLoadTuner == null) {
			final FetchGroupLoadTuner defaultLoadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
			defaultLoadTuner.addFetchGroup(FetchGroupConstants.DEFAULT);
		}
		getFetchPlanHelper().configureFetchGroupLoadTuner(configuredLoadTuner, true);
		final Collection<Product> result = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_SELECT_BY_CATEGORY_UID", categoryUid);
		getFetchPlanHelper().clearFetchPlan();
		return result;
	}

	/**
	 * Returns a list of <code>Product</code> based on the given category Uid. The returned products will be populated based on the given load
	 * tuner.
	 *
	 * @param categoryUid the category Uid
	 * @param loadTuner the load tuner, give <code>null</code> to populate all related data
	 * @return a list of <code>Product</code>
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Product> findByCategoryUid(final long categoryUid, final ProductLoadTuner loadTuner) {
		sanityCheck();
		if (loadTuner == null) {
			getFetchPlanHelper().configureProductFetchPlan(productLoadTunerAll);
		} else {
			getFetchPlanHelper().configureProductFetchPlan(loadTuner);
		}
		final List<Product> result = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_SELECT_BY_CATEGORY_UID", Long.valueOf(categoryUid));
		getFetchPlanHelper().clearFetchPlan();
		return result;
	}

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
	@Override
	public List<Product> findByCategoryUidPaginated(final long categoryUid, final int startIndex, final int numProducts,
			final ProductLoadTuner loadTuner) {
		sanityCheck();
		final List<Long> productUids = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_UID_SELECT_BY_CATEGORY_UID",
				new Object[] { Long.valueOf(categoryUid) }, startIndex, numProducts);

		if (productUids.isEmpty()) {
			return new ArrayList<Product>(0);
		}

		return findByUids(productUids, loadTuner);
	}

	/**
	 * Find the product with the given guid, for product, i.e. product code.
	 *
	 * @param guid the product code.
	 * @return the product that matches the given guid (code), otherwise null
	 * @throws EpServiceException - in case of any errors
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public Product findByGuid(final String guid) throws EpServiceException {
		sanityCheck();
		if (guid == null) {
			throw new EpServiceException("Cannot retrieve null code.");
		}
		final List<Product> results = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_SELECT_BY_GUID", guid);
		Product product = null;
		if (results.size() == 1) {
			product = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate product code exist -- " + guid);
		}
		return product;
	}

	/**
	 * Find the product with the given guid, for product, i.e. product code, with
	 * a product load tuner.  If product load tuner is null, then ProductLoadTunerAll
	 * will be used.
	 * @param guid the product code
	 * @param productLoadTuner the load tuner, give <code>null</code> to populate all related data
	 * @return the product that matches the given guid (code), otherwise null
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public Product findByGuid(final String guid, final LoadTuner productLoadTuner) {
		if (productLoadTuner == null) {
			getFetchPlanHelper().configureLoadTuner(productLoadTunerAll);
		} else {
			getFetchPlanHelper().configureLoadTuner(productLoadTuner);
		}
		final Product product = findByGuid(guid);
		getFetchPlanHelper().clearFetchPlan();
		return product;
	}

	/**
	 * Retrieves list of <code>Product</code> where the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Product</code> whose last modified date is later than the specified date
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Product> findByModifiedDate(final Date date) {
		sanityCheck();
		List<Product> result;
		getFetchPlanHelper().configureProductFetchPlan(productLoadTunerAll);
		result = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_SELECT_BY_MODIFIED_DATE", date);
		getFetchPlanHelper().clearFetchPlan();
		return result;
	}

	/**
	 * Returns a list of <code>Product</code> based on the given uids. The returned products will be populated based on the given load tuner.
	 *
	 * @param productUids a collection of product uids
	 * @param productLoadTuner the load tuner
	 * @return a list of <code>Product</code>s
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Product> findByUids(final Collection<Long> productUids, final ProductLoadTuner productLoadTuner) {
		sanityCheck();

		if (productUids == null || productUids.isEmpty()) {
			return Collections.emptyList();
		}

		getFetchPlanHelper().configureProductFetchPlan(productLoadTuner);
		final List<Product> result = getPersistenceEngine().retrieveByNamedQueryWithList("PRODUCT_BY_UIDS", PLACEHOLDER_FOR_LIST, productUids);
		getFetchPlanHelper().clearFetchPlan();
		return result;
	}

	/**
	 * Returns a list of <code>Product</code> based on the given uids. The returned products
	 * will be populated based on the given {@link FetchGroupLoadTuner}.
	 *
	 * @param productUids a collection of product uids
	 * @param fetchGroupLoadTuner the {@link FetchGroupLoadTuner}
	 * @return a list of <code>Product</code>s
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Product> findByUidsWithFetchGroupLoadTuner(final Collection<Long> productUids, final FetchGroupLoadTuner fetchGroupLoadTuner) {
		sanityCheck();

		if (CollectionUtils.isEmpty(productUids)) {
			return Collections.emptyList();
		}

		getFetchPlanHelper().configureFetchGroupLoadTuner(fetchGroupLoadTuner);
		final List<Product> result = getPersistenceEngine().retrieveByNamedQueryWithList("PRODUCT_BY_UIDS", PLACEHOLDER_FOR_LIST, productUids);
		getFetchPlanHelper().clearFetchPlan();
		return result;
	}

	/**
	 * Returns a list of <code>Product</code> based on the given guids.
	 *
	 * @param productGuids a collection of product uids
	 * @return a list of <code>Product</code>s
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Product> findByGuids(final Collection<String> productGuids) {
		sanityCheck();

		if (productGuids == null || productGuids.isEmpty()) {
			return Collections.emptyList();
		}

		final List<Product> result = getPersistenceEngine().retrieveByNamedQueryWithList(
				"PRODUCT_BY_GUIDS",
				PLACEHOLDER_FOR_LIST,
				productGuids);
		return result;
	}

	/**
	 * Find the enriching data within given named query.
	 * @param queryName name of named query
	 * @param guids collection of product codes
	 * @param locale locale
	 * @return list of object array, that represent selected by named query fileds.
	 */
	@Override
	public List<Object[]> findEnrichingData(final String queryName, final Collection<String> guids,
			final Locale locale) {
		return getPersistenceEngine().retrievePartByNamedQueryWithList(
				queryName,
				PLACEHOLDER_FOR_LIST,
				guids,
				locale);

	}

	/**
	 * Find a list of order skus that reference the given product sku.
	 *
	 * @param productSku the product sku to check
	 * @return the list of order skus
	 * TODO : Fortification : This should probably be in OrderDao or OrderSkuDao.
	 */
	@Override
	public List<OrderSku> findOrderSkuByProductSku(final ProductSku productSku) {
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_SKU_SELECT_BY_SKUCODE", productSku.getSkuCode());
	}

	/**
	 * Retrieve a list of top sellers of the whole store.
	 *
	 * @param topCount the number of top seller products to retrieve
	 * @param productLoadTuner the product load tuner to control data get loaded
	 * @return the list of top sellers of the whole store
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public List<Product> findProductTopSeller(final int topCount, final ProductLoadTuner productLoadTuner) throws EpServiceException {
		getFetchPlanHelper().configureProductFetchPlan(productLoadTuner);
		final List<Product> result = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_SELECT_TOP_SELLERS", 0, topCount);
		getFetchPlanHelper().clearFetchPlan();

		return result;
	}

	@Override
	public List<Product> findProductTopSellerForCategories(final List<Long> categoryUids, final int topSellerMaxCount) throws EpServiceException {
		sanityCheck();

		// the data received is of type new Object[] { <product sales count>, <product UID> }
		final List<Object[]> data = getPersistenceEngine().retrieveByNamedQueryWithList("PRODUCT_SELECT_SALES_COUNT_AND_CATEGORY_UIDS",
				PLACEHOLDER_FOR_LIST, categoryUids);

		List<Product> products = null;
		if (data.isEmpty()) {
			products = new ArrayList<Product>(0);
		} else {
			final int topSellerCount = Math.min(data.size(), topSellerMaxCount);
			final List<Long> productUids = new ArrayList<Long>(data.size());
			for (int i = 0; i < topSellerCount; i++) {
				final Object[] entry = data.get(i);
				productUids.add((Long) entry[1]);
			}
			products = findByUids(productUids, productLoadTunerAll);
		}
		return products;
	}

	/**
	 * Get the product UID of the given product identifier. The given product identifier will first be dealt as a guid to try to find a product UID.
	 * It no product UID is found 0 will be returned.
	 *
	 * @param productId the Product Guid or UID.
	 * @return the product UID, otherwise 0
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public long findUidById(final String productId) throws EpServiceException {
		final List<Long> result = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_UID_SELECT_BY_GUID", productId);
		final int size = result.size();
		if (size > 1) {
			throw new EpServiceException("Inconsistent data, duplicate product guid:" + productId);
		}
		long productUid = 0L;
		if (size == 1) {
			productUid = result.get(0).longValue();
		}
		
		return productUid;
	}
	
	@Override
	public Map<Long, String> findCodesByUids(final List<Long> productUids) {
		final List<Object[]> result = getPersistenceEngine().retrieveByNamedQueryWithList("PRODUCT_CODE_SELECT_BY_UID", "list", productUids);
		Map<Long, String> map = new HashMap<Long, String>();
		for (Object[] oArray : result) {
			// uidPk, code
			map.put((Long) oArray[0], (String) oArray[1]);
		}
		return map;
	}

	/**
	 * Retrieves list of product uids belongs to either category uids given.
	 *
	 * @param categoryUids category uids
	 * @return list of product uids
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Long> findUidsByCategoryUids(final Collection<Long> categoryUids) {
		sanityCheck();

		if (categoryUids == null || categoryUids.isEmpty()) {
			return Collections.emptyList();
		}

		return getPersistenceEngine().retrieveByNamedQueryWithList("PRODUCT_UID_SELECT_BY_CATEGORY_UIDS", PLACEHOLDER_FOR_LIST, categoryUids);
	}

	/**
	 * Retrieves list of product uids where the deleted date is later than the specified date.
	 *
	 * @param date date to compare with the deleted date
	 * @return list of product uids whose deleted date is later than the specified date
	 */
	@Override
	public List<Long> findUidsByDeletedDate(final Date date) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("PRODUCT_UIDS_SELECT_BY_DELETED_DATE", date);
	}

	/**
	 * Retrieves list of <code>Product</code> uids where the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Long</code> whose last modified date is later than the specified date
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Long> findUidsByModifiedDate(final Date date) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("PRODUCT_UIDS_SELECT_BY_MODIFIED_DATE", date);
	}

	/**
	 * Find a list of product uids that use the given sku option.
	 *
	 * @param skuOption the sku option to search by
	 * @return a list of product uids.
	 */
	@Override
	public List<Long> findUidsBySkuOption(final SkuOption skuOption) {
		return getPersistenceEngine().retrieveByNamedQuery("PRODUCT_UIDS_BY_SKUOPTION", Long.valueOf(skuOption.getUidPk()));
	}

	/**
	 * Retrieves list of product UIDs that belongs to the given store UID.
	 *
	 * @param storeUid the store UID
	 * @return list of product UIDs
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public List<Long> findUidsByStoreUid(final long storeUid) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("PRODUCT_UIDS_FIND_BY_STORE_UID", storeUid);
	}

	/**
	 * Get the product with the given UID. Return a new object if no matching record exists.
	 *
	 * @param productUid the Product UID.
	 * @return the product if UID exists, otherwise a new (empty) Product
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Product get(final long productUid) throws EpServiceException {
		sanityCheck();

		Product product = null;
		if (productUid <= 0) {
			product = getBean(ContextIdNames.PRODUCT);
		} else {
			getFetchPlanHelper().configureProductFetchPlan(productLoadTunerAll);
			product = getPersistentBeanFinder().get(ContextIdNames.PRODUCT, productUid);
			getFetchPlanHelper().clearFetchPlan();
		}

		return product;
	}

	/**
	 * Check with database to find maximum featured product order for the given category.
	 * @param categoryUid the categoryUid of the give category.
	 * @return the maximum featured product order number.
	 */
	@Override
	public int getMaxFeaturedProductOrder(final long categoryUid) {
		sanityCheck();
		Integer maxFeaturedProductOrder = null;
		maxFeaturedProductOrder = (Integer) getPersistenceEngine().retrieveByNamedQuery("MAX_FEATURED_ORDER_BY_CAT",
				Long.valueOf(categoryUid)).get(0);
		if (maxFeaturedProductOrder != null) {
			return maxFeaturedProductOrder.intValue();
		}
		return 0;
	}

	/**
	 * Returns the <code>ProductLoadTuner</code> for populating a minimal data set.
	 *
	 * @return a <code>ProductLoadTuner</code> for populating a minimal data set.
	 */
	@Override
	public ProductLoadTuner getProductLoadTunerMinimal() {
		return productLoadTunerMinimal;
	}

	/**
	 * Get a count of <code>ProductSku</code>s belong to this product.
	 *
	 * @param productUid the uid of the product.
	 * @return a count of <code>ProductSku</code>s belong to this product.
	 */
	@Override
	public int getProductSkuCount(final long productUid) {
		sanityCheck();
		return ((Long) getPersistenceEngine().retrieveByNamedQuery("PRODUCTSKU_COUNT_BY_PRODUCT", Long.valueOf(productUid)).get(0))
		.intValue();
	}

	/**
	 * Get guids of <code>ProductSku</code>s belonging to this product.
	 *
	 * @param productGuid the guid of the product.
	 * @return guids of <code>ProductSku</code>s belonging to this product.
	 */
	@Override
	public List<String> getProductSkuGuids(final String productGuid) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("PRODUCTSKU_GUID_BY_PRODUCT", productGuid);
	}

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
	@Override
	public Product getTuned(final long productUid, final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		sanityCheck();
		if (productUid <= 0) {
			return null;
		}
		getFetchPlanHelper().configureFetchGroupLoadTuner(loadTuner);
		final Product product = getPersistentBeanFinder().get(ContextIdNames.PRODUCT, productUid);
		getFetchPlanHelper().clearFetchPlan();
		return product;
	}

	/**
	 * Get the product with the given UID. Return null if no matching record exists. You can give a product load tuner to fine control what data get
	 * populated of the returned product.
	 *
	 * @param productUid the Product UID.
	 * @param loadTuner the product load tuner
	 * @return the product if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Product getTuned(final long productUid, final ProductLoadTuner loadTuner) throws EpServiceException {
		sanityCheck();
		if (productUid <= 0) {
			return null;
		}

		getFetchPlanHelper().configureProductFetchPlan(loadTuner);
		final Product product = getPersistentBeanFinder().get(ContextIdNames.PRODUCT, productUid);
		getFetchPlanHelper().clearFetchPlan();
		if (product == null) {
			return null;
		}

		return product;
	}

	/**
	 * Load the product with the given UID. Also load all the categories that the product belongs to.
	 *
	 * @param productUid the product UID
	 * @return the product if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Product getWithCategories(final long productUid) throws EpServiceException {
		sanityCheck();
		Product product = null;
		if (productUid <= 0) {
			product = getBean(ContextIdNames.PRODUCT);
		} else {
			getFetchPlanHelper().configureProductFetchPlan(productLoadTunerForUpdate);
			product = getPersistentBeanFinder().get(ContextIdNames.PRODUCT, productUid);
			getFetchPlanHelper().clearFetchPlan();
		}
		return product;
	}

	/**
	 * Checks whether the given product guid exists or not, for product, i.e. product code.
	 *
	 * @param guid the product code.
	 * @return true if the given guid(code) exists.
	 * @throws EpServiceException - in case of any errors
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public boolean guidExists(final String guid) throws EpServiceException {
		if (guid == null) {
			return false;
		}

		final List<String> productGuids = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_GUID_SELECT_BY_GUID", guid);

		return !productGuids.isEmpty();
	}

	/**
	 * Quick check if category contains any Products without loading products.
	 *
	 * @param categoryUid the category uid
	 * @return has products in category
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
	public boolean hasProductsInCategory(final long categoryUid) {
		sanityCheck();
		final Long count = (Long) getPersistenceEngine().retrieveByNamedQuery("COUNT_PRODUCTS_IN_CATEGORY", categoryUid).get(0);
		if (count.intValue() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Load the product with the given Uid.
	 *
	 * @param productUid the product Uid
	 * @return the product if ID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Product load(final long productUid) throws EpServiceException {
		sanityCheck();

		Product product = null;
		if (productUid > 0) {
			getFetchPlanHelper().configureProductFetchPlan(productLoadTunerAll);
			product = getPersistentBeanFinder().get(ContextIdNames.PRODUCT, productUid);
			getFetchPlanHelper().clearFetchPlan();
		}

		return product;
	}

	/**
	 * Getter method for {@link FetchPlanHelper}.
	 *
	 * @return {@link FetchPlanHelper}, never null.
	 */
	public FetchPlanHelper getFetchPlanHelper() {
		return fetchPlanHelper;
	}

	/**
	 * Deletes the list of products.
	 *
	 * @param productUidList the product Uid List to be removed
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public void removeProductList(final List<Long> productUidList) throws EpServiceException {
            sanityCheck();

            //delete product associations
            //have to use two query due to the incorrect SQLs that JPA generated
            List<ProductAssociation> productAssociations = getPersistenceEngine()
                    .retrieveByNamedQueryWithList("PRODUCTASSOCIATION_SELECT_BY_SOURCEPRODUCT_UIDS", PLACEHOLDER_FOR_LIST, productUidList);

            for (final ProductAssociation productAssociation : productAssociations) {
                    getPersistenceEngine().delete(productAssociation);
            }

            productAssociations = getPersistenceEngine()
                    .retrieveByNamedQueryWithList("PRODUCTASSOCIATION_SELECT_BY_TARGETPRODUCT_UIDS", PLACEHOLDER_FOR_LIST, productUidList);

            for (final ProductAssociation productAssociation : productAssociations) {
                    getPersistenceEngine().delete(productAssociation);
            }

            final ProductLoadTuner productLoadTuner = new ProductLoadTunerImpl();
            productLoadTuner.setLoadingSkus(true);

            // delete the product list
            for (final Long productUid : productUidList) {
                    final Product product = getTuned(productUid, productLoadTuner);

                    // delete the cartItem for each sku in the product
                    for (final ProductSku currSku : product.getProductSkus().values()) {
						final List<WishList> wishLists = getPersistenceEngine().retrieveByNamedQuery("WISHLIST_BY_SHOPPING_ITEM_SKU", currSku);

                    	for (final WishList wishList : wishLists) {
                    		wishList.removeItem(currSku.getSkuCode());
                        	getPersistenceEngine().merge(wishList);
                    	}

                    	getPersistenceEngine().executeNamedQuery("CARTITEM_DELETE_BY_SKU_UID", currSku.getUidPk());
                    }

                    // delete the product itself
                    getPersistenceEngine().delete(product);
                    addProductDeleted(productUid);
            }
	}

	/**
	 * Deletes the product and all it associations.
	 *
	 * @param productUid the uid of product to remove
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public void removeProductTree(final long productUid) throws EpServiceException {
		final List<Long> productUidList = new ArrayList<Long>();
		productUidList.add(productUid);
		removeProductList(productUidList);

	}

	/**
	 * Sanity check of this service instance.
	 * @throws EpServiceException - if something goes wrong.
	 */
	public void sanityCheck() throws EpServiceException {
		if (getPersistenceEngine() == null) {
			throw new EpServiceException("The persistence engine is not correctly initialized.");
		}
	}

	/**
	 * Save or update the given product.
	 *
	 * @param product the product to save or update
	 * @return the merged object if it is merged, or the persisted object for save action
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Product saveOrUpdate(final Product product) throws EpServiceException {
		product.setLastModifiedDate(timeService.getCurrentTime());
		return getPersistenceEngine().saveOrMerge(product);
	}

	/**
	 * Update last modified times for the given list of product uids.
	 *
	 * @param affectedProductUids the list of uids identifying the products to update
	 */
	@Override
	public void updateLastModifiedTimes(final Collection<Long> affectedProductUids) {
		final Date currentTime = timeService.getCurrentTime();
		if (!affectedProductUids.isEmpty()) {
			getPersistenceEngine().executeNamedQueryWithList("PRODUCT_UPDATE_MODIFIED_TIME_BY_UIDS", PLACEHOLDER_FOR_LIST,
					affectedProductUids, currentTime);
		}
	}

	/**
	 * Update Product last modified date based by brand.
	 *
	 * @param brand the brand that was updated
	 */
	@Override
	public void updateProductLastModifiedTime(final Brand brand) {
		final Date currentTime = timeService.getCurrentTime();
		getPersistenceEngine().executeNamedQuery("PRODUCT_UPDATE_MODIFIED_TIME_BY_BRAND",
				currentTime, Long.valueOf(brand.getUidPk()));
	}

	/**
	 * Update Product last modified time for the given product.
	 *
	 * @param product the <code>Product</code> that was updated
	 */
	@Override
	public void updateProductLastModifiedTime(final Product product) {
		final Date currentTime = timeService.getCurrentTime();
		getPersistenceEngine().executeNamedQuery("PRODUCT_UPDATE_MODIFIED_TIME_BY_UID",
				currentTime, Long.valueOf(product.getUidPk()));
	}

	/**
	 * Updates Product last modified time based on product type.
	 *
	 * @param productType the productType that was updated
	 */
	@Override
	public void updateProductLastModifiedTime(final ProductType productType) {
		final Date currentTime = timeService.getCurrentTime();
		getPersistenceEngine().executeNamedQuery("PRODUCT_UPDATE_MODIFIED_TIME_BY_PRODUCT_TYPE",
				currentTime, Long.valueOf(productType.getUidPk()));
	}

	private void addProductDeleted(final long uid) {
		final ProductDeleted productDeleted = getBean(ContextIdNames.PRODUCT_DELETED);
		productDeleted.setProductUid(uid);
		productDeleted.setDeletedDate(timeService.getCurrentTime());
		getPersistenceEngine().save(productDeleted);
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
	 * Sets the <code>ProductLoadTuner</code> for populating all data.
	 *
	 * @param productLoadTunerAll the <code>ProductLoadTuner</code> for populating all data.
	 */
	public void setProductLoadTunerAll(final ProductLoadTuner productLoadTunerAll) {
		this.productLoadTunerAll = productLoadTunerAll;
	}

	/**
	 * Sets the <code>ProductLoadTuner</code> for populating a product for CM update.
	 *
	 * @param productLoadTunerForUpdate the <code>ProductLoadTuner</code>
	 */
	public void setProductLoadTunerForUpdate(final ProductLoadTuner productLoadTunerForUpdate) {
		this.productLoadTunerForUpdate = productLoadTunerForUpdate;
	}

	/**
	 * Sets the <code>ProductLoadTuner</code> for populating a minimal data set.
	 *
	 * @param productLoadTunerMinimal the <code>ProductLoadTuner</code>
	 */
	public void setProductLoadTunerMinimal(final ProductLoadTuner productLoadTunerMinimal) {
		this.productLoadTunerMinimal = productLoadTunerMinimal;
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
	 * @return TimeService
	 */
	protected TimeService getTimeService() {
		return timeService;
	}

	/**
	 * {@inheritDoc}
	 * @deprecated Use the QueryService instead.
	 */
	@Override
	@Deprecated
    public long findUidBySkuCode(final String skuCode) {

        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("skuCode", skuCode);
        final List<Long> results = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_UID_FOR_SKU_CODE", parameters);
        if (results.isEmpty()) {
                    return 0;
        }
        return results.get(0);

    }

}
