package com.elasticpath.commons.exception;

import org.springframework.security.authentication.rcp.RemoteAuthenticationException;

/**
 * Thrown if a <code>CmRemoteAuthenticationManager</code> cannot validate the presented authentication request because user is locked.
 */
public class RemoteLockedException extends RemoteAuthenticationException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private String adminEmailAddress;

	/**
	 * Constructs the remote locked exception with given message.
	 * 
	 * @param msg the exception message
	 */
	public RemoteLockedException(final String msg) {
		super(msg);
	}
	
	/**
	 * Constructs the remote locked exception with given message.
	 * 
	 * @param msg the exception message
	 * @param adminEmailAddress the administrator email address to request to unlock account 
	 */
	public RemoteLockedException(final String msg, final String adminEmailAddress) {
		super(msg);
		this.adminEmailAddress = adminEmailAddress;
	}

	/**
	 * Gets the administrator email address for request to unlock account.
	 * 
	 * @return administrator email address
	 */
	public String getAdminEmailAddress() {
		return adminEmailAddress;
	}

}
