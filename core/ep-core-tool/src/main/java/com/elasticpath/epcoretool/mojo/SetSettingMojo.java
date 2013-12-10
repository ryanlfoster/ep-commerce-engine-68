package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractSetSetting;

/** Suppress unknown tag warning.  */
@SuppressWarnings("javadoc")

/**
 * Updates the setting value in the Elastic Path database. If a value already exists, it will be removed.
 * 
 * @goal set-setting
 */
public class SetSettingMojo extends AbstractEpCoreMojo {

	/**
	 * Name of setting.
	 * 
	 * @parameter expression="${settingName}"
	 * @required
	 */
	private String settingName;

	/**
	 * Context for the setting (eg, store code).
	 * 
	 * @parameter expression="${settingContext}"
	 */
	private String settingContext;

	/**
	 * New value.
	 * 
	 * @parameter expression="${settingValue}"
	 * @required
	 */
	private String settingValue;

	/**
	 * Look up a {@code SettingsService}, then fetch the specified setting+context. If the value returned is flagged as persistence, then request
	 * that it's deleted before calling update.
	 *
	 * @throws MojoExecutionException the mojo execution exception
	 * @throws MojoFailureException the mojo failure exception
	 */
	@Override
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
		AbstractSetSetting setSetting = new AbstractSetSetting(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(), getJdbcDriverClass(),
				getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		try {
			setSetting.execute(settingName, settingContext, settingValue);
		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		}
	}

}
