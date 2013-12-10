/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.httpsession;

import javax.servlet.http.HttpSession;

/**
 * A simple wrapper for keeping a handle on an http session object.
 */
public interface HttpSessionHandle {

	/**
	 * @return the http session
	 */
	HttpSession getHttpSession();
	
	/**
	 * @param session the http session to keep a handle to
	 */
	void setHttpSession(final HttpSession session);
}
