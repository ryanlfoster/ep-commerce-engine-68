/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.misc.impl;

import org.apache.log4j.Logger;

/**
 * Empty application initialization. Does nothing other than log the fact it was called.
 */
public class EmptyApplicationInitialization {
	
	private static final Logger LOG = Logger.getLogger(EmptyApplicationInitialization.class);
	
	/**
	 * Do nothing other than log.
	 */
	public void init() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Empty initializer called");
		}
	}

}
