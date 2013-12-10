package com.elasticpath.settings;

import java.util.Set;

import com.elasticpath.settings.domain.SettingValue;

/**
 * Provides read only services for retrieving Setting Values.
 */
public interface SettingsReader {
	/**
	 * Get the set of setting values matched by the given {@link SettingDefiniton} path and 
	 * {@link SettingValue} contexts.
	 * 
	 * @param path the path that defines the setting.
	 * @param contexts the context or contexts of the requested setting values, 
	 * or null if all setting values matching the given path should be retrieved
	 * @return the set of setting values
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	Set<SettingValue> getSettingValues(String path, String... contexts);

	/**
	 * Get the setting value matched by the given {@link SettingDefiniton} path with no practical 
	 * context. For example, settings with max over-ride values of 1 do not need a context.
	 * If a SettingValue does not exist, a non-persistent SettingValue will be created 
	 * based on the setting definition.
	 * 
	 * @param path the path that defines the setting.
	 * @return the requested setting value
	 * @throws com.elasticpath.base.exception.EpServiceException on persistence issues or if the
	 * definition does not exist
	 * @throws IllegalArgumentException if path is null or empty
	 */
	SettingValue getSettingValue(final String path);

	/**
	 * Get the setting value uniquely identified by the given 
	 * {@link SettingDefiniton} path and {@link SettingValue} context. If a SettingValue does
	 * not exist, a non-persistence SettingValue will be created based on the setting definition.
	 * 
	 * @param path the path that defines the setting.
	 * @param context the context of the requested setting value
	 * @return the setting value identified by the given path and context
	 * @throws com.elasticpath.base.exception.EpServiceException on persistence issues or if the
	 * definition does not exist
	 * @throws IllegalArgumentException if path is empty or null
	 */
	SettingValue getSettingValue(String path, String context);
}
