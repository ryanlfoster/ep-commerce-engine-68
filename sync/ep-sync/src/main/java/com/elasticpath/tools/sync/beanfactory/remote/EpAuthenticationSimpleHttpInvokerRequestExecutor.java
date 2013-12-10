package com.elasticpath.tools.sync.beanfactory.remote;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.springframework.security.core.Authentication;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;

/**
 * Prepares HTTP connection.
 */
public class EpAuthenticationSimpleHttpInvokerRequestExecutor extends SimpleHttpInvokerRequestExecutor {
	private static final Logger LOG = Logger.getLogger(EpAuthenticationSimpleHttpInvokerRequestExecutor.class);

	private final Authentication auth;

	/**
	 * Initializes authentication request.
	 * 
	 * @param auth authentication request
	 */
	public EpAuthenticationSimpleHttpInvokerRequestExecutor(final Authentication auth) {
		super();
		this.auth = auth;
	}

	/**
	 * Prepares connection.
	 * 
	 * @param con connection
	 * @param contentLength content length
	 * @throws IOException if unable to prepare connection
	 */
	protected void prepareConnection(final HttpURLConnection con, final int contentLength) throws IOException {
		super.prepareConnection(con, contentLength);

		if ((auth == null) || (auth.getName() == null) || (auth.getCredentials() == null)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Unable to set BASIC authentication header as SecurityContext did not provide " + "valid Authentication: " + auth);
			}
		} else {
			String base64 = auth.getName() + ":" + auth.getCredentials().toString();
			con.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64(base64.getBytes())));
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("HttpInvocation now presenting via BASIC authentication SecurityContextHolder-derived: " + auth.toString());
			}
		}

	}
}
