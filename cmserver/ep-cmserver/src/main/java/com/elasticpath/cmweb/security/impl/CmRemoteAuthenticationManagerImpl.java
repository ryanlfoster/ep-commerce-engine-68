package com.elasticpath.cmweb.security.impl;

import java.util.Collection;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.rcp.RemoteAuthenticationException;
import org.springframework.security.authentication.rcp.RemoteAuthenticationManagerImpl;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import com.elasticpath.commons.exception.RemoteBadCredentialsException;
import com.elasticpath.commons.exception.RemoteLockedException;
import com.elasticpath.settings.SettingsReader;

/**
 * Server-side processor of a remote authentication request.
 * <P>
 * This bean requires no security interceptor to protect it. Instead, the bean uses the configured <code>AuthenticationManager</code> to resolve an
 * authentication request.
 * </p>
 */
public class CmRemoteAuthenticationManagerImpl extends RemoteAuthenticationManagerImpl {

	private static final String SETTING_GLOBAL_SENDER_ADDRESS = "COMMERCE/SYSTEM/EMAIL/emailGlobalSenderAddress";

	private SettingsReader settingsReader;

	@SuppressWarnings("PMD.PreserveStackTrace")
	@Override
	public Collection<? extends GrantedAuthority> attemptAuthentication(final String username, final String password)
			throws RemoteAuthenticationException {

		UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(username, password);
		try {
			return getAuthenticationManager().authenticate(request).getAuthorities();
		} catch (LockedException lockedException) {
			throw new RemoteLockedException(lockedException.getMessage(),
					getSettingsReader().getSettingValue(SETTING_GLOBAL_SENDER_ADDRESS).getValue());
		} catch (AuthenticationException authException) {
			throw new RemoteBadCredentialsException(authException.getMessage());
		}
	}

	/**
	 * @return the settingsReader
	 */
	public SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * @param settingsReader the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
}
