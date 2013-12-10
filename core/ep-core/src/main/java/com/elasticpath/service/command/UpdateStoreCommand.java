package com.elasticpath.service.command;

import java.util.Map;

import com.elasticpath.domain.store.Store;

/**
 * Updates store and related settings.
 */
public interface UpdateStoreCommand extends Command {

	/**
	 * Sets the store to be updated.
	 * 
	 * @param store the store to update during execution
	 */
	void setStore(final Store store);

	/**
	 * Sets setting values to be updated.
	 * 
	 * @param settingValueMap map between setting keys and values
	 */
	void setSettingValues(final Map<String, String> settingValueMap);
}
