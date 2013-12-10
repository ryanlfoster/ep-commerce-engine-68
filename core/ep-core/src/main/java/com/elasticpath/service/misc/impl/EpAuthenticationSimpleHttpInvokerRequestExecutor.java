package com.elasticpath.service.misc.impl;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * Adds BASIC authentication support to <code>SimpleHttpInvokerRequestExecutor</code>.
 */
public class EpAuthenticationSimpleHttpInvokerRequestExecutor extends SimpleHttpInvokerRequestExecutor {
	
	private static final Logger LOG = Logger.getLogger(EpAuthenticationSimpleHttpInvokerRequestExecutor.class);

	private Authentication authentication;

	/**
	 * Provided so subclasses can perform additional configuration if required (eg set additional
	 * request headers for non-security related information etc).
	 * 
	 * @param con the HTTP connection to prepare
	 * @param contentLength the length of the content to send
	 * @throws IOException if thrown by HttpURLConnection methods
	 */
	protected void doPrepareConnection(final HttpURLConnection con, final int contentLength) throws IOException {
		// do nothing
	}

	/**
	 * Called every time a HTTP invocation is made.
	 * <p>
	 * Simply allows the parent to setup the connection, and then adds an
	 * <code>Authorization</code> HTTP header property that will be used for BASIC
	 * authentication.
	 * </p>
	 * 
	 * @param con the HTTP connection to prepare
	 * @param contentLength the length of the content to send
	 * @throws IOException if thrown by HttpURLConnection methods
	 * @throws AuthenticationCredentialsNotFoundException if the
	 *             <code>SecurityContextHolder</code> does not contain a valid
	 *             <code>Authentication</code> with both its <code>principal</code> and
	 *             <code>credentials</code> not <code>null</code>
	 */
	@Override
	protected void prepareConnection(final HttpURLConnection con, final int contentLength) throws IOException,
			AuthenticationCredentialsNotFoundException {
		super.prepareConnection(con, contentLength);
		Authentication auth = this.authentication;

		if (auth == null || auth.getName() == null || auth.getCredentials() == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Unable to set BASIC authentication header as SecurityContext did not provide "
						+ "valid Authentication: " + auth);
			}
		} else {
			String base64 = auth.getName() + ":" + auth.getCredentials().toString();
			con.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64(base64.getBytes())));
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("HttpInvocation now presenting via BASIC authentication SecurityContextHolder-derived: "
						+ auth.toString());
			}
		}

		doPrepareConnection(con, contentLength);
	}
	
	/**
	 * Sets the credentials used for each method invocation.
	 * 
	 * @param username the username
	 * @param password the password
	 */
	public void setCredentials(final String username, final String password) {
		authentication = new UsernamePasswordAuthenticationToken(username, password);
	}
	
	/**
	 * Sets the credentials used for each method invocation.
	 *
	 * @param authentication the credentials to use
	 */
	public void setCredentials(final Authentication authentication) {
		this.authentication = authentication;
	}
}
