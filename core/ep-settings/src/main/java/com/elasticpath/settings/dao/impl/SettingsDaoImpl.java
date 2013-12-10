package com.elasticpath.settings.dao.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.settings.dao.SettingsDao;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Implementation of SettingsDao that uses JPA Persistence to 
 * persist SettingValue and SettingDefinition objects. 
 */
public class SettingsDaoImpl implements SettingsDao {

	private static final String PLACEHOLDER_FOR_LIST = "list";
	private PersistenceEngine persistenceEngine;
	
	/**
	 * Retrieves the setting definition matching the given path from the database.
	 * @param path the string that uniquely identifies the setting definition
	 * @return the requested SettingDefinition, or null if the definition can't be found
	 * @throws com.elasticpath.persistence.api.EpPersistenceException in case of error
	 */
	public SettingDefinition findSettingDefinition(final String path) {
		List<SettingDefinition> definitions = getPersistenceEngine().retrieveByNamedQuery("SETTING_DEFINITION_BY_PATH", path);
		if (!definitions.isEmpty()) {
			return definitions.get(0);
		}
		return null;
	}
	
	/**
	 * Retrieves all setting definitions that are identified by the given partial path.
	 * e.g. "COMMERCE/SYSTEM", "COMMERCE/STORE".
	 * @param partialPath the first part of a path to match against setting definition path identifiers.
	 * @return the requested SettingDefinitions
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error 
	 */
	public Set<SettingDefinition> findSettingDefinitions(final String partialPath) {
		List<SettingDefinition> definitions =
				getPersistenceEngine().retrieveByNamedQuery("SETTING_DEFINITIONS_BY_PARTIAL_PATH", "%" + partialPath + "%");
		Set<SettingDefinition> definitionSet = new HashSet<SettingDefinition>(definitions.size());
		definitionSet.addAll(definitions);
		return definitionSet;
	}
	
	/**
	 * Retrieves all setting definitions.
	 * @return all the settingDefinitions.
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	public Set<SettingDefinition> findAllSettingDefinitions() {
		List<SettingDefinition> definitions = 
			getPersistenceEngine().retrieveByNamedQuery("SETTING_DEFINITIONS_GET_ALL");
		Set<SettingDefinition> definitionSet = new HashSet<SettingDefinition>(definitions.size());
		definitionSet.addAll(definitions);
		return definitionSet;
	}

	/**
	 * Retrieves a setting value for the given path a context.
	 * @param path the path to the setting value's definition
	 * @param context the setting value's context
	 * @return the requested SettingValue, or null if the value can't be found
	 * @throws com.elasticpath.persistence.api.EpPersistenceException in case of error
	 */
	public SettingValue findSettingValue(final String path, final String context) {
		List<Object> values = getPersistenceEngine().retrieveByNamedQuery(
				"SETTING_VALUE_BY_PATH_AND_CONTEXT", path, context);
		
		if (!values.isEmpty()) {
			return (SettingValue) values.get(0);
		}
		return null;
	}

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
	public Set<SettingValue> findSettingValues(final String path, final String... contexts) {
		List<SettingValue> values;
		if (contexts == null || contexts.length == 0) {
			values = getPersistenceEngine().retrieveByNamedQuery("SETTING_VALUES_BY_PATH", path);
		} else {
			values = getPersistenceEngine().retrieveByNamedQueryWithList("SETTING_VALUES_BY_PATH_AND_CONTEXTS", PLACEHOLDER_FOR_LIST,
					Arrays.asList(contexts),
					path);
		}
		Set<SettingValue> valueSet = new HashSet<SettingValue>(values.size());
		valueSet.addAll(values);
		return valueSet;
	}

	@Override
	public Set<SettingDefinition> findSettingDefinitionsByMetadataValue(final String key, final String value) {
		List<SettingDefinition> defs = getPersistenceEngine().retrieveByNamedQuery("SETTING_DEFINITIONS_BY_METADATA_KEY_VALUE", key, value);
		Set<SettingDefinition> defSet = new HashSet<SettingDefinition>(defs.size());
		defSet.addAll(defs);
		return defSet;
	}
	
	@Override
	public Set<SettingDefinition> findSettingDefinitionsByMetadata(final String metadataKey) {
		List<SettingDefinition> defs = getPersistenceEngine().retrieveByNamedQuery("SETTING_DEFINITIONS_BY_METADATA_KEY", metadataKey);
		Set<SettingDefinition> defSet = new HashSet<SettingDefinition>(defs.size());
		defSet.addAll(defs);
		return defSet;		
	}
	
