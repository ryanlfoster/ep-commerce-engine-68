package com.elasticpath.epcoretool.logic;

/**
 * The Class AbstractUnSetSetting.
 */
public abstract class AbstractUnSetSetting extends AbstractBaseSettings {

	/**
	 * Instantiates a new abstract un set setting.
	 *
	 * @param jdbcUrl the jdbc url
	 * @param jdbcUsername the jdbc username
	 * @param jdbcPassword the jdbc password
	 * @param jdbcDriverClass the jdbc driver class
	 * @param jdbcConnectionPoolMinIdle the jdbc connection pool min idle
	 * @param jdbcConnectionPoolMaxIdle the jdbc connection pool max idle
	 */
	public AbstractUnSetSetting(final String jdbcUrl, final String jdbcUsername, final String jdbcPassword, final String jdbcDriverClass,
			final Integer jdbcConnectionPoolMinIdle, final Integer jdbcConnectionPoolMaxIdle) {
		super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriverClass, jdbcConnectionPoolMinIdle, jdbcConnectionPoolMaxIdle);
	}

	/**
	 * Execute.
	 *
	 * @param settingName the setting name
	 * @param settingContext the setting context
	 */
	public void execute(final String settingName, final String settingContext) {
		unsetSetting(settingName, settingContext);
	}
}
