package com.elasticpath.sfweb.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Represents the result of an authentication for a guest customer.  
 */
public class GuestAuthenticationToken extends AbstractAuthenticationToken implements Authentication {

	private static final long serialVersionUID = 8926071141418038121L;

	private final String principal;
	
	/**
	 * Default Constructor.  
	 * @param principal customer's email address.
	 * @param authorities collection of authorities the guest customer has
	 */
	public GuestAuthenticationToken(final String principal, final Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
	}
	
	/** 
	 * Get the customer credentials.  
	 * @return <code>null</code> since a guest customer does not need to provide any credentials.
	 */
	public Object getCredentials() {
		return null;
	}
	
	/**
	 * @return customer's email address.
	 */
	public String getPrincipal() {
		return principal;
	}

}
