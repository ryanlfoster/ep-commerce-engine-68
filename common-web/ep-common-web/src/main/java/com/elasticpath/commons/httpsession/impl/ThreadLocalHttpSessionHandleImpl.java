package com.elasticpath.commons.httpsession.impl;

import javax.servlet.http.HttpSession;

import com.elasticpath.commons.httpsession.HttpSessionHandle;

/**
 * A singleton class that keeps track of the current http session in a thread local variable.
 */
public class ThreadLocalHttpSessionHandleImpl implements HttpSessionHandle {

	private final ThreadLocal<HttpSession> tlHttpSession = new ThreadLocal<HttpSession>();

	/**
	 * @return the http session
	 */
	public HttpSession getHttpSession() {
		return tlHttpSession.get();
	}

	/**
	 * @param session
	 *            the http session to keep a handle to
	 */
	public void setHttpSession(final HttpSession session) {
		this.tlHttpSession.set(session);
	}

}
