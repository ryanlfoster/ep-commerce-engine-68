/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.search;

/**
 * Indicates the type of result from a sku search.
 */
public enum SkuSearchResultType {

	/**
	 * A SKU of a multi-sku product.
	 */
	PRODUCT_SKU(1),
	
	/**
	 * A single-sku product.
	 */
	PRODUCT(2),
	
	/**
	 * A single-sku product bundle. 
	 */
	PRODUCT_BUNDLE(3);
	
	private final int sortOrder;
	
	private SkuSearchResultType(final int sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	/**
	 * Get the sort order.
	 * 
	 * @return the sort order
	 */
	public int getSortOrder() {
		return sortOrder;
	}
	
}
