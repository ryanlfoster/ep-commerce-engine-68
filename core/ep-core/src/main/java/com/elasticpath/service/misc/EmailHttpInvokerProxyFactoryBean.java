package com.elasticpath.service.misc;

import org.springframework.security.remoting.httpinvoker.AuthenticationSimpleHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.service.misc.impl.EpAuthenticationSimpleHttpInvokerRequestExecutor;
import com.elasticpath.settings.SettingsReader;

/**
 * <p>
 * Enhances the Spring HttpInvokerProxyFactoryBean by allowing one to set the serverURL prefix
 * needed to find all the remote beans.
 * </p>
 * <p>
 * Also overrides the method that gets the RequestExecutor so that it by default uses Spring Security's
 * AuthenticationSimpleHttpInvokerRequestExecutor, thereby allowing the BASIC auth credentials to
 * be set in the HTTP headers automatically. Rather than require injecting this bean in every
 * remote bean definition, it was easier to simply inject it programatically.
 * </p>
 * <p>
 * This class is designed so that after the application context has been loaded, once can set the
 * server's URL at runtime.
 * </p>
 */
public class EmailHttpInvokerProxyFactoryBean extends HttpInvokerProxyFactoryBean {

	private static final String SETTING_EMAILSERVICE_HOST_URL = "COMMERCE/SYSTEM/EMAIL/emailCmUrl";
	private static final String SETTING_EMAILSERVER_USERNAME = "COMMERCE/SYSTEM/EMAIL/emailAuthenticationUsername";
	private static final String SETTING_EMAILSERVER_PASSWORD = "COMMERCE/SYSTEM/EMAIL/emailAuthenticationPassword";
		
	private String serverUrlPrefix;
	
	private SettingsReader settingsReader;

	/**
	 * Sets the server URL prefix. e.g. "http://localhost:8080/ep5/".
	 * 
	 * @param serverUrlPrefix the URL to the remote server including the context.
	 */
	public void setServerUrlPrefix(final String serverUrlPrefix) {
		this.serverUrlPrefix = serverUrlPrefix;
	}

	/**
	 * {@inheritDoc} This implementation prepends the serverUrlPrefix set at runtime.
	 */
	@Override
	public String getServiceUrl() {
		if (serverUrlPrefix == null) {
			// method is called in spring initialization method (before elastic path is
			// initialized) -- need to catch this
			try {
				String cmEmailUrl = getConfiguredEmailServiceHostUrl().trim();
				if (!cmEmailUrl.endsWith("/")) {
					cmEmailUrl = cmEmailUrl.concat("/");
				}
				serverUrlPrefix = cmEmailUrl;
			} catch (EpSystemException e) {
				return super.getServiceUrl();
			}
		}
		return serverUrlPrefix.concat(super.getServiceUrl());
	}
	
	/**
	 * @return the configured email service host url, from the settings service
	 */
	String getConfiguredEmailServiceHostUrl() {
		return getSettingsReader().getSettingValue(SETTING_EMAILSERVICE_HOST_URL).getValue();
	}

	/**
	 * {@inheritDoc} This implementation returns an instance of the Spring Security
	 * AuthenticationSimpleHttpInvokerRequestExecutor.
	 * This implementation retrieves the username and password for authentication
	 * from the SettingsService. If there is a problem retrieving the username
	 * and password then the credentials will not be set in the returned object.
	 * 
	 * @return an instance of Spring Security's AuthenticationSimpleHttpInvokerRequestExecutor
	 */
	@Override
	public HttpInvokerRequestExecutor getHttpInvokerRequestExecutor() {
		String username, password;
		try {
			username = getSettingsReader().getSettingValue(SETTING_EMAILSERVER_USERNAME).getValue();
			password = getSettingsReader().getSettingValue(SETTING_EMAILSERVER_PASSWORD).getValue();
		} catch (EpSystemException e) {
			return new AuthenticationSimpleHttpInvokerRequestExecutor();
		}
		EpAuthenticationSimpleHttpInvokerRequestExecutor authExecutor = new EpAuthenticationSimpleHttpInvokerRequestExecutor();
		authExecutor.setCredentials(username, password);
		return authExecutor;
	}

	/**
	 * @return the settingsReader
	 */
	public SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * @param settingsReader the settingsService to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
}
