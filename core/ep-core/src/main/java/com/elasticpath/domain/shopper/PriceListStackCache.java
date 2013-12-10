package com.elasticpath.domain.shopper;

import com.elasticpath.domain.pricing.PriceListStack;

/**
 * Provides access to a cached instance of a {@link PriceListStack}.
 */
public interface PriceListStackCache {

	/**
	 * Returns cached {@link PriceListStack}.
	 *
	 * @return a {@link PriceListStack}.
	 */
	PriceListStack getPriceListStack();
	
	/**
	 * Updates the {@link PriceListStack}.
	 *
	 * @param priceListStack the new updated {@link PriceListStack} to cache.
	 */
	void setPriceListStack(PriceListStack priceListStack);
	
	/**
	 * Get the validity flag for the {@link PriceListStack}.
	 * 
	 * @return false if need to get fresh {@link PriceListStack}.
	 */
	boolean isPriceListStackValid();
	
}
