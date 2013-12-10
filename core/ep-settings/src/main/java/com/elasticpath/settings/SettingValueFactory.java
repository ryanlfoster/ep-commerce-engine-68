package com.elasticpath.settings;

import com.elasticpath.settings.domain.SettingValue;

/**
 * A SettingValueFactory is responsible for creating new SettingValue objects.
 */
public interface SettingValueFactory {

	/**
	 * Create a new SettingValue object with the given initialization objects.
	 * @param initializingObjects any objects required to initialize the SettingValue
	 * @return a new SettingValue object
	 */
	SettingValue createSettingValue(Object... initializingObjects);
}
