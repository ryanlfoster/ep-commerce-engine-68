package com.elasticpath.epcoretool.mojo;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractBulkSetSetting;
import com.elasticpath.epcoretool.logic.dto.EpSetting;

/** Suppress unknown tag warning.  */
@SuppressWarnings("javadoc")

/**
 * Updates the setting value in the Elastic Path database. If a value already exists, it will be removed before being re-added.
 * 
 * @goal bulk-set-settings
 */
public class BulkSetSettingMojo extends AbstractEpCoreMojo {

	/**
	 * @parameter alias="settings"
	 * @required
	 */
	private String[] settings;

	@SuppressWarnings("PMD.ArrayIsStoredDirectly")
	public void setSettings(final String[] settings) {
		this.settings = settings;
	}

	/**
	 * Look up a {@code SettingsService}, then fetch the specified setting+context. If the value returned is flagged as persistence, then request
	 * that it's deleted before calling update.
	 *
	 * @throws MojoExecutionException the mojo execution exception
	 * @throws MojoFailureException the mojo failure exception
	 */
	@Override
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
		AbstractBulkSetSetting bulkSetSetting = new AbstractBulkSetSetting(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(), getJdbcDriverClass(),
				getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		try {
			// bulkSetSetting.execute(new HashSet(Arrays.asList(settings)));
			Set<EpSetting> bulkSettings = new HashSet<EpSetting>();
			for (String setting : settings) {
				bulkSettings.add(bulkSetSetting.parseSettingString(setting));
			}
			bulkSetSetting.execute(bulkSettings);

		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		}
	}
}
