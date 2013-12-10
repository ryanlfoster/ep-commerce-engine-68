/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.settings.refreshstrategy;


import com.elasticpath.settings.domain.SettingValue;

/**
 * Strategy for refreshing setting values.
 */
public interface SettingRefreshStrategy {

	/**
	 * Retrieve the setting value for the given path and context.
	 * 
	 * @param path the setting path
	 * @param context the setting context
	 * @param params parameters for the refresh strategy
	 * @return the setting value
	 */
	SettingValue retrieveSetting(final String path, final String context, final String params);

	/**
	 * Retrieve the setting value for the given path.  Only to be used to settings that don't have a context.
	 * 
	 * @param path the setting path
	 * @param params parameters for the refresh strategy
	 * @return the setting value
	 */
	SettingValue retrieveSetting(final String path, final String params);
}
