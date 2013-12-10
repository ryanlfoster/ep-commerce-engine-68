package com.elasticpath.service.catalogview.impl;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.domain.store.Store;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.domain.SettingValue;

/**
 * A store configuration that provides a store based on the store previously
 * selected for the current thread.
 */
public class ThreadLocalStorageImpl implements StoreConfig {
	
	private static final long DEFAULT_CACHE_TIMEOUT_VALUE = 20000;

	private StoreService storeService;
	
	private final ThreadLocal<String> tlStoreCode = new ThreadLocal<String>();
	
	private final SimpleTimeoutCache<String, Store> storeCache = new SimpleTimeoutCache<String, Store>(DEFAULT_CACHE_TIMEOUT_VALUE);
	
	private SettingsReader settingsService;
		
	/**
	 * Returns the store for the current thread.
	 * 
	 * This implementation only retrieves the store once from the storeService
	 * and then returns a local copy of it on subsequent calls.
	 * Calls {@link #getStoreCode()}.
	 * 
	 * @return The appropriate store for the current application.
	 * @throws EpServiceException if a store cannot be found or the store code has not been
	 *         set for this thread.
	 */
	public Store getStore() {
		
		String storeCode = getStoreCode();
		if (storeCode == null) {
			throw new EpServiceException("Store code has not been set");
		}
		
		Store store = storeCache.get(storeCode);

		if (store == null) {
			store = storeService.findStoreWithCode(tlStoreCode.get());
			storeCache.put(storeCode, store);
		}
		
		if (store == null) {
			throw new EpServiceException("Store not found.");
		}
		
		return store;
	}
	
	/**
	 * Returns the code for the Store associated with this configuration.
	 * 
	 * @return the code for the store associated with this store configuration
	 */
	public String getStoreCode() {
		return tlStoreCode.get();
	}

	/**
	 * Sets the storeCode for the current thread to be used by getStore() to return the store.
	 * @param storeCode the storeCode for the current thread.
	 */
	public void setStoreCode(final String storeCode) {
		tlStoreCode.set(storeCode);
	}
	
	/**
	 * @param storeService the service from which to retrieve the store.
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}	
	
	/**
	 * @param settingsService the service from which to retrieve settings for the threadlocal store.
	 */
	public void setSettingsService(final SettingsReader settingsService) {
		this.settingsService = settingsService;
	}

	/**
	 * Sets the cache timeout in milliseconds.
	 *
	 * @param cacheTimeoutMillis the cacheTimeoutMillis to set
	 */
	public void setCacheTimeoutMillis(final long cacheTimeoutMillis) {
		storeCache.setTimeout(cacheTimeoutMillis);
	}
	
	/**
	 * Get the setting identified by the given path for the store associated with this configuration.
	 * @param path the unique identifier to the setting definition
	 * @return the requested setting value, or null if it cannot be found
	 * @throws EpServiceException on error
	 */
	public SettingValue getSetting(final String path) {
		if (getStoreCode() == null) {
			throw new EpServiceException("StoreCode has not been set in this thread.");
		}
		return settingsService.getSettingValue(path, getStoreCode());
	}

}
