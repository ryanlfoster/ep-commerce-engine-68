package com.elasticpath.importexport.common.exception.runtime;

/**
 * Thrown to indicate that an error arises during population domain or dto objects.
 */
public class PopulationRuntimeException extends EngineRuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new <code>PopulationRuntimeException</code> with the specified detail message.
	 * 
	 * @param code message code
	 */
	public PopulationRuntimeException(final String code) {
		super(code);
	}

	/**
	 * Constructs a new <code>PopulateRuntimeException</code> with the instance of <code>Message</code>.
	 * 
	 * @param code message code
	 * @param params message parameters
	 */
	public PopulationRuntimeException(final String code, final String... params) {
		super(code, params);
	}
	
	/**
	 * Constructs a new <code>EngineRuntimeException</code> with the instance of <code>Message</code> populated with exception.
	 * 
	 * @param code message code
	 * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
	 * @param params message parameters
	 */
	public PopulationRuntimeException(final String code, final Throwable cause, final String... params) {
		super(code, cause, params);
	}
}
