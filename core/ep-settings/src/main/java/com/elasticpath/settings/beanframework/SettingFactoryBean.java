package com.elasticpath.settings.beanframework;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Reads EP setting values as a Spring bean.
 */
public class SettingFactoryBean extends AbstractFactoryBean<String> {

	/**
	 * The @Autowired is necessary as otherwise we do not get the settings reader injected in time for createInstance().
	 */
	@Autowired
	@Qualifier("settingsReader")
	private SettingsReader settingsReader;

	private String path;

	private String context;

	@Override
	public Class<?> getObjectType() {
		return String.class;
	}

	@Override
	protected String createInstance() throws Exception {
		if (path == null) {
			throw new IllegalStateException("Path property must be set prior to invoking this method");
		}

		final SettingValue settingValue = getSettingValue();

		if (settingValue == null) {
			throw new IllegalArgumentException("Failed to load setting " + path + " in context " + context);
		}

		return settingValue.getValue();
	}

	private SettingValue getSettingValue() {
		if (!StringUtils.isBlank(context)) {
			return settingsReader.getSettingValue(path, context);
		}

		return settingsReader.getSettingValue(path);
	}

	/**
	 * Sets the path corresponding to the setting value to retrieve. Must be non-{@code null} and non-empty.
	 * 
	 * @param path the settings path (eg. COMMERCE/FOO/bar)
	 * @throws IllegalArgumentException if path is {@code null} or empty
	 */
	public void setPath(final String path) {
		if (StringUtils.isBlank(path)) {
			throw new IllegalArgumentException("Parameter path must be non-null and non-empty");
		}

		this.path = path;
	}

	/**
	 * Sets the context for which to load the setting value. May be null.
	 * 
	 * @param context the context
	 */
	public void setContext(final String context) {
		this.context = context;
	}

	protected void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

}