/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.httpsession.settings.impl;


import com.elasticpath.commons.httpsession.HttpSessionHandle;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy;

/**
 * Storefront session implementation of SettingRefreshStrategy. This adds settings into a storefront session attribute, which will ensure that
 * settings won't change for the life of the customer's session. Sessions are set on the thread that processes the incoming request.
 */
public class HttpSessionRefreshStrategyImpl implements SettingRefreshStrategy {

	private HttpSessionHandle tlHttpSessionHandle;

	private SettingsReader settingsReader;

	private static final String PREFIX = HttpSessionRefreshStrategyImpl.class.getName();

	/**
	 * Retrieve the setting value for the given path and context from the http session.
	 * 
	 * @param path the setting path
	 * @param context the setting context
	 * @param params the setting metadata
	 * @return the setting value
	 */
	public SettingValue retrieveSetting(final String path, final String context, final String params) {
		// Do this if we have a session. i.e. SF only
		if (tlHttpSessionHandle != null && tlHttpSessionHandle.getHttpSession() != null) {
			SettingValue value = (SettingValue) tlHttpSessionHandle.getHttpSession().getAttribute(PREFIX + path + context);
			if (value == null) {
				value = getSettingsReader().getSettingValue(path, context);
				tlHttpSessionHandle.getHttpSession().setAttribute(PREFIX + path + context, value);
			}
			return value;
		}
		return getSettingsReader().getSettingValue(path, context);
	}

	/**
	 * Retrieve the setting value for the given path from the http session.
	 * 
	 * @param path the setting path
	 * @param params the setting metadata
	 * @return the setting value
	 */
	public SettingValue retrieveSetting(final String path, final String params) {
		// Do this if we have a session. i.e. SF only
		if (tlHttpSessionHandle != null && tlHttpSessionHandle.getHttpSession() != null) {
			SettingValue value = (SettingValue) tlHttpSessionHandle.getHttpSession().getAttribute(PREFIX + path);
			if (value == null) {
				value = getSettingsReader().getSettingValue(path);
				tlHttpSessionHandle.getHttpSession().setAttribute(PREFIX + path, value);
			}
			return value;
		}
		return getSettingsReader().getSettingValue(path);
	}

	/**
	 * @param settingsReader the settings reader to be used for retrieving the setting values
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * @return the settings reader to be used for retrieving the setting values
	 */
	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * @param httpSessionHandle the thread local http session handle to set
	 */
	public void setHttpSessionHandle(final HttpSessionHandle httpSessionHandle) {
		this.tlHttpSessionHandle = httpSessionHandle;
	}
}
