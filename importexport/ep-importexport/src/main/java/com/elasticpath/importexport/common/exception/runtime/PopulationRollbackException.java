package com.elasticpath.importexport.common.exception.runtime;

/**
 * Thrown to indicate an error during population of domain or dto objects. The purpose of this exception is to replace the
 * <code>PopulationRuntimeException</code> in cases when the surrounding transaction has to be rolled back. Use the following rule to decide whether
 * to throw this particular exception instead of the <code>PopulationRuntimeException</code>:
 * <p>
 * <i> If an importer <b>X</b> uses services to save, add or update domain objects, then each adapter that is called from this importer should throw
 * the <code>PopulationRollbackException</code> instead of the <code>PopulationRuntimeException</code>. </i>
 * </p>
 */
public class PopulationRollbackException extends EngineRuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new <code>PopulationRollbackException</code> with the specified detail message.
	 * 
	 * @param code message code
	 */
	public PopulationRollbackException(final String code) {
		super(code);
	}

	/**
	 * Constructs a new <code>PopulateRollbackException</code> with the instance of <code>Message</code>.
	 * 
	 * @param code message code
	 * @param params message parameters
	 */
	public PopulationRollbackException(final String code, final String... params) {
		super(code, params);
	}

	/**
	 * Constructs a new <code>PopulationRollbackException</code> with the instance of <code>Message</code> populated with exception.
	 * 
	 * @param code message code
	 * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
	 * @param params message parameters
	 */
	public PopulationRollbackException(final String code, final Throwable cause, final String... params) {
		super(code, cause, params);
	}

}
