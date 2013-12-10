package com.elasticpath.settings.domain;

import com.elasticpath.persistence.api.Persistable;

import java.util.Date;
import java.util.Map;

/**
 * Defines a Setting, by dictating its key (path), its type, and its default value.
 * A Setting consists of a definition and a value. 
 * e.g. one might define a setting for a Store https port. The Value of the setting for
 * a particular store is a separate concern to the definition.
 */
public interface SettingDefinition extends Persistable, Comparable<SettingDefinition> {

	/**
	 * Gets the path to the setting definition.
	 * @return the path to the setting
	 */
	String getPath();
	
	/**
	 * Set the path to the setting definition.
	 * @param path the path
	 */
	void setPath(String path);
	
	/**
	 * Gets the default value for the setting.
	 * @return the setting's default value
	 */
	String getDefaultValue();
	
	/**
	 * Set the default value for the setting.
	 * @param defaultValue the default value
	 */
	void setDefaultValue(String defaultValue);
	
	/**
	 * Gets the type of the setting defined by this definition.
	 * @return the defined type of the setting
	 */
	String getValueType();
	
	/**
	 * Set the type of the setting defined by this definition.
	 * @param valueType the defined type of the setting
	 */
	void setValueType(String valueType);
	
	/**
	 * @return the number of overrides that the setting definition's value may have, if negative one is
	 * returned that is an unbounded number of overrides.
	 */
	int getMaxOverrideValues();

	/**
	 * @param maxOverrideValues maxOverrideValues to an <code>integer</code> value, if negative one then there are
	 * an unbound number of overrides possible
	 */
	void setMaxOverrideValues(final int maxOverrideValues);
	
	/**
	 * @return the non-localized description of this Setting.
	 */
	String getDescription();
	
	/**
	 * @param description the non-localized description of this Setting.
	 */
	void setDescription(String description);
	
	/**
	 * Get the setting's last modified date.
	 * @return the last modified date
	 */
	Date getLastModifiedDate();
	
	/**
	 * Get the metadata of this setting definition.
	 * 
	 * @return a map of setting metadata key to setting metadata.
	 */
	Map<String, SettingMetadata> getMetadata();

	/**
	 * Set the collection of metadata for this setting definition.
	 * @param metadataSet the set of setting metadata
	 */
	void setMetadata(final Map<String, SettingMetadata> metadataSet);
	
}
