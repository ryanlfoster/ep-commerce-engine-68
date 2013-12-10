package com.elasticpath.epcoretool;

/**
 * The Interface LoggerFacade.
 */
public interface LoggerFacade {

	/**
	 * Error.
	 *
	 * @param message the message
	 */
	void error(String message);

	/**
	 * Warn.
	 *
	 * @param message the message
	 */
	void warn(String message);

	/**
	 * Info.
	 *
	 * @param message the message
	 */
	void info(String message);

	/**
	 * Debug.
	 *
	 * @param message the message
	 */
	void debug(String message);
}
