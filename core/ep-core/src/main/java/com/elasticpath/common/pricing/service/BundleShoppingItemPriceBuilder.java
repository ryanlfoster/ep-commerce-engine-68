package com.elasticpath.common.pricing.service;

import java.util.Set;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;

/**
 *  A strategy interface to build bundle shopping item price.
 *  There are two different implementations now: one for assigned bundle, the other for calculated bundle.
 */
public interface BundleShoppingItemPriceBuilder {

	/**
	 * Builds the <code>Price</code> object for the bundle shopping item.
	 * The result price is promoted and adjusted.
	 *
	 * @param bundleShoppingItem ShoppingItem.
	 * @param shopper CustomerSession.
	 * @param store the store
	 * @param ruleTracker the rule tracker
	 * @return Price.
	 */
	Price build(final ShoppingItem bundleShoppingItem, final Shopper shopper, Store store, Set<Long> ruleTracker);

}