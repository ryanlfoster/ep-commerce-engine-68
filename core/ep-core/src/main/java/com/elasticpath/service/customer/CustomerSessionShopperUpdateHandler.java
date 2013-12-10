package com.elasticpath.service.customer;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;

/**
 * The customer session listener interface. 
 */
public interface CustomerSessionShopperUpdateHandler {
	
	/**
	 * The method to be triggered after {@link Shopper} is updated.
	 * 
	 * @param customerSession the with the new {@link Shopper}.
	 * @param oldShopper the old {@link Shopper}.
	 */
	void invalidateShopper(CustomerSession customerSession, Shopper oldShopper);
}
