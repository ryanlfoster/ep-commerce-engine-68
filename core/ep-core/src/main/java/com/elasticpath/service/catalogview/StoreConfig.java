package com.elasticpath.service.catalogview;

import com.elasticpath.domain.store.Store;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Encapsulates a store configuration.
 */
public interface StoreConfig {

	/**
	 * Returns the store object associated with this configuration.
	 * 
	 * @return the store associated with this store configuration.
	 * @throws com.elasticpath.base.exception.EpServiceException if the Store has not been set
	 */
	Store getStore();
	
	/**
	 * Returns the code for the Store associated with this configuration.
	 * 
	 * @return the code for the store associated with this store configuration
	 */
	String getStoreCode();

	/**
	 * Get the setting identified by the given path for the store associated with this configuration.
	 * @param path the unique identifier to the setting definition
	 * @return the requested setting value
	 */
	SettingValue getSetting(String path);

}
