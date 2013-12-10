package com.elasticpath.domain.catalogview.browsing;

import java.util.List;

import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.StoreProduct;

/**
 * Represents a catalog browsing result.
 */
public interface BrowsingResult extends CatalogViewResult {

	/**
	 * Returns a list of top selling products.
	 *
	 * @return a list of top selling products
	 */
	List<StoreProduct> getTopSellers();

	/**
	 * Sets a list of top selling products.
	 *
	 * @param topSellers a list of top selling products
	 */
	void setTopSellers(final List<StoreProduct> topSellers);

}
