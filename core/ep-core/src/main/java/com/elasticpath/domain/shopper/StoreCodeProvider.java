package com.elasticpath.domain.shopper;

/**
 * Provides a Store Code for a particular {@link com.elasticpath.domain.store.Store}.
 */
public interface StoreCodeProvider {

	/**
	 * Returns the storeCode for a particular {@link com.elasticpath.domain.store.Store}.
	 *
	 * @return storeCode as a string.
	 */
	String getStoreCode();


	/**
	 * Sets the store code.
	 *
	 * @param storeCode the new store code
	 */
	void setStoreCode(String storeCode);
}
