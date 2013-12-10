/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.settings.refreshstrategy.impl;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Timeout cache type implementation of <code>SettingRefreshStrategy</code>.
 */
public class IntervalRefreshStrategyImpl implements SettingRefreshStrategy {

	private SettingsReader settingsReader;
	
	private String timeoutParamKey;

	private static Map<String, SimpleTimeoutCache<String, SettingValue>> timeoutCacheMap
		= new HashMap<String, SimpleTimeoutCache<String, SettingValue>>();

	/**
	 * Retrieve the setting value for the given path and context from the timeout cache.
	 * 
	 * @param path the setting path
	 * @param context the setting context
	 * @param params the setting metadata
	 * @return the setting value
	 */
	public SettingValue retrieveSetting(final String path, final String context, final String params) {
		Map<String, String> parameters = parseParameters(params);
		SimpleTimeoutCache<String, SettingValue> timedCache = getCache(parameters);

		SettingValue value = timedCache.get(path + context);

		// put the setting in the timeout cache
		if (value == null) {
			value = getSettingsReader().getSettingValue(path, context);
			timedCache.put(path + context, value);
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
		Map<String, String> parameters = parseParameters(params);
		SimpleTimeoutCache<String, SettingValue> timedCache = getCache(parameters);
		SettingValue value = timedCache.get(path);

		// put the setting in the timeout cache
		if (value == null) {
			value = getSettingsReader().getSettingValue(path);
			timedCache.put(path, value);
		}

		return value;
	}

	/**
	 * <p>
	 * Builds a map of timeout caches with different intervals.
	 * </p>
	 * <p>
	 * These are keyed on the setting PATH of the timeout value, not the value itself.
	 * </p>
	 * 
	 * @param parameters the refresh strategy parameters
	 * @return the cache
	 */
	protected SimpleTimeoutCache<String, SettingValue> getCache(final Map<String, String> parameters) {
		final String timeoutCacheParam = parameters.get(getTimeoutParamKey());
		SimpleTimeoutCache<String, SettingValue> timedCache = timeoutCacheMap.get(timeoutCacheParam);
		if (timedCache == null) {
			timedCache = new SimpleTimeoutCache<String, SettingValue>(getIntervalFromSetting(timeoutCacheParam));
			timeoutCacheMap.put(timeoutCacheParam, timedCache);
		}
		return timedCache;
	}

	/**
	 * Parses the string of refresh strategy parameters into a <code>Map</code>. Assumes that parameters are separated by "&" signs and that
	 * key-value pairs are separated by "=" signs.
	 * 
	 * @param params the string of refresh strategy parameters
	 * @return a <code>Map</code> of the parameters as key-value pairs
	 */
	private Map<String, String> parseParameters(final String params) {
		Map<String, String> parameters = new HashMap<String, String>();
		String[] paramArray = StringUtils.split(params, '&');
		for (String param : paramArray) {
			parameters.put(StringUtils.substringBefore(param, "="), StringUtils.substringAfter(param, "="));
		}
		return parameters;
	}

	/**
	 * Gets the timeout interval from the cache setting path.
	 *
	 * @param path the cache setting path to get the interval from
	 * @return the cache refresh interval
	 */
	protected long getIntervalFromSetting(final String path) {
		return Long.parseLong(getSettingsReader().getSettingValue(path).getValue());
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

	/**
	 * Gets the key to use when looking for the timeout setting in the refresh strategy params.
	 * 
	 * @return the timeout param key
	 */
	protected String getTimeoutParamKey() {
		return timeoutParamKey;
	}

	/**
	 * Sets the key to use when looking for the timeout setting in the refresh strategy params. This must be set for this refresh strategy to
	 * function properly.
	 * 
	 * @param timeoutParamKey the key to use when looking for the timeout setting
	 */
	public void setTimeoutParamKey(final String timeoutParamKey) {
		this.timeoutParamKey = timeoutParamKey;
	}

}
