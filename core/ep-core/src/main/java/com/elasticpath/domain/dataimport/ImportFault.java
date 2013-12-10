package com.elasticpath.domain.dataimport;

import com.elasticpath.persistence.api.Persistable;

/**
 * Represents a fault in an import.
 */
public interface ImportFault extends Persistable {
	/**
	 * The warning level.
	 */
	int WARNING = 1;

	/**
	 * The error level.
	 */
	int ERROR = 2;

	/**
	 * Returns the level.
	 *
	 * @return the level
	 */
	int getLevel();

	/**
	 * Sets the level.
	 *
	 * @param level the level to set
	 */
	void setLevel(int level);

	/**
	 * Returns <code>true</code> if the fault is a warning.
	 *
	 * @return <code>true</code> if the fault is a warning
	 */
	boolean isWarning();

	/**
	 * Returns <code>true</code> if the fault is an error.
	 *
	 * @return <code>true</code> if the fault is an error
	 */
	boolean isError();

	/**
	 * Returns the fault code.
	 *
	 * @return the fault code.
	 */
	String getCode();

	/**
	 * Sets the fault code.
	 *
	 * @param code the fault code to set
	 */
	void setCode(String code);

	/**
	 * Returns the fault source.
	 *
	 * @return the fault source
	 */
	String getSource();

	/**
	 * Sets the fault source.
	 *
	 * @param source the fault source to set
	 */
	void setSource(String source);

	/**
	 * Sets the arguments for fault code.
	 *
	 * @param args the arguments
	 */
	void setArgs(Object[] args);

	/**
	 * Returns the arguments for fault code.
	 *
	 * @return the arguments for fault code
	 */
	Object[] getArgs();
}
