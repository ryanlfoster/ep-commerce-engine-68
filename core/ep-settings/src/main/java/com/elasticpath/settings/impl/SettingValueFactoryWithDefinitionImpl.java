package com.elasticpath.settings.impl;

import com.elasticpath.settings.SettingValueFactory;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;

/**
 * Creates new SettingValueImpl objects with a wrapped SettingDefinition.
 */
public class SettingValueFactoryWithDefinitionImpl implements SettingValueFactory {

	/**
	 * Create a new {@link SettingValueImpl} implementation of the SettingValue interface.
	 * The {@link SettingValueImpl} wraps a SettingDefinition object.
	 * @param initializingObjects must be a single SettingDefinition object
	 * @return a new SettingValue object
	 * @throws IllegalArgumentException if anything other than a single SettingDefinition is provided as a parameter
	 */
	public SettingValue createSettingValue(final Object... initializingObjects) {
		if (initializingObjects.length > 1 || !(initializingObjects[0] instanceof SettingDefinition)) {
			throw new IllegalArgumentException("The only valid argument is a single instance of SettingDefinition");
		}
		SettingValueImpl settingValueImpl = new SettingValueImpl();
		settingValueImpl.setSettingDefinition((SettingDefinition) initializingObjects[0]);
		return settingValueImpl;
	}
}
