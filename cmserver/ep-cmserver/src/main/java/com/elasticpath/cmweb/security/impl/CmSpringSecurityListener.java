package com.elasticpath.cmweb.security.impl;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Listener that is responsible for handling spring security events.
 */
public class CmSpringSecurityListener implements ApplicationListener<ApplicationEvent> {
	
	private CmUserService cmUserService;

	@Override
	public void onApplicationEvent(final ApplicationEvent event) {
		if (event instanceof AuthenticationFailureBadCredentialsEvent) {
			final BadCredentialsException exception = (BadCredentialsException) ((AuthenticationFailureBadCredentialsEvent) event).getException();
			final CmUser cmUser = (CmUser) exception.getExtraInformation();
			if (cmUser != null) {
				cmUser.addFailedLoginAttempt();
				cmUserService.update(cmUser);
				
				handleLastAttempt(cmUser);
			}
		}

	}

	private void handleLastAttempt(final CmUser cmUser) {
		if (!cmUser.isAccountNonLocked()) {
			throw new LockedException("Account has been locked.");
		}
	}

	/**
	 * Gets the cmUser service.
	 * 
	 * @return the cmUserService
	 */
	public CmUserService getCmUserService() {
		return cmUserService;
	}

	/**
	 * Sets the cmUser service.
	 * 
	 * @param cmUserService the cmUserService
	 */
	public void setCmUserService(final CmUserService cmUserService) {
		this.cmUserService = cmUserService;
	}

}
