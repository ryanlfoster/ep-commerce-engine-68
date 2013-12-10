package com.elasticpath.tools.sync.beanfactory.remote;

/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */

import org.apache.log4j.Logger;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.rcp.RemoteAuthenticationException;
import org.springframework.security.authentication.rcp.RemoteAuthenticationManager;
import org.springframework.security.authentication.rcp.RemoteAuthenticationProvider;
import org.springframework.security.core.Authentication;

/**
 * Handles remote authentication.
 */
public final class AuthenticationServiceImpl {
	private static final Logger LOG = Logger.getLogger(AuthenticationServiceImpl.class);

	private static AuthenticationServiceImpl instance = new AuthenticationServiceImpl();

	private transient Authentication authToken;

	private static final String AUTH_SERVICE_URL = "authenticationService.login";

	private AuthenticationServiceImpl() {
		// Singleton
	}

	/**
	 * Return instance of the singleton.
	 * 
	 * @return instance of the singleton
	 */
	public static AuthenticationServiceImpl getInstance() {
		return instance;
	}

	/**
	 * Determine if the user has been authenticated.
	 * 
	 * @return true if authenticated, false if not
	 */
	public boolean isAuthenticated() {
		if (authToken == null) {
			return false;
		}
		return this.authToken.isAuthenticated();
	}

	/**
	 * Attempt to authenticate a user against a remote server.
	 * 
	 * @param username the userId
	 * @param password the password
	 * @param serverURL the URL of the remote server, including context. e.g. http://localhost:8080/epcm
	 * @return authentication
	 */
	public Authentication login(final String username, final String password, final String serverURL) {
		String authenticationURL;
		authToken = new UsernamePasswordAuthenticationToken(username, password);

		if (serverURL.endsWith("/")) {
			authenticationURL = serverURL;
		} else {
			authenticationURL = serverURL + "/";
		}
		authenticationURL = authenticationURL.concat(AUTH_SERVICE_URL);

		// Get an authentication provider and manager proxy.
		final HttpInvokerProxyFactoryBean authManager = new HttpInvokerProxyFactoryBean();
		authManager.setServiceInterface(RemoteAuthenticationManager.class);
		authManager.setServiceUrl(authenticationURL);
		authManager.afterPropertiesSet();

		LOG.debug("Retrieving reference to remote authentication manager at " + authenticationURL);
		RemoteAuthenticationManager remoteAuthManager = (RemoteAuthenticationManager) authManager.getObject();

		final RemoteAuthenticationProvider authProvider = new RemoteAuthenticationProvider();
		authProvider.setRemoteAuthenticationManager(remoteAuthManager);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Logging in  to '" + authenticationURL + "' with username/pwd = " + username + "/" + password);
		}

		try {
			return authProvider.authenticate(authToken);
		} catch (RemoteAuthenticationException e) {
			LOG.warn("Authentication Failed", e);
			throw e;
		} catch (RemoteAccessException e) {
			LOG.warn("RemoteAccessException", e);
			throw e;
		}
	}
}
