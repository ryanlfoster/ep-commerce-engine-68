package com.elasticpath.epcoretool.logic;

/**
 * The Class AbstractSetSetting.
 */
public abstract class AbstractSetSetting extends AbstractBaseSettings {

	/**
	 * Instantiates a new abstract set setting.
	 * 
	 * @param jdbcUrl the jdbc url
	 * @param jdbcUsername the jdbc username
	 * @param jdbcPassword the jdbc password
	 * @param jdbcDriverClass the jdbc driver class
	 * @param jdbcConnectionPoolMinIdle the jdbc connection pool min idle
	 * @param jdbcConnectionPoolMaxIdle the jdbc connection pool max idle
	 */
	public AbstractSetSetting(final String jdbcUrl, final String jdbcUsername, final String jdbcPassword, final String jdbcDriverClass,
			final Integer jdbcConnectionPoolMinIdle, final Integer jdbcConnectionPoolMaxIdle) {
		super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriverClass, jdbcConnectionPoolMinIdle, jdbcConnectionPoolMaxIdle);
	}

	/**
	 * Execute.
	 * 
	 * @param settingName the setting name
	 * @param settingContext the setting context
	 * @param settingValue the setting value
	 */
	public void execute(final String settingName, final String settingContext, final String settingValue) {
		setSetting(settingName, settingContext, settingValue);
	}
}
