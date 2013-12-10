package com.elasticpath.cmweb.security.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Commerce Manager Server implementation of Spring Security's {@link AccessDeniedHandler}.
 * 
 * This implementation sends a 403 (SC_FORBIDDEN) HTTP error code. In addition, it logs the
 * authentication or authorisation problem.
 * 
 */
public class CmAccessDeniedHandlerImpl implements AccessDeniedHandler {
	
	private static final Logger LOG = Logger.getLogger(CmAccessDeniedHandlerImpl.class);

	/**
	 * Handles an access denied failure.<br>
	 * 
	 * This implementation sends a 403 access denied to the client, and logs the 
	 * authorisation exception. 
	 *
     * Specified by:
     *   handle in interface AccessDeniedHandler
	 *
     * @param request that resulted in an AccessDeniedException
     * @param response so that the user agent can be advised of the failure
     * @param accessDeniedException - that caused the invocation 
     * 
     * @throws IOException - in the event of an IOException 
     * @throws ServletException - in the event of a ServletException
	 */
	public void handle(final HttpServletRequest request,
			final HttpServletResponse response,
			final AccessDeniedException accessDeniedException)
			throws IOException, ServletException {

		if (!response.isCommitted()) {
			LOG.info("Authorization failed: " + accessDeniedException.getMessage());
			// Send 403 (we do this after response has been written)
			response.sendError(
					HttpServletResponse.SC_FORBIDDEN, accessDeniedException
							.getMessage());
		}
	}
}
