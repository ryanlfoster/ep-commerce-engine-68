package com.elasticpath.importexport.common.exception.runtime;

import com.elasticpath.importexport.common.util.Message;

/**
 * <code>EngineRuntimeException</code> is the superclass of those exceptions that can be thrown during the import-export operations.
 */
public class EngineRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final Message ieMessage;

	/**
	 * Constructs a new <code>EngineRuntimeException</code> with the specified detail message.
	 * 
	 * @param code the message code
	 */
	public EngineRuntimeException(final String code) {
		super(code);
		ieMessage = new Message(code);
	}
	
	/**
	 * Constructs a new <code>EngineRuntimeException</code> with the instance of <code>Message</code>.
	 * 
	 * @param code message code
	 * @param params message parameters
	 */
	public EngineRuntimeException(final String code, final String... params) {
		ieMessage = new Message(code, params);
	}

	/**
	 * Constructs a new <code>EngineRuntimeException</code> with the instance of <code>Message</code> populated with exception.
	 * 
	 * @param code message code
	 * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
	 * @param params message parameters
	 */
	public EngineRuntimeException(final String code, final Throwable cause, final String... params) {
		super(cause);
		ieMessage = new Message(code, cause, params);
	}

	/**
	 * Gets Import Export message.
	 * 
	 * @return the ieMessage
	 */
	public Message getIEMessage() {
		return ieMessage;
	}
}