	/**
	 * Updates the given persistent SettingDefinition.
	 * @param settingDefinition the setting definition to update
	 * @return the persisted SettingDefinition
	 * @throws com.elasticpath.persistence.api.EpPersistenceException in case of error
	 */
	public SettingDefinition updateSettingDefinition(final SettingDefinition settingDefinition) {
		SettingDefinition definition;
		
		try {
			definition = getPersistenceEngine().saveOrUpdate(settingDefinition);
		} catch (PersistenceException e) {
			throw new EpPersistenceException("Save or Update failed.", e);
		}
		return definition;
	}

	/**
	 * Updates the given setting value.
	 * @param settingValue the setting value to update
	 * @return the persisted SettingValue
	 * @throws com.elasticpath.persistence.api.EpPersistenceException in case of error
	 */
	public SettingValue updateSettingValue(final SettingValue settingValue) {
		return getPersistenceEngine().saveOrUpdate(settingValue);
	}

	/**
	 * Remove the given setting definition from the persistent store.
	 * @param settingDefinition setting definition to remove
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	public void deleteSettingDefinition(final SettingDefinition settingDefinition) {
		getPersistenceEngine().delete(settingDefinition);
	}

	/**
	 * Removes the given setting value from the persistent store.
	 * @param settingValue the setting value to remove
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	public void deleteSettingValue(final SettingValue settingValue) {
		getPersistenceEngine().delete(settingValue);
	}

	/**
	 * Removes the setting values with the given path and given contexts from the persistent store.
	 * 
	 * @param path the path to the setting values' definition
	 * @param contexts the contexts that uniquely define the setting values to be deleted
	 * @return the number of Setting Values deleted
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	public int deleteSettingValues(final String path, final String... contexts) {
		List<Long> uidsToDelete = getPersistenceEngine().retrieveByNamedQueryWithList("SETTING_VALUE_UIDS_BY_PATH_AND_CONTEXTS",
				PLACEHOLDER_FOR_LIST,
				Arrays.asList(contexts),
				path);
		return getPersistenceEngine().executeNamedQueryWithList("DELETE_SETTINGVALUES_BY_UID", PLACEHOLDER_FOR_LIST, uidsToDelete);
	}
	
	/**
	 * Sets the persistence engine to use.
	 *  
	 * @param persistenceEngine The persistence engine.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * Gets the persistence engine.
	 * 
	 * @return The persistence engine.
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}
	
	/**
	 * Retrieves the number of SettingDefinition entries in the database table that have the same path as defined.
	 * @param path that will be used to count how many entries are using it
	 * @return the number of entries in the database that have the same path as chosen
	 */
	public int getSettingDefinitionCount(final String path) {
		//checking here if the path is null
		if (path == null) {
			return 0;
		}
		
		List<Object> queryValue = getPersistenceEngine().retrieveByNamedQuery("SETTING_DEFINITIONS_COUNT_BY_PATH", path);
		Long count = (Long) queryValue.get(0);
		return count.intValue();
	}
	
	/**
	 * Retrieves the number of value that are able to override a specific SettingDefinition.
	 * @param path that will be used to retrieve the SettingDefinition's max override value
	 * @return the maximum number of values that are permitted to override the setting
	 */
	public int getSettingDefinitionMaxOverrideValues(final String path) {
		//checking here if the path is null
		if (path == null) {
			return 0;
		}

		List<Integer> queryValue = getPersistenceEngine().retrieveByNamedQuery("SETTING_DEFINITION_MAX_OVERRIDE_VALUES", path);
		return queryValue.get(0).intValue();
	}
	
	/**
	 * Retrieves the number of SettingValue entries in the database table that have the same path and context as defined.
	 * @param path that will be used to count how many entries are using it
	 * @param context that will be used to count how many entries are using it
	 * @return the number of entries in the database that have the same path and context as chosen 
	 */
	public int getSettingValueCount(final String path, final String context) {
		
		//Check if either argument is null valued, if they are return zero
		if (path == null) {
			return 0;
		} else if (context == null) {
			List<Object> queryValue = 
				getPersistenceEngine().retrieveByNamedQuery("SETTING_VALUES_COUNT_BY_PATH", path);
			Long count = (Long) queryValue.get(0);
			return count.intValue();
		}
		
		List<Object> queryValue = 
			getPersistenceEngine().retrieveByNamedQuery("SETTING_VALUES_COUNT_BY_PATH_AND_CONTEXT", path, context);
		Long count = (Long) queryValue.get(0);
		return count.intValue();
	}
}
