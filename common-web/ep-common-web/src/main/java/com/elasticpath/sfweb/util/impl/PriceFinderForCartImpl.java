/**
 *
 */
package com.elasticpath.sfweb.util.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sfweb.util.PriceFinderForCart;

/**
 * Provides price lookup methods given context (shopping cart).
 *
 */
public class PriceFinderForCartImpl implements PriceFinderForCart {

	private PriceLookupFacade priceLookupFacade;


	@Override
	public Map<String, Price> findPrices(final Collection<StoreProduct> products, final Shopper shopper) {
		if (products == null) {
			return Collections.emptyMap();
		}
		return getPriceLookupFacade().getPromotedPricesForProducts(
				new ArrayList<Product>(products),
				shopper.getCurrentShoppingCart().getStore(),
				shopper,
				shopper.getCurrentShoppingCart().getAppliedRules());
	}


	@Override
	public Map<String, Boolean> findAdjustments(final Collection<StoreProduct> products, final Shopper shopper) {
		Map<String, Boolean> adjustmentsMap = new HashMap<String, Boolean>();
		if (products == null) {
			return adjustmentsMap;
		}
		for (StoreProduct product : products) {
			Product prod = product.getWrappedProduct();
			if (prod instanceof ProductBundle) {
				Store store = shopper.getCurrentShoppingCart().getStore();
				Map<String, PriceAdjustment> priceAdjustmentsForBundle = getPriceLookupFacade().getPriceAdjustmentsForBundle(
						(ProductBundle) prod, store.getCatalog().getCode(), shopper);
				if (!priceAdjustmentsForBundle.isEmpty()) {
					adjustmentsMap.put(prod.getCode(), true);
				}
			}
		}
		return adjustmentsMap;
	}

	@Override
	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}

	@Override
	public PriceLookupFacade getPriceLookupFacade() {
		return priceLookupFacade;
	}
}