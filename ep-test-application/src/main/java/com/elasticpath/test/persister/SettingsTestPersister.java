package com.elasticpath.test.persister;


import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.impl.SettingValueFactoryWithDefinitionImpl;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Persister allows to create and save into database settings dependent domain objects.
 */
public class SettingsTestPersister {

	private final BeanFactory beanFactory;
	private final SettingsService settingsService;
	
	/**
	 * Constructor initializes the setting service and beanFactory.
	 * 
	 * @param beanFactory Elastic Path factory for creating instances of beans.
	 */
	public SettingsTestPersister(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		settingsService = beanFactory.getBean("settingsService");
	}
	
	/**
	 * Creates a SettingDefinition and a SettingValue with the given parameters.
	 * 
	 * @param path the SettingDefinition's unique identifier path (e.g. COMMERCE/STORE/storeAdminEmailAddress/)
	 * @param defaultValue the SettingDefinition's default value (e.g.admin@demo.elasticpath.com, 8080)
	 * @param valueType the value type of the configuration setting (e.g. String, XML)
	 * @param maxOverrideValues indicates the number of values that can override this setting
	 * @param context the context of the configuration settings (e.g. SNAPITUP)
	 * @param value the context value of the configuration setting (e.g. /home/ep/assets)
	 * @param description the description of the setting
	 */
	public void persistSettings(final String path, final String defaultValue, final String valueType,
			final int maxOverrideValues, final String context, final String value, final String description) {
	
		SettingDefinition def = beanFactory.getBean("settingDefinition");
		
		def.setPath(path);
		def.setMaxOverrideValues(maxOverrideValues);
		def.setDefaultValue(defaultValue);
		def.setDescription(description);
		def.setValueType(valueType);
	
		settingsService.updateSettingDefinition(def);
		
		updateSettingValue(path, context, value);
	}		
	
	/**
	 * Update the given setting definition with a context value.
	 * 
	 * @param path The setting definition to update.
	 * @param context The setting value context.
	 * @param value The new setting value.
	 */
	public void updateSettingValue(final String path, final String context, final String value) {

		SettingDefinition settingDefinition = settingsService.getSettingDefinition(path);
		SettingValueFactoryWithDefinitionImpl factory = beanFactory.getBean("settingValueFactory");
		
		SettingValue settingValue = factory.createSettingValue(settingDefinition);
		settingValue.setContext(context);
		settingValue.setValue(value);
		
		settingsService.updateSettingValue(settingValue);
	}
}
	


