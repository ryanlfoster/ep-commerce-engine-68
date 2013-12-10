package com.elasticpath.epcoretool.logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.elasticpath.epcoretool.logic.dto.EpSetting;
import com.elasticpath.settings.SettingValueFactory;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * The Class AbstractBaseSettings.
 */
public abstract class AbstractBaseSettings extends AbstractEpCore {

	/**
	 * Instantiates a new abstract base settings.
	 * 
	 * @param jdbcUrl the jdbc url
	 * @param jdbcUsername the jdbc username
	 * @param jdbcPassword the jdbc password
	 * @param jdbcDriverClass the jdbc driver class
	 * @param jdbcConnectionPoolMinIdle the jdbc connection pool min idle
	 * @param jdbcConnectionPoolMaxIdle the jdbc connection pool max idle
	 */
	public AbstractBaseSettings(final String jdbcUrl, final String jdbcUsername, final String jdbcPassword, final String jdbcDriverClass,
			final Integer jdbcConnectionPoolMinIdle, final Integer jdbcConnectionPoolMaxIdle) {
		super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriverClass, jdbcConnectionPoolMinIdle, jdbcConnectionPoolMaxIdle);
	}

	/**
	 * First cut at a method which can set a setting. It assumes there's only one value for a given setting and that setting "foo=" means
	 * "set foo to an empty string". There is no mechanism to set it to null (maybe you want to unset the setting). Note this method calls the EP
	 * settingsService so it may throw runtime exceptions.
	 * 
	 * @param sName the name of the setting.
	 * @param sContext the optional context (use null for no context).
	 * @param sValue the value of which to set.
	 */
	protected void setSetting(final String sName, final String sContext, final String sValue) {

		SettingsService settingsService = epCore().getSettingsService();

		SettingValue epSettingValue;

		getLogger().debug("set-setting: name=" + sName + ", context=" + sContext + ", value=" + sValue);

		epSettingValue = settingsService.getSettingValue(sName, sContext);

		if (epSettingValue == null) {
			throw new IllegalArgumentException("No setting value returned from settings service.");
		} else {
			getLogger().info("Old Value: " + epSettingValue.getPath() + "@" + epSettingValue.getContext() + "=" + epSettingValue.getValue());
		}

		if (epSettingValue.isPersisted()) {
			settingsService.deleteSettingValue(epSettingValue);
		}

		SettingDefinition settingDefinition = settingsService.getSettingDefinition(sName);
		SettingValueFactory factory = epCore().getSettingValueFactory();

		epSettingValue = factory.createSettingValue(settingDefinition);
		epSettingValue.setContext(sContext);
		epSettingValue.setValue(sValue);

		epSettingValue = settingsService.updateSettingValue(epSettingValue);
		getLogger().info("New Value: " + epSettingValue.getPath() + "@" + epSettingValue.getContext() + "=" + epSettingValue.getValue());

	}

	/**
	 * First cut at a method which can unset a setting by deleting it.
	 * 
	 * @param sName the name of the setting.
	 * @param sContext the optional context (use null for no context).
	 */
	protected void unsetSetting(final String sName, final String sContext) {

		SettingValue epSettingValue;

		SettingsService settingsService = epCore().getSettingsService();

		epSettingValue = settingsService.getSettingValue(sName, sContext);

		if (epSettingValue == null) {
			throw new IllegalArgumentException("No setting value returned from settings service.");
		}

		if (epSettingValue.isPersisted()) {
			getLogger().info(
					"Unsetting EP Setting: " + epSettingValue.getPath() + "@" + epSettingValue.getContext() + " (currently "
					+ epSettingValue.getValue() + ")");
			settingsService.deleteSettingValue(epSettingValue);
		}
	}

	/**
	 * Parses the setting string. (a @ b = c)
	 * 
	 * @param setting the setting
	 * @return the ep setting
	 */
	public EpSetting parseSettingString(final String setting) {
		getLogger().debug("Parsing: '" + setting + "'");
		EpSetting result = null;
		Pattern pattern = Pattern.compile("([^@]*?)(?:@([^=]+))?=(.+)?");
		Matcher matcher = pattern.matcher(setting);
		if (matcher.matches()) {
			result = new EpSetting(matcher.group(1), matcher.group(2), matcher.group(1 + 2));
		} else {
			throw new IllegalArgumentException(
					"Settings must be in the format NAME=VALUE, or NAME@CONTEXT=VALUE or NAME@CONTEXT=.  Cannot parse setting: " + setting);
		}
		return result;
	}

	/**
	 * Parses the setting string. (a = b)
	 * 
	 * @param settingNameAndContext the setting name and context
	 * @param settingValue the setting value
	 * @return the ep setting
	 */
	public EpSetting parseSettingString(final String settingNameAndContext, final String settingValue) {
		return parseSettingString(settingNameAndContext + "=" + settingValue);
	}
}
