/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.commons.util.impl.DateUtils;
import com.elasticpath.domain.catalog.TopSeller;
import com.elasticpath.domain.catalog.TopSellerProduct;
import com.elasticpath.persistence.PropertiesDao;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.TopSellerService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.settings.SettingsReader;

/**
 * The default implementation of <code>TopSellerService</code>.
 */
public class TopSellerServiceImpl extends AbstractEpPersistenceServiceImpl implements TopSellerService {
	private static final Logger LOG = Logger.getLogger(TopSellerServiceImpl.class);

	private PropertiesDao propertiesDao;

	private ProductService productService;

	private CategoryService categoryService;

	private OrderService orderService;
	
	private SettingsReader settingsReader;

	private Properties topSellerProperties;

	private Date currentDate;

	private static final String TOP_SELLER_PROP_FILE = "topSeller";

	private static final String LAST_PROCESSED_DATE_PROP = "LastProcessedDate";

	private static final String PLACEHOLDER_FOR_LIST = "list";

	private TimeService timeService;

	private Collection<Long> affectedProductUids;

	/**
	 * Calculate the number of sales for a particular product and update its sales count. Also populate top sellers for each category and the whole
	 * store.
	 */
	@Override
	public void updateTopSellers() {
		final long startTime = System.currentTimeMillis();
		LOG.info("Start topseller quartz job at: " + new Date(startTime));
		
		if (calculateSalesCount()) {
			// populate top sellers for each category
			updateTopSellersForCategories();

			// populate top sellers for the whole store
			updateTopSellersForTheStore();
		}
		setLastProcessedDate();

		LOG.info("Topseller update quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * Calculate the number of sales for a product and update its sales count based on the number of items ordered in the order.
	 *
	 * @return <code>true</code> if has updates
	 */
	@Override
	public boolean calculateSalesCount() {
		boolean needUpdate = false;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Start calculating sales count for products.");
		}
		initialize();
		// get the last processed date
		Date lastProcessedDate = getLastProcessedDate();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Last processed date: " + lastProcessedDate);
		}
		if (lastProcessedDate == null) {
			lastProcessedDate = new Date(0);
		}
		List<Object[]> products = calculateProductOrderCountMap(lastProcessedDate);
		if (!products.isEmpty()) {
			affectedProductUids = updateProductSalesCount(products);
			needUpdate = true;
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("End calculating sales count for products.");
		}
		return needUpdate;
	}

