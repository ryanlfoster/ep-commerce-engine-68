package com.elasticpath.domain.shopper;

/**
 * Persistent data for the CustomerSession.
 */
public interface ShopperKey {

	/**
	 * Gets the UID of the referenced {@link com.elasticpath.domain.shopper.Shopper}.
	 *
	 * @return a uid.
	 */
	long getShopperUid();

	/**
	 * Sets the UID of the referenced {@link com.elasticpath.domain.shopper.Shopper}.
	 * 
	 * @param shopperUid the new shopper Uid.
	 */
	void setShopperUid(long shopperUid);

}