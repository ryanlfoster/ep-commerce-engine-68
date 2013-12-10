package com.elasticpath.settings.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.elasticpath.settings.SettingValueFactory;
import com.elasticpath.settings.SettingsService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.settings.SettingMaxOverrideException;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.settings.dao.SettingsDao;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Implementation of the SettingService interface that uses the EP Persistence layer.
 */
public class SettingsServiceImpl implements SettingsService {

	private static final Logger LOG = Logger.getLogger(SettingsServiceImpl.class);

	private static final String NO_CONTEXT = null;

	private SettingsDao settingsDao;
	private SettingValueFactory settingValueFactory;

	/**
	 * Get all setting definitions that are identified by the given partial path.
	 * e.g. "COMMERCE/SYSTEM", "COMMERCE/STORE".
	 * The path does not support wild cards.
	 * This implementation returns a modifiable Set.
	 * @param partialPath the first part of a path to match against setting definition path identifiers.
	 * @return all setting definitions matching the given partial path
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	public Set<SettingDefinition> getSettingDefinitions(final String partialPath) {
		try {
			final Set<SettingDefinition> settingDefinitions = new HashSet<SettingDefinition>();
			settingDefinitions.addAll(settingsDao.findSettingDefinitions(partialPath));
			return settingDefinitions;
		} catch (final EpPersistenceException ex) {
			throw new EpServiceException("Unable to find setting definitions for " + partialPath, ex);
		}
	}

	/**
	 * Get all setting definitions that are persistent in the system.
	 * @return all setting definitions in the system
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	public Set<SettingDefinition> getAllSettingDefinitions() {
		try {
			final Set<SettingDefinition> settingDefinitions = new HashSet<SettingDefinition>();
			settingDefinitions.addAll(settingsDao.findAllSettingDefinitions());
			return settingDefinitions;
		} catch (final EpPersistenceException ex) {
			throw new EpServiceException("Unable to find any setting definitions.", ex);
		}
	}

	/**
	 * Get the setting definition keyed on the given path from the persistence layer.
	 * @param path the path that identifies the setting definition to find
	 * @return the setting definition
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	public SettingDefinition getSettingDefinition(final String path) {
		try {
			return settingsDao.findSettingDefinition(path);
		} catch (final EpPersistenceException ex) {
			throw new EpServiceException("Unable to get the setting definition for " + path, ex);
		}
	}

	@Override
	public SettingValue getSettingValue(final String path) {
		return getSettingValue(path, NO_CONTEXT);
	}

	@Override
	public SettingValue getSettingValue(final String path, final String context) {
		if (StringUtils.isEmpty(path)) {
			throw new IllegalArgumentException("Path must be specified.");
		}

		// empty context values should be null
		String adjustedContext = context;
		if (StringUtils.isEmpty(adjustedContext)) {
			adjustedContext = NO_CONTEXT;
		}

		SettingValue value;
		try {
			value = settingsDao.findSettingValue(path, adjustedContext);
		} catch (final EpPersistenceException ex) {
			throw new EpServiceException(
					"Unable to get the setting value for path and context: '" + path + "', '" + context + "'", ex);
		}
		if (value == null) {
			LOG.debug("No SettingValue for path and context: path and context: '"
					+ path + "', '" + context + "'. Creating a default setting value.");

			final SettingDefinition settingDef = getSettingDefinition(path);
			if (settingDef == null) {
				throw new EpServiceException(
						"No setting definition exists for path: " + path);
			}
			value = createSettingValue(settingDef, context);
		}
		return value;
	}

	/**
	 * Creates a SettingValue object from a SettingDefinition.
	 * This implementation uses the {@link SettingValueFactory} to create a new SettingValue.
	 * 
	 * @param definition the SettingDefinition from which to create a SettingValue
	 * @return a SettingValue based on the given SettingDefinition
	 */
	public SettingValue createSettingValue(final SettingDefinition definition) {
		return createSettingValue(definition, "");
	}

	/**
	 * Creates a SettingValue object from a SettingDefinition and the context.
	 * This implementation uses the {@link SettingValueFactory} to create a new SettingValue.
	 * @param definition the SettingDefinition from which to create a SettingValue
	 * @param context in which the SettingValue is valid
	 * @return a SettingValue based on the given SettingDefinition
	 */
	public SettingValue createSettingValue(final SettingDefinition definition, final String context) {
		final SettingValue value = settingValueFactory.createSettingValue(definition);
		if (StringUtils.isEmpty(context)) {
			value.setContext(NO_CONTEXT);
		} else {
			value.setContext(context);
		}

		return value;
	}

	/**
	 * Get the set of setting values matched by the given {@link SettingDefiniton} path and
	 * {@link SettingValue} contexts.
	 * 
	 * This implementation calls {@link #getSettingValue(String, String)}, and returns
	 * and modifiable Set.
	 * 
	 * @param path the path that defines the setting.
	 * @param contexts the context or contexts of the requested setting values,
	 * if null or empty all setting values matching the given path should be retrieved
	 * @return the set of setting values
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	public Set<SettingValue> getSettingValues(final String path, final String... contexts) {
		Set<SettingValue> values = Collections.emptySet();
		try {
			values = settingsDao.findSettingValues(path, contexts);
		} catch (final EpPersistenceException ex) {
			if (contexts == null) {
				throw new EpServiceException(
						"Unable to get the setting values for path and contexts: '" + path + "', null", ex);
			}
			throw new EpServiceException(
					"Unable to get the setting values for path and contexts: '" + path + "', " + Arrays.toString(contexts), ex);
		}

		final Set<SettingValue> settingValues = new HashSet<SettingValue>();
		settingValues.addAll(values);

		return settingValues;
	}

	/**
	 * Remove the given setting definition from the persistence layer.
	 * 
	 * @param definition the SettingDefinition to remove
	 */
	public void deleteSettingDefinition(final SettingDefinition definition) {
		settingsDao.deleteSettingDefinition(definition);
	}

