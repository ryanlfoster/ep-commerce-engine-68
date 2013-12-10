package com.elasticpath.importexport.common.exception.runtime;

/**
 * Thrown to indicate that error arises during marshalling or unmarshalling operations.
 */
public class MarshallingRuntimeException extends EngineRuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new <code>MarshallingRuntimeException</code> exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is <i>not</i> automatically incorporated in this exception's detail
	 * message.
	 * 
	 * @param code the detail message
	 * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
	 * @param params message parameters
	 */
	public MarshallingRuntimeException(final String code, final Throwable cause, final String... params) {
		super(code, cause, params);
	}

	/**
	 * Constructs a new <code>MarshallingRuntimeException</code> with the specified detail message.
	 * 
	 * @param code the detail message
	 */
	public MarshallingRuntimeException(final String code) {
		super(code);
	}
}
