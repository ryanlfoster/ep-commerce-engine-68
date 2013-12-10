package com.elasticpath.domain.shoppingcart;

import java.util.List;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.catalog.Product;

/**
 * This class represents a collection of products viewed by a user.
 */
public interface ViewHistory extends EpDomain {

	/**
	 * Adds a product to the viewHistory.
	 *
	 * @param product the <code>Product</code> to be added
	 */
	void addProduct(final Product product);

	/**
	 * Get the most recently viewed product.
	 * @return a <code>ViewHistoryProduct</code> representing the most recently viewed product
	 */
	ViewHistoryProduct getLastViewedHistoryProduct();

	/**
	 * Get a list of the most recently viewed products in the view history.
	 * @return a <code>List</code> of <code>ViewHistoryProduct</code>s
	 */
	List<ViewHistoryProduct> getViewedProducts();
}