	/**
	 * Remove the given setting value from the persistence layer.
	 * @param settingValue the setting value to remove
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	public void deleteSettingValue(final SettingValue settingValue) {
		try {
			settingsDao.deleteSettingValue(settingValue);
		} catch (final EpPersistenceException ex) {
			throw new EpServiceException(
					"Unable to remove the settingValue: " + settingValue, ex);
		}
	}

	/**
	 * Remove the SettingValues designated by the given path and context(s).
	 * This method would, for example, allow the removal of all setting values where the context
	 * is a particular store code.
	 * Calls {@link #deleteSettingValue}.
	 * @param path the path of the setting's definition
	 * @param contexts the context(s) within which the settings are valid.
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error
	 */
	public void deleteSettingValues(final String path, final String... contexts) {
		settingsDao.deleteSettingValues(path, contexts);
	}

	/**
	 * Update the given SettingDefinition in the persistence layer. If the given
	 * SettingDefinition is not already peristent, it will be persisted.
	 * @param settingDefinition the setting definition to update
	 * @return the updated SettingDefinition
	 * @throws com.elasticpath.base.exception.EpServiceException if the given SettingDefinition
	 * is not persistent but shares a PATH with a persistent SettingDefinition.
	 */
	public SettingDefinition updateSettingDefinition(final SettingDefinition settingDefinition) {
		if (settingDefinition.isPersisted() || !settingDefinitionExists(settingDefinition.getPath())) {
			return settingsDao.updateSettingDefinition(settingDefinition);
		}
		throw new EpServiceException("SettingDefinition with specified path already exists.");
	}

	/**
	 * Update the given setting value.
	 * @param settingValue the setting value to update
	 * @return the updated SettingValue
	 * @throws com.elasticpath.base.exception.EpServiceException in case of error of if SettingValue with path and contexts already exists
	 */
	public SettingValue updateSettingValue(final SettingValue settingValue) {
		final int maxOverrideValues = settingDefinitionMaxOverrideValues(settingValue.getPath());
		if (!settingValue.isPersisted() && settingValueExists(settingValue.getPath(), settingValue.getContext())) {
			throw new EpServiceException("SettingValue with specified path and context already exists.");
		} else if (!settingValue.isPersisted() &&  maxOverrideValues != -1
				&& settingsDao.getSettingValueCount(settingValue.getPath(), null) >= maxOverrideValues) {
			throw new SettingMaxOverrideException("Maximum number of overrides already exist for this setting.");
		}

		return settingsDao.updateSettingValue(settingValue);
	}

	/**
	 * Generic get method for all persistable domain models.
	 * This method is not supported in this implementation.
	 *
	 * @param uid not used
	 * @return nothing
	 * @deprecated
	 * @throws UnsupportedOperationException - always
	 */
	@Deprecated
	public Object getObject(final long uid) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return the settings DAO
	 */
	public SettingsDao getSettingsDao() {
		return settingsDao;
	}

	/**
	 * @param settingsDao the settings DAO to use
	 */
	public void setSettingsDao(final SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
	}

	/**
	 * Method to check if the SettingDefinition exists for a specific path.
	 * @param path for which the method checks if the SettingDefinition exists
	 * @return a boolean specifying whether the SettingDefinition exists.
	 */
	boolean settingDefinitionExists(final String path) {
		if (settingsDao.getSettingDefinitionCount(path) > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Method to obtain the number of values that can override a setting.
	 * @param path of the setting definition
	 * @return the number of values that can override a setting
	 */
	int settingDefinitionMaxOverrideValues(final String path) {
		return settingsDao.getSettingDefinitionMaxOverrideValues(path);
	}

	/**
	 * Method to check if the SettingValue exists for a specific path and context.
	 * @param path used in part to check if the SettingValue exists
	 * @param context used in part to check if the SettingValue exists
	 * @return a boolean specifying whether the SettingDefinition exists.
	 */
	boolean settingValueExists(final String path, final String context) {
		if (settingsDao.getSettingValueCount(path, context) > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Set the setting value factory that should be used for creating new values.
	 * 
	 * @param settingValueFactory the settingValueFactory to set
	 */
	public void setSettingValueFactory(final SettingValueFactory settingValueFactory) {
		this.settingValueFactory = settingValueFactory;
	}

	@Override
	public Set<SettingDefinition> findSettingDefinitionsByMetadataValue(final String key,
			final String value) {
		return settingsDao.findSettingDefinitionsByMetadataValue(key, value);
	}

	@Override
	public Set<SettingDefinition> findSettingDefinitionsByMetadata(final String metadataKey) {
		return settingsDao.findSettingDefinitionsByMetadata(metadataKey);
	}
}
