package com.elasticpath.settings.dao;

import java.util.Set;

import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * DAO interface for storing and retrieving SettingValue and SettingDefinition objects.
 */
public interface SettingsDao {

	/**
	 * Retrieves a setting value for the given path and context. If a SettingValue does
	 * not exist for the given path and context, this method will return null.
	 * @param path the path to the setting value's definition
	 * @param context the setting value's context
	 * @return the requested SettingValue, or null if one cannot be found
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	SettingValue findSettingValue(String path, String context);
	
	/**
	 * Get the set of setting values matched by the given {@link SettingDefiniton} path and 
	 * {@link SettingValue} contexts.
	 * 
	 * This implementation calls {@link #getSettingValue(String, String)}, and returns
	 * and unmodifiable Set.
	 * 
	 * @param path the path that defines the setting.
	 * @param contexts the context or contexts of the requested setting values. If null
	 * or an empty array, all setting values having the given path will be returned.
	 * @return the set of setting values
	 * @throws com.elasticpath.persistence.api.EpPersistenceException in case of error
	 */
	Set<SettingValue> findSettingValues(String path, String... contexts);
	
	/**
	 * Updates the given setting value.
	 * @param settingValue the setting value to update
	 * @return the persisted SettingValue
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	SettingValue updateSettingValue(SettingValue settingValue);
	
	/**
	 * Retrieves the setting definition matching the given path from the database.
	 * @param path the unique identifier to the setting definition
	 * @return the requested SettingDefinition
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error 
	 */
	SettingDefinition findSettingDefinition(String path);
	
	/**
	 * Retrieves all setting definitions that are identified by the given partial path.
	 * e.g. "COMMERCE/SYSTEM", "COMMERCE/STORE".
	 * @param partialPath the first part of a path to match against setting definition path identifiers.
	 * @return the requested SettingDefinitions
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error 
	 */
	Set<SettingDefinition> findSettingDefinitions(String partialPath);
	
	/**
	 * Retrieves all setting definitions.
	 * @return all the settingDefinitions.
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	Set<SettingDefinition> findAllSettingDefinitions();
	
	
	/**
	 * Find setting definitions with the specified metadata value.
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
	
	/**
	 * Updates the given persistent SettingDefinition.
	 * @param settingDefinition the setting definition to update
	 * @return the persisted SettingDefinition
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	SettingDefinition updateSettingDefinition(SettingDefinition settingDefinition);
	
	/**
	 * Remove the given setting definition from the persistent store.
	 * @param settingDefinition setting definition to remove
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	void deleteSettingDefinition(SettingDefinition settingDefinition);
	
	/**
	 * Removes the given setting value from the persistent store.
	 * @param settingValue the setting value to remove
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	void deleteSettingValue(SettingValue settingValue);
	
	/**
	 * Removes the setting values with the given path and given contexts from the persistent store.
	 * @param path the path to the setting values' definition
	 * @param contexts the contexts that uniquely define the setting values to be deleted
	 * @return the number of setting values deleted
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	int deleteSettingValues(String path, String... contexts);
	
	/**
	 * Returns the number of SettingDefinition elements in the database that have the same path.
	 * @param path used to count how many setting definitions are using this path
	 * @return the number of elements with the same path
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	int getSettingDefinitionCount(String path);
	
	/**
	 * Retrieves the number of value that are able to override a specific SettingDefinition.
	 * @param path that will be used to retrieve the SettingDefinition's max override value
	 * @return the maximum number of values that are permitted to override the setting
	 */
	int getSettingDefinitionMaxOverrideValues(final String path);
	
	/**
	 * Returns the number of SettingValue elements in the database that have the same path and context.
	 * @param path that will be used to count how many entries are using it
	 * @param context that will be used to count how many entries are using it
	 * @return the number of entries in the database that have the same path and context as chosen 
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	int getSettingValueCount(String path, String context);
	
}
