/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.view.helpers;

import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Helper for determining a store-specific setting. This class retrieves
 * settings using the SettingsReader.
 */
public class PerStoreSettingHelper {
	
	private String path;
	
	private StoreConfig storeConfig;
	
	private SettingsReader settingsReader;
	
	/**
	 * Retrieves and returns a boolean setting using the SettingsReader.
	 * 
	 * @return The setting value associated with the <code>storeCode</code>.
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getBoolean() {
		// Attempt to get the store code from the store config
		String storeCode = null;
		if (storeConfig != null) {
			storeCode = storeConfig.getStoreCode();
		}
		SettingValue returnedValue = settingsReader.getSettingValue(path, storeCode);
		return returnedValue.getBooleanValue();
	}
	
	/**
	 * Retrieves and returns a integer setting using the SettingsReader.
	 * 
	 * @return The setting value associated with the <code>storeCode</code>.
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public int getInteger() {
		// Attempt to get the store code from the store config
		String storeCode = null;
		if (storeConfig != null) {
			storeCode = storeConfig.getStoreCode();
		}
		SettingValue returnedValue = settingsReader.getSettingValue(path, storeCode);
		return returnedValue.getIntegerValue();
	}
	
	
	/**
	 * Sets the store config instance.
	 * 
	 * @param storeConfig the store config
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}
	
	/**
	 * Sets the settings reader.
	 * 
	 * @param settingsReader the settings reader
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
	
	/**
	 * Sets the path.
	 * @param path the path of the setting.
	 */
	public void setPath(final String path) {
		this.path = path;
	}
}
