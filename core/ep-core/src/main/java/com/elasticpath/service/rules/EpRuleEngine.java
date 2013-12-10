/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules;

import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.EpService;

/**
 * Executes rules-engine rules on objects passed as parameters to this class.
 */
public interface EpRuleEngine extends EpService {

	/**
	 * Executes promotion rules for the given products.
	 * 
	 * @param products the product list whose promotion price is to be computed
	 * @param ruleTracker the set of ids tracking what rules have been run on. Note: it has to be a mutable set, otherwise rules won't be updated.
	 * @param activeCurrency the active currency of the shopping context
	 * @param store the {@link Store} the rules will be fired against. The rules will be fired on the catalog related to that store.
	 * @param prices the map of product code to its list of prices
	 */
	void fireCatalogPromotionRules(final Collection< ? extends Product> products, 
			final Set <Long> ruleTracker, final Currency activeCurrency, Store store, Map <String, List <Price>> prices);

	/**
	 * Executes order promotion rules on the specified shopping cart. Only rules affecting
	 * cart item discounts are fired by this method. To fire cart subtotal discount rules call
	 * fireOrderPromotionSubtotalRules.
	 * 
	 * @param shoppingCart the cart to which promotion rules are to be applied.
	 */
	void fireOrderPromotionRules(final ShoppingCart shoppingCart);
	
	/**
	 * Executes order promotion rules on the specified shopping cart. Only rules affecting
	 * cart subtotal discounts are fired by this method. To fire cart item discount rules call
	 * fireOrderPromotionRules.
	 * 
	 * @param shoppingCart the cart to which promotion rules are to be applied.
	 */
	void fireOrderPromotionSubtotalRules(final ShoppingCart shoppingCart);
}
