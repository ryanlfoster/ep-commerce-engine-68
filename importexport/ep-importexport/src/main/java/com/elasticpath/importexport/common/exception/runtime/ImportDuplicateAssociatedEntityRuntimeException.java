package com.elasticpath.importexport.common.exception.runtime;


/**
 * Thrown in case entity with an associated unique guid entity has been already imported.
 */
public class ImportDuplicateAssociatedEntityRuntimeException extends ImportRuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new <code>ImportDuplicateAssociatedEntityRuntimeException</code> with the specified detail message.
	 *
	 * @param code message code
	 */
	public ImportDuplicateAssociatedEntityRuntimeException(final String code) {
		super(code);
	}

	/**
	 * Constructs a new <code>ImportDuplicateAssociatedEntityRuntimeException</code> with the instance of <code>Message</code>.
	 *
	 * @param code message code
	 * @param params message parameters
	 */
	public ImportDuplicateAssociatedEntityRuntimeException(final String code, final String... params) {
		super(code, params);
	}
}
