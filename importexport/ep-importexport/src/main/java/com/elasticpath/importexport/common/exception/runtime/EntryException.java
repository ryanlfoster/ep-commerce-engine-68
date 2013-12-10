/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.importexport.common.exception.runtime;

/**
 * An exception which is thrown by errors with an ExportEntry.
 */
public class EntryException extends EngineRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Exception is created with a message and a cause.
	 * 
	 * @param code message code
	 * @param cause cause of the exception
	 * @param params message parameters
	 * 
	 */
	public EntryException(final String code, final Throwable cause, final String... params) {
		super(code, cause, params);
	}
}
