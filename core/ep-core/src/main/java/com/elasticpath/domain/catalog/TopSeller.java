/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import java.util.Collection;
import java.util.Map;

import com.elasticpath.persistence.api.Persistable;

/**
 * <code>TopSeller</code> represents a category for grouping top sellers.
 */
public interface TopSeller extends Persistable {

	/**
	 * Returns the category uid.
	 * 
	 * @return the category uid
	 */
	long getCategoryUid();

	/**
	 * Sets the category uid.
	 * 
	 * @param categoryUid the category uid
	 */
	void setCategoryUid(long categoryUid);

	/**
	 * Returns the top seller products.
	 * 
	 * @return the top seller products
	 */
	Map<Long, TopSellerProduct> getTopSellerProducts();

	/**
	 * Sets the top seller products.
	 * 
	 * @param topSellerProducts the top seller products
	 */
	void setTopSellerProducts(final Map<Long, TopSellerProduct> topSellerProducts);

	/**
	 * Returns a collection of top selling products uids.
	 * 
	 * @return a collection of top selling products uids
	 */
	Collection<Long> getProductUids();
}