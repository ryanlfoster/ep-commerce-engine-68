/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.importexport.importer.controller;

import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;

/**
 * An exception signifying that an import stage failed to execute properly.
 */
public class ImportStageFailedException extends ImportRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new exception object.
	 * 
	 * @param code the code
	 * @param exception the exception
	 * @param params the parameters of the message
	 */
	public ImportStageFailedException(final String code, final Throwable exception, final String... params) {
		super(code, exception, params);
	}

}
