package com.elasticpath.domain.shopper;

/**
 * Persistent data for the CustomerSession.
 */
public interface ShopperReference {

	/**
	 * Gets the referenced {@link Shopper}.
	 *
	 * @return a Shopper.
	 */
	Shopper getShopper();

	/**
	 * Sets the referenced {@link Shopper}.
	 * 
	 * @param shopper the new shopper.
	 */
	void setShopper(Shopper shopper);

}