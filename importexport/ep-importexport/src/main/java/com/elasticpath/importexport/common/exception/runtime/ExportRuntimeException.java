package com.elasticpath.importexport.common.exception.runtime;

/**
 * Thrown in case of errors during delivery, package and other export operations.
 */
public class ExportRuntimeException extends EngineRuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new <code>ExportRuntimeException</code> exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is <i>not</i> automatically incorporated in this exception's detail
	 * message.
	 * 
	 * @param code the detail message
	 * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
	 * @param params message parameters
	 */
	public ExportRuntimeException(final String code, final Throwable cause, final String... params) {
		super(code, cause, params);
	}

	/**
	 * Constructs a new <code>ExportRuntimeException</code> exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is <i>not</i> automatically incorporated in
	 * this exception's detail message.
	 * 
	 * @param code message code
	 * @param params message parameters
	 */
	public ExportRuntimeException(final String code, final String... params) {
		super(code, params);
	}

	/**
	 * Constructs a new <code>ExportRuntimeException</code> with the specified detail message.
	 * 
	 * @param code message code
	 */
	public ExportRuntimeException(final String code) {
		super(code);
	}
}
