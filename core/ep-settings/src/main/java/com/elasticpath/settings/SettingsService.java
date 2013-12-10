package com.elasticpath.settings;

import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

import java.util.Set;

/**
 * Provides services for using Settings.
 */
public interface SettingsService extends SettingsReader {

	/**
	 * Get all setting definitions that are identified by the given partial path.
	 * e.g. "COMMERCE/SYSTEM", "COMMERCE/STORE". 
	 * The path does not support wild cards.
	 * @param partialPath the first part of a path to match against setting definition path identifiers.
	 * @return all setting definitions matching the given partial path
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	Set<SettingDefinition> getSettingDefinitions(String partialPath);
	
	/**
	 * Get all setting definitions that are persistent in the system.
	 * @return all setting definitions in the system
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	Set<SettingDefinition> getAllSettingDefinitions();
	
	/**
	 * Creates a SettingValue object from a SettingDefinition.
	 * This implementation uses the {@link SettingValueFactory} to create a new SettingValue.
	 * @param definition the SettingDefinition from which to create a SettingValue
	 * @return a SettingValue based on the given SettingDefinition
	 */
	SettingValue createSettingValue(final SettingDefinition definition);
	
	/**
	 * Creates a SettingValue object from a SettingDefinition and the context.
	 * This implementation uses the {@link SettingValueFactory} to create a new SettingValue.
	 * @param definition the SettingDefinition from which to create a SettingValue
	 * @param context in which the SettingValue is valid
	 * @return a SettingValue based on the given SettingDefinition
	 */
	SettingValue createSettingValue(final SettingDefinition definition, final String context);
	
	/**
	 * Update the given setting value.
	 * @param settingValue the setting value to update
	 * @return the updated SettingValue
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	SettingValue updateSettingValue(SettingValue settingValue);
	
	/**
	 * Remove the given setting value from the persistence layer.
	 * @param settingValue the setting value to remove
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	void deleteSettingValue(SettingValue settingValue);
	
	/**
	 * Remove the SettingValues designated by the given path and context(s). 
	 * This method would, for example, allow the removal of all setting values where the context
	 * is a particular store code.
	 * @param path the path of the setting's definition
	 * @param contexts the context(s) within which the settings are valid.
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	void deleteSettingValues(String path, String... contexts);

	/**
	 * Update the given SettingDefinition in the persistence layer. If the given
	 * SettingDefinition is not already peristent, it will be persisted.
	 * @param settingDefinition the setting definition to update
	 * @return the updated SettingDefinition
	 * @throws com.elasticpath.base.exception.EpServiceException if the given SettingDefinition
	 * is not persistent but shares a PATH with a persistent SettingDefinition. 
	 */
	SettingDefinition updateSettingDefinition(SettingDefinition settingDefinition);
	
	/**
	 * Remove the given setting definition from the persistence layer.
	 * @param definition the SettingDefinition to remove
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	void deleteSettingDefinition(SettingDefinition definition);
	
	/**
	 * Get the setting definition keyed on the given path from the persistence layer.
	 * @param path the path that identifies the setting definition to find
	 * @return the setting definition
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	SettingDefinition getSettingDefinition(String path);
	
	/**
	 * Find setting definitions with the specified metadata key and value.
	 * 
	 * @param key the metadata key to match
	 * @param value the value for the metadata key to match
	 * @return all setting definitions that match the specified metadata value
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	Set<SettingDefinition> findSettingDefinitionsByMetadataValue(String key, String value);	
	
	
	/**
	 * Find any setting definitions with the specified metadata key, regardless of the metadata value.
	 * 
	 * @param metadataKey the metadata key to match
	 * @return all setting definitions that have a metadata of specified key
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	Set<SettingDefinition> findSettingDefinitionsByMetadata(String metadataKey);	
}
