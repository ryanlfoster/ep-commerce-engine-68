/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.catalogview;

import com.elasticpath.domain.catalog.Product;

/**
 * Product extension to Product used by indexing service.
 */
public interface IndexProduct extends Product {

	
	/**
	 * Returns <code>true</code> if the product is available for purchase from the given
	 * storeCode. Checks whether the current date is within the product's date range and that
	 * the product has at least one SKU in stock.
	 * 
	 * @param storeCode the store's code to check stock for
	 * @return <code>true</code> if the product is available for purchase, <code>false</code>
	 *         otherwise
	 */
	boolean isAvailable(final String storeCode);

	/**
	 * Returns <code>true</code> if the product can be displayed for the given storeCode.
	 * Checks whether the product is not hidden, current date is within the product's date range
	 * and that the product has at least one SKU in stock or is out of stock but should be
	 * visible.
	 * 
	 * @param storeCode the storeCode to check stock for
	 * @return <code>true</code> if the product is available for purchase, <code>false</code>
	 *         otherwise
	 */
	boolean isDisplayable(final String storeCode);
	
	/**
	 *
	 * @return wrapped product.
	 */
	Product getWrappedProduct();
}
