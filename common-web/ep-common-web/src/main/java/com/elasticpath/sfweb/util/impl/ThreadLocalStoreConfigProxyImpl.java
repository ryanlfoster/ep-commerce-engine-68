/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.util.impl;

import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.catalogview.impl.ThreadLocalStorageImpl;

/**
 * Used to get the current instance of the {@link ThreadLocalStorageImpl} from classes
 * that cannot be instantiated using spring.
 */
public class ThreadLocalStoreConfigProxyImpl extends ThreadLocalStorageImpl {

	private static StoreConfig instance = new ThreadLocalStoreConfigProxyImpl();

	/**
	 * A factory method to get the storeConfig instance.
	 * 
	 * @return the StoreConfig instance
	 */
	public static StoreConfig getInstance() {
		return instance;
	}

	/**
	 * Sets the store config.
	 * 
	 * @param storeConfig the StoreConfig instance
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		instance = storeConfig;
	}

}
