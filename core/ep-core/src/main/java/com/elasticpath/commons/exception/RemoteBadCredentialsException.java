package com.elasticpath.commons.exception;

import org.springframework.security.authentication.rcp.RemoteAuthenticationException;

/**
 * Thrown if a <code>CmRemoteAuthenticationManager</code> cannot validate the presented authentication request because of bad credentials.
 */
public class RemoteBadCredentialsException extends RemoteAuthenticationException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Constructs the remote bad credentials exception with given message.
	 * 
	 * @param msg the exception message
	 */
	public RemoteBadCredentialsException(final String msg) {
		super(msg);
	}

}
