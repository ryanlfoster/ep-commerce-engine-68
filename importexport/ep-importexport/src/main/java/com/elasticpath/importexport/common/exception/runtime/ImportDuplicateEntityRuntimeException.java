package com.elasticpath.importexport.common.exception.runtime;

/**
 * Thrown in case entity with some guid has been already imported. 
 */
public class ImportDuplicateEntityRuntimeException extends ImportRuntimeException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new <code>ImportDuplicateEntityRuntimeException</code> with the specified detail message.
	 * 
	 * @param code message code
	 */
	public ImportDuplicateEntityRuntimeException(final String code) {
		super(code);
	}
	
	/**
	 * Constructs a new <code>ImportDuplicateEntityRuntimeException</code> with the instance of <code>Message</code>.
	 * 
	 * @param code message code
	 * @param params message parameters
	 */
	public ImportDuplicateEntityRuntimeException(final String code, final String... params) {
		super(code, params);
	}
}
