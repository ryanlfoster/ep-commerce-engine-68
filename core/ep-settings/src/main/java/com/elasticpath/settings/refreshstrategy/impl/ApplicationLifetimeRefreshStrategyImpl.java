/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.settings.refreshstrategy.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Timeout cache type implementation of <code>SettingRefreshStrategy</code>.
 */
public class ApplicationLifetimeRefreshStrategyImpl implements SettingRefreshStrategy {

	private SettingsReader settingsReader;

	private static Map<String, SettingValue> cacheMap = new HashMap<String, SettingValue>();

	/**
	 * Retrieve the setting value for the given path and context from the timeout cache.
	 * 
	 * @param path the setting path
	 * @param context the setting context
	 * @param params the setting metadata
	 * @return the setting value
	 */
	public SettingValue retrieveSetting(final String path, final String context, final String params) {
		SettingValue value = getCache().get(path + context);

		// put the setting in the timeout cache
		if (value == null) {
			value = getSettingsReader().getSettingValue(path, context);
			getCache().put(path + context, value);
		}

		return value;
	}

	/**
	 * Retrieve the setting value for the given path from the timeout cache.
	 * 
	 * @param path the setting path
	 * @param params the setting metadata
	 * @return the setting value
	 */
	public SettingValue retrieveSetting(final String path, final String params) {
		SettingValue value = getCache().get(path);

		// put the setting in the timeout cache
		if (value == null) {
			value = getSettingsReader().getSettingValue(path);
			getCache().put(path, value);
		}

		return value;
	}

	/**
	 * <p>
	 * Gets the map of cached <code>SettingValue</code>'s.
	 * </p>
	 * 
	 * @return the cache
	 */
	protected Map<String, SettingValue> getCache() {
		return cacheMap;
	}

	/**
	 * @param settingsReader the settings reader to be used for retrieving the setting values
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * @return the settings reader to be used for retrieving the setting values
	 */
	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

}
