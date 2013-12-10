/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog;

import java.util.Date;

import org.apache.commons.collections.map.ListOrderedMap;

import com.elasticpath.domain.catalog.TopSeller;
import com.elasticpath.service.EpPersistenceService;

/**
 * Provides services to process top seller related information.
 */
public interface TopSellerService extends EpPersistenceService {
	/**
	 * Calculate the number of sales for a particular product and update its
	 * sales count. Also populate top sellers for each category and the whole
	 * store.
	 */
	void updateTopSellers();

	/**
	 * Calculate the number of sales for a product and update its sales count
	 * based on the number of items ordered in the order.
	 *
	 * @return <code>true</code> if has updates
	 */
	boolean calculateSalesCount();

	/**
	 * Get map of Products and number of sales since a specific date.
	 *
	 * @param startDate Date to start calculation from
	 * @param numResults The maximum number of products to return
	 * @return map of product and associated number of sales, sorted by sale numbers
	 */
	ListOrderedMap getTopProductsFromDate(final Date startDate, final int numResults);

	/**
	 * Get map of leaf Categories and number of sales since a specific date.
	 *
	 * @param startDate Date to start calculation from
	 * @param numResults The maximum number of products to return
	 * @return map of categories and associated number of sales, sorted by sale numbers
	 */
	ListOrderedMap getTopCategoriesFromDate(final Date startDate, final int numResults);

	/**
	 * Populate top sellers for the store.
	 */
	void updateTopSellersForTheStore();

	/**
	 * Populate top sellers for each categories.
	 */
	void updateTopSellersForCategories();

	/**
	 * Find top sellers of the given category uid. Give 0 to find top sellers
	 * for the whole store.
	 *
	 * @param categoryUid
	 *            the category uid, give 0 to find top sellers for the whole
	 *            store
	 * @return a <code>TopSeller</code> if found, otherwise <code>null</code>
	 */
	TopSeller findTopSellerByCategoryUid(long categoryUid);
}
