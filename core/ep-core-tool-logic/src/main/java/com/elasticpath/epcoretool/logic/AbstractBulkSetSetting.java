package com.elasticpath.epcoretool.logic;

import java.util.Set;

import com.elasticpath.epcoretool.logic.dto.EpSetting;

/**
 * The Class AbstractBulkSetSetting.
 */
public abstract class AbstractBulkSetSetting extends AbstractBaseSettings {

	/**
	 * Instantiates a new abstract bulk set setting.
	 *
	 * @param jdbcUrl the jdbc url
	 * @param jdbcUsername the jdbc username
	 * @param jdbcPassword the jdbc password
	 * @param jdbcDriverClass the jdbc driver class
	 * @param jdbcConnectionPoolMinIdle the jdbc connection pool min idle
	 * @param jdbcConnectionPoolMaxIdle the jdbc connection pool max idle
	 */
	public AbstractBulkSetSetting(final String jdbcUrl, final String jdbcUsername, final String jdbcPassword, final String jdbcDriverClass,
			final Integer jdbcConnectionPoolMinIdle, final Integer jdbcConnectionPoolMaxIdle) {
		super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriverClass, jdbcConnectionPoolMinIdle, jdbcConnectionPoolMaxIdle);
	}

	/**
	 * Execute.
	 *
	 * @param settings the settings
	 */
	public void execute(final Set<EpSetting> settings) {
		for (EpSetting setting : settings) {
			setSetting(setting.getName(), setting.getContext(), setting.getValue());
		}
	}
}
