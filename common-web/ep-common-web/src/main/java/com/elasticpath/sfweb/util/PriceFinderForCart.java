package com.elasticpath.sfweb.util;

import java.util.Collection;
import java.util.Map;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shopper.Shopper;

/**
 * Interface that provides convenience methods for fetching prices.
 *
 */
public interface PriceFinderForCart {

	/**
	 * Finds the prices of the given StoreProducts and applies any promotions to these prices.
	 * @param products The StoreProducts
	 * @param shopper The session
	 * @return A map of product codes as keys and the value as its lowest price. Returns empty map if
	 * no products are found
	 */
	Map<String, Price> findPrices(final Collection<StoreProduct> products,  final Shopper shopper);

	/**
	 * Returns a map with adjustments.
	 * @param products The products to query
	 * @param shopper The session
	 * @return A map with product codes as keys and a boolean as value specifying whether the product
	 * has any adjustments. Returns an empty map if none found.
	 */
	Map<String, Boolean> findAdjustments(final Collection<StoreProduct> products, final Shopper shopper);

	/**
	 * @param priceLookupFacade the priceLookupFacade to set
	 */
	void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade);

	/**
	 * @return the priceLookupFacade
	 */
	PriceLookupFacade getPriceLookupFacade();

}