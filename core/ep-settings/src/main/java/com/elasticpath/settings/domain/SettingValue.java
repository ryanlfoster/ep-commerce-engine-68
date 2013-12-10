package com.elasticpath.settings.domain;

import java.util.Date;

import com.elasticpath.persistence.api.Persistable;

/**
 * <p>An object that holds the value of a setting. Allows a user to access the setting's
 * default value, assigned value, type, and path as well as context and last modified date.</p>
 */
public interface SettingValue extends Persistable {

	/**
	 * Gets the path to the definition for this SettingValue. The path is usually defined by a separate
	 * {@link SettingDefinition} object.
	 * @return the path to the setting
	 */
	String getPath();
	
	/**
	 * Gets the default value for the setting, usually contained within a separate {@link SettingDefinition} object.
	 * @return the setting's default value
	 */
	String getDefaultValue();
	
	/**
	 * Gets the type of the SettingValue, which could be anything, but is typically
	 * something like "int", "String", "xml", and so-on. Usually defined by a separate {@link SettingDefinition} object. 
	 * The type generally is used by helper classes (such as settings editors) that need to how to deal with the Value of a setting.
	 * @return the defined type of the setting
	 */
	String getValueType();
	
	/**
	 * Get the context within which the setting value is valid. 
	 * A Context is the context within which a SettingValue is valid, e.g SNAPITUP. 
	 * An empty Context generally means that the SettingValue is valid within all contexts under the Path.
	 * @return the context within which the SettingValue is valid
	 */
	String getContext();
	
	/**
	 * Set the context within which the setting value is valid.
	 * A Context is the context within which a SettingValue is valid, e.g SNAPITUP. 
	 * @param context the context within which the SettingValue is valid
	 */
	void setContext(String context);
	
	/**
	 * Get the setting's assigned value.
	 * @return the value of the setting
	 */
	String getValue();
	
	/**
	 * If the settings value is a "true" string then returns true, otherwise returns
	 * false.
	 * @return the boolean value of the setting
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName")
	boolean getBooleanValue();
	
	/**
	 * Set the setting's assigned value.
	 * Setting the value to null means that the default value will be used.
	 * @param value the value to set
	 */
	void setValue(String value);
	
	/**
	 * Set the setting's value with a boolean.
	 * @param value the boolean value to set
	 */
	void setBooleanValue(boolean value);
	
	/**
	 * Get the setting's last modified date.
	 * @return the last modified date
	 */
	Date getLastModifiedDate();
	
	
	/**
	 * Get the setting's value as a integer.
	 * @return the integer value of the setting.
	 */
	int getIntegerValue();

	/**
	 * Set the setting's value with a boolean.
	 * @param value the boolean value to set
	 */
	void setIntegerValue(final int value);	
	
}