	/**
	 * Populate top sellers for the store.
	 */
	@Override
	public void updateTopSellersForTheStore() {
		long startTime = System.currentTimeMillis();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Start updating top sellers for the whole store.");
		}
		final List<Object[]> topSellers = this.findProductTopSellerData(getMaxTopSellerCount());
		populateTopSellerCategory(0L, topSellers);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Updated top sellers for the whole store for (ms): " + (System.currentTimeMillis() - startTime));
		}
	}

	/**
	 * Gets the maximum count of top sellers per category.
	 * 
	 * @return an integer value
	 */
	protected int getMaxTopSellerCount() {
		return Integer.parseInt(getSettingsReader().getSettingValue("COMMERCE/SYSTEM/CATALOG/catalogTopSellerCount", StringUtils.EMPTY).getValue());
	}

	/**
	 * Populate top sellers for each categories.
	 */
	@Override
	public void updateTopSellersForCategories() {
		long startTime = System.currentTimeMillis();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Start updating top sellers for categories.");
		}
		final List<Long> categoryUids = this.categoryService.findAvailableUids();
		for (final Long categoryUid : categoryUids) {

			List<Object[]> topSellingProductUids = this.findProductUidsTopSellerForCategory(categoryUid);
			final List<Object[]> topSellingProducts = new ArrayList<Object[]>();
			
			// get only the first few to process
			topSellingProductUids = topSellingProductUids.subList(0, Math.min(getMaxTopSellerCount(), topSellingProductUids.size()));
			
			for (Object[] productData : topSellingProductUids) {
				if (affectedProductUids != null && !affectedProductUids.contains(productData[1])) {
					continue;
				}
				topSellingProducts.add(productData);
			}
			populateTopSellerCategory(categoryUid, topSellingProducts);
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("End updating top sellers for categories for (ms): " + (System.currentTimeMillis() - startTime));
		}
	}

	private List<Object[]> findProductUidsTopSellerForCategory(final long categoryUid) throws EpServiceException {
		sanityCheck();
		final List<Long> categoryUids = this.categoryService.findDescendantCategoryUids(categoryUid);
		categoryUids.add(categoryUid);

		final List<Object[]> productUids = getPersistenceEngine().retrieveByNamedQueryWithList("PRODUCT_SELECT_SALES_COUNT_AND_CATEGORY_UIDS",
				PLACEHOLDER_FOR_LIST, categoryUids);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Found " + productUids.size() + " top selling products for category -- " + categoryUid);
		}
		return productUids;
	}
	
	/**
	 * Retrieve a list of top sellers of the whole store.
	 * 
	 * @param topCount the number of top seller products to retrieve
	 * @return the list of top sellers of the whole store
	 * @throws EpServiceException in case of any error
	 */
	protected List<Object[]> findProductTopSellerData(final int topCount) throws EpServiceException {
		final List<Object[]> result = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_DATA_SELECT_TOP_SELLERS", 0, topCount);

		return result;
	}

	/**
	 * Get map of Products and number of sales since a specific date.
	 *
	 * @param startDate Date to start calculation from
	 * @param numResults The maximum number of products to return
	 * @return map of product and associated number of sales, sorted by sale numbers
	 */
	@Override
	public ListOrderedMap getTopProductsFromDate(final Date startDate, final int numResults) {
		// retrieve orders since start date
		List<Object[]> counts = calculateProductOrderCountMap(startDate);

		// Get a list of the entries in the map
		List<Object[]> sortedResults = new ArrayList<Object[]>(counts);

		// Sort the list according to count value
		Collections.sort(sortedResults, new Comparator<Object[]>() {
			@Override
			public int compare(final Object[] entry1, final Object[] entry2) {
				int count1 = Integer.parseInt(entry1[0].toString());
				int count2 = Integer.parseInt(entry2[0].toString());

				return count2 - count1;
			}
		});

		// Build the sorted map of product uid -> sales count
		ListOrderedMap results = new ListOrderedMap();
		int toIndex = Math.min(sortedResults.size(), Math.max(numResults, 0));
		for (Object[] entry : sortedResults.subList(0, toIndex)) {
			results.put(entry[1], entry[0]);
		}

		return results;
	}

	/**
	 * Get map of leaf Categories and number of sales since a specific date.
	 *
	 * @param startDate Date to start calculation from
	 * @param numResults The maximum number of products to return
	 * @return map of categories and associated number of sales in an array, sorted by sale numbers
	 */
	@Override
	public ListOrderedMap getTopCategoriesFromDate(final Date startDate, final int numResults) {
		// retrieve orders since start date
		Map<Long, Integer> counts = calculateCategoryOrderCountMap(startDate);

		// Get a list of the entries in the map
		List<Entry<Long, Integer>> sortedResults = new ArrayList<Entry<Long, Integer>>(counts.entrySet());

		// Sort the list according to count value
		Collections.sort(sortedResults, new Comparator<Entry<Long, Integer>>() {
			@Override
			public int compare(final Entry<Long, Integer> entry1, final Entry<Long, Integer> entry2) {
				int count1 = entry1.getValue();
				int count2 = entry2.getValue();

				return count2 - count1;
			}
		});

		ListOrderedMap results = new ListOrderedMap();
		// copy from list of Map.Entry to list of [productUid, count] arrays.
		int toIndex = Math.min(sortedResults.size(), Math.max(numResults, 0));
		for (Entry<Long, Integer> entry : sortedResults.subList(0, toIndex)) {
			results.put(entry.getKey(), entry.getValue());
		}
		return results;
	}

	/**
	 * Save or update the given top seller.
	 *
	 * @param topSeller the category to save or update
	 * @return the updated top seller instance
	 * @throws EpServiceException - in case of any errors
	 */
	public TopSeller saveOrUpdate(final TopSeller topSeller) throws EpServiceException {
		return getPersistenceEngine().saveOrMerge(topSeller);
	}

	/**
	 * Populates the given TopSeller category with a list of new top-selling products.
	 * If any of the products are already in the given category their salesCount will be reset,
	 * otherwise a new TopSellerProduct will be added to the category.
	 * @param categoryUid the TopSeller category that will be populated
	 * @param newTopSellers list of top-selling products to be added/updated in the given category
	 */	
	void populateTopSellerCategory(final long categoryUid, final List<Object[]> newTopSellers) {
		if (newTopSellers.isEmpty()) {
			return;
		}
		final TopSeller topSellerCategory = loadTopSellerCategory(categoryUid);

		for (final Object[] productData : newTopSellers) {
			final int salesCount = Integer.parseInt(productData[0].toString());
			long productUid = Long.parseLong(productData[1].toString());
			if (topSellerCategory.getProductUids().contains(productUid)) {
				topSellerCategory.getTopSellerProducts().get(productUid).setSalesCount(salesCount);
			} else {
				TopSellerProduct topSellerProduct = getBean(ContextIdNames.TOP_SELLER_PRODUCT);
				topSellerProduct.setProductUid(productUid);
				topSellerProduct.setSalesCount(salesCount);
				topSellerCategory.getTopSellerProducts().put(topSellerProduct.getProductUid(), topSellerProduct);				
			}
		}
		
		saveOrUpdate(topSellerCategory);
	}

	/**
	 * Get the topSeller in the category with the given UID.
	 * @param categoryUid the UID of the category from which to return the top seller
	 * @return the requested top seller
	 */
	TopSeller loadTopSellerCategory(final Long categoryUid) {
		final List<TopSeller> result = getPersistenceEngine().retrieveByNamedQuery("TOP_SELLER_SELECT_BY_CATEGORY_UID", categoryUid);
		final int size = result.size();
		if (size > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate category uid:" + categoryUid);
		}
		if (size == 0) {
			TopSeller topSeller = getBean(ContextIdNames.TOP_SELLER);
			topSeller.setCategoryUid(categoryUid);
			return topSeller;
		}
		return result.get(0);
	}

	/**
	 * Populates a map with all the products that have been checked out in any order.
	 * 
	 * @param lastProcessedDate the last processed date
	 * @return a map of product
	 */
	protected List<Object[]> calculateProductOrderCountMap(final Date lastProcessedDate) {
		long startTime = System.currentTimeMillis();
		
		List<Object[]> result = retrieveSoldProductsCount(lastProcessedDate);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Number of products found: " + result.size() + " for (ms): " + (System.currentTimeMillis() - startTime));
		}	
		return result;
	}

	/**
	 * Retrieves the count of all the products sold starting from lastProcessedDate.
	 * 
	 * @param lastProcessedDate the date to start from
	 * @return a list of arrays of length 2 - [the total count of a product, product UID] 
	 */
	protected List<Object[]> retrieveSoldProductsCount(final Date lastProcessedDate) {
		return getPersistenceEngine().retrieveByNamedQuery("COUNT_ORDER_SKU_BY_ORDER_DATE", lastProcessedDate);
	}

	private Map<Long, Integer> calculateCategoryOrderCountMap(final Date startDate) {
		final Map<Long, Integer> categoryMap = new HashMap<Long, Integer>();
		
		List<Object[]> result = retrieveSoldProductsCount(startDate);
		
		for (Object[] productData : result) {
			final int count = Integer.parseInt(String.valueOf(productData[0]));
			final long productUid = Long.parseLong(String.valueOf(productData[1]));

			List<Long> categoryUids = getPersistenceEngine().retrieveByNamedQuery("SELECT_CATEGORY_UID_BY_PRODUCT_UID", productUid);
			// loop through the categories
			for (Long categoryUid : categoryUids) {
				if (categoryMap.containsKey(categoryUid)) {
					// check whether category already exists in map, if it
					// does, add the count
					final Integer countInt = categoryMap.get(categoryUid);
					categoryMap.put(categoryUid, countInt + count);
				} else {
					// else add the category and count to the map
					categoryMap.put(categoryUid, count);
				}
			}
		}
		return categoryMap;
	}

	/**
	 * Updates the product sales count per product iterating through all the products in the map.
	 * 
	 * @param products the product list [uidPk, count]
	 * @return list of affected product UIDs
	 */
	protected Collection<Long> updateProductSalesCount(final List<Object[]> products) {
		final long startTime = System.currentTimeMillis();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Start updating products' sales counts");
		}
		List<Long> affectedProductUids = new ArrayList<Long>();
		for (final Object[] entry : products) {
			final long productUid = Long.parseLong(entry[1].toString());
			final int count = Integer.parseInt(entry[0].toString());

			final List<Integer> value = getPersistenceEngine().retrieveByNamedQuery("SELECT_PRODUCT_SALES_COUNT", productUid);
			
			if (value.isEmpty()) {
				LOG.warn("An OrderSku references a product that cannot be found. Product's UID: " + productUid);
			} else {
				final Date currentTime = timeService.getCurrentTime();
				getPersistenceEngine().executeNamedQuery("UPDATE_PRODUCT_SALES_COUNT_BY",
						value.get(0) + count, currentTime, productUid);
				affectedProductUids.add(productUid);
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Product sales count updated for (ms): " + (System.currentTimeMillis() - startTime));
		}	
		return affectedProductUids;
	}

	private void initialize() {
		sanityCheck();
		currentDate = this.timeService.getCurrentTime();
		topSellerProperties = propertiesDao.getPropertiesFile(TOP_SELLER_PROP_FILE);
	}

	private Date getLastProcessedDate() {
		Date date = null;
		final String dateString = topSellerProperties.getProperty(LAST_PROCESSED_DATE_PROP);
		if (dateString != null && dateString.length() > 0) {
			date = ConverterUtils.string2Date(dateString, DateUtils.DATE_TIME_FORMAT_STRING_US_INTERNAL, Locale.US);
		}
		return date;
	}

	private void setLastProcessedDate() {
		topSellerProperties.setProperty(LAST_PROCESSED_DATE_PROP,
				ConverterUtils.date2String(currentDate, DateUtils.DATE_TIME_FORMAT_STRING_US_INTERNAL, Locale.US));
		propertiesDao.storePropertiesFile(topSellerProperties, TOP_SELLER_PROP_FILE);
	}

	/**
	 * Checks that the required objects have been set.
	 */
	@Override
	protected void sanityCheck() {
		if (propertiesDao == null) {
			throw new EpServiceException("The properties dao has not been set.");
		}
		if (productService == null) {
			throw new EpServiceException("The product service has not been set.");
		}
		if (orderService == null) {
			throw new EpServiceException("The order service has not been set.");
		}
		if (getSettingsReader() == null) {
			throw new EpServiceException("The settings reader has not been set.");
		}
	}

	/**
	 * Sets the DAO used to load properties.
	 *
	 * @param propertiesDao the properties DAO used to retrieve properties information
	 */
	public void setPropertiesDao(final PropertiesDao propertiesDao) {
		this.propertiesDao = propertiesDao;
	}

	/**
	 * Set the order service.
	 *
	 * @param orderService the order service
	 */
	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * Set the product service.
	 *
	 * @param productService the product service
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Set the category service.
	 *
	 * @param categoryService the category service
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * Get a persistent instance with the given id.
	 *
	 * @param uid the persistent instance uid
	 * @return the persistent instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Object getObject(final long uid) throws EpServiceException {
		throw new EpUnsupportedOperationException("Not supported.");
	}

	/**
	 * Find top sellers of the given category uid. Give 0 to find top sellers for the whole store.
	 *
	 * @param categoryUid the category uid, give 0 to find top sellers for the whole store
	 * @return a <code>TopSeller</code> if found, otherwise <code>null</code>
	 */
	@Override
	public TopSeller findTopSellerByCategoryUid(final long categoryUid) {
		final List<TopSeller> result = getPersistenceEngine().retrieveByNamedQuery("TOP_SELLER_SELECT_BY_CATEGORY_UID", new Long(categoryUid));
		final int size = result.size();
		if (size > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate category uid used in top seller:" + categoryUid);
		}
		if (size == 0) {
			return null;
		}

		return result.get(0);
	}

	/**
	 * Returns the time service.
	 *
	 * @return the time service.
	 */
	protected TimeService getTimeService() {
		return timeService;
	}

	/**
	 * Sets the time service.
	 *
	 * @param timeService the time service
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * @return the settingsReader
	 */
	public SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * @param settingsReader the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
}
