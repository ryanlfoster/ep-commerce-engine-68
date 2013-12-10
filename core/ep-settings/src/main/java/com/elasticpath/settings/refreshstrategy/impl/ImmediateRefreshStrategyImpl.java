/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.settings.refreshstrategy.impl;


import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Immediate timeout implementation of <code>SettingRefreshStrategy</code>.
 */
public class ImmediateRefreshStrategyImpl implements SettingRefreshStrategy {

	private SettingsReader settingsReader;

	/**
	 * Retrieve the setting value for the given path and context from the settings service.
	 * 
	 * @param path the setting path
	 * @param context the setting context
	 * @param params the setting metadata
	 * @return the setting value
	 */
	public SettingValue retrieveSetting(final String path, final String context, final String params) {
		// just pass the request straight through to the settings service since we are not performing any caching
		return getSettingsReader().getSettingValue(path, context);
	}

	/**
	 * Retrieve the setting value for the given path from the settings service.
	 * 
	 * @param path the setting path
	 * @param params the setting metadata
	 * @return the setting value
	 */
	public SettingValue retrieveSetting(final String path, final String params) {
		// just pass the request straight through to the settings service since we are not performing any caching
		return getSettingsReader().getSettingValue(path);
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
