package com.elasticpath.service.pricing;

import com.elasticpath.domain.catalog.Price;

/**
 * Interface representing an object that is priced.
 */
public interface Priced {
	/**
	 * Get the price of this object.
	 * 
	 * @return the price
	 */
	Price getPrice();
}
