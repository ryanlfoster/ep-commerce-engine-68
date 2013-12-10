package com.elasticpath.sfweb.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

/**
 * Provides authentication for {@link GuestAuthenticationToken}.
 */
public class GuestAuthenticationProvider implements AuthenticationProvider {

	
	/**
	 * Performs authentication on a {@link GuestAuthenticationToken}. 
	 * 
	 * @param authentication the authentication from the request
	 * @return the fully authenticated object
	 *  
	 * @see org.springframework.security.authentication.AuthenticationManager#authenticate(Authentication).
	 */
	public Authentication authenticate(final Authentication authentication) {
		return authentication;
	}

	@Override
	public boolean supports(final Class<? extends Object> authentication) {
		return authentication.isAssignableFrom(GuestAuthenticationToken.class);
	}

}
