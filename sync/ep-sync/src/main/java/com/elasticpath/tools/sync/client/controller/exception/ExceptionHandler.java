/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client.controller.exception;

import com.elasticpath.tools.sync.target.result.Summary;

/**
 * An exception hander.
 */
public interface ExceptionHandler {

	/**
	 * Checks whether this exception handler is applicable for the exception.
	 * 
	 * @param exc the exception instance
	 * @return true if the exception is applicable
	 */
	boolean canHandle(Exception exc);
	
	/**
	 * Handles an exception by reporting to the summary.
	 * 
	 * @param exc the exception to handle
	 * @param summary the summary object
	 */
	void handleException(Exception exc, Summary summary);
}
